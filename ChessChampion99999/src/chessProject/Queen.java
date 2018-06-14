package chessProject;

import javafx.scene.image.Image;

public class Queen extends chessPiece {

	public Queen(int x, int y, boolean white) {
		super(x, y, white, chessBoard);
		charCode = white ? "\u2655" : "\u265B";
		charRepresentation = "Q";
		instance = (p -> p instanceof Queen);
		pieceImg = new Image(chessPiece.class.getResource(white ? "/WQ.png" : "/BQ.png").toExternalForm());
	}

	@Override
	protected void placeThreats(chessPiece[][] board) {
		super.placeThreatsDiagonal(board);
		super.placeThreatsHorisontal(board);
	}
	
	@Override
	protected boolean legalMove(int x, int y) {
		return (super.legalMove(x, y) && chessBoard.getThreatBoard().get(x).get(y).contains(this));
	}
}
