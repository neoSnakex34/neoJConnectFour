import java.util.Arrays;
import java.util.Scanner;
import java.io.*;
/**
 * Main class of the game
 * it holds a series of constant
 * describing:
 * a standard board width
 * <p>
 * a standard board height 
 * <p>
 * the players symbols and the empty box filler symbol
 * <p>
 * some ansi escape codes
 * <p>
 * a variable used to check wheter ansi colors are usable or not
 * <p>
 * a string representing the saved games folder
 */
public class Game implements AnsiCheck{

	public static final int BOARD_WIDTH = 6;
	public static final int BOARD_HEIGHT = 7;
	public static final char[] PLAYER_SYMBOLS = {'Y', 'R'};
	public static final char EMPTY_BOX_FILLER = '#';

	public static final String RED_CONSOLE_COLOR = "\u001B[31m"; //ANSI escape code for RED
	public static final String YELLOW_CONSOLE_COLOR = "\u001B[33m";//ANSI escape code for YELLOW
	public static final String RESET_COLOR = "\u001B[0m";//ANSI escape code for WHITE

	public static final String SUPER_FOLDER_SAVED_GAMES = "Saved/";
	public static final String TERM_VARIABLE = System.getenv().get("TERM");
	public static final boolean ANSI_VISUALIZER = AnsiCheck.termIsAnsiCapable();	

	//scanner used all over the class to get user input
	private static Scanner inputGetter = new Scanner(System.in);

	//two flags used in conditional statements later
	private static boolean cannotMoveFlag = false;
	private static boolean isSuspended = false;

	//not in the docs, it changes every time a new version is released 
	private static String releaseVersion = "1.01";

	public static void main(String[] args) throws IOException, ClassNotFoundException{

		boolean keepPlaying = true;
		while(keepPlaying){
			runGame();
			System.out.println(); //eye candy
			System.out.println("play again? [y/n]");
			// Scanner s1 = new Scanner(System.in);
			String input = inputGetter.next();
			String[] validInputs = {"yes", "y", "no", "n"};
			while(!(Arrays.asList(validInputs).contains(input.toLowerCase()))){
				System.out.println("invalid input, please enter [y/n] or [yes/no]");
				input = inputGetter.next();
			}
			if (!(input.toLowerCase().equals("yes") || input.toLowerCase().equals("y"))){
				System.out.println("Bye!\n");
				keepPlaying = false;
			}else{
				isSuspended = false;
				cannotMoveFlag = false;
			}

		}

	}

	//==============================A SERIES OF PRIVATE METHODS USED BY MAIN METHOD ONLY===========
	/**
	 * Creates the condition for the game to start:
	 * <p>
	 * checks for the saved games folder, else it creates it
	 * then lists, if they exists, the saved games and let user decide
	 * if start a new game or resume an old one
	 * 
	 * @return user choice on how to start the game
	 */
	private static String gameStarter(){
		File savedGamesFolder = new File(Game.SUPER_FOLDER_SAVED_GAMES);
		if (!savedGamesFolder.exists()) savedGamesFolder.mkdir();
		String[] fileList = savedGamesFolder.list();
		for (int i = 0; i<fileList.length; i++){
			int len = fileList[i].length();
			fileList[i] = fileList[i].substring(0, len-4);
		}

		int numOfOldGames = fileList.length;
		String userChoice = "new"; //default is a new game, but there is a check just below

		System.out.println();
		System.out.println("   =============================   ");
		System.out.printf("====  WELCOME TO JConnect4 v. %s ====\n", Game.releaseVersion);
		System.out.println("   ==== game by neoSnakex34 ====   \n");
		if (numOfOldGames > 0){
			System.out.println("want to resume a game?");
			System.out.println("those are the old games: ");
			System.out.println();

			int fileCounter = 1;
			for(String f: fileList){
				System.out.printf("%d) %s\n", fileCounter, f);
				fileCounter++;
			}
			System.out.println("\nwrite the name to open it");
			System.out.println("or write \"new\" to start a fresh game\n");


			// Scanner input = new Scanner(System.in);
			System.out.printf("input:  ");
			userChoice = inputGetter.next();
			while(!((Arrays.asList(fileList).contains(userChoice)) || userChoice.toLowerCase().equals("new"))){
				System.out.printf("input:  ");
				System.out.println("invalid input, write one of the names you see up or write \"new\"\n");
				userChoice = inputGetter.next();
			}
		}else{
			System.out.println("\n=== there are no saved games ===");
		}

		return userChoice;
	}

