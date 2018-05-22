package mic.base.heuristic;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;

public class KeepAliveHeuristic implements Heuristic {

	@Override
	public KAValue eval(Role role, MachineState state, StateMachine machine) throws GoalDefinitionException, MoveDefinitionException {
		return new KAValue(machine.getGoal(state, role), machine.findTerminalp(state));
	}


	@Override
	public KAValue min() {
		return new KAValue(0, true);

	}

	@Override
	public KAValue max() {
		return new KAValue(100, true);
	}



}
