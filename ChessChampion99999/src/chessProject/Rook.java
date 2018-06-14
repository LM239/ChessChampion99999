package chessProject;

import javafx.scene.image.Image;

public class Rook extends chessPiece{
	private boolean hasMoved = false;

	public Rook(int x, int y, boolean white) {
		super(x, y, white, chessBoard);
		charCode = white ? "\u2656" : "\u265C";
		charRepresentation = "R";
		instance = (p -> p instanceof Rook);
		pieceImg = new Image(chessPiece.class.getResource(white ? "/WR.png" : "/BR.png").toExternalForm());
	}

	@Override
	protected boolean legalMove(int x, int y) {
		if (super.legalMove(x, y) && (x == this.xCoordinate || y == this.yCoordinate)) {
			hasMoved = true;
			return true;
		}
		return false;
	}
	
	public boolean getHasMoved() {
		return hasMoved;
	}

	@Override
	protected void placeThreats(chessPiece[][] board) {
		super.placeThreatsHorisontal(board);
	}
}
