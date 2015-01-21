
public class Play{

	public static long startTime = System.currentTimeMillis();
	public static int num_contestants = 13;
	public static int numRounds = 2;
	public static int numQuestions = 5;
	public static int questionValues = 200;
	public static double rightPercent = 0.65;
	public static int room_capacity = 4;
	public static String[] names = {"Harry", "Ron", "Hermione", "Neville", "Draco", "Ginny", "Cedric", "Luna", "Albus", "Severus", "Minerva", "Rubeus", "Molly", "Fred", "George", "Voldemort", "Gilderoy", "Sirius", "Peter", "Remus", "Kingsley", "Tonks", "Cho", "Viktor", "Reeta", "Dolores", "Lucius", "Bellatrix", "Alastor", "Cornelius", "Rufus", "Hedwig", "Argus"};
	public static Show show = new Show();
	
	public static void main(String[] args){
		
		if (args.length > 0){
			numRounds = Integer.parseInt(args[0]);
			numQuestions = Integer.parseInt(args[1]);
			questionValues = Integer.parseInt(args[2]);
			rightPercent = Double.parseDouble(args[3]);
			room_capacity = Integer.parseInt(args[4]);
			num_contestants = Integer.parseInt(args[5]);
		}
		
		Game g = new Game(room_capacity);
		Group gr = new Group(room_capacity);
		//create the announcer
		new Announcer(g,gr);

		//System.out.println("There are "+num_contestants+" potential contestants waiting to play.");
		
		//Create the contestants
		for (int i=0;i < num_contestants; i++){
			new Contestant(names[i], i, 1000, g, room_capacity, gr);
		}
		//System.exit(0);

		
	}

}
