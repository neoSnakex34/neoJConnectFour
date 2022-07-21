import java.io.Serializable;
import java.security.cert.TrustAnchor;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Class representing a connect4 game board
 * the board is a 2d array called grid
 * such grid has a width and an height parameters
 * in this implementation of the game they uses a fixed size
 * but the program may be extended overloading the constructor
 * and taking different dimensions
 * 
 * the class implements the interface Serializable in order
 * to allow the user to save the state of the current game
 */
public class Board implements Serializable {

	private char[][] grid;
	/**
	 * those params will contain a costant each
	 * they may be not necessary but may be useful
	 * if one decides to implement a grater board
	 * with a custom init method
	 * 
	 * so now they are used as a wrapper for Game.BOARD_COSTANT_DIMENSION vars
	 * in order to write less
	 */
	private int width;
	private int height;
	private Player p1;
	private Player p2;
	// private Player debugger = new HumanPlayer('x', this);
	private String boardName;

	// public Player getDebugger(){
	// return debugger;
	// }

	/**
	 * init method that setup the grid
	 * 
	 * @param name the name of the game board;
	 *             it is used as nameGameBoard.sav to serialize
	 *             and save the state of the board by the SaveStateSetter class
	 */
	public Board(String name) {
		this.width = Game.BOARD_WIDTH;
		this.height = Game.BOARD_HEIGHT;
		grid = new char[height][width];
		setup(name);
	}

	/**
	 * Method that fills the grid, gives the board class a name
	 * and prints the empty grid in terminal
	 * 
	 * @param name the name that will be assigned to the game board; used in the
	 *             init method
	 */
	public void setup(String name) {
		setBoardName(name);
		fillEmptyGrid();
		updateGridView();
		// display();
	}

