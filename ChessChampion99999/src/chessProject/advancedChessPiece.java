package chessProject;

import chessProject.ChessBoard_v2;
import chessProject.chessPiece;

public abstract class advancedChessPiece extends chessPiece{

	public advancedChessPiece(int x, int y, boolean white, ChessBoard_v2 chessBoard_v2) {
		super(x, y, white, chessBoard_v2);
	}
	protected void placeThreatsDiagonal(chessPiece[][] board) {
		for (int i = -1; i < 2; i+=2) {
			for (int o = -1; o < 2; o += 2) {
				int indexX = 0;
				int indexY = 0;
				while(true) {
					indexX += i;
					indexY += o;
					if (this.yCoordinate + indexY >= 0 && this.xCoordinate + indexX >= 0 && this.yCoordinate + indexY <= 7 && this.xCoordinate + indexX <= 7) {
						chessBoard.updateThreatBoard(this.xCoordinate + indexX, this.yCoordinate + indexY, this);
					} else {break;}
					if(board[this.xCoordinate + indexX][this.yCoordinate + indexY] != null) {break;}
				}
			}
		}
	}
	
	protected void placeThreatsHorisontal(chessPiece[][] board) {
		for (int i = -1; i < 2; i+=2) {
			int index = i;
			while(true) {
				if (this.xCoordinate + index >= 0 && this.xCoordinate + index <= 7) {
					chessBoard.updateThreatBoard(this.xCoordinate + index, this.yCoordinate, this);
				} else {break;}
				if (board[this.xCoordinate + index][this.yCoordinate] != null) {break;}
				index += i;
			}
			index = i;
			while(true) {
				if (this.yCoordinate + index >= 0 && this.yCoordinate + index <= 7) {
					chessBoard.updateThreatBoard(this.xCoordinate, this.yCoordinate + index, this);
				} else {break;}
				if (board[this.xCoordinate][this.yCoordinate + index] != null) {break;}
				index += i;
			}			
		}
	}
		
	protected abstract void placeThreats(chessPiece[][] board);
	
}
