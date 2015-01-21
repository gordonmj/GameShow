import java.util.Random;

public class Contestant implements Runnable {

	private int id = 0;
	private String name = "";
	private int nap = 0;
	private Game g = null;
	private Group gr = null;
	private int room_capacity = 0;
	private int group_num = 0;
	private boolean isPlaying = false;
	private int score = 0;
	private boolean correct;
	private boolean answered = false;

	public Contestant(String name, int id, int nap, Game g, int cap, Group gr) {
		super();
		this.name = name;
		this.id = id;
		this.nap = nap;
		this.g = g;
		this.gr = gr;
		this.room_capacity = cap;
		new Thread(this).start();
	}

	public void run() {
		System.out.println(this.name + ": Hi, I'm " + this.name
				+ " and I'm contestant #" + (id + 1) + ".");
		formGroup();
		takeExam();
		g.examResults(id);
		isPlaying = g.isWinner(id);
		if (isPlaying) {
			status("is playing.");
			g.introSelf(name);
			status(randMessage());
			Show s = Play.show;
			s.onStage(name);
			while (Show.playing) {
				s.waitForQuestion(name);
				think();
				answered = s.answer(name);
				if (answered) {
					status("answered first!");
					correct = s.rightOrWrong();
					if (correct) {
						score += Play.questionValues;
						status("correct!");
					} else {
						score -= Play.questionValues;
						status("WRONG!");
					}
					status("score is: " + score);
				} else
					status("didn't answer first.");
				answered = false;
			}
			if (score >= 0) {
				int wager = wager();
				Random r = new Random();
				int num = r.nextInt(100);
				if (num % 2 == 0) {
					score += wager;
				} else {
					score -= wager;
				}
				s.finalScore(name, score);
			} else
				status("My score is negative. Goodbye!");
		}
	}

	private void nap(int naptime) {
		try {
			Thread.sleep(naptime);
		} catch (InterruptedException e) {

		}
	}

	private void think() {
		status("Thinking...");
		Random n = new Random();
		int num = n.nextInt(3000);
		nap(num);
	}

	protected static final long age() {
		return System.currentTimeMillis() - Play.startTime;
	}

	private void status(String m) {
		System.out.println("Time: " + age() + ", " + name + ": " + m);
	}

	public String randMessage() {

		Random rnd = new Random();
		int number = rnd.nextInt(7) + 0;

		String[] messages = { "I'm really excited to be here!",
				"I'm here to win, not to make friends.",
				"I'm a big fan of the show.", "Hi Mom! Look I'm on TV!",
				"I'm a lean, mean, game-show-winning machine!",
				"Um... is this the Dumbledore's Army meeting?",
				"Can I get a 'WOOP WOOP!'?", "ARE YOU READY TO ROCK!?!",
				"Heeeeeere's Johnny!" };
		return messages[number];
	}

	public void formGroup() {
		status("is trying to enter a group.");
		g.formGroup(id, name);
		status("is moving on with the rest of the group.");

	}

	public void takeExam() {
		status("is waiting for the exam.");
		g.waitForExam(name);
		status("is taking the exam.");
		g.takeExam(name);
		// status("is awaiting the exam results.");
	}

	public int wager() {
		Random rnd = new Random();
		return rnd.nextInt(score + 1);
	}

}
