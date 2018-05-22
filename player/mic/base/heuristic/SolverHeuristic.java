package mic.base.heuristic;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;

public class SolverHeuristic implements Heuristic {

	@Override
	public TwoStateValue eval(Role role, MachineState state, StateMachine machine) throws GoalDefinitionException, MoveDefinitionException {
		return new TwoStateValue(machine.getGoal(state, role), machine.findTerminalp(state));
	}


	@Override
	public TwoStateValue min() {
		return new TwoStateValue(0, true);

	}

	@Override
	public TwoStateValue max() {
		return new TwoStateValue(100, true);
	}



}
