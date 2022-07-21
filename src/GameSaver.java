import java.io.Serializable;

/**
 * Class used to store the objects needed to serialize and deserialize a game.
 * when serializing, a new GameSaver Obj is created (with the given attributes);
 * when deserializing getters are used to divide attributes.
 */
public class GameSaver implements Serializable{
    private Board board;
    private Player p1;
    private Player p2;


    /**
     * Constructor
     * @param sharedBoard the shared board
     * @param p1 player one
     * @param p2 player two
     */
    public GameSaver(Board sharedBoard, Player p1, Player p2){
        this.board = sharedBoard;
        this.p1 = p1;
        this.p2 = p2;
    }

	/**
	 * getter for board instance
	 * @return the board instance
	 *
	 *  */
    public Board getBoard() {
        return board;
    }
    
	/**
	 * getter for player one instance
	 * @return player one instance
	 * */
    public Player getP1() {
        return p1;
    }
   

	/**
	 * getter for player two instance
	 * @return player two instance
	 * 
	 * */

    public Player getP2() {
        return p2;
    }
}
