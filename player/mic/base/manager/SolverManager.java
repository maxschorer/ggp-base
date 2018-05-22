package mic.base.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ggp.base.util.statemachine.Move;

import mic.base.heuristic.SolverHeuristic;
import mic.base.worker.AlphaBetaWorker;
import mic.base.worker.Worker;

public class SolverManager extends ManagerBase {
	protected Worker solver;

	public SolverManager(List<Worker> workers) {
		super(workers);
		solver = new AlphaBetaWorker(new SolverHeuristic());
		Thread t = new Thread(solver);
		this.workers.put(solver, t);
	}

	/*
	 * Looks through all the workers and counts the frequency of a move being chosen as best
	 */
	@Override
	protected  Move computeBestMove() {
		Map<Move, Integer> scores = new HashMap<Move, Integer>();
		Map<Move, Integer> solverScore = new HashMap<Move, Integer>();
		int nWorkers = workers.size() - 1;
		for (Move m: legals) {
			int score = 0;
			for (Worker w : workers.keySet()) {
				if (w.equals(solver)) {
					solverScore.put(m, w.eval(m));
				}
				score += w.eval(m);
			}

			scores.put(m, score/nWorkers);
		}

		Move bestMove = legals.get(0);
		int bestScore = 0;
		boolean solved = false;
		for (Entry<Move, Integer> e : scores.entrySet()) {
			Integer sScore = solverScore.get(e.getKey());
			if (sScore == -1) {
				if (e.getValue() > bestScore) {
					bestScore = e.getValue();
					bestMove = e.getKey();
					solved = false;
				}
			} else {
				if (sScore > bestScore) {
					bestScore = sScore;
					bestMove = e.getKey();
					solved = true;
				}
			}
		}
		if (solved) {
			System.out.println("EvalManager solved: " + bestScore);
		} else {
			System.out.println("EvalManager eval: " + bestScore);
		}
		return bestMove;
	}
}
