/*
 * Daren Wang
 * CS2336.501
 * 
 * Class contains attributes and methods to create a NotOthello game board.
 */
public class NotOthelloBoard extends GameBoard{
	private final char Player1 = 'B',
						Player2 = 'W';
	
	// Constructor - default
	public NotOthelloBoard() {
		createBoard();
	} // end of default Constructor
	
	// Constructor - takes user-defined board size
	public NotOthelloBoard(int boardSize) {
		super(boardSize);
		createBoard();
	} // end of Constructor with defined board size

	// Method creates a NotOthello game board
	protected void createBoard() {
		board = new char[boardSize][boardSize];
		
		// Board is initialized to blank spaces except for center 4 spaces tiled with B and W pieces
		for(int i = 0; i < boardSize; i++) {
			for(int j = 0; j < boardSize; j++) {
				if(i == (boardSize / 2) - 1 && j == (boardSize /2) - 1)
					board[i][j] = Player2;
				else if(i == (boardSize / 2) - 1 && j == boardSize /2)
					board[i][j] = Player1;
				else if(i == boardSize / 2 && j == (boardSize /2) - 1)
					board[i][j] = Player1;
				else if(i == boardSize / 2 && j == boardSize /2)
					board[i][j] = Player2;
				else
					board[i][j] = '-';
			} // end of column creation for() loop
		} // end of row creation for() loop
	} // end of createBoard() method
	
	// Method displays game board
	public void displayBoard() {
		for(int i = 0; i < boardSize; i++) {
			for(int j = 0; j < boardSize; j++) {
				System.out.print(board[i][j] + "|");
			} // end of column display for() loop
			System.out.println(String.valueOf(i)); // print row number
		} // end of row display for() loop
		// print column numbers
		for(int a = 0; a < boardSize; a++) {
			System.out.print(a % 10);
			if(a < boardSize - 1)
				System.out.print('|');
		} // end of column number display for() loop
		System.out.println();
		// print column number 10s digit if applicable, empty line otherwise
		for(int a = 0; a < boardSize; a++) {
			if(a == 0)
				System.out.print(" ");
			else if(a % 10 != 0)
				System.out.print("  ");
			else
				System.out.print(" " + a / 10);
		} // end of column 10s digit display for() loop
		System.out.println();
	} // end of displayBoard() method
	
	// Method updates board state with provided move
	public void updateBoard(int[] move, char player) {
		board[move[0]][move[1]] = player; // add piece to board
		// flip appropriate pieces
		// create list of adjacent spaces to flip: up, right, down, left
		int[][] adjacent = {{move[0] - 1, move[1]}, {move[0], move[1] + 1}, {move[0] + 1, move[1]}, {move[0], move[1] - 1}};
		for(int[] checkFlip: adjacent) {
			// skip empty and out-of-bound adjacent spaces
			if(checkFlip[0] < 0 || checkFlip[0] >= boardSize || checkFlip[1] < 0 || checkFlip[1] >= boardSize 
					|| spaceIsEmpty(checkFlip))
				continue;
			// skip lines that are not capped by same color piece
			else {
				if(checkFlip[1] == move[1]) {
					int increment = checkFlip[0] - move[0];
					int nextRow = checkFlip[0];
					boolean capped = false;
					
					do {
						nextRow += increment;
						if(nextRow < 0 || nextRow >= boardSize)
							break;
						if(board[nextRow][checkFlip[1]] == player)
							capped = true;
					} while(board[nextRow][checkFlip[1]] != player); // end check line do-while() loop
					if(!capped)
						continue;
				} // end of row flip if() statement
				else {
					int increment = checkFlip[1] - move[1];
					int nextCol = checkFlip[1];
					boolean capped = false;
					
					do {
						nextCol += increment;
						if(nextCol < 0 || nextCol >= boardSize)
							break;
						if(board[checkFlip[0]][nextCol] == player)
							capped = true;
					} while(board[checkFlip[0]][nextCol] != player); // end check line do-while() loop
					if(!capped)
						continue;
				} // end of column flip if() statement
			} // end of capped line check else statement
			if(checkFlip[1] == move[1]) {
				int increment = checkFlip[0] - move[0];
				int nextRow = checkFlip[0];
				
				do {
					board[nextRow][checkFlip[1]] = player;
					nextRow += increment;
					if(nextRow < 0 || nextRow >= boardSize)
						break;
				} while(board[nextRow][checkFlip[1]] != player); // end flip line do-while() loop
			} // end of row flip if() statement
			else {
				int increment = checkFlip[1] - move[1];
				int nextCol = checkFlip[1];
				
				do {
					board[checkFlip[0]][nextCol] = player;
					nextCol += increment;
					if(nextCol < 0 || nextCol >= boardSize)
						break;
				} while(board[checkFlip[0]][nextCol] != player); // end flip line do-while() loop
			} // end of column flip if() statement
		} // end of piece flip for() loop
		displayBoard(); // display updated board
		System.out.println(getScore() + "\n");// display current score
	} // end of updateBoard() method
	
