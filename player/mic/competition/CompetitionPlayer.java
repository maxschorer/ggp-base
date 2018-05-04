package mic.competition;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import org.ggp.base.apps.player.Player;

import mic.base.heuristic.MonteCarloHeuristic;
import mic.base.manager.EvalManager;
import mic.base.manager.Manager;
import mic.base.player.ThreadPlayer;
import mic.base.worker.MCTSWorker;
import mic.base.worker.Worker;

public class CompetitionPlayer extends ThreadPlayer {

	@Override
	protected Manager getInitialManager() {
		List<Worker> workers = new Vector<Worker>();
		workers.add(new MCTSWorker(new MonteCarloHeuristic(1, ThreadLocalRandom.current())));
		workers.add(new MCTSWorker(new MonteCarloHeuristic(2, ThreadLocalRandom.current())));
		workers.add(new MCTSWorker(new MonteCarloHeuristic(3, ThreadLocalRandom.current())));
		workers.add(new MCTSWorker(new MonteCarloHeuristic(4, ThreadLocalRandom.current())));
		return new EvalManager(workers);
	}

	@Override
	public String getName() {
		return "CompetitionPlayer";
	}

	public static void main(String[] args) {
		Player.initialize(new CompetitionPlayer().getName());
	}

}
