package chessProject;

import javafx.scene.image.Image;

public class King extends chessPiece {
	private boolean hasMoved = false;

	public King(int x, int y, boolean white, ChessBoard_v2 chessBoard_v2) {
		super(x, y, white, chessBoard_v2);
		charCode = white ? "\u2654" : "\u265A";
		pieceImg = new Image(chessPiece.class.getResource(white ? "/WK.png" : "/BK.png").toExternalForm());
	}

	@Override
	protected boolean legalMove(int x, int y) {
		if (!hasMoved && Math.abs(this.xCoordinate - x) == 2 || super.legalMove(x, y) && chessBoard.getThreatBoard().get(x).get(y).stream()
			.allMatch(p -> p.white == this.white)&& Math.abs(this.xCoordinate - x) <=1 && Math.abs(this.yCoordinate - y) <=1) {
			hasMoved = true;
			return true;
		}
		return false;
	}

	@Override
	protected void placeThreats(chessPiece[][] board) {
		for (int i = this.xCoordinate - 1; i <= this.xCoordinate + 1; i++) {
			if (i < 0 || i > 7) {continue;}
			for (int o = this.yCoordinate - 1; o <= this.yCoordinate + 1; o++) {
				if (o < 0 || o > 7 || i == this.xCoordinate && o == this.yCoordinate) {continue;}
				chessBoard.updateThreatBoard(i, o, this);
			}
		}	
	}
	
	
	public boolean getHasMoved() {
		return this.hasMoved;
	}
	
}
