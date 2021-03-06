
import java.util.ArrayList;
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


public class TimedMobilityPlayer extends GGPlayer {

	/**
	 * All we have to do here is call the Player's initialize method with
	 * our name as the argument so that the Player GUI knows which name to
	 * have selected at startup. This is all you need in the main method
	 * of your own player.
	 */
	public static void main(String[] args) {
		Player.initialize(new TimedMobilityPlayer().getName());
	}

	/*
	 * List of threads so join can be called at the end;
	 */
	private List<Thread> threadList = new ArrayList<Thread>();

	/**
	 * Worker does AlphaBeta search with increasing window size
	 *
	 */
	private class WorkerPlayer implements Runnable {
		private volatile boolean m_halt;
		private volatile Move m_bestMove;
		private volatile int m_explored;

		private int m_score;
		private StateMachine m_machine;
		private Role m_role;
		private MachineState m_state;
		private List<Move> m_legalMoves;

		public WorkerPlayer(StateMachine machine, Role role, MachineState state)
				throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
			m_halt = false;
			m_machine = machine;
			m_role = role;
			m_state = state;

			m_legalMoves = findLegals(role, state, machine);		// All legal moves from current state
			m_bestMove = m_legalMoves.get(0);
			m_score = 0;
			m_explored = 0;
		}

		private int heuristic(Role role, MachineState state, StateMachine machine) throws GoalDefinitionException, MoveDefinitionException {
			if (findTerminalp(state, machine)) {return findReward(role, state, machine);}
			return (int)(100.0 *findLegals(role, state, machine).size() / findActions(role, machine).size());
		}

		public void stop() {
			m_halt = true;
		}
		public Move getBest() {
			return m_bestMove;
		}

		public int numExplored() {
			return m_explored;
		}

		@Override
		public void run()  {
			int depth = 0;
			while (!m_halt) {
				for (int i = 0; i < m_legalMoves.size() && !m_halt; i++) {
					Move nextMove = m_legalMoves.get(i);
					int result;
					try {
						result = computeMinScore(m_role, m_state, m_machine, nextMove, 0, 100, depth);
					} catch (GoalDefinitionException | MoveDefinitionException | TransitionDefinitionException e) {
						//Something went wrong
						e.printStackTrace();
						continue;
					}
					if (result > m_score) {
						m_score = result;
						m_bestMove = nextMove;
					}
				}

				// increase the search depth
				++depth;
			}
		}

		/*
		 * computeMinScore
		 * @Params:
		 * Role, MachineState, StateMachine
		 * @Returns:
		 * int max score found
		 * Iterates through all legal moves in current state, recursively searching entire tree of possible moves from those, unless beta exceeds alpha.
		 * Returns the best score possible from the given possible moves
		 */
		private int computeMinScore(Role role, MachineState state, StateMachine machine, Move move, int alpha, int beta, int depth)
				throws GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException {
			++m_explored;
			/* Iterate through all legal moves from current state and recur */
			List<List<Move>> legalJoints = findLegalJoints(role, move, state, machine);

			for (int i = 0; i < legalJoints.size() && !m_halt; i++) {
				List<Move> moves = legalJoints.get(i);
				MachineState newState = findNext(moves, state, machine);
				int result = computeMaxScore(role, newState, machine, alpha, beta, depth);
				if (result < beta) { beta = result; }
				if (beta <= alpha) { return alpha; }
			}
			return beta;
		}


		/*
		 * computeMaxScore
		 * @Params:
		 * Role, MachineState, StateMachine
		 * @Returns:
		 * int max score found
		 * Iterates through all legal moves in current state, recursively searching entire tree of possible moves from those, unless alpha exceeds beta.
		 * Returns the best score possible from the given possible moves
		 */
		private int computeMaxScore(Role role, MachineState state, StateMachine machine, int alpha, int beta, int depth)
				throws GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException {
			++m_explored;
			if (findTerminalp(state, machine)) {
				return findReward(role, state, machine); // Return current state's reward if in terminal state
			} else if (depth == 0) {
				return heuristic(role, state, machine);  // Return current state's heuristic
			}

			/* Iterate through all legal moves from current state and recur */
			List<Move> legalMoves = findLegals(role, state, machine);		// All legal moves from current state
			for (int i = 0; i < legalMoves.size() && !m_halt; i++) {
				int result = computeMinScore(role, state, machine, legalMoves.get(i), alpha, beta, depth - 1);
				if (result > alpha) { alpha = result; }
				if (alpha >= beta) { return beta; }
			}
			return alpha;
		}
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

		WorkerPlayer w = new WorkerPlayer(machine, role, state);
		Thread t = new Thread(w);
		t.start();
		threadList.add(t);

		/* Let the thread run until 1.5 seconds are left to search */
		long tleft = timeout - System.currentTimeMillis() - 1500;
		if (tleft <= 0) {
			tleft = 1;
		}
		try {
			t.join(tleft);
		} catch (InterruptedException e) {

		}
		//Stop the slave
		w.stop();
		/* Let the thread run until 1 seconds are left to halt (shouldn't take that long) */
		tleft = timeout - System.currentTimeMillis() - 1000;
		if (tleft <= 0) {
			tleft = 1;
		}
		try {
			t.join(tleft);
		} catch (InterruptedException e) {

		}

		Move chosenMove = w.getBest();
		int explored = w.numExplored();

		//Logging what decisions your player is making as well as other statistics
		//is a great way to debug your player and benchmark it against other players.
		System.out.println("I am playing: " + chosenMove + " after exploring " + explored + " nodes");
		return chosenMove;
	}


	/**
	 * Can be used for cleanup at the end of a game, if it is needed.
	 */
	@Override
	public void stop() {
		for (Thread t: threadList) {
			try {
				t.join();
			} catch (InterruptedException e) {
				// Something went wrong
				e.printStackTrace();
			}
		}


	}

	/**
	 * Can be used for cleanup in the event a game is aborted while
	 * still in progress, if it is needed.
	 */
	@Override
	public void abort() {
		for (Thread t: threadList) {
			try {
				t.join();
			} catch (InterruptedException e) {
				// Something went wrong
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns the name of the player.
	 */
	@Override
	public String getName() {
		return "TimedMobility_player";
	}



}
