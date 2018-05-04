package mic.base.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ggp.base.util.statemachine.Move;

import mic.base.worker.Worker;

public class TopChoiceManager extends ManagerBase {

	public TopChoiceManager(List<Worker> workers) {
		super(workers);
	}

	/*
	 * Looks through all the workers and counts the frequency of a move being chosen as best
	 */
	@Override
	protected  Move computeBestMove() {
		Map<Move, Integer> scores = new HashMap<Move, Integer>();
		for (Worker w : workers.keySet()) {
			scores.compute(w.getBest(), (k,v) -> v == null ? 1 : v + 1);
		}
		Move bestMove = legals.get(0);
		int bestScore = 0;
		for (Entry<Move, Integer> e : scores.entrySet()) {
			if (e.getValue() > bestScore) {
				bestScore = e.getValue();
				bestMove = e.getKey();
			}
		}
		System.out.println("TopChoiceManager eval: " + bestScore);
		return bestMove;
	}
}
