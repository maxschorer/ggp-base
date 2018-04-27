package mic;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;


public interface Manager extends Runnable {
	//Tells the manager to start searching this state
	public void start(StateMachine machine, MachineState state, Role role);
	//Tells the manager to stop searching this move
	public void stop();
	//Gets the best move found by the manager
	public Move getBestMove();
	//Tells the manager the game is over
	public void gameOver();

}
