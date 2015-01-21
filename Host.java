import java.util.Random;

public class Host implements Runnable {

	private Game g = null;

	public Host(Game g) {
		super();
		this.g = g;
		new Thread(this).start();
	}

	public void run() {
		Show s = Play.show;
		s.beginGame();
		System.out.println("HOST: WHOOOOOOO'S READY TO PLAY!?");
		for (int i = 0; i < Play.numRounds; i++) {
			status("ROUND " + (i + 1) + ": Get ready...");
			for (int j = 0; j < Play.numQuestions; j++) {
				s.askQuestion(j + 1);
				s.getAnswer();
				s.decision(correct());
			}
		}
		Show.playing = false;

		// Final round
		s.getFinalScores();
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
		System.out.println("Time: " + age() + ", HOST: " + m);
	}

	public boolean correct() {
		Random rnd = new Random();
		int score = rnd.nextInt(100) + 1;
		if (score > (1.0 - Play.rightPercent) * 100)
			return true;
		return false;

	}

}
