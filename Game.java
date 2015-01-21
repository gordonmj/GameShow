import java.util.Vector;
import java.util.Random;

public class Game {

	private int num_contestants = 0;
	private Vector<Object> waitingGroups = new Vector();
	private Group group = null;
	private int group_num = 1;
	private int room_capacity = 0;
	private int filled = 0;
	private boolean isAnnouncerAlertingGroups = false;
	private int seated = 0;
	private int tookExam = 0;
	private Vector<Object> waitingForTest = new Vector();
	private boolean AnnouncerSaysExamOver = false;
	private Vector<Object> waitingToFinish = new Vector();
	private boolean AnnounceResults = false;
	private Vector<Object> waitingForResults = new Vector();
	private int[] winners = new int[4];
	private Vector<Object> waitingToIntro = new Vector();
	private boolean AnnouncerIntroDone = false;
	private int waiting = 0;
	private Object blockedAnnouncer = new Object();
	private boolean everyoneDone = false;
	private boolean allIntrosDone = true;

	public void checkin() {
		System.out.println("wg: " + waitingGroups.size() + ", wFT: "
				+ waitingForTest.size() + ", wTF: " + waitingToFinish.size()
				+ ", wFR: " + waitingForResults.size() + ", wTI: "
				+ waitingToIntro.size());
	}

	public Game(int room_capacity) {
		super();
		this.room_capacity = room_capacity;
	}

	public String toString() {
		return "Number of contestants = " + num_contestants;
	}

	protected static final long age() {
		return System.currentTimeMillis() - Play.startTime;
	}

	/* GROUP METHODS */
	public void formGroup(int id, String name) {
		Object obj = new Object();// I really tried to block multiple players on
									// one group but it broke everything
		synchronized (obj) {
			if (alertGroup(obj)) {
				while (true) {
					try {
						obj.wait();
						break;
					}// try
					catch (InterruptedException e) {
						continue;
					}// catch
				}// while
			}// if
		}// synch
	}// formGroup

	public boolean groupNotReady(Object announcer) {
		synchronized (announcer) {
			if (num_contestants < Play.num_contestants) {
				return true;
			} else
				return false;
		}
	}

	private boolean alertGroup(Object obj) {
		synchronized (obj) {
			filled++;
			num_contestants++; // Game needs to keep track of this
			waitingGroups.addElement(obj);
			if (waitingGroups.size() == Play.num_contestants) {
				synchronized (blockedAnnouncer) {
					blockedAnnouncer.notify();
					return false;
				}
			}
			return true;
		}
	}

	public void alertAllGroups() {
		synchronized (blockedAnnouncer) {
			if (groupNotReady(blockedAnnouncer)) {
				while (true) {
					try {
						blockedAnnouncer.wait();
						break;
					} catch (InterruptedException e) {
						continue;
					}
				}// while
			}// if
		}// synch
		isAnnouncerAlertingGroups = true;
		while (waitingGroups.size() > 0) {
			synchronized (waitingGroups.elementAt(0)) {
				waitingGroups.elementAt(0).notify();
				waitingGroups.removeElementAt(0);
			} // Sorry, I could not get notifyAll() to work. I got
				// IllegalMonitorStateException
		}
	}

	/* EXAM METHODS */
	public void waitForExam(String name) {
		Object obj = new Object();
		synchronized (obj) {
			if (announcerNotReady(obj, name)) {
				while (true) {
					try {
						obj.wait();
						break;
					}// try
					catch (InterruptedException e) {
						continue;
					}// catch
				}// while
			}// if
		}// synch
	}// takeExam

	public boolean announcerNotReady(Object obj, String name) {
		synchronized (obj) {
			boolean status;
			System.out.println("Time: "+age() + ": " + name + " has taken a seat.");
			waitingForTest.addElement(obj);
			if (waitingForTest.size() == Play.num_contestants) {
				synchronized (blockedAnnouncer) {
					blockedAnnouncer.notify();
				}
				status = false;
			} else
				status = true;
			return status;
		}
	}// notEvSeat

	public boolean notEveryoneSeated(Object announcer) {
		synchronized (blockedAnnouncer) {
			System.out.println("# waiting for test: " + waitingForTest.size());
			if (waitingForTest.size() < Play.num_contestants) {
				return true;
			} else
				return false;
		}
	}

	public void startExam() {
		synchronized (blockedAnnouncer) {
			if (notEveryoneSeated(blockedAnnouncer)) {
				while (true) {
					try {
						blockedAnnouncer.wait();
						break;
					} catch (InterruptedException e) {
						continue;
					}
				}// while
			}// if
		}// synch
		isAnnouncerAlertingGroups = true;
		System.out.println("Time: "+age() + ": " + "Announcer: The exam is starting.");
		while (waitingForTest.size() > 0) {
			synchronized (waitingForTest.elementAt(0)) {
				waitingForTest.elementAt(0).notify();
			}// synch
			waitingForTest.removeElementAt(0);
		}// while
	}// startexame

	public void takeExam(String name) {
		Object obj = new Object();
		synchronized (obj) {
			if (examNotOver(obj, name)) {
				while (true) {
					try {
						obj.wait();
						break;
					}// try
					catch (InterruptedException e) {
						continue;
					}// catch
				}// while
			}// if
		}// synch
	}// takeExam

	public boolean examNotOver(Object obj, String name) {
		synchronized (obj) {
			boolean status;
			waitingToFinish.addElement(obj);
			tookExam++;
			if (tookExam == Play.num_contestants) {
				synchronized (blockedAnnouncer) {
					blockedAnnouncer.notify();
				}
				everyoneDone = true;
				status = false;
			} else
				status = true;
			return status;
		}
	}// notEvSeat

