package mic.competition;

import java.util.List;

import org.ggp.base.apps.player.Player;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.cache.CachedStateMachine;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

import com.google.common.collect.ImmutableList;

import mic.base.manager.EvalManager;
import mic.base.manager.Manager;
import mic.base.player.ThreadPlayer;
import mic.base.worker.MCTSWorker;
import mic.base.worker.Worker;

public class CompetitionPlayer extends ThreadPlayer {

	@Override
	protected Manager getInitialManager() {
		List<Worker> workers = ImmutableList.of(
				new MCTSWorker(1),
				new MCTSWorker(2),
				new MCTSWorker(3),
				new MCTSWorker(4)
			);

		return new EvalManager(workers);
	}

	@Override
	public String getName() {
		return "CompetitionPlayer";
	}

	public static void main(String[] args) {
		Player.initialize(new CompetitionPlayer().getName());
	}

	@Override
	public StateMachine getInitialStateMachine() {
		return new CachedStateMachine(new ProverStateMachine());
	}
}
