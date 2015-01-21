import java.util.Vector;

public class Show {

	private Object blockedAnnouncer = new Object();
	private Object blockedHost = new Object();
	private Object blockedContestant = new Object();
	private int players_arrived = 0;
	private boolean ready_to_begin = false;
	private Vector waitingToPlay = new Vector();
	private boolean hostReady = false;
	private Vector waitingToAnswer = new Vector();
	private boolean questionAsked = false;
	private boolean readyToAsk = true;
	private boolean questionAnswered = false;
	private Vector waitingForAnswer = new Vector();
	private Vector tryingToAnswer = new Vector();
	private boolean hostDecision = false;
	private boolean hostDecided = false;
	public static boolean playing = false;
	private int[] scores = { 0, 0, 0, 0 };
	private String[] names = { "", "", "", "" };
	private boolean allScoresIn = false;

	public Show() {
		super();
	}

	public void checkin() {
		System.out.println("wTP: " + waitingToPlay.size() + ", wTA: "
				+ waitingToAnswer.size() + ", wFA: " + waitingForAnswer.size()
				+ ", tTA: " + tryingToAnswer.size());
	}

	protected static final long age() {
		return System.currentTimeMillis() - Play.startTime;
	}

	/* START */
	public void signalHost() {
		synchronized (blockedAnnouncer) {
			if (introsDone()) {
				while (true) {
					try {
						blockedAnnouncer.wait();
						break;
					}// try
					catch (InterruptedException e) {
						continue;
					}// catch
				}// while
			}
		}// synch
		synchronized (blockedHost) {
			ready_to_begin = true;
			blockedHost.notify();
		}
	}// method

	public boolean introsDone() {
		synchronized (blockedAnnouncer) {
			if (waitingToPlay.size() == 4) {
				return false;
			} else
				return true;
		}
	}

	public void beginGame() {
		synchronized (blockedHost) {
			if (introsDone()) {
				while (true) {
					try {
						blockedHost.wait();
						break;
					}// try
					catch (InterruptedException e) {
						continue;
					}// catch
				}// while
			}
			hostReady = true;
			while (waitingToPlay.size() > 0) {
				synchronized (waitingToPlay.elementAt(0)) {
					waitingToPlay.elementAt(0).notify();
				}
				waitingToPlay.removeElementAt(0);
			}
			playing = true;
		}// synch

	}

	public void onStage(String name) {
		Object obj = new Object();
		synchronized (obj) {
			if (HostNotReady(obj)) {
				while (true) {
					try {
						obj.wait();
						break;
					}// try
					catch (InterruptedException e) {
						continue;
					}// catch
				}// while
			}
			System.out.println(age() + ": " + name + " has taken the stage.");
		}// synch
	}

	public boolean HostNotReady(Object obj) {
		synchronized (obj) {
			if (!hostReady) {
				waitingToPlay.addElement(obj);
				if (waitingToPlay.size() == 4) {
					synchronized (blockedAnnouncer) {
						blockedAnnouncer.notify();
					}
					synchronized (blockedHost) {
						blockedHost.notify();
					}
				}
				return true;
			}
			return false;
		}
	}

	/* ASK QUESTION */
	public void waitForQuestion(String name) {
		Object obj = new Object();
		synchronized (obj) {
			if (notAsked(obj)) {
				while (true) {
					try {
						obj.wait();
						break;
					}// try
					catch (InterruptedException e) {
						continue;
					}// catch
				}// while
			}
		}// synch
	}

	public boolean notAsked(Object obj) {
		synchronized (obj) {
			waitingToAnswer.addElement(obj);
			if (waitingToAnswer.size() == 4)
				synchronized (blockedHost) {
					blockedHost.notify();
					return false;
				}
			return true;
		}
	}

	public boolean notReadyToAsk() {
		synchronized (blockedHost) {
			if (waitingToAnswer.size() == 4)
				return false;
			else
				return true;
		}
	}

