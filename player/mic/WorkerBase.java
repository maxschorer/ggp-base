package mic;

import java.util.List;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

abstract public class WorkerBase implements Worker {

	/*
	 *  these are the methods you need to implement
	 */
	abstract public void stop();
	abstract public void initialize(
				StateMachine machine,
				MachineState state,
				Role role, GameProperties properties);


	abstract public int eval(Move move);
	abstract public Move getBest();
	abstract public Worker clone();
	
	/*
	 * Run should more or less be equivalent to computeBestMove 
	 * except instead of returning the best move it should store
	 * it.
	 */
	abstract public void run();


	public MachineState findNext(List<Move> actions, MachineState s, StateMachine m)
			throws TransitionDefinitionException {
		return m.getNextState(s, actions);
	}

	public List<Move> findLegals(Role r, MachineState s, StateMachine m)
			throws MoveDefinitionException {
		return m.getLegalMoves(s, r);
	}

	public List<List<Move>> findLegalJoints(Role r, Move action, MachineState s, StateMachine m)
			throws MoveDefinitionException {
		return m.getLegalJointMoves(s, r, action);
	}

	public List<Move> findActions(Role r, StateMachine m)
			throws MoveDefinitionException {
		return m.findActions(r);
	}

	public boolean findTerminalp(MachineState s, StateMachine m) {
		return m.findTerminalp(s);
	}

	public int findReward(Role r, MachineState s, StateMachine m)
			throws GoalDefinitionException {
		return m.getGoal(s, r);
	}

	public List<Role> findOpponents(Role r, StateMachine m) {
		List<Role> roles = m.getRoles();
		roles.remove(r);
		return roles;
	}


}
