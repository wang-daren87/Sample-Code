/*
 * Daren Wang
 * CS2336.501
 *
 * Class contains the main operating procedures for the game NotOthello.
 */
import java.util.Scanner;
public class NotOthello {

	private static final char Player1 = 'B',
							Player2 = 'W';
	
	public static void main(String[] args) {
		NotOthelloBoard board = new NotOthelloBoard(getBoardSize());
		int gameType = selectGameType();
		
		switch(gameType) {
			case 1: startGame1(board);
					break;
			case 2: startGame2(board);
					break;
			case 3: startGame3(board);
		} // end of game type switch() statement
		endGame(board);
	} // end of main() method

	// Method prompts player for board size.
	@SuppressWarnings("resource")
	public static int getBoardSize() {
		Scanner input = new Scanner(System.in);
		int size;
		
		do {
			System.out.println("Enter size of game board (>2): ");
			while(!input.hasNextInt()) {
				System.out.println("Invalid input, enter a number larger than 2.");
				input.next();
			} // end of number validation while() loop
			size = input.nextInt();
		} while(size <= 2); // end of input check do-while() loop
		
		return size;
	} // end of getBoardSize() method
	

	// Method prompts user for game type to play.
	@SuppressWarnings("resource")
	public static int selectGameType() {
		Scanner input = new Scanner(System.in);
		int type;
		
		System.out.println("Game Modes:\n1) Player vs. Comp\n2) Player vs. Player\n3) Comp vs. Comp");
		do {
			System.out.println("Select game type:");
			while(!input.hasNextInt()) {
				System.out.println("Invalid input, please choose from selections provided.");
				input.next();
			} // end of number validation while() loop
			type = input.nextInt();
		} while(type <= 0 || type > 3); // end of input check do-while() loop
		
		return type;
	} // end of selectGameType() method
	
	// Method contains game play procedures for game mode 1, player vs computer.
	public static void startGame1(NotOthelloBoard board) {
		char player = Player1; // set Black to play first
		int[] move;
		board.displayBoard(); // display initial board state
		// game play continues while legal moves remain on-board
		while(board.legalMovesRemain(player)) {
			System.out.println("Black to move.");
			move = playerMove(board); // get player move
			// check if move is legal
			while(!board.moveIsLegal(move, player)) {
				System.out.println("Illegal move!");
				board.displayBoard();
				move = playerMove(board);
			} // end of move validation while() loop
			board.updateBoard(move, player);// update board with move
			player = Player2;
			// check if computer has move, make move if available
			if(board.legalMovesRemain(player)) {
				System.out.println("White to move.");
				move = computerMove(board, player);
				board.updateBoard(move, player);
				player = Player1;
			} // end computer move if() statement
		} // end of game play while() loop
	} // end of startGame1() method
	
	// Method contains game play procedures for game mode 2, player vs player.
	public static void startGame2(NotOthelloBoard board) {
		char player = Player1; // set Black to play first
		int[] move;
		board.displayBoard(); // display initial board state
		// game play continues while legal moves remain on-board
		while(board.legalMovesRemain(player)) {
			if(player == Player1)
				System.out.println("Black to move");
			else
				System.out.println("White to move");
			move = playerMove(board); // get player move
			// check if move is legal
			while(!board.moveIsLegal(move, player)) {
				System.out.println("Illegal move!");
				board.displayBoard();
				move = playerMove(board);
			} // end of move validation while() loop
			board.updateBoard(move, player); // add piece to board
			// change player
			if(player == Player1)
				player = Player2;
			else
				player = Player1;
		} // end of game play while() loop
	} // end of startGame2() method
	
	public static void startGame3(NotOthelloBoard board) {
		char player = Player1; // set Black to play first
		int[] move;
		board.displayBoard(); // display initial board state
		// game play continues while legal moves remain on-board
		while(board.legalMovesRemain(player)) {
			System.out.println("Black to move.");
			move = computerMove(board, player);
			board.updateBoard(move, player);
			player = Player2;
			if(board.legalMovesRemain(player)) {
				System.out.println("White to move.");
				move = computerMove(board, player);
				board.updateBoard(move, player);
				player = Player1;
			} // end white move if() statement
		} // end of game play while() loop
	} // end of startGame3() method
	
	// Method contains end of game procedures.
	public static void endGame(NotOthelloBoard board) {
		String[] finalScore = board.getScore().split(" ");
		// compare final scores to determine winner
		if(Integer.parseInt(finalScore[1]) > Integer.parseInt(finalScore[3]))
			System.out.println("Black wins!");
		else if(Integer.parseInt(finalScore[3]) > Integer.parseInt(finalScore[1]))
			System.out.println("White wins!");
		else
			System.out.println("It's a tie!");
		System.out.println("Game Over. Thank you for playing.");
	} // end of endGame() method
	
	// Method prompts player for move
	@SuppressWarnings("resource")
	public static int[] playerMove(NotOthelloBoard board) {
		Scanner input = new Scanner(System.in);
		int coord;
		int[] move = new int[2];
		
		System.out.println("Make a move");
		do {
			System.out.print("Row: ");
			while(!input.hasNextInt()) {
				System.out.println("Invalid input, must be a number on the board.");
				input.next();
			} // end of integer validation while() loop
			coord = input.nextInt();
		} while(coord < 0 || coord >= board.getBoardSize()); // end of row input do-while() loop
		move[0] = coord;
		
		do {
			System.out.print("Column: ");
			while(!input.hasNextInt()) {
				System.out.println("Invalid input, must be a number on the board.");
				input.next();
			} // end of integer validation while() loop
			coord = input.nextInt();
		} while(coord < 0 || coord >= board.getBoardSize()); // end of column input do-while() loop
		move[1] = coord;
		
		return move;
	} // end of getMove() method
	
	// Method determines computer move
	public static int[] computerMove(NotOthelloBoard board, char player) {
		int[] move = new int[2];
		
		do {
			move[0] = (int) (Math.random() * board.getBoardSize());
			move[1] = (int) (Math.random() * board.getBoardSize());
		} while(!board.moveIsLegal(move, player)); // end of computer move validation do-while() loop
		System.out.println("Computer move at Row: " + move[0] + "|Column: " + move[1]);
		return move;
	} // end of computerMove() method

} // end of NotOthello class
