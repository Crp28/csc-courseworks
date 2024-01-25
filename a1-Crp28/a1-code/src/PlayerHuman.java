import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A Human controlled player playing Othello.
 * 
 */
public class PlayerHuman {
	
	private static final String INVALID_INPUT_MESSAGE = "Invalid number, please enter 1-8";
	private static final String IO_ERROR_MESSAGE = "I/O Error";
	private static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

	private Othello othello;
	private char player;

	/**
	 * Constructs a new Human player controlling a token in a Othello game.
	 * @param othello a Othello game the player is playing
	 * @param player the token controlled by the player
	 */
	public PlayerHuman(Othello othello, char player) {
		
		this.othello = othello;
		this.player = player;
	}

	/**
	 * Returns the coordinates of the last move made by player
	 * 
	 * @return Move class representing the last move by player
	 */
	public Move getMove() {
		
		int row = getMove("row: ");
		int col = getMove("col: ");
		return new Move(row, col);
	}
	/**
	 * Helper method for public method getMove().
	 * 
	 * Returns the column/row of the previous move base on the given message.
	 * 
	 * @param message string indicates row or col
	 * @return integer indicating the row/col coordinate
	 */
	private int getMove(String message) {
		
		int move, lower = 0, upper = 7;
		while (true) {
			try {
				System.out.print(message);
				String line = PlayerHuman.stdin.readLine();
				move = Integer.parseInt(line);
				if (lower <= move && move <= upper) {
					return move;
				} else {
					System.out.println(INVALID_INPUT_MESSAGE);
				}
			} catch (IOException e) {
				System.out.println(INVALID_INPUT_MESSAGE);
				break;
			} catch (NumberFormatException e) {
				System.out.println(INVALID_INPUT_MESSAGE);
			}
		}
		return -1;
	}
}
