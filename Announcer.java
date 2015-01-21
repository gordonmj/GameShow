import java.util.Random;
import java.util.Vector;

public class Announcer implements Runnable {

	private Game g = null;
	private Group gr = null;
	private String name = "Announcer";
	private int naptime = 1000;

	public Announcer(Game g, Group gr) {
		super();
		this.g = g;
		this.gr = gr;
		this.name = "Announcer";
		new Thread(this).start();
	}

	public void run() {
		status("Welcome, Ladies and Gentlemen, for another exciting game of 'Guess Who or What?'");
		g.alertAllGroups();
		g.startExam();
		int examTime = 1500;
		status("Exam in progress.");
		nap(examTime);
		g.endExam();
		status("calculating grades.");
		g.winners();
		g.announceWinners();
		g.intro();
		status("\n*********************\nLET'S PLAY!\n*********************");
		Host host = new Host(g);
		Show s = Play.show;
		s.signalHost();
	}

	private void nap(int naptime) {
		try {
			Thread.sleep(naptime);
		} catch (InterruptedException e) {

		}
	}

	protected static final long age() {
		return System.currentTimeMillis() - Play.startTime;
	}

	private void status(String m) {
		System.out.println("Time: " + age() + ", Announcer: " + m);
	}

}
