package chessProject;

import javafx.scene.image.Image;

public abstract class chessPiece {
	protected int xCoordinate, yCoordinate;
	protected final boolean white;
	protected String charCode;
	protected static ChessBoard_v2 chessBoard;
	protected Image pieceImg;
	
	public chessPiece(int x, int y, boolean white, ChessBoard_v2 chessBoard_v2) {
		this.xCoordinate = x;
		this.yCoordinate = y;
		this.white = white;
		chessPiece.chessBoard = chessBoard_v2;
	}
		
	
	protected boolean legalMove(int x, int y) {
		return (x >= 0 && y >= 0 && x <= 7 && y <= 7 && (x != this.xCoordinate || y!= this.yCoordinate) 
				&& (chessBoard.getPiece(x, y) == null || chessBoard.getPiece(x, y).isWhitePiece() != this.isWhitePiece()));
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
			int index = 0;
			while(true) {
				index += x;
				if (this.xCoordinate + index >= 0 && this.xCoordinate + index <= 7) {
					chessBoard.updateThreatBoard(this.xCoordinate + index, this.yCoordinate, this);
				} else {break;}
				if(board[this.xCoordinate + index][this.yCoordinate] != null) {break;}
			}
			index = 0;
			while(true) {
				index += x;
				if (this.yCoordinate + index >= 0 && this.yCoordinate + index <= 7) {
					chessBoard.updateThreatBoard(this.xCoordinate, this.yCoordinate + index, this);
				} else {break;}
				if(board[this.xCoordinate][this.yCoordinate + index] != null) {break;}
			}
		}
	}
		
	protected abstract void placeThreats(chessPiece[][] board);
	
	public Image getImage() {
		return this.pieceImg;
	}
	
	public int getXCoordinate() {
		return this.xCoordinate;
	}
	
	public int getYCoordinate() {
		return this.yCoordinate;
	}
	
	public boolean isWhitePiece() {
		return this.white;
	}
	public String getCharCode() {
		return this.charCode;
	}

	public void setCoordinates(int x, int y) {
		this.xCoordinate = x;
		this.yCoordinate = y;
	}

}
