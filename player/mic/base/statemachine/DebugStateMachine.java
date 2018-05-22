package mic.base.statemachine;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class DebugStateMachine extends StateMachine {
	private final StateMachine n;
	private final StateMachine g;

	public DebugStateMachine(StateMachine bugStateMachine, StateMachine goldStateMachine) {
		n = bugStateMachine;
		g = goldStateMachine;
	}

	@Override
	public List<Move> findActions(Role role) throws MoveDefinitionException {
		List<Move> gold = g.findActions(role);
		List<Move> other = n.findActions(role);

		assert gold.equals(other);
		return gold;
	}

	@Override
	public void initialize(List<Gdl> description) {
		n.initialize(description);
		g.initialize(description);
	}

	@Override
	public int getGoal(MachineState state, Role role) throws GoalDefinitionException {
		int gold = g.getGoal(state, role);
		int other = n.getGoal(state, role);
		assert gold == other;
		return gold;
	}

	@Override
	public boolean isTerminal(MachineState state) {
		boolean gold = g.isTerminal(state);
		boolean other = n.isTerminal(state);
		assert gold == other;
		return gold;
	}

	@Override
	public List<Role> getRoles() {
		List<Role> gold = g.getRoles();
		List<Role> other = n.getRoles();

		assert gold.equals(other);
		return gold;
	}

	@Override
	public MachineState getInitialState() {
		MachineState gold = g.getInitialState();
		MachineState other = n.getInitialState();

		if (!gold.equals(other)) {
			System.out.println(gold);
			System.out.println(other);
			assert false;
		}
		return gold;
	}

	@Override
	public List<Move> getLegalMoves(MachineState state, Role role) throws MoveDefinitionException {
		List<Move> gl = g.getLegalMoves(state, role);
		Set<Move> gold = new HashSet<Move>(gl);
		Set<Move> other = new HashSet<Move>(n.getLegalMoves(state, role));

		if (!gold.equals(other)) {
			System.out.println(gold);
			System.out.println(other);
			assert false;
		}
		return gl;
	}

	@Override
	public MachineState getNextState(MachineState state, List<Move> moves) throws TransitionDefinitionException {
		MachineState gold = g.getNextState(state, moves);
		MachineState other = n.getNextState(state, moves);

		if (!gold.equals(other)) {
			System.out.println(gold);
			System.out.println(other);
			assert false;
		}
		return gold;
	}

}
