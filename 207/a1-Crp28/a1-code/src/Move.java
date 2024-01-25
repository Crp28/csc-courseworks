/***
 * A class to represent a token placement (move) on a game board
 * based on the placement's row and column position.
 */
public class Move {
 
	private int row, col;

	/**
	 * Constructs a move (token placement) that knows its row, col
	 * position.
	 * 
	 * @param row	integer representing the row this move takes place at
	 * @param col	integer representing the col this move takes place at
	 */
	public Move(int row, int col) {
		this.row = row;
		this.col = col;
	}

	/**
	 * Returns the row of this move.
	 * 
	 * @return row number of this move
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Returns the column of this move.
	 * 
	 * @return column number of this move
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Returns string representation of this move in the form "(row, col)".
	 * 
	 * @return a string in the form of "(row, col)" stating this move's position
	 */
	public String toString() {
		return "(" + this.row + "," + this.col + ")";
	}
}