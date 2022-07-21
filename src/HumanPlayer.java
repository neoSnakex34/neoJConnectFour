import java.io.Serializable;
import java.util.Scanner;
/**
 * Class representing a human player of the game
 * <p>
 * every player has a unique symbol, a board (shared with the other player)
 * and a win condition flag.
 */
public class HumanPlayer extends Player implements Serializable{

	private String name;

	/** 
	 * init method, it sets symbol and board to the ones passed; and defaultly sets the win condition flag to false
	 * @param symbol one of two symbols representing the color of the player chip, yellow or red 
	 * @param board the board instance in which every player will put his own chip 
	 */
	//public HumanPlayer(){

	//}
	
	public HumanPlayer(char symbol, Board board) {
		super(symbol, board);
	}

	/**
	 * will prompt the player for his name, then sets the name with input
	 */
	public void askName(){
		System.out.println("\nWhat's your name?");
		Scanner s = new Scanner(System.in);
		this.name = s.next();
	}


	/**
	 * name getter
	 * @return name attribute
	 */
 	public String getName(){
		return name;
	}
	
	


	
	
}
