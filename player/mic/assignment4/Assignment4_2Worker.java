package mic.assignment4;

import java.util.concurrent.ThreadLocalRandom;

import mic.base.heuristic.MonteCarloHeuristic;
import mic.base.worker.MCTSWorker;

public class Assignment4_2Worker extends MCTSWorker {

	public Assignment4_2Worker() {
		super(new MonteCarloHeuristic(4, ThreadLocalRandom.current()));
	}

}
