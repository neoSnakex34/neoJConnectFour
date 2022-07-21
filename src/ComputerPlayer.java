import java.io.Serializable;
import java.util.Random;

/**
 * A Player class that uses a random generator as 
 * column selector, it servers as a demo of playing
 * against another player
 */
public class ComputerPlayer extends Player implements Serializable{
    private Random rng = new Random();

	//public ComputerPlayer(){

	//}
    /**
     * init method 
     * @param symbol the symbol of the player
     * @param sharedBoard the board of the game, where the player will be put 
     */
    public ComputerPlayer(char symbol, Board sharedBoard){
        super(symbol, sharedBoard);
    }
    
	/**
	 * makes a move on the board via board's placeGameChip method
	 * @param column the column in which the chip will be placed
	 * @return true if the chip can be placed, false otherwise
	 */


    /**
     * generates a random int representing the number of a column from 0,5
     * @return the number generated
     */
    public int rngColumn(){
		//it can hit invalid columns, if this happends it retries
        int col = rng.nextInt(10);
        return col;
    }

    
}
