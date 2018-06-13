package chessProject;

import javafx.scene.image.Image;

public class Pawn extends chessPiece{
	
	private boolean longMove = true;
	private final int coefficient;

	public Pawn(int x, int y, boolean white) {
		super(x, y, white, chessBoard);
		charCode = white ? "\u2659" : "\u265F";
		coefficient = white ? 1 : -1;
		pieceImg = new Image(chessPiece.class.getResource(white ? "/WP.png" : "/BP.png").toExternalForm());
	}

	@Override
	protected boolean legalMove(int x, int y) {
		if (super.legalMove(x, y) && y - this.yCoordinate == coefficient && (chessBoard.getPiece(x, y) == null && this.xCoordinate == x
				|| Math.abs(this.xCoordinate - x) == 1 && chessBoard.getPiece(x, y) != null) || longMove && validLongMove(x,y) && chessBoard.getPiece(x, y) == null) {
			longMove = false;
			return true;
		}
		return false;
	}

	private boolean validLongMove(int x, int y) {
		if (this.xCoordinate == x && Math.abs(this.yCoordinate-y) == 2) {
			this.longMove = false;
			return true;
		}
		return false;
	}

	@Override
	protected void placeThreats(chessPiece[][] board) {
		for (int i = -1; i < 2; i+= 2 ) {
			if (this.xCoordinate + i >= 0 && this.xCoordinate + i <= 7) {
				chessBoard.updateThreatBoard(this.xCoordinate + i, this.yCoordinate + coefficient, this);
			}
		}
	}
	
	public boolean isLongMove() {
		return this.longMove;
	}
	
	public int getCoefficient() {
		return this.coefficient;
	}
}
