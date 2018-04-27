package mic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;

abstract public class ManagerBase implements Manager {
	/*
	 *  Computes the 'scores' that will be used to calculate bestMove
	 */
	protected abstract void computeScores(List<Worker> workers, Map<Move, Integer> scores, List<Move> legals);
	/*
	 * Given the scores calculates the newBestMove (does not set bestMove!)
	 */

	private volatile AtomicBoolean stop;
	private volatile boolean end;
	private volatile AtomicBoolean go;

	private volatile Move bestMove;
	private volatile StateMachine machine;
	private volatile MachineState state;
	private volatile Role role;

	private List<Worker> workerPrototypes;


	public ManagerBase(List<Worker> workers) {
		end = false;
		stop = new AtomicBoolean(false);
		go = new AtomicBoolean(false);
		workerPrototypes = workers;
	}

	@Override
	public void run() {
		List<Worker> currentWorkers = new Vector<Worker>();
		GameProperties properties = new GameProperties();

		while (!end) {
			while (!go.getAndSet(false)) {
				try {
					//we aren't doing anything so spinning is fine
					synchronized (go) {
						go.wait(100);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			//Start workers on the move
			for (Worker proto : workerPrototypes) {
				Worker w = proto.clone();
				w.initialize(machine, state, role, properties);
				Thread t = new Thread(w);
				t.start();
				currentWorkers.add(w);
			}

			//Compute legal moves will be used later
			//but might as well compute them now
			List<Move> legals = null;
			try {
				legals = machine.getLegalMoves(state, role);
				bestMove = legals.get(0);
			} catch (MoveDefinitionException e1) {
				e1.printStackTrace();
			}

			Map<Move, Integer> scores = new HashMap<Move, Integer>();

			while(!stop.getAndSet(false)) {
				try {
					//Ensure the best move is updated every 500 ms
					synchronized (stop) {
						stop.wait(500);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				computeScores(currentWorkers, scores, legals);
				Integer mostVotes = 0;
				Move bestNext = legals.get(0);
				for (Map.Entry<Move, Integer> e : scores.entrySet()) {
					if (e.getValue() > mostVotes) {
						bestNext = e.getKey();
						mostVotes = e.getValue();
					}
				}

				synchronized (bestMove) {
					bestMove = bestNext;
				}
			}
			for (Worker worker: currentWorkers) {
				worker.stop();
			}
			currentWorkers.clear();
		}

	}
	@Override
	public void start(StateMachine machineIn, MachineState stateIn, Role roleIn) {
		machine = machineIn;
		state = stateIn;
		role = roleIn;
		go.set(true);
		synchronized (go) {
			go.notifyAll();
		}
	}

	@Override
	public void stop() {
		stop.set(true);
		synchronized (stop) {
			stop.notifyAll();
		}
	}

	@Override
	public Move getBestMove() {
		synchronized (bestMove) {
			return bestMove;
		}
	}

	@Override
	public void gameOver() {
		end = true;
	}

}
