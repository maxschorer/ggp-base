import java.util.List;

import org.ggp.base.apps.player.Player;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.cache.CachedStateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;


public class MinimaxPlayer extends GGPlayer {

	/**
	 * All we have to do here is call the Player's initialize method with
	 * our name as the argument so that the Player GUI knows which name to
	 * have selected at startup. This is all you need in the main method
	 * of your own player.
	 */
	public static void main(String[] args) {
		Player.initialize(new MinimaxPlayer().getName());
	}

	/**
	 * Currently, we can get along just fine by using the Prover State Machine.
	 * We will implement a more optimized PropNet State Machine later. The Cached
	 * State Machine is a wrapper that reduces the number of calls to the Prover
	 * State Machine by returning results of method calls that have been made previously.
	 * (e.g. getNextState calls or getLegalMoves for the same combination of parameters)
	 */
	@Override
	public StateMachine getInitialStateMachine() {
		return new CachedStateMachine(new ProverStateMachine());
	}

	/**
	 * If we wanted to use the metagame (or start) clock to compute something
	 * about the game (or explore the game tree), we could do so here. Since
	 * this is just a legal player, there is no need for such computation.
	 */
	@Override
	public void start(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {

	}

	/**
	 * Where your player selects the move they want to play. In-line comments
	 * explain each line of code. Your goal essentially boils down to returning the best
	 * move possible.
	 *
	 * The current state for the player is updated between moves automatically for you.
	 *
	 * The value of the timeout variable is the UNIX time by which you need to submit your move.
	 * You can determine how much time your player has left (in milliseconds) by using the following line of code:
	 * long timeLeft = timeout - System.currentTimeMillis();
	 *
	 * Make sure to submit your move before this time runs out. It's also a good
	 * idea to leave a couple seconds (2-4) as buffer for network lag/spikes and
	 * so that you don't overrun your time thus timing out (which plays
	 * a random move for you and counts as an error -- two very bad things).
	 */
	@Override
	public Move play(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		//Gets our state machine (the same one as returned in getInitialStateMachine)
		//This State Machine simulates the game we are currently playing.
		StateMachine machine = getStateMachine();

		//Gets the current state we're in (e.g. move 2 of a game of tic tac toe where X just played in the center)
		MachineState state = getCurrentState();

		//Gets our role (e.g. X or O in a game of tic tac toe)
		Role role = getRole();

		//Returns the best move after searching full tree
		Move chosenMove = computeBestMove(machine, role, state);

		//Logging what decisions your player is making as well as other statistics
		//is a great way to debug your player and benchmark it against other players.
		System.out.println("I am playing: " + chosenMove);
		return chosenMove;
	}

	/*
	 * computeBestMove
	 * @Params:
	 * StateMachine, Role, MachineState
	 * @Returns:
	 * Move best move found
	 * Iterates through all legal moves in current state, recursively searching the entire tree of possible moves from those.
	 * Returns the move that results in the highest reward state
	 */
	private Move computeBestMove(StateMachine machine, Role role, MachineState state) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {
		List<Move> legalMoves = findLegals(role, state, machine);		// All legal moves from current state
		Move move = legalMoves.get(0);									// Keep track of best move found
		int score = 0;													// Keep track of best score found

		/* Iterate through all legal moves from current state, chose best */
		for (int i = 0; i < legalMoves.size(); i++) {
			Move nextMove = legalMoves.get(i);
			int result = computeMinScore(role, state, machine, nextMove);
			if (result == 100) return nextMove;
			if (result > score) {
				score = result;
				move = nextMove;
			}
		}
		return move;
	}

	/*
	 * computeMinScore
	 * @Params:
	 * Role, MachineState, StateMachine
	 * @Returns:
	 * int max score found
	 * Iterates through all legal moves in current state, recursively searching entire tree of possible moves from those.
	 * Returns the best score possible from the given possible moves
	 */
	private int computeMinScore(Role role, MachineState state, StateMachine machine, Move move) throws GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException {

		/* Iterate through all legal moves from current state and recur */
		List<List<Move>> legalJoints = findLegalJoints(role, move, state, machine);
		int score = 100;
		for (int i = 0; i < legalJoints.size(); i++) {
			List<Move> moves = legalJoints.get(i);
			MachineState newState = findNext(moves, state, machine);
			int result = computeMaxScore(role, newState, machine);
			if (result == 0) return 0;
			if (result < score) score = result;
		}
		return score;
	}


	/*
	 * computeMaxScore
	 * @Params:
	 * Role, MachineState, StateMachine
	 * @Returns:
	 * int max score found
	 * Iterates through all legal moves in current state, recursively searching entire tree of possible moves from those.
	 * Returns the best score possible from the given possible moves
	 */
	private int computeMaxScore(Role role, MachineState state, StateMachine machine) throws GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException {
		if (findTerminalp(state, machine)) return findReward(role, state, machine);			// Return current state's reward if in terminal state

		/* Iterate through all legal moves from current state and recur */
		List<Move> legalMoves = findLegals(role, state, machine);		// All legal moves from current state
		int score = 0;
		for (int i = 0; i < legalMoves.size(); i++) {
			Move move = legalMoves.get(i);
			int result = computeMinScore(role, state, machine, move);
			if (result > score) score = result;
		}
		return score;
	}

	/**
	 * Can be used for cleanup at the end of a game, if it is needed.
	 */
	@Override
	public void stop() {

	}

	/**
	 * Can be used for cleanup in the event a game is aborted while
	 * still in progress, if it is needed.
	 */
	@Override
	public void abort() {

	}

	/**
	 * Returns the name of the player.
	 */
	@Override
	public String getName() {
		return "minimax_player";
	}



}
