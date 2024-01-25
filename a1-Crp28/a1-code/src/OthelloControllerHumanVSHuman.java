/**
 * This class creates an Othello game between two Human players.
 *
 */
public class OthelloControllerHumanVSHuman {

	protected Othello othello;
	PlayerHuman player1, player2;

	/**
	 * Constructs a new OthelloController with a new Othello game, ready to play
	 * with two users at the console.
	 */
	public OthelloControllerHumanVSHuman() {
		
		this.othello = new Othello();
		this.player1 = new PlayerHuman(this.othello, OthelloBoard.P1);
		this.player2 = new PlayerHuman(this.othello, OthelloBoard.P2);
	}
	
	/**
	 * The method that controls the game: determines whose turn it is to move,
	 * make the move, and show when will the game end.
	 * 
	 */
	public void play() {
		
		while (!othello.isGameOver()) {
			this.report();

			Move move = null;
			char whosTurn = othello.getWhoseTurn();

			if (whosTurn == OthelloBoard.P1)
				move = player1.getMove();
			if (whosTurn == OthelloBoard.P2)
				move = player2.getMove();

			this.reportMove(whosTurn, move);
			othello.move(move.getRow(), move.getCol());
		}
		this.reportFinal();
	}
	
	/**
	 * Prints out which player maked the move at which location.
	 * 
	 * @param whosTurn character representing the player who took the turn
	 * @param move a move performed by a player at a specific location on board
	 */
	private void reportMove(char whosTurn, Move move) {
		System.out.println(whosTurn + " makes move " + move + "\n");
	}
	
	/**
	 * Reports number of tokens controlled by each player and who moves next.
	 * 
	 */
	private void report() {
		
		String s = othello.getBoardString() + OthelloBoard.P1 + ":" 
				+ othello.getCount(OthelloBoard.P1) + " "
				+ OthelloBoard.P2 + ":" + othello.getCount(OthelloBoard.P2) + "  " 
				+ othello.getWhoseTurn() + " moves next";
		System.out.println(s);
	}
	
	/**
	 * Reports number of tokens controlled by each player and the winner of this Othello game.
	 * 
	 */
	private void reportFinal() {
		
		String s = othello.getBoardString() + OthelloBoard.P1 + ":" 
				+ othello.getCount(OthelloBoard.P1) + " "
				+ OthelloBoard.P2 + ":" + othello.getCount(OthelloBoard.P2) 
				+ "  " + othello.getWinner() + " won\n";
		System.out.println(s);
	}
	
	/**
	 * Run main to play two Humans against each other at the console.
	 * @param args
	 */
	public static void main(String[] args) {
		
		OthelloControllerHumanVSHuman oc = new OthelloControllerHumanVSHuman();
		oc.play();
	}

}
