package mic.base.statemachine;

import java.util.List;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import mic.base.statemachine.gdlrewriter.GdlRewriter;

public class RewriteStateMachine extends StateMachine {
    private final StateMachine backingStateMachine;
    private final List<GdlRewriter> rewriters;

    public RewriteStateMachine(StateMachine backingStateMachine, List<GdlRewriter> rewriters) {
    	this.backingStateMachine = backingStateMachine;
    	this.rewriters = rewriters;
    }

	@Override
	public List<Move> findActions(Role role) throws MoveDefinitionException {
		// TODO Auto-generated method stub
		return backingStateMachine.findActions(role);
	}

	@Override
	public void initialize(List<Gdl> description) {
		for (GdlRewriter rewriter: rewriters) {
			description = rewriter.rewrite(description);
		}
		backingStateMachine.initialize(description);
	}

	@Override
	public int getGoal(MachineState state, Role role) throws GoalDefinitionException {
		return backingStateMachine.getGoal(state, role);
	}

	@Override
	public boolean isTerminal(MachineState state) {
		return backingStateMachine.isTerminal(state);
	}

	@Override
	public List<Role> getRoles() {
		return backingStateMachine.getRoles();
	}

	@Override
	public MachineState getInitialState() {
		return backingStateMachine.getInitialState();
	}

	@Override
	public List<Move> getLegalMoves(MachineState state, Role role) throws MoveDefinitionException {
		return backingStateMachine.getLegalMoves(state, role);
	}

	@Override
	public MachineState getNextState(MachineState state, List<Move> moves) throws TransitionDefinitionException {
		return backingStateMachine.getNextState(state, moves);
	}



}
