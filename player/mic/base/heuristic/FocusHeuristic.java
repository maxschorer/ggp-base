package mic.base.heuristic;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;

public class FocusHeuristic implements Heuristic {

	public class FocusValue implements Value {
		private final int R;
		private final int M;
		private final boolean T;

		public FocusValue(int R, int M, boolean T) {
			this.R = R;
			this.M = M;
			this.T = T;
		}

		@Override
		public int compareTo(Value other) {
			int score = toInt();
			int otherScore = other.toInt();

			if (other instanceof FocusValue) {
				boolean otherT = (((FocusValue) other).T);
				if (T && !otherT) {
					if (R == 100 || score > otherScore) {
						return 1;
					} else {
						return -1;
					}
				} else if (!T && otherT) {
					if (R != 100 && score >= otherScore) {
						return 1;
					} else {
						return -1;
					}
				}
			}
			return score - otherScore;
		}

		@Override
		public int toInt() {
			if (T) {
				return R;
			} else {
				return M;
			}
		}
	}

	@Override
	public Value eval(Role role, MachineState state, StateMachine machine)
			throws GoalDefinitionException, MoveDefinitionException {
		int mobility = 0;
		for (Role r : machine.getRoles()) {
			mobility += (r == role ? 1 : -1)
					* (int) (100.0 * machine.getLegalMoves(state, r).size() / machine.findActions(r).size());
		}
		return new FocusValue(machine.getGoal(state, role), mobility, machine.findTerminalp(state));

	}

	@Override
	public Value min() {
		return new FocusValue(0,0,true);
	}

	@Override
	public Value max() {
		return new FocusValue(0,0,false);
	}

}
