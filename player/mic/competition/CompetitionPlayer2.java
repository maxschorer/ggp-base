package mic.competition;

import java.util.List;
import java.util.Vector;

import org.ggp.base.apps.player.Player;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.cache.CachedStateMachine;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

import mic.base.heuristic.MonteCarloHeuristic;
import mic.base.manager.EvalManager;
import mic.base.manager.Manager;
import mic.base.player.ThreadPlayer;
import mic.base.worker.AlphaBetaWorker;
import mic.base.worker.Worker;

public class CompetitionPlayer2 extends ThreadPlayer {

	@Override
	protected Manager getInitialManager() {
		List<Worker> workers = new Vector<Worker>();
		//workers.add(new AlphaBetaWorker(new FocusHeuristic()));


		workers.add(new AlphaBetaWorker(new MonteCarloHeuristic(1)));
		workers.add(new AlphaBetaWorker(new MonteCarloHeuristic(2)));
		workers.add(new AlphaBetaWorker(new MonteCarloHeuristic(3)));
		workers.add(new AlphaBetaWorker(new MonteCarloHeuristic(4)));
		return new EvalManager(workers);
	}

	@Override
	public String getName() {
		return "CompetitionPlayer2";
	}

	public static void main(String[] args) {
		Player.initialize(new CompetitionPlayer2().getName());
	}

	@Override
	public StateMachine getInitialStateMachine() {
		return new CachedStateMachine(new ProverStateMachine());
	}
}
