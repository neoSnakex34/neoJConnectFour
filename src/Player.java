import java.io.Serializable;

/**
 * Class representing a generic player of the game
 * <p>
 * every player has a unique symbol, a board (shared with the other player)
 * and a win condition flag.
 * this class holds some basic methods such as getters and setters for attributes
 */
public class Player implements Serializable {

	private char symbol;
	private Board sharedBoard;
	private boolean hasWon;

	//public Player(){

	//}
	public Player(char symbol, Board board) {
		this.symbol = symbol;
		this.sharedBoard = board;
		this.hasWon = false;
	}

	/**
	 * winner setter
	 * <p>
	 * sets the player as a winner
	 */
	public void setWinner(){
		hasWon = true;
	}

	/**
	 *won status getter
	 *<p> 
	 * @return the winning flag state
	 */
	public boolean getWonStatus(){
		return hasWon;
	}

	/**
	 * player's symbol getter
	 * @return the symbol of the player
	 */
	public Character getSymbol(){
		return symbol;
	}

	/**
	 * player symbol name getter
	 * @return the name of the color tied to the player
	 */
	public String getSymbolName(){
		if(((Character) symbol).equals('Y')){
			return "YELLOW";
		}else{
			return "RED";
		}
	}

	public boolean makeMove(int column){
		
		Boolean result = sharedBoard.placeGameChip(column, this.symbol);
		/**
		* after every move the view is updated 
		* if the term is ANSI COLOR capable player will have a color
		* else they will only use their unique chars
		*/
		if(Game.ANSI_VISUALIZER){
			this.sharedBoard.updateGridView(Game.RED_CONSOLE_COLOR, Game.YELLOW_CONSOLE_COLOR, Game.RESET_COLOR);
		}else{
			this.sharedBoard.updateGridView();
		}
		
		return result;	
		
	}
}
