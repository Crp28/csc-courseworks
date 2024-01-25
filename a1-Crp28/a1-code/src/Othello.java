import java.util.Random;

/**
 * An Othello game class consisting of a game board, and keeping track of which player's
 * turn it currently is and some statistics about the game (e.g. how many tokens each player
 * has). It knows who the winner of the game is, and when the game is
 * over.
 */
public class Othello {
	public static final int DIMENSION = 8; // This is an 8x8 game
	private OthelloBoard board = new OthelloBoard(Othello.DIMENSION); // The main game board

	private char whoseTurn = OthelloBoard.P1; // P1 moves first!
	private int numMoves = 0;

	/**
	 * Returns the character for P1, P2 or EMPTY depending on who moves next.
	 * 
	 * @return the character for P1, P2 or EMPTY
	 */
	public char getWhoseTurn() {
		return this.whoseTurn;
	}

	/**
	 * Returns the number of tokens on this board for the given player.
	 * 
	 * @param character representing player P1 or P2
	 * @return the number of tokens on this board for the given player 
	 */
	public int getCount(char player) {
		return board.getCount(player);
	}
	
	/**
	 * Attempts to make a move for P1 or P2 (depending on whose turn it is) at
	 * position row, col. Returns true if the move was successfully made.
	 * 
	 * @param row integer representing the row 
	 * @param col integer representing the column
	 * @return true if the move was successfully made, false otherwise
	 */
	public boolean move(int row, int col) {
		// TODO: Complete this method (Task 2.1)
		// Hint: Use existing methods (e.g. from OthelloBoard class) as helpers here
		//		 This method should also modify whoseTurn it is to be the next
		//			player or EMPTY if no more moves are possible (there is a method
		//			in OthelloBoard that can help you determine this)
		//		 This method also updates the numMoves counter if the move was successful
		boolean valid = board.move(row, col, getWhoseTurn());
		if (valid == true) {
			if (board.hasMove()==OthelloBoard.EMPTY) {
				this.whoseTurn = OthelloBoard.EMPTY;
			} else if (board.hasMove()==OthelloBoard.P1) {
				this.whoseTurn = OthelloBoard.P1;
			} else if (board.hasMove()==OthelloBoard.P2) {
				this.whoseTurn = OthelloBoard.P2;
			} else {
				this.whoseTurn = OthelloBoard.otherPlayer(this.whoseTurn);
			}
			this.numMoves++;
		}
		return valid;
	}
	

	/**
	 * Returns the winner of the game if the game is over, or the board token
	 * for EMPTY if the game is not yet finished. As per Othello's rules, the
	 * winner of the game is the player with the most tokens on the board at
	 * the end of the game.
	 * 
	 * @return the board token for the winning player (P1 or P2) or the token 
	 * 			for EMPTY if there is no winner or the game is not finished.
	 */
	public char getWinner() {
		// TODO: Complete this method (Task 2.2)
		// Hint: Use existing methods as helpers here
		//		 Use OthelloBoard.P1, OthelloBoard.P2, etc. to access the board tokens
		if (board.hasMove()==OthelloBoard.EMPTY) {
			if (board.getCount(OthelloBoard.P1)>board.getCount(OthelloBoard.P2)) {
				return OthelloBoard.P1;
			} else if (board.getCount(OthelloBoard.P1)<board.getCount(OthelloBoard.P2)) {
				return OthelloBoard.P2;
			}
		}
		return OthelloBoard.EMPTY;
	}

	/**
	 * Returns true iff the game is over (no player can move next).
	 * 
	 * @return true if the game is over, false otherwise
	 */
	// NOTE: This method is already done for you. DO NOT MODIFY THIS!!
	public boolean isGameOver() {
		return this.whoseTurn==OthelloBoard.EMPTY;
	}

	/**
	 * Returns string representation of this board.
	 * 
	 * @return a string representation of this board
	 */
	// NOTE: This method is already done for you. DO NOT MODIFY THIS!!
	public String getBoardString() {
		return this.board.toString()+"\n";
	}

	/**
	 * Main function which creates and runs a random Othello game.
	 */
	// DO NOT MODIFY THIS!!
	// Run this to test the current class. We play a completely random game, but
	// the output will be of similar structure to the text in gameOthelloSampleA.txt
	// and gameOthelloSampleB.txt, as mentioned in the README file
	public static void main(String[] args) {
		
		Random rand = new Random();

		Othello o = new Othello();
		System.out.println(o.getBoardString());
		while (!o.isGameOver()) {
			int row = rand.nextInt(8);
			int col = rand.nextInt(8);

			if (o.move(row, col)) {
				System.out.println("makes move (" + row + "," + col + ")");
				System.out.println(o.getBoardString() + o.getWhoseTurn() + " moves next");
			}
		}

	}
}