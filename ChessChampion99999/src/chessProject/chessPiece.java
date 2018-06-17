package chessProject;

import java.util.function.Predicate;

import javafx.scene.image.Image;

public abstract class chessPiece {
	protected int xCoordinate, yCoordinate;
	protected final boolean white;
	protected String charCode;
	protected static ChessBoard_v2 chessBoard;
	protected Image pieceImg;
	protected String charRepresentation;
	protected Predicate<chessPiece> instance;
	
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
	
	public String getCharrepresentation() {
		return this.charRepresentation;
	}
	
	public Predicate<chessPiece> getInstance() {
		return this.instance;
	}

	public void setCoordinates(int x, int y) {
		this.xCoordinate = x;
		this.yCoordinate = y;
	}

}
