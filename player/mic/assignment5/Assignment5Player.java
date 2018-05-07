package mic.assignment5;

import java.util.List;
import java.util.Vector;

import org.ggp.base.apps.player.Player;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.cache.CachedStateMachine;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

import mic.base.manager.Manager;
import mic.base.manager.TopChoiceManager;
import mic.base.player.ThreadPlayer;
import mic.base.statemachine.RewriteStateMachine;
import mic.base.statemachine.gdlrewriter.GdlRewriter;
import mic.base.statemachine.gdlrewriter.RedundantSubgoalPruner;
import mic.base.worker.MCTSWorker;
import mic.base.worker.Worker;

public class Assignment5Player extends ThreadPlayer {


	@Override
	protected Manager getInitialManager() {
		List<Worker> workers = new Vector<Worker>();
		workers.add(new MCTSWorker(4));
		return new TopChoiceManager(workers);
	}

	@Override
	public String getName() {
		return "Assignment5Player";
	}

	public static void main(String[] args) {
		Player.initialize(new Assignment5Player().getName());
	}

	@Override
	public StateMachine getInitialStateMachine() {
		List<GdlRewriter> rewrites = new Vector<GdlRewriter>();
		rewrites.add(new RedundantSubgoalPruner());
		return new RewriteStateMachine(
				new CachedStateMachine(new ProverStateMachine()),
				rewrites);
	}

}