	/**
	 * let the user select a name for the board (and the match)
	 * letting one resume a saved, unfinished game
	 * @return the name of the board
	 */
	private static String gameNameSelector(){
		System.out.println("\n---> Choose a name for this gameboard <---\nyou can suspend the game and play again on it later");
		System.out.println("(please don't use spaces in your name but chars like \"_\" as a separator)\n");
		// Scanner s1 = new Scanner(System.in);
		System.out.printf("name:  ");
		String nameToReturn = inputGetter.next();

		return nameToReturn;
	}

	/**
	 * let user select if he wants to play against a computerPlayer 
	 * or a human one
	 * @param board the board where the match will be played
	 * @return a Player instance (p2)
	 */
	private static Player playerTwoTypeSelector(Board board){
		System.out.println();//eye candy
		System.out.println("Do you want to play against CPU? [Y/n]");
		Player player = new ComputerPlayer(PLAYER_SYMBOLS[1], board);
		// Scanner in = new Scanner(System.in);

		// to lowercase so that whatever input i get it will be compared with lowercase 
		String input = inputGetter.next().toLowerCase();

		if (input.equals("y") || input.equals("yes")){
			//THAT PLAYER WILL ALWAYS BE PLAYER TWO
			System.out.println("\nNow playing against CPU\n");

		}else if(input.equals("n") || input.equals("no")){
			player = new HumanPlayer(PLAYER_SYMBOLS[1], board);
			System.out.println("\nNow playing against player two!\n");
		}else{
			System.out.println("\nInput of player selector invalid, I'm assuming you wanted a demo with CPU player.\nnow playing against CPU.\n");
		}
		return player;
	}	

	/**
	 * executed to run a match against two player;
	 * will be executed until one decides to stop it
	 * @throws IOException if the deserializer fails
	 * @throws ClassNotFoundException if the deserializer fails
	 */
	private static void runGame() throws IOException, ClassNotFoundException{

		String gameName;
		Board gameBoard;
		boolean goOn;

		Player p1;
		Player p2;


		String init = gameStarter();
		if (init.toLowerCase().equals("new")){
			gameName = gameNameSelector();
			gameBoard = new Board(gameName);

			//debug reason
			// p1 = new ComputerPlayer(PLAYER_SYMBOLS[0], gameBoard);
			p1 = new HumanPlayer(PLAYER_SYMBOLS[0], gameBoard);
			((HumanPlayer)p1).askName();

			p2 = playerTwoTypeSelector(gameBoard);
			if (p2 instanceof HumanPlayer){	
				((HumanPlayer)p2).askName();
			}
		}else{
			gameName = init;
			GameSaver gameSaver;
			gameSaver = (GameSaver) load(gameName);
			gameBoard = gameSaver.getBoard();
			p1 = gameSaver.getP1();
			p2 = gameSaver.getP2();
			// gameBoard = (Board)restoreState(SUPER_FOLDER_SAVED_GAMES+gameName);
			if(ANSI_VISUALIZER){
				gameBoard.updateGridView(RED_CONSOLE_COLOR, YELLOW_CONSOLE_COLOR, RESET_COLOR);
			}else{
				gameBoard.updateGridView();
			}
		}

		// debug reason
		// Player p3 = new  ComputerPlayer('f', gameBoard);

		//if this is false, players are invalid and the game can't be played
		goOn = gameBoard.setPlayers(p1, p2);

		//for(int i=0; i<18; i++){ gameBoard.askNextMove(p3); }
		// boolean goOn = true;	
		while((hasWinner(p1, p2)==false) && (isSuspended==false) && (goOn == true)){


			goOn = gameBoard.askNextMove(p1); //#

			if(goOn){
				gameBoard.askNextMove(p2);
			}


		}

		if(!isSuspended || (cannotMoveFlag == true)) flushClosedGame(gameName);
		if (isSuspended) save(gameBoard, p1, p2);
	}

