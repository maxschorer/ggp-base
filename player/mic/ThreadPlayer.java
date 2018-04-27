package mic;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.cache.CachedStateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

public abstract class ThreadPlayer extends GGPlayer {

	private Manager manager;
	private Thread  managerThread;

	/*
	 * Sets the manager
	 */
	abstract protected Manager getInitialManager();

	@Override
	public void start(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		manager = getInitialManager();
		managerThread = new Thread(manager);
		managerThread.start();
	}

	@Override
	public Move play(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {

		StateMachine machine = getStateMachine();
		MachineState state = getCurrentState();
		Role role = getRole();
		manager.start(machine, state, role);

		try {
			managerThread.join(timeout - System.currentTimeMillis() - PREFERRED_PLAY_BUFFER);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		manager.stop();
	    System.out.println(manager.getBestMove());
		return manager.getBestMove();
	}

	@Override
	public void abort() {
		stop();
	}

	@Override
	public void stop() {
		manager.stop();
		manager.gameOver();
		managerThread = null;
		cleanupAfterMatch();

	}

	@Override
	public StateMachine getInitialStateMachine() {
		return new CachedStateMachine(new ProverStateMachine());
	}



}
