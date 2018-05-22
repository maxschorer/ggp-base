package mic.assignment7;

import java.util.List;
import java.util.Vector;

import org.ggp.base.apps.player.Player;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.cache.CachedStateMachine;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

import mic.base.manager.EvalManager;
import mic.base.manager.Manager;
import mic.base.player.ThreadPlayer;
import mic.base.statemachine.DebugStateMachine;
import mic.base.statemachine.PropNetStateMachine;
import mic.base.worker.MCTSWorker;
import mic.base.worker.Worker;

public class Assignment7Player extends ThreadPlayer {


	@Override
	protected Manager getInitialManager() {
		List<Worker> workers = new Vector<Worker>();
		workers.add(new MCTSWorker(4));
		return new EvalManager(workers);
	}

	@Override
	public String getName() {
		return "Assignment7Player";
	}

	public static void main(String[] args) {
		Player.initialize(new Assignment7Player().getName());
	}

	@Override
	public StateMachine getInitialStateMachine() {
		return new DebugStateMachine(
				new CachedStateMachine(new PropNetStateMachine()),
				new CachedStateMachine(new  ProverStateMachine())
			);
	}

}
