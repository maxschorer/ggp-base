package mic.base.worker;

import java.util.ArrayList;
import java.util.List;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import mic.base.heuristic.Heuristic;
import mic.base.heuristic.MonteCarloHeuristic;

public class MCTSWorker extends WorkerBase {
	private final Heuristic heuristic;

	public MCTSWorker(int count) {
		heuristic = new MonteCarloHeuristic(count);
	}

	private Node root = null;

	public class Node {
		public Node(Node parent, Role role, MachineState state, Move move) {
			this.parent = parent;
			this.role = role;
			this.state = state;
			this.utility = 0.0;
			this.visits = 0;
			this.move = move;
			children = new ArrayList<Node>();
		}

		Role role;
		MachineState state;
		Move move;
		double utility;
		int visits;
		Node parent;
		List<Node> children;
	}

	@Override
	protected void search() throws GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException {
		//Create or update root
		if (root == null) {
			//First search root is null
			root = new Node(null, role, state, null);
		} else {
			//Move root should be the grandchild whose state is the current state
			boolean found = false;
			childloop: for (Node child : root.children) {
				for (Node grandchild : child.children) {
					if (grandchild.state.equals(state)) {
						root = grandchild;
						root.parent = null;
						found = true;
						break childloop;
					}
				}
			}
			if (!found) {
				//Something weird happened
				root = new Node(null, role, state, null);
			}
		}
		init = false;
		while (!stop && !init) {
			Node node = select(root);
			expand(node);
			int score = simulate(node);
			backpropagate(node, score);
		}
		System.out.println("Preformed: " + ((MonteCarloHeuristic) heuristic).counter + " depthcharges");
		((MonteCarloHeuristic) heuristic).counter=0;
		if (halt) {
			root = null;
		}
	}

	private Node select(Node node) {
		if (node.children.isEmpty()) {
			return node;
		}

		for (Node child : node.children)  {
			for (Node grandchild : child.children) {
				if (grandchild.visits == 0) {
					return grandchild;
				}
			}
		}

		Node branch = null;
		Node next = null;

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
		if (findTerminalp(node.state, machine)) {
			return;
		}
		for (Move move : findLegals(node.role, node.state, machine)) {
			// Node(Role role, MachineState state, Move move, Node parent, String type)
			Node child = new Node(node, node.role, node.state, move);
			node.children.add(child);
			for (List<Move> moveWithResponses : findLegalJoints(node.role, move, node.state, machine)) {
				MachineState newState = findNext(moveWithResponses, node.state, machine);
				Node grandChild = new Node(child, node.role, newState, null);
				child.children.add(grandChild);
			}
		}
	}

	private int simulate(Node node)
			throws GoalDefinitionException, TransitionDefinitionException, MoveDefinitionException {
		return heuristic.eval(node.role, node.state, machine).toInt();
	}

	private void backpropagate(Node node, int score) {
		node.visits += 1;
		node.utility += score;
		if (node.parent != null) {
			backpropagate(node.parent, score);
		}
	}


	@Override
	public int eval(Move move) {
		if (!stop) {
			return 0;
		} else {
			for (Node node : root.children) {
				if (node.move.equals(move)) {
					return (int) (node.utility / node.visits);
				}
			}
			return 0;
		}
	}

	@Override
	public Move getBest() {
		if (!stop) {
			return legals.get(0);
		} else {
			Move bestMove = legals.get(0);
			double bestScore = Double.NEGATIVE_INFINITY;
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
}
