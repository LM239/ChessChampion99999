package chessProject;

public abstract class advancedChessPiece extends chessPiece{

	public advancedChessPiece(int x, int y, boolean white, ChessBoard_v2 chessBoard_v2) {
		super(x, y, white, chessBoard_v2);
	}
	protected void placeThreatsDiagonal(chessPiece[][] board) {
		for (int x = -1; x < 2; x+=2) {
			for (int y = -1; y < 2; y += 2) {
				int indexX = 0;
				int indexY = 0;
				while(true) {
					indexX += x;
					indexY += y;
					if (this.yCoordinate + indexY >= 0 && this.xCoordinate + indexX >= 0 && this.yCoordinate + indexY <= 7 && this.xCoordinate + indexX <= 7) {
						chessBoard.updateThreatBoard(this.xCoordinate + indexX, this.yCoordinate + indexY, this);
					} else {break;}
					if(board[this.xCoordinate + indexX][this.yCoordinate + indexY] != null) {break;}
				}
			}
		}
	}
	
	protected void placeThreatsHorisontal(chessPiece[][] board) {
		for (int x = -1; x < 2; x+=2) {
			int index = x;
			if (this.xCoordinate + index >= 0 && this.xCoordinate + index <= 7) {
				while(board[this.xCoordinate + index][this.yCoordinate] == null) {
					if (this.xCoordinate + index >= 0 && this.xCoordinate + index <= 7) {
						chessBoard.updateThreatBoard(this.xCoordinate + index, this.yCoordinate, this);
					} else {break;}
					index += x;
				}
			}
			index = x;
			if (this.yCoordinate + index >= 0 && this.yCoordinate + index <= 7) {
				while(board[this.xCoordinate][this.yCoordinate + index] == null) {
					if (this.yCoordinate + index >= 0 && this.yCoordinate + index <= 7) {
						chessBoard.updateThreatBoard(this.xCoordinate, this.yCoordinate + index, this);
					} else {break;}
					index += x;
				}
			}			
		}
	}
		
	protected abstract void placeThreats(chessPiece[][] board);
	
}
