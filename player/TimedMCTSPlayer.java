
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

//import Node;

public class TimedMCTSPlayer extends GGPlayer {

	/**
	 * All we have to do here is call the Player's initialize method with our name
	 * as the argument so that the Player GUI knows which name to have selected at
	 * startup. This is all you need in the main method of your own player.
	 */
	public static void main(String[] args) {
		Player.initialize(new TimedMCTSPlayer().getName());
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
		private volatile Node root;

		public WorkerPlayer(StateMachine machine, Role role, MachineState state)
				throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
			m_halt = false;
			m_machine = machine;
			m_role = role;
			m_state = state;

			m_legalMoves = findLegals(role, state, machine); // All legal moves from current state
			m_bestMove = m_legalMoves.get(0);
			m_explored = 0;
		}

		public void stop() {
			m_halt = true;
		}


		public Move getBest() {
			//force data race
			if (!m_halt) {
				return m_bestMove;
			} else {
				Move bestMove = m_bestMove;
				double bestScore = -1;
				for (Node node : root.children) {
					double score = node.utility / node.visits;
					if (score > bestScore) {
						bestScore = score;
						bestMove = node.move;
					}
				}
				return bestMove;
			}
		}

		public int numExplored() {
			if (!m_halt) {
				return m_explored;
			} else {
				return root.visits;
			}
		}

		@Override
		public void run() {
			// int depth = 0;
			root = new Node(null, m_role, m_state, null);
			try {

				while (!m_halt) {
					Node node = select(root);
					expand(node);
					int score = simulate(node);
					backpropagate(node, score);
				}

			} catch (GoalDefinitionException | MoveDefinitionException | TransitionDefinitionException e) {

				e.printStackTrace();
			}
		}

		/*
		 * Select
		 * If our node has 0 visits, then return the node.  If the node has an empty
		 * grandchild, i.e. a grandchild with 0 visits, return the grandchild.
		 * Otherwise, recursively call select on the selectedGrandChild.
		 */

		/*
		 * Use the selectFn to identify the child with the highest score.
		 * If the current node is a min node, that means the selected child
		 * is a max node, and we return that node.  Otherwise, recursively
		 * call grandChild.
		 */
		private Node select(Node node) {
			if (node.children.isEmpty()) {
				return node;
			}
			Node branch = null;
			Node next = null;

			for (Node child : node.children)  {
				for (Node grandchild : child.children) {
					if (grandchild.visits == 0) {
						return grandchild;
					}
				}
			}

			double bestScore = Double.NEGATIVE_INFINITY;
			for (Node child: node.children) {
				double score = selectFnMax(child);
				if (score > bestScore) {
					bestScore = score;
					branch = child;
				}
			}

			bestScore = Double.NEGATIVE_INFINITY;
			for (Node grandchild: branch.children) {
				double score = selectFnMin(grandchild);
				if (score > bestScore) {
					bestScore = score;
					next = grandchild;
				}
			}
			return select(next);

		}


		private double selectFnMax(Node node) {
			return node.utility / node.visits + Math.sqrt(2 * Math.log(node.parent.visits) / node.visits);
		}
		private double selectFnMin(Node node) {
			return -1* node.utility / node.visits + Math.sqrt(2 * Math.log(node.parent.visits) / node.visits);
		}

		/*
		 * Expand
		 * Creates children (min nodes) as well as grandchildren nodes.
		 */
		private void expand(Node node) throws MoveDefinitionException, TransitionDefinitionException {
			if (findTerminalp(node.state, m_machine)) {
				return;
			}
			for (Move move : findLegals(node.role, node.state, m_machine)) {
				// Node(Role role, MachineState state, Move move, Node parent, String type)
				Node child = new Node(node, node.role, node.state, move);
				node.children.add(child);
				for (List<Move> moveWithResponses : findLegalJoints(node.role, move, node.state, m_machine)) {
					MachineState newState = findNext(moveWithResponses, node.state, m_machine);
					Node grandChild = new Node(child, node.role, newState, null);
					child.children.add(grandChild);
				}
			}
		}

		/*
		 * Simulate
		 */

		private int simulate(Node node)
				throws GoalDefinitionException, TransitionDefinitionException, MoveDefinitionException {
			return montecarlo(node.role, node.state, m_machine, 4);
		}

