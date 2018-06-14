package chessProject;

import java.util.ArrayList;
import java.util.Collection;

import javafx.scene.image.Image;

public class Pawn extends chessPiece{
	private final int coefficient;
	private boolean justMovedLong = false;

	public Pawn(int x, int y, boolean white) {
		super(x, y, white, chessBoard);
		charCode = white ? "\u2659" : "\u265F";
		coefficient = white ? 1 : -1;
		charRepresentation = "P";
		instance = (p -> p instanceof Pawn);
		pieceImg = new Image(chessPiece.class.getResource(white ? "/WP.png" : "/BP.png").toExternalForm());
	}

	@Override
	protected boolean legalMove(int x, int y) {
		if (super.legalMove(x, y) && (y - this.yCoordinate == coefficient && (chessBoard.getPiece(x, y) == null && this.xCoordinate == x
				|| Math.abs(this.xCoordinate - x) == 1 && chessBoard.getPiece(x, y) != null ||
				chessBoard.getPiece(x,yCoordinate) instanceof Pawn && chessBoard.getPiece(x,yCoordinate).white !=
				this.white && ((Pawn)chessBoard.getPiece(x,yCoordinate)).getJustMovedLong()) || 
				this.yCoordinate == (white ? 1 : 6) && chessBoard.getPiece(x, y) == null
				&& Math.abs(this.yCoordinate - y) == 2)) {
			
			justMovedLong = (Math.abs(this.yCoordinate - y) == 2);
			return true;
		}
		return false;
	}
	
	private void placePawnMoves() {
		Collection<int[]> coordinates = new ArrayList<>();
		if (chessBoard.getBoard()[xCoordinate][yCoordinate + coefficient] == null) {
			coordinates.add(new int[] {xCoordinate, yCoordinate + coefficient});
			
			if (this.yCoordinate == (white ? 1 : 6) && chessBoard.getBoard()[xCoordinate][yCoordinate + 2*coefficient] == null) {
				coordinates.add(new int[] {xCoordinate, yCoordinate + 2*coefficient});
			}
		}
		for (int x = -1; x < 2; x+= 2 ) {
			if (this.xCoordinate + x >= 0 && this.xCoordinate + x <= 7) {
				chessPiece targetPiece = chessBoard.getPiece(this.xCoordinate + x,yCoordinate);
				if (targetPiece instanceof Pawn && ((Pawn)targetPiece).getJustMovedLong())  {
					coordinates.add(new int[] {this.xCoordinate + x, yCoordinate + coefficient});
				}	
			}
		}
		chessBoard.updatePawnMoves(this, coordinates);
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
	
	public void setJustMovedFalse() {
		justMovedLong = false;
	}
	
	public boolean getJustMovedLong() {
		return justMovedLong;
	}
	
	public int getCoefficient() {
		return this.coefficient;
	}
}
