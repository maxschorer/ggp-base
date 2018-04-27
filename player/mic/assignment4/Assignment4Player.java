package mic.assignment4;

import java.util.List;
import java.util.Vector;

import org.ggp.base.apps.player.Player;

import mic.Manager;
import mic.ThreadPlayer;
import mic.TopChoiceManager;
import mic.Worker;
import mic.competition.CompetitionPlayer;

public class Assignment4Player extends ThreadPlayer {

	@Override
	protected Manager getInitialManager() {
		List<Worker> workers = new Vector<Worker>();
		//workers.add(new Assignment4_1Worker());
		//workers.add(new Assignment4_2Worker());
		return new TopChoiceManager(workers);
	}

	@Override
	public String getName() {
		return "Assignment4Player";
	}

	public static void main(String[] args) {
		Player.initialize(new CompetitionPlayer().getName());
	}

}
