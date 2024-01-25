/**
 * This class keeps track of all of the tokens on the board. 
 * 
 * It knows what the board looks like at the start of the game, what the players 
 * tokens look like ('X' and 'O'), whether given coordinates are on the board, 
 * whether either of the players have a move somewhere on the board, and what happens 
 * when a player makes a move at a specific location (the opposite players 
 * tokens are flipped).
 */
// Othello makes use of the OthelloBoard
public class OthelloBoard {
	
	public static final char EMPTY = ' ', P1 = 'X', P2 = 'O', BOTH = 'B';
	private int size = 8;
	private char[][] board;

	/**
	 * Constructs an empty Othello board with the given size as its width
	 * and height. Places two player one and two player two tokens
	 * in the middle of the board.
	 * 
	 * @param size	this board's width and height
	 */
	public OthelloBoard(int size) {
		this.size = size;
		board = new char[this.size][this.size];
		for (int row = 0; row < this.size; row++) {
			for (int col = 0; col < this.size; col++) {
				this.board[row][col] = EMPTY;
			}
		}
		int mid = this.size / 2;
		this.board[mid - 1][mid - 1] = this.board[mid][mid] = P1;
		this.board[mid][mid - 1] = this.board[mid - 1][mid] = P2;
	}

	/**
	 * Sets the given position on the board to be the given player (or empty) token.
	 * 
	 * @param row  integer representing a row on this board
	 * @param col  integer representing a column on this board
	 * @param token  character for P1, P2 or EMPTY
	 */
	public void setToken(int row, int col, char token) {
		this.board[row][col] = token;
	}
	
	/**
	 * Returns the dimensions of this board.
	 * 
	 * @return this board's width/height
	 */
	public int getDimension() {
		return this.size;
	}

	/**
	 * Given one player, returns the other player. If the given player
	 * is invalid, returns the character representing empty.
	 * 
	 * @param player  character representing a player - should be either P1 or P2
	 * @return P2 or P1, the opposite of the given player, or empty is the given
	 * 			player character was invalid
	 */
	public static char otherPlayer(char player) {
		if (player == P1) {
			return P2;
		}
		if (player == P2) {
			return P1;
		}
		return EMPTY;
	}

	/**
	 * Returns the player token that is in the given position, or the empty
	 * character if no token is there or if the position provided is invalid.
	 * 
	 * @param row  integer representing a row on this board
	 * @param col  integer representing a column on this board
	 * @return character representing P1,P2 or EMPTY
	 */
	public char getToken(int row, int col) {
		if (this.validCoordinate(row, col)) {
			return this.board[row][col];
		}
		return EMPTY;	
	}
	
	/**
	 * Returns true iff the provided coordinates are valid (exists on the board).
	 * 
	 * @param row  integer representing a row on this board
	 * @param col  integer representing a column on this board
	 * @return whether (row, col) is a position on the board. Example: (6,12) is not
	 *         a valid position on an 8x8 board.
	 */
	private boolean validCoordinate(int row, int col) {
		return 0<=row && 0<=col && row<this.size && col<this.size;

	}

	/**
	 * Returns the number of tokens on the board for the given player.
	 * 
	 * @param player P1 or P2
	 * @return the number of tokens on the board for the given player
	 */
	public int getCount(char player) {
		int count=0;
		for (int row = 0; row < this.size; row++) {
			for (int col = 0; col < this.size; col++) {
				if (this.board[row][col] == player) {
					count++;
				}
			}
		}
		return count;
	}
	
	/**
	 * Given a row and column, and a direction drow and dcol, check if there is
	 * a pattern of one player token appearing right after one or more tokens of
	 * the other player, starting from the given (row, col) position and moving
	 * toward the given direction (i.e. adding drow to the starting row, and dcol to
	 * the starting col). 
	 * 
	 * If you encounter a sequence of one or more P1 tokens followed by a P2
	 * in this line starting at (row, col) in the direction (drow, dcol), then return
	 * P2. If you encounter a sequence of one or more P2 tokens followed by a P1,
	 * return P1.
	 * 
	 * If no such sequence occurs in this line, return EMPTY.
	 * 
	 * @param row  the row to begin searching for the pattern from
	 * @param col  the column to begin searching for the pattern from
	 * @param drow the row direction which is either -1 (up on the board), 
	 * 				1 (down on the board), or 0 (no change in row)
	 * @param dcol the col direction which is either -1 (left on the board), 
	 * 				1 (right on the board), or 0 (no change in column)
	 * @return P1, if there is the pattern P2 ...P2 P1, or P2 if there is the
	 *         pattern P1 ... P1 P2 in the given direction, EMPTY if there is no
	 *         such pattern
	 */
	private char checkForPattern(int row, int col, int drow, int dcol) {
		if (!this.validCoordinate(row, col)) {
			return EMPTY;
		}
		if (drow==0 && dcol==0) {
			return EMPTY;
		}
		char init=this.board[row][col];
		while (this.validCoordinate(row, col)) {
			if (this.board[row][col] == EMPTY) {
				return EMPTY;
			} else if (this.board[row][col] == otherPlayer(init)) {
				return this.board[row][col];
			}
			row+=drow;
			col+=dcol;
		}
		return EMPTY;
	}

