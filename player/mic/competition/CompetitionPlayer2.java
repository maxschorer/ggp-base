package mic.competition;

import java.util.List;

import org.ggp.base.apps.player.Player;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.cache.CachedStateMachine;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

import com.google.common.collect.ImmutableList;

import mic.base.manager.Manager;
import mic.base.manager.SolverManager;
import mic.base.player.ThreadPlayer;
import mic.base.worker.MCTSWorker;
import mic.base.worker.Worker;

public class CompetitionPlayer2 extends ThreadPlayer {

	@Override
	protected Manager getInitialManager() {
		List<Worker> workers = ImmutableList.of(
				new MCTSWorker(4)
			);

		return new SolverManager(workers);
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
