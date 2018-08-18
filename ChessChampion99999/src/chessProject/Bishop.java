package chessProject;

import javafx.scene.image.Image;

public class Bishop extends advancedChessPiece {
	public Bishop(int x, int y, boolean white) {
		super(x, y, white, chessBoard);
		charCode = white ? "\u2657" : "\u265D";
		charRepresentation = "B";
		instance = (p -> p instanceof Bishop);
		try {		
			pieceImg = new Image(chessPiece.class.getResource(white ? "/WB.png" : "/BB.png").toExternalForm());
		}
		catch(RuntimeException e) {
			pieceImg = null;
		}
	}

	@Override
	protected void placeThreats(chessPiece[][] board) {
		super.placeThreatsDiagonal(board);
	}
}
