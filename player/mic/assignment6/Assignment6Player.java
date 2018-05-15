package mic.assignment6;

import java.util.List;
import java.util.Vector;

import org.ggp.base.apps.player.Player;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.implementation.propnet.SamplePropNetStateMachine;

import mic.base.manager.Manager;
import mic.base.manager.TopChoiceManager;
import mic.base.player.ThreadPlayer;
import mic.base.worker.MCTSWorker;
import mic.base.worker.Worker;

public class Assignment6Player extends ThreadPlayer {


	@Override
	protected Manager getInitialManager() {
		List<Worker> workers = new Vector<Worker>();
		workers.add(new MCTSWorker(4));
		return new TopChoiceManager(workers);
	}

	@Override
	public String getName() {
		return "Assignment6Player";
	}

	public static void main(String[] args) {
		Player.initialize(new Assignment6Player().getName());
	}

	@Override
	public StateMachine getInitialStateMachine() {
		SamplePropNetStateMachine propNet = new SamplePropNetStateMachine();
		System.out.println("propNet size = " + propNet.getOrdering().size());
		return propNet;
	}

}
