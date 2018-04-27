package mic;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

public interface Worker extends Runnable {
	/*
	 * This method should cause the worker to stop to stop its search
	 */
	public void stop();

	/*
	 * This method sets up the player
	 * properties object is empty right now but in the feature we
	 * might want to store info about the game
	 */
	public void initialize(
			StateMachine machine,
			MachineState state,
			Role role, GameProperties properties);

	/*
	 * Given a legal move return an evaluation for it
	 * For non best move returning 0 is fine
	 */
	public int eval(Move move);

	/*
	 * Should return the best move found so far
	 */
	public Move getBest();


	/*
	 * Should basically be
	 * Worker w =  new <CLASS>(...)
	 * w.initiliaze(...)
	 * return w
	 */
	public Worker clone();
}
