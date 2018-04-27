package mic.competition;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import mic.AlphaBetaWorker;
import mic.Worker;

public class FocusWorker extends AlphaBetaWorker {

	@Override
	protected int heuristic(Role role, MachineState state, StateMachine machine) throws GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException {
		if (findTerminalp(state, machine)) {
			int r = findReward(role, state, machine);
			if (r == 100) {
				return 1000;
			} else {
				return r-1;
			}
		} else {
			int focus = 0;
			for (Role o : machine.getRoles()) {
				if (o == role) {
					focus += 100 * findLegals(o, state, machine).size() / findActions(o, machine).size();
				} else {
					focus -= 100 * findLegals(o, state, machine).size() / findActions(o, machine).size();
				}
			}
			return focus;
		}
	}

	@Override
	public Worker clone() {
		return new FocusWorker();
	}

}
