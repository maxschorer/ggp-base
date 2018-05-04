package mic.base.worker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import mic.base.GameProperties;
import mic.base.heuristic.Heuristic;
import mic.base.heuristic.Value;

public class AlphaBetaWorker extends WorkerBase {

	private Map<Move, Value> scoreMap;
	private final Heuristic heuristic;

	public AlphaBetaWorker(Heuristic h) {
		heuristic = h;
	}

	@Override
	protected void search() throws GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException {
		int depth = 0;
		while (!stop) {
			for (Move move : legals) {
				Value result = computeMinScore(role, state, machine, move, heuristic.min(), heuristic.max(), depth);
				if (!stop) {
					scoreMap.put(move, result);
				} else {
					break;
				}
			}
			++depth;
		}
	}

	private Value computeMinScore(Role role, MachineState state, StateMachine machine, Move move, Value alpha,
			Value beta, int depth)
			throws GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException {
		/* Iterate through all legal moves from current state and recur */
		List<List<Move>> legalJoints = findLegalJoints(role, move, state, machine);

		for (int i = 0; i < legalJoints.size() && !stop; i++) {
			List<Move> moves = legalJoints.get(i);
			MachineState newState = findNext(moves, state, machine);
			Value result = computeMaxScore(role, newState, machine, alpha, beta, depth);
			if (result.compareTo(beta) < 0) {
				beta = result;
			}
			if (beta.compareTo(alpha) <= 0) {
				return alpha;
			}
		}
		return beta;
	}

	private Value computeMaxScore(Role role, MachineState state, StateMachine machine, Value alpha,
			Value beta, int depth)
			throws GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException {
		if (findTerminalp(state, machine) || depth == 0) {
			return heuristic.eval(role, state, machine);
		}

		/* Iterate through all legal moves from current state and recur */
		List<Move> legalMoves = findLegals(role, state, machine); // All legal moves from current state
		for (int i = 0; i < legalMoves.size() && !stop; i++) {
			Value result = computeMinScore(role, state, machine, legalMoves.get(i), alpha, beta, depth - 1);
			if (result.compareTo(alpha) > 0) {
				alpha = result;
			}
			if (alpha.compareTo(beta) >= 0) {
				return beta;
			}
		}
		return alpha;
	}
	@Override
	public void initialize(StateMachine machine, MachineState state, Role role, GameProperties properties)
			throws MoveDefinitionException {

		scoreMap = new HashMap<Move, Value>();
		for (Move m : machine.getLegalMoves(state, role)) {
			scoreMap.put(m, heuristic.min());
		}
		super.initialize(machine, state, role, properties);
	}
	@Override
	public int eval(Move move) {
		if (!stop) {
			return 0;
		} else {
			return scoreMap.get(move).toInt();
		}
	}

	@Override
	public Move getBest() {
		if (!stop) {
			return legals.get(0);
		} else {
			Move bestMove = legals.get(0);
			Value bestScore = heuristic.min();
			for (Entry<Move, Value> e : scoreMap.entrySet()) {
				if (e.getValue().compareTo(bestScore) > 0) {
					bestMove = e.getKey();
					bestScore = e.getValue();
				}
			}
			return bestMove;
		}
	}
}