	// Method checks if provided move is legal
	public boolean moveIsLegal(int[] move, char player) {
		// check if space is already occupied
		if(!spaceIsEmpty(move))
			return false;
		// create list of adjacent spaces to check: up, right, down, left
		int[][] adjacent = {{move[0] - 1, move[1]}, {move[0], move[1] + 1}, {move[0] + 1, move[1]}, {move[0], move[1] - 1}};
		int invalidAdjacent = 0; // count of invalid adjacent spaces
		
		for(int[] checkSpace: adjacent) {
			// check if space to be checked is in-bounds
			if(checkSpace[0] < 0 || checkSpace[0] >= boardSize || checkSpace[1] < 0 || checkSpace[1] >= boardSize) {
				invalidAdjacent++; // out-of-bounds counts as invalid
				continue;
			} // end of out-of-bounds check if() statement
			// check if space is occupied
			else if(!spaceIsEmpty(checkSpace)) {
				// check if occupied space is same color
				if(board[checkSpace[0]][checkSpace[1]] == player) {
					invalidAdjacent++;
					continue;
				} // end of same color check if() statement
				// if occupied adjacent space is opposing color, check for same color disc at end of line
				if(checkSpace[1] == move[1]) {
					int increment = checkSpace[0] - move[0];
					int nextRow = checkSpace[0];
					
					do {
						nextRow += increment;
						if(nextRow < 0 || nextRow >= boardSize) {
							invalidAdjacent++; // count as invalid space if line not capped by same color disc
							break;
						} // end of out-of-bounds if() statement
						int[] nextSpace = {nextRow, checkSpace[1]};
						if(spaceIsEmpty(nextSpace)) {
							invalidAdjacent++; // count as invalid space if line not capped by same color disc
							break;
						} // end of empty space at end of line if() statement
					} while(board[nextRow][checkSpace[1]] != player); // end of invalid line check do-while() loop
				} // end of row check if() statement
				else {
					int increment = checkSpace[1] - move[1];
					int nextCol = checkSpace[1];
					
					do {
						nextCol += increment;
						if(nextCol < 0 || nextCol >= boardSize) {
							invalidAdjacent++; // count as invalid space if line not capped by same color disc
							break;
						} // end of out-of-bounds if() statement
						int[] nextSpace = {checkSpace[0], nextCol};
						if(spaceIsEmpty(nextSpace)) {
							invalidAdjacent++; // count as invalid space if line not capped by same color disc
							break;
						} // end of empty space at end of line if() statement
					} while(board[checkSpace[0]][nextCol] != player); // end of invalid line check do-while() loop
				} // end of column check if() statement
			} // end of occupied space check if() statement
			// in-bounds, non-occupied spaces can only be empty
			else
				invalidAdjacent++;
		} // end of adjacent space check for() loop
		// check at least one adjacent space has opposite colored piece and line capped by same colored piece
		if(invalidAdjacent == 4)
			return false;
		else
			return true;
	} // end of moveIsLegal() method

	// Method checks if any legal moves remain on-board
	public boolean legalMovesRemain(char player) {
		for(int i = 0; i < boardSize; i++) {
			for(int j = 0; j < boardSize; j++) {
				int[] move = {i, j};
				if(!spaceIsEmpty(move)) // skip check if space occupied
					continue;
				else if(moveIsLegal(move, player)) // check if space is legal move
					return true;
			} // end of column check for() loop
		} // end of row check for() loop
		return false;
	} // end of legalMovesRemain() method
	
	// Method checks if requested space is empty
	public boolean spaceIsEmpty(int[] move) {
		if(board[move[0]][move[1]] == '-')
			return true;
		else
			return false;
	} // end of spaceIsEmpty() method
	
	// Method returns current score
	public String getScore() {
		int bScore = 0,
			wScore = 0;
		
		for(int i = 0; i < boardSize; i++) {
			for(int j = 0; j < boardSize; j++) {
				int[] space = {i, j}; 
				if(!spaceIsEmpty(space)) {
					if(board[i][j] == Player1)
						bScore++;
					else
						wScore++;
				} // end of space check if() statement
					
			} // end of row check for() loop
		} // end of column check for() loop
		
		return "Black: " + bScore + " |White: " + wScore;
	} // end of displayScore() method
	
} // end of NotOthelloBoard class
