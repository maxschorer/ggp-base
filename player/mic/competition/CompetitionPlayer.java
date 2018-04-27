package mic.competition;

import java.util.List;
import java.util.Vector;

import org.ggp.base.apps.player.Player;

import mic.Manager;
import mic.ThreadPlayer;
import mic.TopChoiceManager;
import mic.Worker;

public class CompetitionPlayer extends ThreadPlayer {

	@Override
	protected Manager getInitialManager() {
		List<Worker> workers = new Vector<Worker>();
		workers.add(new FocusWorker());
		return new TopChoiceManager(workers);
	}

	@Override
	public String getName() {
		return "CompetitionPlayer";
	}

	public static void main(String[] args) {
		Player.initialize(new CompetitionPlayer().getName());
	}

}
