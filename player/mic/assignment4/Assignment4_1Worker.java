package mic.assignment4;

import java.util.concurrent.ThreadLocalRandom;

import mic.base.heuristic.MonteCarloHeuristic;
import mic.base.worker.AlphaBetaWorker;

public class Assignment4_1Worker extends AlphaBetaWorker {

	public Assignment4_1Worker() {
		super(new MonteCarloHeuristic(4, ThreadLocalRandom.current()));
	}

}
