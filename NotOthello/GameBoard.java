/*
 * Daren Wang
 * CS2336.501
 * 
 * Class contains attributes and methods associated with a generic game board.
 */
public abstract class GameBoard {
	protected int boardSize;
	protected char[][] board;
	
	// Constructor - default
	public GameBoard() {
		setBoardSize(4);
		createBoard();
	} // end of default Constructor
	
	// Constructor - takes user-defined board size
	public GameBoard(int boardSize) {
		setBoardSize(boardSize);
		createBoard();
	} // end of Constructor with defined board size

	// Getter for boardSize attribute
	public int getBoardSize() {
		return boardSize;
	} // end of getBoardSize() method
	
	// Setter for boardSize attribute
	protected void setBoardSize(int boardSize) {
		this.boardSize = boardSize;
	} // end of setBoardSize() method
	
	// Method creates a game board
	protected abstract void createBoard();
	
	// Method displays a game board
	public abstract void displayBoard();
	
	// Method returns score
	public abstract String getScore();
		
} // end of GameBoard() class