	/**
	 * deletes savegames for closed games.
	 * <p> cases:
	 * <p>
	 * - a game ended with no winner
	 * <p>
	 * - one player won
	 * @param gameName the name of the file to delete
	 */
	private static void flushClosedGame(String gameName){
		File opened = new File(SUPER_FOLDER_SAVED_GAMES+gameName+".sav");
		opened.delete();
		System.out.printf("\nSavestate of game \"%s\" deleted!\n", gameName);
	}


	/**
	 * restores the previous state of an Object in the game 
	 * @param path the path of the saved game
	 * @return the deserialized object
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 */
	private static Object restoreState(String path) throws IOException, FileNotFoundException, ClassNotFoundException{
		FileInputStream fileInputStream = new FileInputStream(path+".sav");
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
		Object object = objectInputStream.readObject();
		objectInputStream.close(); 
		return object;
	}

	/**
	 * saves the state of an object into a given file
	 * @param object the object which state need to be saved
	 * @param path the path of the file in which save the obj
	 * @throws IOException if problems occurs during file handling
	 */
	public static void setSaveState(Object object, String path) throws IOException{ // file name will be a unique id
		FileOutputStream fileOutputStream = new FileOutputStream(path+".sav");
		ObjectOutputStream objectOutputSteam = new ObjectOutputStream(fileOutputStream);
		objectOutputSteam.writeObject(object);
		objectOutputSteam.close();
	}
	//==============================SOME PUBLIC METHODS TO BE USED IN OUTER CLASSES=================

	/**
	 * checks if the current game has a winner or not
	 * @param p1 the player one
	 * @param p2 the player two
	 * @return true if the game has a winner, false otherwise
	 */
	public static boolean hasWinner(Player p1, Player p2){
		if ((p1.getWonStatus() == true) || (p2.getWonStatus() == true)){

			if (p1.getWonStatus() == true){
				System.out.printf("Player %s: %s won the game!\n", p1.getSymbolName(), ((HumanPlayer) p1).getName());
				// System.out.println("won");
			}else{

				if (p2 instanceof HumanPlayer){
					System.out.printf("Player %s: %s won the game!\n", p2.getSymbolName(), ((HumanPlayer) p2).getName());
				}else{
					System.out.printf("Player %s won the game!\n", p2.getSymbolName());
				}

			}

			return true;
		}
		return false;
	}

	/**
	 * setter for the flag cannotMoveFlag
	 */
	public static void setCannotMove(){
		cannotMoveFlag = true;
	}

	/**
	 * saves the state of the game running in the board passed
	 * @param gameBoard the board of the game to save 
	 * @throws IOException if there are problems with SaveStateSetter class (handling files)
	 */
	private static void save(Board gameBoard, Player p1, Player p2) throws IOException{
		String path = SUPER_FOLDER_SAVED_GAMES+gameBoard.getBoardName();
		GameSaver toSave = new GameSaver(gameBoard, p1, p2);
		setSaveState(toSave, path);

	}

	private static Object load(String gameName) throws FileNotFoundException, ClassNotFoundException, IOException{
		String path = SUPER_FOLDER_SAVED_GAMES+gameName;
		return restoreState(path);
	}
	/**
	 * sets the flag isSuspended to true
	 */
	public static void suspend(){
		isSuspended = true;
	}



}
