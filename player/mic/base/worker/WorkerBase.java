package mic.base.worker;

import java.util.List;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import mic.base.GameProperties;

abstract public class WorkerBase implements Worker {
	protected StateMachine machine;
	protected MachineState state;
	protected Role role;
	protected GameProperties properties;
	protected List<Move> legals;

	protected volatile Boolean halt;
	protected volatile Boolean stop;

	abstract protected void search() throws GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException;

	@Override
	abstract public int eval(Move move);
	@Override
	abstract public Move getBest();


	public WorkerBase() {
		halt = false;
		stop = true;
	}
	@Override
	public void stop() {
		stop = true;
	}
	@Override
	public void halt() {
		halt = true;
		stop();
	}

	@Override
	public void initialize(StateMachine machineIn, MachineState stateIn, Role roleIn, GameProperties propertiesIn)
			throws MoveDefinitionException {
		machine = machineIn;
		state = stateIn;
		role = roleIn;
		properties = propertiesIn;
		legals = machine.getLegalMoves(state, role);
		stop = false;
	}
	
	protected void startup() {}

	/*
	 * Run should more or less be equivalent to computeBestMove
	 * except instead of returning the best move it should store
	 * it.
	 */
	@Override
	public void run() {
		try {
			while (!halt) {
				while (stop && !halt) {
					Thread.yield();
				}
				search();
			}
		} catch (GoalDefinitionException | MoveDefinitionException | TransitionDefinitionException e) {
			// Something went wrong
			e.printStackTrace();
		}
	}



	public MachineState findNext(List<Move> actions, MachineState s, StateMachine m)
			throws TransitionDefinitionException {
		return m.getNextState(s, actions);
	}

	public List<Move> findLegals(Role r, MachineState s, StateMachine m)
			throws MoveDefinitionException {
		return m.getLegalMoves(s, r);
	}

	public List<List<Move>> findLegalJoints(Role r, Move action, MachineState s, StateMachine m)
			throws MoveDefinitionException {
		return m.getLegalJointMoves(s, r, action);
	}

	public List<Move> findActions(Role r, StateMachine m)
			throws MoveDefinitionException {
		return m.findActions(r);
	}

	public boolean findTerminalp(MachineState s, StateMachine m) {
		return m.findTerminalp(s);
	}

	public int findReward(Role r, MachineState s, StateMachine m)
			throws GoalDefinitionException {
		return m.getGoal(s, r);
	}

	public List<Role> findOpponents(Role r, StateMachine m) {
		List<Role> roles = m.getRoles();
		roles.remove(r);
		return roles;
	}


}
