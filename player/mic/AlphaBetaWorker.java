package mic;

import java.util.List;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

abstract public class AlphaBetaWorker extends WorkerBase  {

	abstract protected int heuristic(Role role, MachineState state, StateMachine machine) throws GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException;

	private StateMachine machine;
	private MachineState state;
	private Role role;
	private GameProperties properties;

	private volatile boolean stop;
	private volatile Move bestMove;
	private volatile int bestScore;

	@Override
	public void run() {
		int depth = 0;
		List<Move> legals = null;
		try {
			legals = machine.getLegalMoves(state, role);
		} catch (MoveDefinitionException e1) {
			e1.printStackTrace();
		}
		bestMove = legals.get(0);
		bestScore = -50;
		while (!stop) {
			for (int i = 0; i <	legals.size() && !stop; i++) {
				Move nextMove = legals.get(i);
				int result;
				try {
					result = computeMinScore(role, state, machine, nextMove, 0, 100, depth);
				} catch (GoalDefinitionException | MoveDefinitionException | TransitionDefinitionException e) {
					//Something went wrong
					e.printStackTrace();
					continue;
				}
				if (result > bestScore) {
					bestScore = result;
					bestMove = nextMove;
				}
			}
			++depth;
		}
	}
	private int computeMinScore(Role role, MachineState state, StateMachine machine, Move move, int alpha, int beta, int depth)
			throws GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException {
		/* Iterate through all legal moves from current state and recur */
		List<List<Move>> legalJoints = findLegalJoints(role, move, state, machine);

		for (int i = 0; i < legalJoints.size() && !stop; i++) {
			List<Move> moves = legalJoints.get(i);
			MachineState newState = findNext(moves, state, machine);
			int result = computeMaxScore(role, newState, machine, alpha, beta, depth);
			if (result < beta) { beta = result; }
			if (beta <= alpha) { return alpha; }
		}
		return beta;
	}
	private int computeMaxScore(Role role, MachineState state, StateMachine machine, int alpha, int beta, int depth)
			throws GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException {
		if (findTerminalp(state, machine)) {
			return findReward(role, state, machine); // Return current state's reward if in terminal state
		} else if (depth == 0) {
			return heuristic(role, state, machine);  // Return current state's heuristic
		}

		/* Iterate through all legal moves from current state and recur */
		List<Move> legalMoves = findLegals(role, state, machine);		// All legal moves from current state
		for (int i = 0; i < legalMoves.size() && !stop; i++) {
			int result = computeMinScore(role, state, machine, legalMoves.get(i), alpha, beta, depth - 1);
			if (result > alpha) { alpha = result; }
			if (alpha >= beta) { return beta; }
		}
		return alpha;
	}

	@Override
	public void stop() {
		stop = true;
	}

	@Override
	public void initialize(StateMachine machineIn, MachineState stateIn, Role roleIn, GameProperties propertiesIn) {
		machine = machineIn;
		state = stateIn;
		role = roleIn;
		properties = propertiesIn;
	}

	@Override
	public int eval(Move move) {
		if (move == bestMove) {
			if (bestScore < 0) {
				return 0;
			} else if (bestScore > 100) {
				return 100;
			} else {
				return bestScore;
			}
		} else {
			return 0;
		}
	}

	@Override
	public Move getBest() {
		return bestMove;
	}

}
