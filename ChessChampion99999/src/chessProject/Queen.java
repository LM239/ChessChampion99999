package chessProject;

import javafx.scene.image.Image;

public class Queen extends advancedChessPiece {

	public Queen(int x, int y, boolean white) {
		super(x, y, white, chessBoard);
		charCode = white ? "\u2655" : "\u265B";
		charRepresentation = "Q";
		instance = (p -> p instanceof Queen);
		try {		
			pieceImg = new Image(chessPiece.class.getResource(white ? "/WQ.png" : "/BQ.png").toExternalForm());
		}
		catch(RuntimeException e) {
			pieceImg = null;
		}
	}

	@Override
	protected void placeThreats(chessPiece[][] board) {
		super.placeThreatsDiagonal(board);
		super.placeThreatsHorisontal(board);
	}
}
