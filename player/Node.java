
import java.util.List;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;


public class Node {
	public Node(Role role, StateMachine machine, MachineState state, Node parent) {
		this.role = role;
		this.machine = machine;
		this.state = state;
		this.utility = 0.0;
		this.visits = 0;
	}

	Role role;
	StateMachine machine;
	MachineState state;
	double utility;
	int visits;
	Node parent;
	List<Node> children;
}
