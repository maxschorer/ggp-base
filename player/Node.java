
import java.util.ArrayList;
import java.util.List;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;


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
