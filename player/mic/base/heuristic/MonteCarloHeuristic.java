package mic.base.heuristic;

import java.util.List;
import java.util.Random;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class MonteCarloHeuristic implements Heuristic {
	private int count;
	private final Random rand;

	public MonteCarloHeuristic(int count, Random rand) {
		this.count = count;
		this.rand = rand;
	}


	@Override
	public Value eval(Role role, MachineState state, StateMachine machine)
			throws GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException {
		int score = 0;
		// TODO Auto-generated method stub
		for (int i = 0; i < count; i++) {
			score += depthcharge(role, state, machine);
		}
		score = score / count;
		return new KAValue(score, machine.findTerminalp(state));
	}
	private int depthcharge(Role role, MachineState state, StateMachine machine) throws GoalDefinitionException, TransitionDefinitionException, MoveDefinitionException {
		if (machine.findTerminalp(state)) { return machine.getGoal(state, role); } // Return current state's reward if in terminal state

		/* Choose random legal move from current state and recur */
		List<Move> legalMoves = machine.getLegalMoves(state, role);		// All legal moves from current state
		int randIdx = rand.nextInt(legalMoves.size());					// Random legal move
		List<List<Move>> legalJoints = machine.getLegalJointMoves(state, role, legalMoves.get(randIdx));
		int randIdx2 = rand.nextInt(legalJoints.size());
		MachineState newState = machine.getNextState(state, legalJoints.get(randIdx2));		// Random legal joint move
		return depthcharge(role, newState, machine);
	}

	@Override
	public Value min() {
		return new KAValue(0, true);
	}

	@Override
	public Value max() {
		return new KAValue(100, true);
	}
}