		/*
		 * Backpropagate
		 */
		private void backpropagate(Node node, int score) {
			node.visits += 1;
			node.utility += score;
			if (node.parent != null) {
				backpropagate(node.parent, score);
			}
		}
	}

	private int montecarlo(Role role, MachineState state, StateMachine machine, int count)
			throws GoalDefinitionException, TransitionDefinitionException, MoveDefinitionException {
		int total = 0;
		for (int i = 0; i < count; i++) {
			total += depthcharge(role, state, machine);
		}
		return total / count;
	}

	private int depthcharge(Role role, MachineState state, StateMachine machine)
			throws GoalDefinitionException, TransitionDefinitionException, MoveDefinitionException {
		if (findTerminalp(state, machine)) {
			return findReward(role, state, machine);
		} // Return current state's reward if in terminal state

		/* Choose random legal move from current state and recur */
		List<Move> legalMoves = findLegals(role, state, machine); // All legal moves from current state
		Random rand = new Random();
		int randIdx = rand.nextInt(legalMoves.size()); // Random legal move
		List<List<Move>> legalJoints = findLegalJoints(role, legalMoves.get(randIdx), state, machine);
		int randIdx2 = rand.nextInt(legalJoints.size());
		MachineState newState = findNext(legalJoints.get(randIdx2), state, machine); // Random legal joint move
		return depthcharge(role, newState, machine);
	}

	/**
	 * Currently, we can get along just fine by using the Prover State Machine. We
	 * will implement a more optimized PropNet State Machine later. The Cached State
	 * Machine is a wrapper that reduces the number of calls to the Prover State
	 * Machine by returning results of method calls that have been made previously.
	 * (e.g. getNextState calls or getLegalMoves for the same combination of
	 * parameters)
	 */
	@Override
	public StateMachine getInitialStateMachine() {
		return new CachedStateMachine(new ProverStateMachine());
	}

	/**
	 * If we wanted to use the metagame (or start) clock to compute something about
	 * the game (or explore the game tree), we could do so here. Since this is just
	 * a legal player, there is no need for such computation.
	 */
	@Override
	public void start(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {

	}

	/**
	 * Where your player selects the move they want to play. In-line comments
	 * explain each line of code. Your goal essentially boils down to returning the
	 * best move possible.
	 *
	 * The current state for the player is updated between moves automatically for
	 * you.
	 *
	 * The value of the timeout variable is the UNIX time by which you need to
	 * submit your move. You can determine how much time your player has left (in
	 * milliseconds) by using the following line of code: long timeLeft = timeout -
	 * System.currentTimeMillis();
	 *
	 * Make sure to submit your move before this time runs out. It's also a good
	 * idea to leave a couple seconds (2-4) as buffer for network lag/spikes and so
	 * that you don't overrun your time thus timing out (which plays a random move
	 * for you and counts as an error -- two very bad things).
	 */
	@Override
	public Move play(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		// Gets our state machine (the same one as returned in getInitialStateMachine)
		// This State Machine simulates the game we are currently playing.
		StateMachine machine = getStateMachine();

		// Gets the current state we're in (e.g. move 2 of a game of tic tac toe where X
		// just played in the center)
		MachineState state = getCurrentState();

		// Gets our role (e.g. X or O in a game of tic tac toe)
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
		// Stop the slave
		w.stop();
		/*
		 * Let the thread run until 1 seconds are left to halt (shouldn't take that
		 * long)
		 */
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

		// Logging what decisions your player is making as well as other statistics
		// is a great way to debug your player and benchmark it against other players.
		System.out.println("I am playing: " + chosenMove + " after exploring " + explored + " nodes");
		return chosenMove;
	}

	/**
	 * Can be used for cleanup at the end of a game, if it is needed.
	 */
	@Override
	public void stop() {
		for (Thread t : threadList) {
			try {
				t.join();
			} catch (InterruptedException e) {
				// Something went wrong
				e.printStackTrace();
			}
		}

	}

	/**
	 * Can be used for cleanup in the event a game is aborted while still in
	 * progress, if it is needed.
	 */
	@Override
	public void abort() {
		for (Thread t : threadList) {
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
		return "TimedMCTSPlayer";
	}

}