	/**
	 * Flips all other player tokens to player, starting at (row,col) in direction
	 * (drow, dcol). Example: If (drow,dcol)=(0,1) and player==O then XXXO will
	 * result in a flip to OOOO. Returns the number of tokens actually flipped,
	 * or -1 if this was not a valid move.
	 * 
	 * @param row  the row to begin flipping from
	 * @param col  the column to begin flipping from
	 * @param drow the row direction which is either -1 (up on the board), 
	 * 				1 (down on the board), or 0 (no change in row)
	 * @param dcol the col direction which is either -1 (left on the board), 
	 * 				1 (right on the board), or 0 (no change in column)
	 * @param player the character representing the target player token to
	 * 					flip to, either P1 or P2
	 * @return the number of other player tokens actually flipped; -1 if this is not
	 *         a valid move in this one direction (i.e. EMPTY or the end of the
	 *         board is reached before seeing a player token)
	 */
	private int flip(int row, int col, int drow, int dcol, char player) {
		int count = 0;
		if (this.checkForPattern(row, col, drow, dcol) == player) {
			int curr_row = row;
			int curr_col = col;
			while (this.getToken(curr_row, curr_col) == otherPlayer(player)) {
				count++;
				this.board[curr_row][curr_col]=player;
				curr_row+=drow;
				curr_col+=dcol;
			}
		} else if (this.getToken(row, col)==player) {
			return count;
		}
		else {
			count = -1;
		}
		return count;
	}

	/**
	 * Makes a move for player at position (row,col) according to Othello rules,
	 * making appropriate modifications to the board. That is, in ALL directions
	 * from the starting (row, col) position, appropriate flips will occur to 
	 * make affected tokens change to be the current player token.
	 * 
	 * Nothing is changed on the board if the given move is not valid (i.e. the
	 * coordinate is not on the board, or the (row, col) position is not empty, or
	 * if no flips occurred).
	 * 
	 * Returns true iff the move was valid and false otherwise. 
	 * 
	 * @param row  the row to make a move at
	 * @param col  the column to make a move at
	 * @param player the player making this move (either P1 or P2)
	 * @return true if player makes a mark successfully at (row, col), false otherwise
	 */
	public boolean move(int row, int col, char player) {
		if (!this.validCoordinate(row, col)) {
			return false;
		} else if (this.getToken(row, col) != EMPTY) {
			return false;
		}
		boolean valid = false;
		for (int drow = -1; drow <= 1; drow++) {
			for (int dcol = -1; dcol <= 1; dcol++) {
				if (this.flip(row+drow, col+dcol, drow, dcol, player)>0) {
					valid = true;
				}
			}
		}
		if (valid) {
			this.setToken(row, col, player);
		}
		return valid;
	}

	

	/**
	 * Returns which player, if any, can make a valid move at (row, col) 
	 * based on the pattern that appears from (row, col) in the direction (drow, dcol).
	 * 
	 * @param row  the row to check for valid move
	 * @param col  the column to check for valid move
	 * @param drow the row direction which is either -1 (up on the board), 
	 * 				1 (down on the board), or 0 (no change in row)
	 * @param dcol the col direction which is either -1 (left on the board), 
	 * 				1 (right on the board), or 0 (no change in column)
	 * @return the player that can make a valid move (P1, P2, or EMPTY if neither)
	 */
	// NOTE: This method is already completed for you. You do NOT need to modify this!

	// Helper function for the public hasMove method
	private char hasMove(int row, int col, int drow, int dcol) {
		if (!this.validCoordinate(row, col) || this.getToken(row, col) != EMPTY)
			return EMPTY;
		return this.checkForPattern(row + drow, col + dcol, drow, dcol);
	}

	/**
	 * Returns the player token that has a valid move somewhere on the board. 
	 * Return the character representing BOTH if both players have such
	 * a move, and EMPTY if neither do.
	 * 
	 * @return the character representing the player that has a move somewhere on the board, 
	 * 			or the one representing BOTH if both players do, and EMPTY if neither do
	 */
	// NOTE: This method is already completed for you. You do NOT need to modify this!
	public char hasMove() {
		char retVal = EMPTY;
		for (int row = 0; row < this.size; row++) {
			for (int col = 0; col < this.size; col++) {
				for (int drow = -1; drow <= 1; drow++) {
					for (int dcol = -1; dcol <= 1; dcol++) {
						if (drow == 0 && dcol == 0)
							continue;
						char p = this.hasMove(row, col, drow, dcol);
						if (p == P1 && retVal == P2)
							return BOTH;
						if (p == P2 && retVal == P1)
							return BOTH;
						if (retVal == EMPTY)
							retVal = p;
					}
				}
			}
		}
		return retVal;
	}

