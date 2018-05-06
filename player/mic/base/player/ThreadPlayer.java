package mic.base.player;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import mic.base.manager.Manager;

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
			long tleft = Math.max(timeout - System.currentTimeMillis()  - PREFERRED_PLAY_BUFFER - 550, 1);
			managerThread.join(tleft);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		manager.stop();
		try {
			long tleft = Math.max(timeout - System.currentTimeMillis()  - PREFERRED_PLAY_BUFFER - 50, 1);
			managerThread.join(tleft);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


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
		manager.halt();
		managerThread = null;
		cleanupAfterMatch();

	}
}