	/**
	 * board name setter
	 * 
	 * @param boardName the name of the gameboard
	 */
	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}

	/**
	 * board name getter
	 * 
	 * @return the name of the board
	 */
	public String getBoardName() {
		return boardName;
	}

	/**
	 * Method that assigns Player object to the board
	 * 
	 * @param p1 the player one
	 * @param p2 the player two (may be a real player or a CPU one)
	 * @return true if player symbols are valid (so different), false otherwise
	 */
	public boolean setPlayers(Player p1, Player p2) {
		if (p1.getSymbol().equals(p2.getSymbol())) {
			System.out.println("CANNOT HAVE TWO PLAYER WITH THE SAME SYMBOL");
			return false;
		}
		this.p1 = p1;
		this.p2 = p2;
		return true;
	}

	/**
	 * takes a column and a player symbol and drop the chip(player symbol) in the
	 * given column
	 * if the column is valid and has empty space
	 * 
	 * @param column       the column where the chip must be placed
	 * @param playerSymbol the symbol of the player placing the chip
	 * @return true if the chip can be placed, false otherwise
	 */
	public boolean placeGameChip(int column, char playerSymbol) { // each player will place his own symbol

		if ((column < 0) || (column > (width - 1))) {
			return false;
		} else {
			try {
				// if the column exists in the given grid
				int row_index = height - 1;

				while (!((Character) grid[row_index][column]).equals(Game.EMPTY_BOX_FILLER)) {
					row_index -= 1;
				}

				grid[row_index][column] = playerSymbol;

				return true;

			} catch (IndexOutOfBoundsException e) {
				System.out.printf("\nTop of column %d, reached. \n", column);
				return false;
			}
		}
	}

	/**
	 * checks if the upper row of the grid has space left
	 * so that the game can continue (else the board is full)
	 * 
	 * @return true if the space exist, false otherwise
	 */
	public boolean canMove() {
		int occupied = 0;
		for (int topRowColIndex = 0; topRowColIndex < width; topRowColIndex++) {
			if (grid[0][topRowColIndex] != Game.EMPTY_BOX_FILLER)
				occupied += 1;
		}
		if (occupied < (width)) {
			return true;
		}

		if (!Game.hasWinner(p1, p2)) {
			// TODO are we sure that they cannot win in this condition?! like with a last
			// move
			System.out.println("GAME ENDED WITH NO WINNER");
		}

		return false;
	}

	/**
	 * ask each player to play his turn, they can even choose to exit the game
	 * saving the state in a file
	 * 
	 * @param currentPlayer the player that is currently playing
	 * @return the column in which the chip must be placed, -1 if the player decides
	 *         to exit, -4 if some error ever occurs
	 */
	private int getInputFromConsole(Player currentPlayer) {
		System.out.println(); // eye candy
		if (currentPlayer instanceof HumanPlayer) {
			System.out.printf("%s Player: %s, must select a column in range 0, %d\n", currentPlayer.getSymbolName(),
					((HumanPlayer) currentPlayer).getName(), (width - 1));
		} else {
			System.out.printf("%s Player, must select a column in range 0, %d\n", currentPlayer.getSymbolName(),
					(width - 1));
			// System.out.println("Player " + currentPlayer.getSymbolName() + " must select
			// a column in range 0, " + (width-1));
		}
		System.out.println("or type \"exit\" to quit and save state");

		Scanner myInput = new Scanner(System.in);

		boolean flag = true;
		while (flag) {
			try {
				System.out.printf("input:  ");
				String input = myInput.next();
				if (input.toLowerCase().equals("exit")) {
					flag = false;
					return -1;
				} else {
					int column = Integer.parseInt(input);
					flag = false;
					return column;
				}
			} catch (NumberFormatException e) {
				System.out.println("you must enter a valid number or write exit!");
				continue;
			}

		}
		return -4;
	}

	/**
	 * gets a random column from cpu player
	 * 
	 * @param currentPlayer the cpu player playing the game (p2)
	 * @return true if there is still no winner after the move, false otherwise
	 */
	private boolean cpuMove(ComputerPlayer currentPlayer) {
		int column = currentPlayer.rngColumn();
		while (!currentPlayer.makeMove(column)) {
			System.out.println("No moves allowed on column " + column + "!");
			column = currentPlayer.rngColumn();
		}

		boolean winnerChecker = checkWin(currentPlayer.getSymbol());
		if (winnerChecker == true) {
			currentPlayer.setWinner();
			return false;
			// return false;
		}
		return true;

	}

	/**
	 * gets the input from player, choosing a column or to exit and save
	 * if exit is selected suspends the game and saves the state in a file
	 * 
	 * @param currentPlayer the player in the current turn (p1 or p2)
	 * @return true if the game can continue, false otherwise
	 */
	private boolean humanMove(Player currentPlayer) {
		// 2) Ask for a move and repeat until valid option has been chosen
		int column, genericInput;

		column = genericInput = getInputFromConsole(currentPlayer);
		// fixed here the bug where if a impossible column is hit it never exits cause
		// it loops into while
		while (!currentPlayer.makeMove(column)) {

			if (genericInput == -1) {
				Game.suspend();
				return false;
			} else if (genericInput == -4) {
				System.out.println("SOME ERROR OCCURRED");
				System.out.println("saving game and closing");
				Game.suspend();
				return false;
			} else {
				System.out.println("No moves allowed on column " + column + "!");
				column = genericInput = getInputFromConsole(currentPlayer);

			}

		}

		boolean winnerChecker = checkWin(currentPlayer.getSymbol());
		if (winnerChecker == true) {
			currentPlayer.setWinner();
			return false;
			// return false;
		}
		return true;
	}

	/**
	 * asks the current player for the next move
	 * 
	 * @param currentPlayer the current player
	 * @return true if the move can be performed, false otherwise
	 */
	public boolean askNextMove(Player currentPlayer) {
		if (!canMove()) {
			Game.setCannotMove();
			return false;
		}

		boolean moveResult = false;
		if (currentPlayer.getClass().getSimpleName().equals("ComputerPlayer")) {
			moveResult = cpuMove((ComputerPlayer) currentPlayer);

		} else {
			moveResult = humanMove((HumanPlayer) currentPlayer);
		}
		return moveResult;
	}

	/**
	 * Checks if chars in four given position on the grid are the same as target.
	 * 
	 * @return true if all chars equals target.
	 */
	private boolean fourChecker(char a, char b, char c, char d, char target) {
		if ((a == b) && (c == d) && (a == d) && (a == target)) {
			return true;
		}
		return false;
	}

	/**
	 *
	 * checks for winning sequences trough the board
	 * it checks for 4 equal symbols
	 * <p>
	 * in vertical lines (checking from a position to 4 rows below)
	 * <p>
	 * in horizontal lines(checking from a position to 4 columns further)
	 * <p>
	 * in upper going and down going diagonals(checking only in range where a
	 * diagonal of 4 can exist)
	 * <p>
	 * 
	 * @param playerSymbol the symbol that must be in a line of 4
	 * @return true if it finds a winning sequence, false otherwise
	 */
	private boolean checkWin(char playerSymbol) {

		/**
		 * declaring indexes
		 */
		int row_index, col_index;
		char pointOne, pointTwo, pointThree, pointFour;

		/**
		 * checking for vertical matches
		 * row index stops in a range where it can reach the end of the board by
		 * an extension of 4 but cannot go out of bound
		 */
		for (col_index = 0; col_index < width; col_index++) {
			for (row_index = 0; row_index < height - 3; row_index++) {

				pointOne = grid[row_index][col_index];
				pointTwo = grid[row_index + 1][col_index];
				pointThree = grid[row_index + 2][col_index];
				pointFour = grid[row_index + 3][col_index];

				if ((fourChecker(pointOne, pointTwo, pointThree, pointFour, playerSymbol)) == true)
					return true;
			}
		}

		/**
		 * checking for horizontal matches
		 * col index stops in a range where it can reach the end of the board extending
		 * 4 steps ahead but without going out of range
		 */
		for (row_index = 0; row_index < height; row_index++) {
			for (col_index = 0; col_index < width - 3; col_index++) {

				pointOne = grid[row_index][col_index];
				pointTwo = grid[row_index][col_index + 1];
				pointThree = grid[row_index][col_index + 2];
				pointFour = grid[row_index][col_index + 3];
				if ((fourChecker(pointOne, pointTwo, pointThree, pointFour, playerSymbol)) == true)
					return true;

			}
		}

		/**
		 * checking for minor (downgoing direction) diagonal matches
		 * indexes stops where they cannot go out of bound, taking all the possible
		 * diagonals of len 4
		 * some indexes (smaller diagonals) are never hit:
		 *
		 */
		for (row_index = 0; row_index < height - 3; row_index++) {
			for (col_index = 0; col_index < width - 3; col_index++) {
				pointOne = grid[row_index][col_index];
				pointTwo = grid[row_index + 1][col_index + 1];
				pointThree = grid[row_index + 2][col_index + 2];
				pointFour = grid[row_index + 3][col_index + 3];
				if ((fourChecker(pointOne, pointTwo, pointThree, pointFour, playerSymbol)) == true)
					return true;
			}
		}

		/**
		 * checking for major (uppergoing direction) diagonal matches
		 * indexes stops where they cannot go out of bound, taking all the possible
		 * diagonals of len 4
		 * indexes of smaller diags are never hit:
		 * upper-left border and lower-right one
		 */
		for (row_index = 0; row_index < height - 3; row_index++) {
			for (col_index = width - 1; col_index >= 3; col_index--) {
				pointOne = grid[row_index][col_index];
				pointTwo = grid[row_index + 1][col_index - 1];
				pointThree = grid[row_index + 2][col_index - 2];
				pointFour = grid[row_index + 3][col_index - 3];
				if ((fourChecker(pointOne, pointTwo, pointThree, pointFour, playerSymbol)) == true)
					return true;
			}
		}
		return false;
	}

	/**
	 * fills the grid with empty box filler
	 * a generic symbol that represents an empty box
	 */
	private void fillEmptyGrid() {
		for (char[] row : grid) {
			Arrays.fill(row, Game.EMPTY_BOX_FILLER);
		}
	}

	/**
	 * prints in a eye candy way every update on the board
	 * with NO ANSI SUPPORT
	 */
	public void updateGridView() {
		printNumsAndHeader();
		for (char[] row : grid) {
			for (Character gridBoxContent : row) {
				System.out.printf(" %c ", gridBoxContent);
			}
			System.out.println();
		}
		printFooter();
	}

	/**
	 * prints in a eye candy way every update on the board
	 * with ANSI SUPPORT
	 * 
	 * @param color1       first ANSI escape code
	 * @param color2       second ANSI escape code
	 * @param defaultColor default color to reset after coloring the chips
	 */
	public void updateGridView(String color1, String color2, String defaultColor) {
		printNumsAndHeader();
		for (char[] row : grid) {
			for (Character gridBoxContent : row) {

				if (gridBoxContent.equals('R')) {
					System.out.printf(" %s%c ", color1, gridBoxContent);
				} else if (gridBoxContent.equals('Y')) {
					System.out.printf(" %s%c ", color2, gridBoxContent);
				} else {
					System.out.printf(" %s%c ", defaultColor, gridBoxContent);
				}
			}

			// In order to manage the case in wich the else is never hit
			// i reset the color in this print
			System.out.println(defaultColor);
		}
		printFooter();
	}

	/**
	 * prints numbers and header for the grid
	 */
	private void printNumsAndHeader() {
		System.out.println();
		for (int i = 0; i < width; i++) {
			System.out.printf(" %d ", i);
		}
		System.out.println();
		System.out.println(" = ".repeat(width));
	}

	/**
	 * prints the footer of the grid
	 */
	private void printFooter() {
		System.out.println(" = ".repeat(width));
	}

	// DEBUGGING MATERIAL
	// public void setGrid(char[][] grid){
	// this.grid = grid;
	// }

}