	/**
	 * Returns a string representation of this game board.
	 * 
	 * @return a string representation of this, just the play area, with no
	 *         additional information.
	 */
	// DO NOT MODIFY THIS!! DOING SO MAY CAUSE OUR AUTOTESTS TO FAIL
	// WHICH WOULD LEAD TO A SIGNIFICANT LOSS OF CORRECTNESS MARKS
	public String toString() {
		/**
		 * See assignment web page for sample output.
		 */
		String s = "";
		s += "  ";
		for (int col = 0; col < this.size; col++) {
			s += col + " ";
		}
		s += '\n';

		s += " +";
		for (int col = 0; col < this.size; col++) {
			s += "-+";
		}
		s += '\n';

		for (int row = 0; row < this.size; row++) {
			s += row + "|";
			for (int col = 0; col < this.size; col++) {
				s += this.board[row][col] + "|";
			}
			s += row + "\n";

			s += " +";
			for (int col = 0; col < this.size; col++) {
				s += "-+";
			}
			s += '\n';
		}
		s += "  ";
		for (int col = 0; col < this.size; col++) {
			s += col + " ";
		}
		s += '\n';
		return s;
	}

	/**
	 * A quick test of OthelloBoard.
	 * 
	 * @param args
	 */
	// The expected output is in othelloBoardOutput.txt, as mentioned in the README file
	public static void main(String[] args) {
		
		OthelloBoard ob = new OthelloBoard(8);
		System.out.println(ob.toString());
		System.out.println("getCount(P1)=" + ob.getCount(P1));
		System.out.println("getCount(P2)=" + ob.getCount(P2));
		for (int row = 0; row < ob.size; row++) {
			for (int col = 0; col < ob.size; col++) {
				ob.board[row][col] = P1;
			}
		}
		System.out.println(ob.toString());
		System.out.println("getCount(P1)=" + ob.getCount(P1));
		System.out.println("getCount(P2)=" + ob.getCount(P2));

		// Should all be blank
		for (int drow = -1; drow <= 1; drow++) {
			for (int dcol = -1; dcol <= 1; dcol++) {
				System.out.println("checkForPattern=" + ob.checkForPattern(4, 4, drow, dcol));
			}
		}

		for (int row = 0; row < ob.size; row++) {
			for (int col = 0; col < ob.size; col++) {
				if (row == 0 || col == 0) {
					ob.board[row][col] = P2;
				}
			}
		}
		System.out.println(ob.toString());

		// Should all be P2 ('O') except drow=0,dcol=0
		for (int drow = -1; drow <= 1; drow++) {
			for (int dcol = -1; dcol <= 1; dcol++) {
				System.out.println("direction=(" + drow + "," + dcol + ")");
				System.out.println("checkForPattern=" + ob.checkForPattern(4, 4, drow, dcol));
			}
		}

		// Can't move to (4,4) since the square is not empty
		System.out.println("Trying to move to (4,4) move=" + ob.move(4, 4, P2));

		ob.board[4][4] = EMPTY;
		ob.board[2][4] = EMPTY;
		

		System.out.println(ob.toString());

		for (int drow = -1; drow <= 1; drow++) {
			for (int dcol = -1; dcol <= 1; dcol++) {
				System.out.println("direction=(" + drow + "," + dcol + ")");
				System.out.println("hasMove at (4,4) in above direction =" + ob.hasMove(4, 4, drow, dcol));
			}
		}
		System.out.println("who has a move=" + ob.hasMove());
		System.out.println("Trying to move to (4,4) move=" + ob.move(4, 4, P2));
		System.out.println(ob.toString());

		
		System.out.println("Testing flip method: before");
		ob = new OthelloBoard(Othello.DIMENSION);
		for(int row=0;row<Othello.DIMENSION;row++) {
			for(int col=0;col<Othello.DIMENSION;col++) {
				if(row>col)ob.board[row][col]=EMPTY;
				else ob.board[row][col]=P2;
				if(col==Othello.DIMENSION-1)ob.board[row][col]=P1;
			}
		}
		ob.board[0][7]=EMPTY;
		ob.board[1][7]=P2;
		ob.board[4][4]=P1;
		System.out.println(ob.toString());

		System.out.println("Testing flip method: flipping");
		for(int row=0;row<Othello.DIMENSION;row++) {
			int num=ob.flip(row, 4, 0,1, P1);
			System.out.println("flip("+row+",4,0,1, P1)="+num);
		}
		System.out.println("Testing flip method: after");
		System.out.println(ob.toString());
		
		System.out.println("Testing checkForPattern:");
		for(int row=0;row<Othello.DIMENSION;row++) {
			char result=ob.checkForPattern(row, row, 0,1);
			System.out.println("checkForPattern("+row+","+row+",0,1)="+result);
		}
		for(int row=0;row<Othello.DIMENSION;row++) {
			char result=ob.checkForPattern(row, row-1, 0,1);
			System.out.println("checkForPattern("+row+","+(row-1)+",0,1)="+result);
		}
		
		System.out.println("Testing hasMove:");
		for(int row=0;row<Othello.DIMENSION;row++) {
			char result=ob.hasMove(row, row, 0,1);
			System.out.println("hasMove("+row+","+row+",0,1)="+result);
		}
		for(int row=0;row<Othello.DIMENSION;row++) {
			char result=ob.hasMove(row, row-1, 0,1);
			System.out.println("hasMove("+row+","+(row-1)+",0,1)="+result);
		}
	}
}