	public void askQuestion(int qNum) {
		synchronized (blockedHost) {
			if (notReadyToAsk()) {
				while (true) {
					try {
						blockedHost.wait();
						break;
					}// try
					catch (InterruptedException e) {
						continue;
					}// catch
				}// while
			}
			System.out.println(age() + ": " + "HOST: Question " + qNum
					+ " is: What number am I thinking of?");
			questionAsked = true;
			while (waitingToAnswer.size() > 0) {
				synchronized (waitingToAnswer.elementAt(0)) {
					waitingToAnswer.elementAt(0).notify();
				}
				waitingToAnswer.removeElementAt(0);
			}// while
		}// synch
	}// method

	/* ANSWER QUESTION */
	public boolean answer(String name) {
		Object obj = new Object();
		synchronized (obj) {
			if (answered(obj)) {
				while (true) {
					try {
						obj.wait();
						break;
					}// try
					catch (InterruptedException e) {
						continue;
					}// catch
				}// while
			}
		}// synch
		if (!questionAnswered) {
			System.out.println(age() + ": " + name + " has an answer.");
			questionAnswered = true;
			synchronized (blockedHost) {
				blockedHost.notify();
			}
			return true;
		}
		return false;
	}

	public boolean answered(Object obj) {
		synchronized (obj) {
			if (!questionAnswered) {
				return false;
			}
			tryingToAnswer.addElement(obj);
			return true;
		}
	}

	public boolean noOneAnswered() {
		synchronized (blockedHost) {
			if (questionAnswered && tryingToAnswer.size() == 3)
				return false;
			else
				return true;
		}
	}

	public void getAnswer() {
		synchronized (blockedHost) {
			if (noOneAnswered()) {
				while (true) {
					try {
						blockedHost.wait();
						break;
					}// try
					catch (InterruptedException e) {
						continue;
					}// catch
				}// while
			}
			while (tryingToAnswer.size() > 0) {
				synchronized (tryingToAnswer.elementAt(0)) {
					tryingToAnswer.elementAt(0).notify();
				}
				tryingToAnswer.removeElementAt(0);
			}// while
		}// synch
	}// method

	public boolean rightOrWrong() {
		synchronized (blockedContestant) {
			if (hostNotDecided()) {
				while (true) {
					try {
						blockedContestant.wait();
						break;
					}// try
					catch (InterruptedException e) {
						continue;
					}// catch
				}// while
			}
		}// synch
		return hostDecision;
	}

	public boolean hostNotDecided() {
		if (hostDecided)
			return false;
		return true;
	}

	public void decision(boolean decision) {
		hostDecided = true;
		hostDecision = decision;
		if (decision) {
			System.out.println(age() + ": "
					+ "HOST: Good job! You got it right!");
		} else
			System.out.println(age() + ": "
					+ "HOST: I'm sorry. That's incorrect.");
		synchronized (blockedContestant) {
			blockedContestant.notify();
		}

	}

	public void finalScore(String name, int score) {
		Object obj = new Object();
		synchronized (obj) {

			for (int i = 0; i < 4; i++) {
				if (names[i] == "") {
					names[i] = name;
					scores[i] = score;
					break;
				}
			}
			if (names[3] != "") {
				allScoresIn = true;
				synchronized (blockedHost) {
					blockedHost.notify();
				}
			}
		}
	}

	public void getFinalScores() {
		synchronized (blockedHost) {
			if (!allScoresIn) {
				while (true) {
					try {
						blockedContestant.wait();
						break;
					}// try
					catch (InterruptedException e) {
						continue;
					}// catch
				}
			}
		}
		int max = 0;
		for (int i = 0; i < 4; i++) {
			if (scores[i] > scores[max])
				max = i;
		}
		System.out.println(age() + ": " + "HOST: And the winner is: "
				+ names[max]);
	}

}// class
