
import java.util.List;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;


public class Node {
	public Node(Role role, MachineState state, Move move, Node parent, String type) {
		this.role = role;
		this.state = state;
		this.utility = 0.0;
		this.visits = 0;
		this.type = type;
		this.move = move;
	}

	Role role;
	MachineState state;
	Move move;
	double utility;
	int visits;
	Node parent;
	List<Node> children;
	String type; //two types: max (for our player) and min (for opponent)
}
