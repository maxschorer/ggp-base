package mic;

import java.util.List;
import java.util.Map;

import org.ggp.base.util.statemachine.Move;

public class TopChoiceManager extends ManagerBase {
	public TopChoiceManager(List<Worker> workers) {
		super(workers);
	}

	/*
	 * Looks through all the workers and counts the frequency of a move being chosen as best
	 */
	@Override
	protected  void computeScores(List<Worker> workers, Map<Move, Integer> scores, List<Move> legals) {
		scores.clear();
		for (Move m : legals) {
			scores.put(m, 0);
		}
		for (Worker w : workers ) {
			scores.compute(w.getBest(), (k,v) -> v + 1);
		}
	}
}
