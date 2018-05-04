package mic.base.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;

import mic.base.GameProperties;
import mic.base.worker.Worker;

abstract public class ManagerBase implements Manager {
	/*
	 * Computes the 'scores' that will be used to calculate bestMove
	 */
	protected abstract Move computeBestMove();
	/*
	 * Given the scores calculates the newBestMove (does not set bestMove!)
	 */

	protected volatile Boolean stop;
	protected volatile Boolean halt;

	protected Move bestMove;
	protected StateMachine machine;
	protected MachineState state;
	protected Role role;

	protected Map<Worker, Thread> workers;
	protected List<Move> legals;

	public ManagerBase(List<Worker> workers) {
		halt = false;
		stop = true;
		this.workers = new HashMap<Worker, Thread>();
		for (Worker worker : workers) {
			Thread t = new Thread(worker);
			t.start();
			this.workers.put(worker, t);
		}
	}

	@Override
	public void run() {
		GameProperties properties = new GameProperties();
		try {
			while (!halt) {
				while (stop && !halt) {
					// we aren't doing anything so spinning is fine
					Thread.yield();
				}

				// Start workers on the move
				for (Entry<Worker, Thread> wt : workers.entrySet()) {
					//restart any dead workers
					if (!wt.getValue().isAlive()) {
						Thread t = new Thread(wt.getValue());
						t.start();
						wt.setValue(t);
					}
					wt.getKey().initialize(machine, state, role, properties);
				}


				while (!stop && !halt) {
					synchronized (stop) {
						try {
							stop.wait(100);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}

			}
		} catch (MoveDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void start(StateMachine machineIn, MachineState stateIn, Role roleIn) throws MoveDefinitionException {
		machine = machineIn;
		state = stateIn;
		role = roleIn;
		legals = machine.getLegalMoves(state, role);
		bestMove = legals.get(0);
		stop = false;
	}

	@Override
	public void stop() {
		stop = true;
		for (Worker worker : workers.keySet()) {
			worker.stop();
		}
		synchronized (stop) {
			stop.notifyAll();
		}
	}

	@Override
	public Move getBestMove() {
		if (!stop) {
			return legals.get(0);
		} else {
			return computeBestMove();
		}
	}

	@Override
	public void halt() {
		halt = true;
		stop();
		for (Worker worker : workers.keySet()) {
			worker.halt();
		}
	}

}
