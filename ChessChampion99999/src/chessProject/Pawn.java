package chessProject;

import java.util.ArrayList;
import java.util.Collection;

import javafx.scene.image.Image;

public class Pawn extends chessPiece{
	
	//private boolean longMove = true;
	private final int coefficient;

	public Pawn(int x, int y, boolean white) {
		super(x, y, white, chessBoard);
		charCode = white ? "\u2659" : "\u265F";
		coefficient = white ? 1 : -1;
		pieceImg = new Image(chessPiece.class.getResource(white ? "/WP.png" : "/BP.png").toExternalForm());
	}

	@Override
	protected boolean legalMove(int x, int y) {
		return (super.legalMove(x, y) && y - this.yCoordinate == coefficient && (chessBoard.getPiece(x, y) == null && this.xCoordinate == x
				|| Math.abs(this.xCoordinate - x) == 1 && chessBoard.getPiece(x, y) != null) || 
				this.yCoordinate == (white ? 1 : 6) && chessBoard.getPiece(x, y) == null
				&& Math.abs(this.yCoordinate - y) == 2); 
	}
	
	private void placePawnMoves() {
		if (chessBoard.getBoard()[xCoordinate][yCoordinate + coefficient] == null) {
			Collection<int[]> coordinates = new ArrayList<>();
			coordinates.add(new int[] {xCoordinate, yCoordinate + coefficient});
			
			if (this.yCoordinate == (white ? 1 : 6) && chessBoard.getBoard()[xCoordinate][yCoordinate + 2*coefficient] == null) {
				coordinates.add(new int[] {xCoordinate, yCoordinate + 2*coefficient});
			}
			chessBoard.updatePawnMoves(this, coordinates);
		}
	}

	@Override
	protected void placeThreats(chessPiece[][] board) {
		for (int i = -1; i < 2; i+= 2 ) {
			if (this.xCoordinate + i >= 0 && this.xCoordinate + i <= 7) {
				chessBoard.updateThreatBoard(this.xCoordinate + i, this.yCoordinate + coefficient, this);
			}
		}
		placePawnMoves();
	}
	
	
	public int getCoefficient() {
		return this.coefficient;
	}
}
