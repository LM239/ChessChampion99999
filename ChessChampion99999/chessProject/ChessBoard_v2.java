package chessProject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChessBoard_v2{	
	private boolean isFinished = false;
	private boolean whiteToMove = true;
	private boolean highlighting  = true;
	private chessPiece whiteKing, blackKing;
	private String summary = "";
	private boolean inCheck = false;
	private boolean mustMoveKing = false;
	private chessPiece checkingEnemy = null;
	private final chessPiece[][] chessBoard = new chessPiece[8][8];
	private final List<List<Set<chessPiece>>> threatBoard = new ArrayList<>();
	private Collection<int[]> highlights = new ArrayList<>();
	private chessPiece highlightedPiece = null;
	GUIController_v2 controller;
	
	public ChessBoard_v2(GUIController_v2 controller) {
		for (int i = 0; i < 8; i++) {
			threatBoard.add(new ArrayList<>());
			for (int o = 0; o < 8; o++) {
				threatBoard.get(i).add(new HashSet<>());
			}
		}
		initializeBoard();
		this.controller = controller;
	}
			
			
	private void updateBoard(chessPiece piece, int x, int y) {
		if (piece.legalMove(x,y)) {		
			if (piece instanceof King && Math.abs(piece.getXCoordinate() - x) == 2) {
				int currentX = piece.getXCoordinate();
				int currentY = piece.getYCoordinate();
				
				chessPiece rook = currentX - x > 0 ? chessBoard[0][currentY]
				: chessBoard[7][currentY];
				
				chessBoard[rook.getXCoordinate()][rook.getYCoordinate()] = null;
				chessBoard[rook.getXCoordinate() + (currentX - x > 0 ? 3 : -2)][currentY] = rook;
				rook.setCoordinates(rook.getXCoordinate() + (currentX - x > 0 ? 3 : -2) ,currentY);
			} 
			
			chessBoard[piece.getXCoordinate()][piece.getYCoordinate()] = null;
			piece.setCoordinates(x,y);
			chessBoard[x][y] = piece;
			
			if (piece instanceof Pawn && (y == 7 || y == 0)) {
				chessBoard[x][y] = new Queen(x,y, whiteToMove);
			} 
			updateThreatBoard(); 
			
			inCheck = false;
			mustMoveKing = false;
			whiteToMove = !whiteToMove; 
			highlights.clear();
			highlighting = true;
			highlightedPiece = null;
			checkingEnemy = null;
			
			summary += (whiteToMove ? "White to move\n" : "Black to move\n") + this.toString() + "\n\n";
			
			if (mustMoveKing() && surrounded()) {
				this.isFinished = true;
				summary += "Check Mate!\n" + (whiteToMove ? "Black" : "White") + " wins!\nCongratulations!\n";
			}
		} 
		else {
			summary += "Invalid move\n\n";
		}
	}

			
	private void updateThreatBoard() {
		threatBoard.stream().forEach(a -> a.stream().forEach(Set::clear));
		
		Arrays.stream(this.chessBoard).forEach(x -> Arrays.stream(x).filter(y -> y != null)
				.forEach(c -> c.placeThreats(this.chessBoard)));
	}
	
	public void getInput(int[] input) {
		if (!highlighting && highlightContainsTuple(input)) {
			updateBoard(highlightedPiece,input[0], input[1]);
		}
		else if (chessBoard[input[0]][input[1]] != null && chessBoard[input[0]][input[1]].isWhitePiece() == whiteToMove) {
			highlights.clear();
			saveHiglights(chessBoard[input[0]][input[1]]);
			highlightedPiece = chessBoard[input[0]][input[1]];
			highlighting = false;
		}
		else {
			summary += "Illegal choice\n\n" + (whiteToMove ? "White to move\n" : "Black to move\n")
			+ this.toString() + "\n\n";;
		}
	}
			
	
	private boolean highlightContainsTuple(int[] input) {
		for (int[] tuple : highlights) {
			if (tuple[0] == input[0] && tuple[1] == input[1]) {
				return true;
			}
		}
		return false;
	}


	@Override
	public String toString() {
		boolean whiteSquare = true;
		String boardString = "";
		for (int x = 7; x>=0; x--) {
			boardString += Integer.toString(x+1) + " ";
			for (int y = 0; y < 8; y++) {
				boardString += chessBoard[y][x] != null ? chessBoard[y][x].getCharCode() : whiteSquare ? "\u2610" : "\u2B1B";
				whiteSquare = !whiteSquare;
			}
			boardString += "\n";
			whiteSquare = !whiteSquare;
		}
		return boardString + "   A B C D E F G H";
	}
	
	public void saveHiglights(chessPiece chessPiece) {
		chessPiece king = whiteToMove ? whiteKing : blackKing;
		int kingX = king.getXCoordinate();
		int kingY = king.getYCoordinate();
		int pieceX = chessPiece.getXCoordinate();
		int pieceY = chessPiece.getYCoordinate();
		if (inCheck) {
			if (mustMoveKing) {
				if (!(chessPiece instanceof King)) {
					return;
				}
			} 
			else if (!(chessPiece instanceof King)) {
				int enemyX = checkingEnemy.getXCoordinate();
				int enemyY = checkingEnemy.getYCoordinate();
				if (kingX == enemyX || kingY == enemyY) {
					int dynamicValue = enemyX != kingX ? enemyX : enemyY;
					boolean dynamicValueWasX = enemyX != kingX;
					int coefficient = dynamicValueWasX && enemyX - kingX > 0 || !dynamicValueWasX && enemyY - kingY > 0 ? -1 : 1;
					while (true) {
						if ((dynamicValueWasX ? kingX : kingY)-dynamicValue == 0) {break;}
						if (threatBoard.get(dynamicValueWasX ? dynamicValue : enemyX).get(dynamicValueWasX ? enemyY : dynamicValue).contains(chessPiece)
							&& !(chessPiece instanceof Pawn) || (chessPiece instanceof Pawn &&((chessBoard[dynamicValueWasX ? dynamicValue : enemyX][dynamicValueWasX ? enemyY : dynamicValue] != null 
							&& chessBoard[dynamicValueWasX ? dynamicValue : enemyX][dynamicValueWasX ? enemyY : dynamicValue].isWhitePiece() != whiteToMove)
							&& threatBoard.get(dynamicValueWasX ? dynamicValue : enemyX).get(dynamicValueWasX ? enemyY : dynamicValue).contains(chessPiece)		
							|| ((dynamicValueWasX ? dynamicValue : enemyX) == pieceX &&
							chessBoard[dynamicValueWasX ? dynamicValue : enemyX][dynamicValueWasX ? enemyY : dynamicValue] == null
							&& ((dynamicValueWasX ? enemyY : dynamicValue) == pieceY + ((Pawn)chessPiece).getCoefficient()
							|| (dynamicValueWasX ? enemyY : dynamicValue) == pieceY + ((Pawn)chessPiece).getCoefficient()*2 
							&& ((Pawn)chessPiece).isLongMove()))))) 
						{
							highlights.add(new int[]{dynamicValueWasX ? dynamicValue : enemyX, dynamicValueWasX ? enemyY : dynamicValue});
						}
						dynamicValue += coefficient;
					}
				} 
				else if (Math.abs(enemyX - kingX) == Math.abs(enemyY - kingY)) {
					int coefficientX = enemyX - kingX > 0 ? -1 : 1;
					int coefficientY = enemyY - kingY > 0 ? -1 : 1;
						
					while (true) {
						if (enemyX-kingX == 0) {break;}
						if (threatBoard.get(enemyX).get(enemyY).contains(chessPiece) && !(chessPiece instanceof Pawn) 
							|| (chessPiece instanceof Pawn) && ((chessBoard[enemyX][enemyY] != null 
							&& chessBoard[enemyX][enemyY].isWhitePiece() != whiteToMove) && threatBoard.get(enemyX).get(enemyY).contains(chessPiece)
							|| (enemyX == pieceX && chessBoard[enemyX][enemyY] == null && (enemyY == pieceY + ((Pawn)chessPiece).getCoefficient() 
							|| enemyY == pieceY + 2*((Pawn)chessPiece).getCoefficient() && ((Pawn)chessPiece).isLongMove()))))
						{
							highlights.add(new int[] {enemyX,enemyY});
						}
						enemyX += coefficientX;
						enemyY += coefficientY;
					}
				} 
				else {
					if (threatBoard.get(enemyX).get(enemyY).contains(chessPiece)) {
						highlights.add(new int[] {enemyX,enemyY});
					}
				}
				return;
			}
		}
		if (chessPiece instanceof King) {
			for (int x = kingX - 1; x <= kingX + 1; x++) {
				if (x < 0 || x > 7) {continue;}
				for (int y = kingY - 1; y <= kingY + 1; y++) {
					if (y < 0 || y > 7 || x == kingX && y == kingX) {continue;}
					if ((chessBoard[x][y] == null || chessBoard[x][y].isWhitePiece() != chessPiece.isWhitePiece())
						&& threatBoard.get(x).get(y).stream().allMatch(p-> p.isWhitePiece() == whiteToMove))
					{
						highlights.add(new int[]{x,y});
					}
				}
			}
			if (!((King)king).getHasMoved()) {
				for (int x = -1; x < 2; x+=2) {
					int coefficient = kingX + x;
					while(coefficient != (x == -1 ? -1 : 8)) {
						if (chessBoard[coefficient][kingY] != null) {
							if (chessBoard[coefficient][kingY] instanceof Rook && !((Rook)chessBoard[coefficient][kingY]).getHasMoved()) {
								highlights.add(new int[]{kingX + (x == -1 ? -2 : 2 ),kingY});
							}
							break;
						}
						coefficient += x;
					}
				}
			}
			return;
		}
		else if (pieceX == kingX || pieceY == kingY) {
			int dynamicValue = pieceX != kingX ? pieceX : pieceY;
			boolean dynamicValueWasX = pieceX != kingX;
			boolean clearHorisontal = false;
			int coefficient = dynamicValueWasX && pieceX - kingX > 0 || !dynamicValueWasX && pieceY - kingY > 0 ? 1 : -1;
			int testValue = (dynamicValueWasX ? kingX : kingY) + coefficient;
			while(true) {
				if (chessBoard[dynamicValueWasX ? testValue : kingX][dynamicValueWasX ? kingY : testValue] != null) {
					if (chessBoard[dynamicValueWasX ? testValue : kingX][dynamicValueWasX ? kingY : testValue] == chessPiece) {
						clearHorisontal = true;
					}
					break;
				}
				testValue += coefficient;
			}
			while (clearHorisontal) {
				dynamicValue += coefficient;
				if (dynamicValue < 0 || dynamicValue > 7) {break;}
				if (chessBoard[dynamicValueWasX ? dynamicValue : pieceX][dynamicValueWasX ? pieceY : dynamicValue] != null) {
					chessPiece potentialEnemy = chessBoard[dynamicValueWasX ? dynamicValue : pieceX]
					[dynamicValueWasX ? pieceY : dynamicValue];
					if((potentialEnemy instanceof Rook || potentialEnemy instanceof Queen)
						&& potentialEnemy.isWhitePiece() != whiteToMove) {
						
						for (int x = 0; x < 8; x++) {
							for (int y = 0; y < 8; y++) {
								if (threatBoard.get(x).get(y).contains(chessPiece)) {
									if ((chessBoard[x][y] == null || chessBoard[x][y].isWhitePiece() != chessPiece.isWhitePiece()) 
										&& x == kingX || y == kingY	&& (x == kingX && (x - kingX > 0 ? 1 : -1) == coefficient 
										|| y == kingX && (y - kingY > 0 ? 1 : -1) == coefficient) && (!(chessPiece instanceof Pawn) 
										|| !dynamicValueWasX && chessBoard[x][y] != null && chessBoard[x][y].isWhitePiece() != chessPiece.isWhitePiece())) 
									{
										highlights.add(new int[]{x,y});
									}
								}
							}
						}
						
						if (chessPiece instanceof Pawn && !dynamicValueWasX && 
								chessBoard[pieceX][pieceY + (chessPiece.isWhitePiece() ? 1 : -1)] == null) {
							highlights.add(new int[]{pieceX,pieceY + (chessPiece.isWhitePiece() ? 1 : -1)});
							if (((Pawn) chessPiece).isLongMove() && chessBoard[pieceX][pieceY + (chessPiece.isWhitePiece() ? 2 : -2)] == null) {
								highlights.add(new int[]{pieceX,pieceY + (chessPiece.isWhitePiece() ? 2 : -2)});
							}
						}
						return;
					} 
					else {
						break;
					}
				}			
			}
		}
		else if (Math.abs(kingX - pieceX) == Math.abs(kingY - pieceY)) {
			int coefficientX = pieceX - kingX > 0 ? 1 : -1;
			int coefficientY = pieceY - kingY > 0 ? 1 : -1;
			int testValueX = kingX + coefficientX;
			int testValueY = kingY + coefficientY;
			boolean clearDiagonal = true;
			
			while(true) {
				if (chessBoard[testValueX][testValueY] != null) {
					if (chessBoard[testValueX][testValueY] == chessPiece) {
						clearDiagonal = true;
					}
					break;
				}
				testValueX += coefficientX;
				testValueY += coefficientY;
			}
			while (clearDiagonal) {
				pieceX += coefficientX;
				pieceY += coefficientY;
				if (pieceX < 0 || pieceX > 7 || pieceY < 0 || pieceY > 7) {break;}
				if (chessBoard[pieceX][pieceY] != null) {
					chessPiece potentialEnemy = chessBoard[pieceX][pieceY];
					if(potentialEnemy.isWhitePiece() != whiteToMove && 
						(potentialEnemy instanceof Bishop || potentialEnemy instanceof Queen)) {
						
						for (int x = 0; x < 8; x++) {
							for (int y = 0; y < 8; y++) {
								if (threatBoard.get(x).get(y).contains(chessPiece)) {
									if ((chessBoard[x][y] == null || chessBoard[x][y].isWhitePiece() != chessPiece.isWhitePiece())
									&& Math.abs(x - kingX) == Math.abs(y - kingY) && (x - kingX > 0 ? 1 : -1) == coefficientX && 
									(y - kingY > 0 ? 1 : -1) == coefficientY && (!(chessPiece instanceof Pawn)
									|| chessBoard[x][y] != null && chessBoard[x][y].isWhitePiece() != chessPiece.isWhitePiece())) 
									{
										highlights.add(new int[]{x,y});
									}
								}
							}
						}
						return;
					} 
					else {
						break;
					}
				}
			}
		} 
			
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (threatBoard.get(x).get(y).contains(chessPiece)) {
					if ((chessBoard[x][y] == null || chessBoard[x][y].isWhitePiece() != chessPiece.isWhitePiece()) && 
						(!(chessPiece instanceof Pawn) || chessBoard[x][y] != null && chessBoard[x][y].isWhitePiece() 
						!= chessPiece.isWhitePiece()))
					{
						highlights.add(new int[]{x,y});
					}
				}
			}
		}
		
		pieceX = chessPiece.getXCoordinate();
		pieceY = chessPiece.getYCoordinate();
		if (chessPiece instanceof Pawn && chessBoard[pieceX][pieceY + (chessPiece.isWhitePiece() ? 1 : -1)] == null) {
			highlights.add(new int[]{pieceX,pieceY + (chessPiece.isWhitePiece() ? 1 : -1)});
			if (((Pawn) chessPiece).isLongMove() && chessBoard[pieceX][pieceY + (chessPiece.isWhitePiece() ? 2 : -2)] == null) {
				highlights.add(new int[]{pieceX,pieceY + (chessPiece.isWhitePiece() ? 2 : -2)});
			}
		}
	}
			
			
	protected boolean surrounded() {		
		chessPiece king = whiteToMove ? whiteKing : blackKing;
		int kingX = king.getXCoordinate();
		int kingY = king.getYCoordinate();
		for (int i = kingX - 1; i <= kingX + 1; i++) {
			if(i < 0 || i > 7) {continue;}
			for (int o = kingY - 1; o <= kingY + 1; o++) {
				if (o < 0 || o > 7) {continue;}
				if ((chessBoard[i][o] == null || chessBoard[i][o].isWhitePiece() != whiteToMove)
						&& (threatBoard.get(i).get(o).stream().allMatch(p -> p.isWhitePiece() == whiteToMove))) {
						return false;
					}
				}
			}
		return true;
	}
			
			
	protected boolean mustMoveKing() {
		chessPiece king = whiteToMove ? whiteKing : blackKing;
		Set<chessPiece> kingPosition = threatBoard.get(king.getXCoordinate()).get(king.getYCoordinate());
			
		switch(kingPosition.stream().filter(d -> d.isWhitePiece() != whiteToMove).mapToInt(c -> 1).sum()) {
			case 0 : return false;
			case 2 : highlightedPiece = king; mustMoveKing = true; inCheck = true; return true;
		}
		inCheck = true;
		highlightedPiece = king;
		chessPiece enemy = kingPosition.stream().filter(d -> d.isWhitePiece() != whiteToMove).findFirst().get();		
		this.checkingEnemy = enemy;
		
		if (threatBoard.get(enemy.getXCoordinate()).get(enemy.getYCoordinate()).stream().anyMatch
				(d -> d.isWhitePiece() == whiteToMove && !(d instanceof King))) {return false;}
		if(Math.abs(king.getXCoordinate() - enemy.getXCoordinate()) <= 1 && Math.abs(king.getYCoordinate() 
				- enemy.getYCoordinate()) <= 1 || enemy instanceof Knight) {
			mustMoveKing = true;
			return true;
		}
		
		int kingX = king.getXCoordinate();
		int kingY = king.getYCoordinate();
		int enemyX = enemy.getXCoordinate();
		int enemyY = enemy.getYCoordinate();
		List<int[]> threatenedIndexes = new ArrayList<>();
		if (kingX == enemyX || kingY == enemyY) {
			int dynamicValue = enemyX != kingX ? enemyX : enemyY;
			boolean dynamicValueWasX = enemyX != kingX;
			int coefficient = dynamicValueWasX && enemyX - kingX > 0 || !dynamicValueWasX && enemyY - kingY > 0 ? -1 : 1;
			while (dynamicValueWasX && Math.abs(kingX - dynamicValue) != 1 ||
					!dynamicValueWasX && Math.abs(kingY - dynamicValue) != 1) {
				dynamicValue += coefficient;
				threatenedIndexes.add(new int[]{dynamicValueWasX ? dynamicValue : kingX , dynamicValueWasX ? kingY : dynamicValue});
			}			
		} 
		else {
			int coefficientX = enemyX - kingX > 0 ? -1 : 1;
			int coefficientY = enemyY - kingY > 0 ? -1 : 1;
			while (Math.abs(kingX - enemyX) != 1) {
				enemyX += coefficientX;
				enemyY += coefficientY;
				threatenedIndexes.add(new int[]{enemyX,enemyY});
			}
		}
		boolean returnValue = !threatenedIndexes.stream().anyMatch(p -> Arrays.stream(chessBoard).anyMatch(c -> Arrays.stream(c)
				.anyMatch(d -> d != null && d.isWhitePiece() == whiteToMove && !(d instanceof King) 
				&& (((d instanceof Pawn) && (p[0] == d.getXCoordinate() && p[1] == d.getYCoordinate()
				+ 2*((Pawn)d).getCoefficient() && ((Pawn)d).isLongMove())) || d.legalMove(p[0], p[1])))));
		
		mustMoveKing = returnValue;
		return returnValue;
	}
			
	public chessPiece getPiece(int x, int y) {
		return chessBoard[x][y];
	}
	
	public String getSummary() {
		String summaryText = this.summary;
		this.summary = "";
		return summaryText;
	}
			
	public List<List<Set<chessPiece>>> getThreatBoard() {
		return this.threatBoard;
	}
			
	public void updateThreatBoard(int x, int y,chessPiece value) {
		threatBoard.get(x).get(y).add(value);
	}

	
	public Collection<int[]> getHighlights() {
		return this.highlights;
	}
	
	public int[] getHighlightedPiece() {
		return highlightedPiece != null ? new int[] {highlightedPiece.getXCoordinate(), highlightedPiece.getYCoordinate()} : null;
	}
	
	public chessPiece[][] getBoard() {
		return this.chessBoard;
	}

	protected void initializeBoard() {
		chessBoard[3][7] = new Queen(3, 7, false);
		chessBoard[3][0] = new Queen(3, 0, true);
		chessBoard[4][7] = new King(4, 7, false, this);
		chessBoard[4][0] = new King(4, 0, true, this);
		this.whiteKing = chessBoard[4][0];
		this.blackKing = chessBoard[4][7];
		for (int x = 0; x < 8; x++) {
			chessBoard[x][6] = new Pawn(x, 6, false);
			chessBoard[x][1] = new Pawn(x, 1, true);
		}
		for (int x = 0; x < 8; x+=7) {
			chessBoard[x][7] = new Rook(x, 7, false);
			chessBoard[x][0] = new Rook(x, 0, true);
		}
		for (int x = 1; x < 8; x+=5) {
			chessBoard[x][7] = new Knight(x, 7, false);
			chessBoard[x][0] = new Knight(x, 0, true);
		}
		for (int x = 2; x < 8; x+=3) {
			chessBoard[x][7] = new Bishop(x, 7, false);
			chessBoard[x][0] = new Bishop(x, 0, true);
		}
		summary += this.toString() + "\n\n";
		updateThreatBoard();
	}
	
	public static void main(String[] args) {
		ChessBoard_v2 game = new ChessBoard_v2(null);
		/*game.getInput(new int[]{4,1});
		game.getInput(new int[]{4,2});
		game.getInput(new int[]{6,7});
		game.getInput(new int[]{5,5});
		game.getInput(new int[]{3,0});
		game.getInput(new int[]{7,4});
		game.getInput(new int[]{4,6});
		game.getInput(new int[]{4,4});
		game.getInput(new int[]{7,4});
		game.getInput(new int[]{7,5});
		game.getInput(new int[]{4,7});
		game.getInput(new int[]{4,6});
		game.getInput(new int[]{3,1});
		game.getInput(new int[]{3,2});
		game.getInput(new int[]{4,6});
		game.getInput(new int[]{4,5});
		game.getInput(new int[]{2,1});
		game.getInput(new int[]{2,2});
		game.getInput(new int[]{5,5});*/
		
		/*game.getInput(new int[]{5,1});
		game.getInput(new int[]{5,2});
		game.getInput(new int[]{4,6});
		game.getInput(new int[]{4,5});
		game.getInput(new int[]{7,1});
		game.getInput(new int[]{7,3});
		game.getInput(new int[]{3,7});
		game.getInput(new int[]{7,3});
		game.getInput(new int[]{7,0});*/
		
		/*game.getInput(new int[]{3,1});
		game.getInput(new int[]{3,3});
		game.getInput(new int[]{2,6});
		game.getInput(new int[]{2,4});
		game.getInput(new int[]{7,1});
		game.getInput(new int[]{7,2});
		game.getInput(new int[]{3,7});
		game.getInput(new int[]{0,4});
		game.getInput(new int[]{1,1});
		game.getInput(new int[]{1,3});
		game.getInput(new int[]{1,7});
		game.getInput(new int[]{2,5});
		game.getInput(new int[]{7,2});
		game.getInput(new int[]{7,3});
		game.getInput(new int[]{2,5});
		game.getInput(new int[]{1,3});
		game.getInput(new int[]{7,3});
		game.getInput(new int[]{7,4});
		game.getInput(new int[]{1,3});
		game.getInput(new int[]{2,1});*/
		
		/*game.getInput(new int[]{6,1});
		game.getInput(new int[]{6,2});
		game.getInput(new int[]{3,6});
		game.getInput(new int[]{3,5});
		game.getInput(new int[]{5,0});
		game.getInput(new int[]{6,1});
		game.getInput(new int[]{2,7});
		game.getInput(new int[]{4,5});
		game.getInput(new int[]{6,0});
		game.getInput(new int[]{5,2});
		game.getInput(new int[]{1,7});
		game.getInput(new int[]{2,5});
		game.getInput(new int[]{4,0});
		game.getInput(new int[]{6,0});*/
		
		/*game.getInput(new int[]{4,1});
		game.getInput(new int[]{4,3});
		game.getInput(new int[]{3,6});
		game.getInput(new int[]{3,4});
		game.getInput(new int[]{4,3});
		game.getInput(new int[]{3,4});
		game.getInput(new int[]{3,7});
		game.getInput(new int[]{3,4});
		game.getInput(new int[]{3,1});
		game.getInput(new int[]{3,3});
		game.getInput(new int[]{4,7});
		game.getInput(new int[]{3,7});
		game.getInput(new int[]{3,0});
		game.getInput(new int[]{3,2});
		game.getInput(new int[]{3,4});*/
		
		/*game.getInput(new int[]{4,1});
		game.getInput(new int[]{4,3});
		game.getInput(new int[]{4,6});
		game.getInput(new int[]{4,4});
		
		game.getInput(new int[]{3,0});
		game.getInput(new int[]{7,4});
		game.getInput(new int[]{3,7});
		game.getInput(new int[]{7,3});
		game.getInput(new int[]{7,4});
		game.getInput(new int[]{4,4});
		game.getInput(new int[]{7,3});
		game.getInput(new int[]{4,6});
		game.getInput(new int[]{4,4});
		game.getInput(new int[]{3,3});*/
		

		game.getInput(new int[]{4,1});
		game.getInput(new int[]{4,3});
		game.getInput(new int[]{5,6});
		game.getInput(new int[]{5,4});
		game.getInput(new int[]{3,0});
		game.getInput(new int[]{7,4});
		game.getInput(new int[]{6,6});
		
		
	}
}
