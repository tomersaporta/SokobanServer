package model.sokobanSolver.searchable;

/**
 *Define Sokoban state.
 *Contains the level board.
 *Implements toString, hashCode, equals according to the game defenitions 
 */
public class SokobanState {

	private char[][]board;

	public SokobanState(char[][] board) {
		this.board = board;
	}
	
	
	@Override
	public String toString() {
		String str = "";
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				str += board[i][j];
			}
			str += "\n";
		}
		return str;	
	}
	
	@Override
	public int hashCode() {		
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {	
		SokobanState state = (SokobanState)obj;		
		
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				if (board[i][j] != state.board[i][j])
					return false;
			}			
		}
		
		return true;
	}
	
}