	public boolean notEveryoneDone(Object announcer) {
		synchronized (blockedAnnouncer) {
			if (tookExam < Play.num_contestants && !everyoneDone) {
				return true;
			} else
				return false;
		}
	}

	public void endExam() {
		synchronized (blockedAnnouncer) {
			if (notEveryoneDone(blockedAnnouncer)) {
				while (true) {
					try {
						blockedAnnouncer.wait();
						break;
					} catch (InterruptedException e) {
						continue;
					}
				}// while
			}// if
		}// synch
		System.out.println("Time: "+age() + ": " + "Announcer: the exam is over.");
		AnnouncerSaysExamOver = true;
		while (waitingToFinish.size() > 0) {
			synchronized (waitingToFinish.elementAt(0)) {
				waitingToFinish.elementAt(0).notify();
				waitingToFinish.removeElementAt(0);
			}// sncnh
		}// while
	}// startexame

	/* EXAM RESULTS METHODS */
	public void examResults(int id) {
		Object obj = new Object();
		synchronized (obj) {
			if (resultsNotIn(obj)) {
				while (true) {
					try {
						obj.wait();
						break;
					}// try
					catch (InterruptedException e) {
						continue;
					}// catch
				}// while
			}// if
		}
	}

	public boolean resultsNotIn(Object obj) {
		synchronized (obj) {
			boolean status;
			waitingForResults.addElement(obj);
			if (waitingForResults.size() == Play.num_contestants) {
				synchronized (blockedAnnouncer) {
					blockedAnnouncer.notify();
				}
				AnnounceResults = true;
				status = false;
			} else
				status = true;
			return status;
		}
	}

	public boolean resultsNotInAnnouncer(Object announcer) {
		synchronized (announcer) {
			if (waitingForResults.size() < Play.num_contestants
					&& !AnnounceResults) {
				System.out.println(waitingForResults.size());
				blockedAnnouncer = announcer;
				return true;
			} else
				return false;
		}
	}

	public boolean isWinner(int id) {
		System.out.println(id + " is in isWinner");
		return winner(id);
	}

	public int rando(int minimum, int maximum) {

		Random rnd = new Random();
		int range = (maximum - minimum) + 1;
		int number = rnd.nextInt(range) + minimum;

		return number;
	}

	public boolean winner(int id) {
		Object obj = new Object();
		synchronized (obj) {
			boolean found = false;
			for (int i = 0; i < 4; i++) {
				if (winners[i] == id)
					found = true;
			}
			return found;
		}
	}

	public void winners() {
		synchronized (blockedAnnouncer) {
			Vector<Grade> grades = new Vector();
			for (int i = 0; i < num_contestants; i++) {
				Grade g = new Grade(i, rando(0, 100));
				grades.addElement(g);
				System.out.println("Time: "+age() + ": " + Play.names[g.id]
						+ "'s grade is " + g.grade + ".");
			}
			for (int k = 0; k < 4; k++) {
				int max = 0;
				for (int j = 0; j < grades.size(); j++) {
					if (grades.elementAt(max).grade < grades.elementAt(j).grade) {
						max = j;
					}
				}
				// System.out.println(Play.names[grades.elementAt(max).id]+" is in the top four.");
				winners[k] = grades.elementAt(max).id;
				grades.removeElementAt(max);
			}
		}// synch
	}

	public void announceWinners() {
		// System.out.println("Supposed to announce winners!");
		synchronized (blockedAnnouncer) {
			if (resultsNotInAnnouncer(blockedAnnouncer)) {
				while (true) {
					try {
						blockedAnnouncer.wait();
						break;
					} catch (InterruptedException e) {
						continue;
					}
				}// while
			}// if
		}// synch
		while (waitingForResults.size() > 0) {
			synchronized (waitingForResults.elementAt(0)) {
				waitingForResults.elementAt(0).notify();
				waitingForResults.removeElementAt(0);
			}// sncnh
		}// while
	}

	public void introSelf(String name) {
		Object obj = new Object();
		synchronized (obj) {
			if (introNotReady(obj, name)) {
				while (true) {
					try {
						obj.wait();
						break;
					}// try
					catch (InterruptedException e) {
						continue;
					}// catch
				}// while
			}// if
		}// synch
	}// t

	public boolean introNotReady(Object obj, String name) {
		synchronized (obj) {
			waitingToIntro.addElement(obj);
			if (waitingToIntro.size() == 4) {
				allIntrosDone = true;
				synchronized (blockedAnnouncer) {
					blockedAnnouncer.notify();
				}
				return false;
			}
			return true;
		}
	}// notEvSeat

	public boolean everybodyNotReady() {
		if (waitingToIntro.size() < 4 || !allIntrosDone)
			return true;
		else
			return false;
	}

	public void intro() {
		synchronized (blockedAnnouncer) {
			if (everybodyNotReady()) {
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
			AnnouncerIntroDone = true;
			System.out
					.println("Time: "+age() + ": " + "Announcer: And our players are:");
			for (int i = 0; i < 4; i++) {
				System.out.println(Play.names[winners[i]]);
			}
			while (waitingToIntro.size() > 0) {
				synchronized (waitingToIntro.elementAt(0)) {
					waitingToIntro.elementAt(0).notify();
					waitingToIntro.removeElementAt(0);
				}// synch
			}// while
		}// synch
	}

}// class
