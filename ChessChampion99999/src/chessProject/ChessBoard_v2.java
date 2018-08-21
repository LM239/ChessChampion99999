package chessProject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;
import java.util.function.Predicate;

public class ChessBoard_v2{	
	private boolean whiteToMove = true;
	private boolean highlighting  = true;
	private String summary = "May the best player\nwin!\n\nWhite to move:\n";
	private boolean inCheck, mustMoveKing, promotingPawn, twoEnemies;
	private chessPiece[][] chessBoard = new chessPiece[8][8];
	private chessPiece[][] copyOfBoard = new chessPiece[8][8];
	private chessPiece checkingEnemy, highlightedPiece , promotingPiece, whiteKing, blackKing;
	private final List<List<Set<chessPiece>>> threatBoard = new ArrayList<>();
	private final Collection<int[]> highlights = new ArrayList<>();
	private final Map<Pawn, Collection<int[]>> pawnMoves = new HashMap<>();
	private final Stack<chessPiece[][]> history = new Stack<>();
	private final Stack<chessPiece> enemyStack = new Stack<>();
	private final Stack<chessPiece> movedPieceStack = new Stack<>();
	private final Stack<Boolean> checkStack = new Stack<>();
	private final Stack<Boolean> mustMoveStack = new Stack<>();
	private final Stack<Pawn> longMovedStack = new Stack<>();
	private final Stack<Boolean> twoEnemiesStack = new Stack<>();
	private String moveString = "";
	private Pawn longMovedPawn;
	private Boolean engineIsWhite = null;
	private chessEngine chessEngine;
	private engineListener listener;

	public ChessBoard_v2() {
		for (int i = 0; i < 8; i++) {
			threatBoard.add(new ArrayList<>());
			for (int o = 0; o < 8; o++) {
				threatBoard.get(i).add(new HashSet<>());
			}
		}
		initializeBoard();
	}

	private void updateBoard(chessPiece piece, int x, int y) {
		if (piece.legalMove(x,y)) {
			moveString  = getMoveString(piece,x,y);
			if (piece instanceof King && Math.abs(piece.getXCoordinate() - x) == 2) {
				int currentX = piece.getXCoordinate();
				int currentY = piece.getYCoordinate();
				
				chessPiece rook = currentX - x > 0 ? chessBoard[0][currentY]
				: chessBoard[7][currentY];
				
				chessBoard[rook.getXCoordinate()][currentY] = null;
				chessBoard[(currentX - x > 0 ? 3 : 5)][currentY] = rook;
				rook.setCoordinates((currentX - x > 0 ? 3 : 5) ,currentY);
			} 
			
			chessBoard[piece.getXCoordinate()][piece.getYCoordinate()] = null;
			highlightedPiece = null;
			highlights.clear();
			
			if (piece instanceof Pawn) { 
				if (y == 7 || y == 0) {
					if (engineIsWhite != null && engineIsWhite == whiteToMove) {
						chessBoard[x][y] = new Queen(x,y,whiteToMove);
						finishMove(moveString);
						return;
					}
					chessBoard[x][y] = piece;
					copyOfBoard = cloneArray(chessBoard);
					chessBoard = new chessPiece[8][8];
					summary += "Choose wich piece\nto promote to\n\n";
					chessBoard[2][5] = new Bishop(2,5,whiteToMove);
					chessBoard[3][5] = new Rook(3,5,whiteToMove);
					chessBoard[4][5] = new Queen(4,5,whiteToMove);
					chessBoard[5][5] = new Knight(5,5,whiteToMove);
					piece.setCoordinates(x,y);
					promotingPiece = piece;
					promotingPawn = true;
					return;
				}
				else if (x != piece.getXCoordinate() && chessBoard[x][y] == null) {
					chessBoard[x][y - ((Pawn)piece).getCoefficient()] = null;
				}
				else if ((Math.abs(piece.yCoordinate - y) == 2)) {
					longMovedPawn = (Pawn) piece;
				}
			}  
			
			piece.setCoordinates(x,y);
			chessBoard[x][y] = piece;
			finishMove(moveString);
		} 
		else {
			summary += "Invalid move\n\n";
		}
	}


	private void finishMove(String moveString) {
		inCheck = false;
		mustMoveKing = false;
		whiteToMove = !whiteToMove; 
		highlighting = true;
		checkingEnemy = null;
		twoEnemies = false;
		updateThreatBoard(this.chessBoard);
		
		if (longMovedPawn != null && longMovedPawn.isWhitePiece() == whiteToMove) {
			longMovedPawn.setJustMovedFalse();
			longMovedPawn = null;
		}
		
		mustMoveKing = mustMoveKing(whiteToMove, this.chessBoard);
		boolean surrounded = surrounded(whiteToMove);
		
		if (inCheck) {
			highlightedPiece = whiteToMove ? whiteKing : blackKing;
			moveString += surrounded && mustMoveKing ? "#" : "+";
		}
		summary += moveString + "\n" + (whiteToMove ? "White to move:\n" : "Black to move:\n") + this.toString() + "\n\n";
		
		if (surrounded) {
			if (mustMoveKing) {
				summary += "Check Mate!\n" + (whiteToMove ? "Black" : "White") + " wins!\nCongratulations!\n";
			} 
			else if(!inCheck) {
				for (chessPiece[] column : chessBoard) {
					for (chessPiece piece : column) {
						if (piece != null && piece.isWhitePiece() == whiteToMove && !(piece instanceof King)) {
							saveHighlights(piece, this.chessBoard);
							if (highlights.size() > 0) {
								highlights.clear();
								return;
							}
						}
					}
				}
				summary += "Draw!\n" + (!whiteToMove ? "Black" : "White") + " can't move!\n1/2-1/2";
			}
		}
	}
	

	public void checkEngine() {
		if (engineIsWhite != null && engineIsWhite == whiteToMove) {
			Object[] move = chessEngine.findMove(4, chessBoard, whiteToMove);
			updateBoard((chessPiece)move[0], (int)move[1], (int)move[2]);
			if (listener != null) {
				listener.fireEventListener();
			}
		}
	}

	public chessPiece[][] cloneArray(chessPiece[][] board) {
	    chessPiece[][] newBoard = new chessPiece[8][8];
	    for (int i = 0; i < 8; i++) {
	    	newBoard[i] = new chessPiece[8];
	    	for (int o = 0; o < 8; o++) {
	    		newBoard[i][o] = board[i][o];
	    	}
	    }
	    return newBoard;
	}

			
	private String getMoveString(chessPiece piece, int x, int y) {
		String result = "";
		if (piece instanceof Pawn) {
			if (x != piece.getXCoordinate()) {
				result = Character.toString(("abcdefgh".charAt(piece.getXCoordinate()))) + "x";
			}
			return result + Character.toString(("abcdefgh".charAt(x))) + Integer.toString(y+1);
		} 
		Predicate<chessPiece> g = null;
		if(piece instanceof King) {
			if (Math.abs(piece.getXCoordinate() - x) == 2) {
				return x > piece.getXCoordinate() ? "O-O" : "O-O-O";
			}
			g = (a -> false);
			result = "K";
		}
		else {
			g = piece.getInstance();
			result = piece.getCharrepresentation();
		}
		
		try {
			chessPiece clone = threatBoard.get(x).get(y).stream().filter(g).filter(p -> p != piece && p.isWhitePiece() == whiteToMove).findFirst().get();
			if (clone.getXCoordinate() == piece.getXCoordinate()) {
				result += Integer.toString(piece.getYCoordinate() +1);
			} 
			else {
				result += Character.toString(("abcdefgh".charAt(piece.getXCoordinate())));
			}
		} 
		catch (NoSuchElementException e) {
		
		}
		if (chessBoard[x][y] != null) {
			result += "x";
		}
		
		return result + Character.toString(("abcdefgh".charAt(x))) + Integer.toString(y+1);
	}
	
	public void updateThreatBoard(chessPiece[][] board) {
		pawnMoves.clear();
		threatBoard.stream().forEach(a -> a.stream().forEach(Set::clear));
		
		Arrays.stream(board).forEach(x -> Arrays.stream(x).filter(y -> y != null)
			.forEach(c -> c.placeThreats(board)));
	}
	
	public void getInput(int x, int y) {
		if (!highlighting && highlights.stream().anyMatch(p -> p[0] == x && p[1] == y) && !promotingPawn) {
			updateBoard(highlightedPiece,x, y);
		}
		else if (!promotingPawn && chessBoard[x][y] != null && chessBoard[x][y].isWhitePiece() == whiteToMove) {
			highlights.clear();
			saveHighlights(chessBoard[x][y], this.chessBoard);
			highlightedPiece = chessBoard[x][y];
			highlighting = false;
		}
		else if(promotingPawn) {
			if (y == 5 && x > 1 && x < 6) {
				promotingPawn = false;
				int xCoor = promotingPiece.getXCoordinate();
				int yCoor = promotingPiece.getYCoordinate();
				chessBoard = copyOfBoard;
				switch(x) {
					case 2 : chessBoard[xCoor][yCoor] = new Bishop(xCoor,yCoor,whiteToMove); break;
					case 3 : chessBoard[xCoor][yCoor] = new Rook(xCoor,yCoor,whiteToMove); break;
					case 4 : chessBoard[xCoor][yCoor] = new Queen(xCoor,yCoor,whiteToMove);break;
					case 5 : chessBoard[xCoor][yCoor] = new Knight(xCoor,yCoor,whiteToMove); break;
				}
				promotingPiece = null;
				finishMove(moveString + "=" + chessBoard[xCoor][yCoor].getCharrepresentation());
			}
			else {
				summary = "Invalid choice";
			}
		}
		else {
			summary += "Illegal choice\n\n" + (whiteToMove ? "White to move\n" : "Black to move\n")
			+ this.toString() + "\n\n";;
		}
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
	
	public void saveHighlights(chessPiece chessPiece, chessPiece[][] chessBoard) {
		chessPiece king = whiteToMove ? whiteKing : blackKing;
		int kingX = king.getXCoordinate();
		int kingY = king.getYCoordinate();
		int pieceX = chessPiece.getXCoordinate();
		int pieceY = chessPiece.getYCoordinate();
		if (inCheck) {
			int enemyX = checkingEnemy.getXCoordinate();
			int enemyY = checkingEnemy.getYCoordinate();
			if (mustMoveKing) {
				if (!(chessPiece instanceof King)) {
					return;
				}
			}
			if (chessPiece instanceof King) {
				chessPiece enemyNrTwo = null;
				int enemyX2 = 0;
				int enemyY2 = 0;
				if (twoEnemies) {
					System.out.println(this.toString());
					enemyNrTwo = threatBoard.get(kingX).get(kingY).stream().filter(d -> d != checkingEnemy && d.isWhitePiece() != whiteToMove).findFirst().get();
					enemyX2 = enemyNrTwo.getXCoordinate();
					enemyY2 = enemyNrTwo.getYCoordinate();
				}
				for (int x = kingX - 1; x <= kingX + 1; x++) {
					if (x < 0 || x > 7) {continue;}
					yloop:
					for (int y = kingY - 1; y <= kingY + 1; y++) {
						if (y < 0 || y > 7 || ((y == kingY && enemyY == kingY && x != enemyX) ||
							(x == kingX && enemyX == kingX && y != enemyY) ||
							Math.abs(enemyX - kingX) == Math.abs(enemyY - kingY)
							&& Math.abs(enemyX - x) == Math.abs(enemyY - y)
							&& !(x == enemyX && y == enemyY) && !(checkingEnemy instanceof Pawn)) || (enemyNrTwo != null 
							&& ((y == kingY && enemyY2 == kingY && x != enemyX2) ||
							(x == kingX && enemyX2 == kingX && y != enemyY2) ||
							Math.abs(enemyX2 - kingX) == Math.abs(enemyY2 - kingY)
							&& Math.abs(enemyX2 - x) == Math.abs(enemyY2 - y)
							&& !(x == enemyX2 && y == enemyY2) && !(enemyNrTwo instanceof Pawn)))) {continue yloop;}
						
						if ((chessBoard[x][y] == null || chessBoard[x][y].isWhitePiece() != whiteToMove)
							&& threatBoard.get(x).get(y).stream().allMatch(p-> p.isWhitePiece() == whiteToMove))
						{
							highlights.add(new int[]{x,y});
						}
					}
				}
				return;
			}
			else if (kingX == enemyX || kingY == enemyY) {
				int dynamicValue = enemyX != kingX ? enemyX : enemyY;
				boolean dynamicValueWasX = enemyX != kingX;
				int coefficient = dynamicValueWasX && enemyX - kingX > 0 || !dynamicValueWasX && enemyY - kingY > 0 ? -1 : 1;
				while ((dynamicValueWasX ? kingX : kingY)-dynamicValue != 0) {
					final int finalValueX = dynamicValueWasX ? dynamicValue : enemyX;
					final int finalValueY = dynamicValueWasX ? enemyY : dynamicValue;
					
					if (threatBoard.get(finalValueX).get(finalValueY).contains(chessPiece)
						&& (!(chessPiece instanceof Pawn) || chessBoard[finalValueX][finalValueY] != null) 
						|| chessPiece instanceof Pawn && dynamicValueWasX && pawnMoves.get(chessPiece).stream()
						.anyMatch(a -> a[0] == finalValueX && a[1] == finalValueY)) 
					{
						highlights.add(new int[]{finalValueX, finalValueY});
					}
					dynamicValue += coefficient;
				}
			} 
			else if (Math.abs(enemyX - kingX) == Math.abs(enemyY - kingY)) {
				int coefficientX = enemyX - kingX > 0 ? -1 : 1;
				int coefficientY = enemyY - kingY > 0 ? -1 : 1;
					
				while (true) {
					if (enemyX-kingX == 0) {break;}
					final int finalValueX = enemyX;
					final int finalValueY = enemyY;
					
					if (threatBoard.get(enemyX).get(enemyY).contains(chessPiece) && 
						(!(chessPiece instanceof Pawn) || chessBoard[enemyX][enemyY] != null)
						|| chessPiece instanceof Pawn && pawnMoves.get(chessPiece).stream()
						.anyMatch(a -> a[0] == finalValueX && a[1] == finalValueY))
					{
						highlights.add(new int[] {enemyX,enemyY});
					}
					enemyX += coefficientX;
					enemyY += coefficientY;
				}
				if (checkingEnemy == longMovedPawn && chessPiece instanceof Pawn) {
					pawnMoves.get(chessPiece).stream().filter(a -> a[0] == checkingEnemy.getXCoordinate()
					&& a[1] == checkingEnemy.getYCoordinate() + ((Pawn)chessPiece).getCoefficient())
					.forEach(b -> highlights.add(b));
				}
			} 
			else if (threatBoard.get(enemyX).get(enemyY).contains(chessPiece)) {
				highlights.add(new int[] {enemyX,enemyY});
			}
			return;
		}
		if (chessPiece instanceof King) {
			for (int x = kingX - 1; x <= kingX + 1; x++) {
				if (x < 0 || x > 7) {continue;}
				yloop:
				for (int y = kingY - 1; y <= kingY + 1; y++) {
					if (y < 0 || y > 7) {continue yloop;}
					if ((chessBoard[x][y] == null || chessBoard[x][y].isWhitePiece() != whiteToMove)
						&& threatBoard.get(x).get(y).stream().allMatch(p-> p.isWhitePiece() == whiteToMove))
					{
						highlights.add(new int[]{x,y});
					}
				}
			}
			if (!((King)king).getHasMoved() && !inCheck && !((King)king).hasMovedTest()) {
				for (int x = -1; x < 2; x+=2) {
					int index = kingX + x;
					while(index >= 0 && index < 8) {
						if (chessBoard[index][kingY] != null) {
							if (chessBoard[index][kingY] instanceof Rook && !((Rook)chessBoard[index][kingY]).getHasMoved()) {
								highlights.add(new int[]{kingX + (x == -1 ? -2 : 2),kingY});
							}
							break;
						}
						if (Math.abs(kingX - index) < 3 && threatBoard.get(index).get(kingY).stream()
							.anyMatch(b -> b.isWhitePiece() != whiteToMove)) {break;}
						index += x;
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
				final int finalX = dynamicValueWasX ? testValue : kingX;
				final int finalY = dynamicValueWasX ? kingY : testValue;
				if (chessBoard[finalX][finalY] != null) {
					if (chessBoard[finalX][finalY] == chessPiece) {
						clearHorisontal = true;
						if (longMovedPawn != null && chessBoard[finalX + coefficient][finalY] == longMovedPawn && pieceY == kingY && chessPiece instanceof Pawn) {
							clearHorisontal = false;
							for (int x = longMovedPawn.getXCoordinate() + coefficient; x < 8; x += coefficient) {
								if (x < 0) {break;}
								chessPiece piece = chessBoard[x][finalY];
								if (piece != null) {
									if ((piece instanceof Queen || piece instanceof Rook) && piece.isWhitePiece() != whiteToMove) {
										pawnMoves.get(chessPiece).removeIf(a -> a[0] != chessPiece.getXCoordinate());
									}
									else {break;}
								}						
							}
						}
					}
					else if (chessBoard[finalX][finalY] == longMovedPawn && pieceY == kingY && chessBoard[finalX + coefficient][finalY] == chessPiece && chessPiece instanceof Pawn) {
						for (int x = pieceX + coefficient; x < 8; x += coefficient) {
							if (x < 0) {break;}
							chessPiece piece = chessBoard[x][finalY];
							if (piece != null) {
								if ((piece instanceof Queen || piece instanceof Rook) && piece.isWhitePiece() != whiteToMove) {
									pawnMoves.get(chessPiece).removeIf(a -> a[0] != chessPiece.getXCoordinate());
								}
								else {break;}
							}						
						}
					}
					break;
				}
				testValue += coefficient;
			}
			while (clearHorisontal) {
				dynamicValue += coefficient;
				int finalX = dynamicValueWasX ? dynamicValue : kingX;
				int finalY = dynamicValueWasX ? kingY : dynamicValue;
				if (dynamicValue < 0 || dynamicValue > 7) {break;}
				if (chessBoard[finalX][finalY] != null) {
					chessPiece potentialEnemy = chessBoard[finalX][finalY];
					if((potentialEnemy instanceof Rook || potentialEnemy instanceof Queen)
						&& potentialEnemy.isWhitePiece() != whiteToMove) {
												
						int dynamicIndex = dynamicValueWasX ? kingX : kingY;
						while (dynamicIndex >= 0 && dynamicIndex <= 7) {
							dynamicIndex += coefficient;
							final int finalKingX = dynamicValueWasX ? dynamicIndex : kingX;
							final int finalKingY = dynamicValueWasX ?  kingY : dynamicIndex;
							if (!(chessPiece instanceof Pawn) && (chessBoard[finalKingX][finalKingY] == null
								|| chessBoard[finalKingX][finalKingY].isWhitePiece() != whiteToMove)
								&& threatBoard.get(finalKingX).get(finalKingY).contains(chessPiece))
							{
								highlights.add(new int[] {finalKingX, finalKingY});
							}
							if (chessBoard[finalKingX][finalKingY] != null && chessBoard[finalKingX]
								[finalKingY].isWhitePiece() != whiteToMove) {break;}
						}
						if (chessPiece instanceof Pawn && !dynamicValueWasX) {
							pawnMoves.get(chessPiece).stream().filter(b -> b[0] == kingX).forEach(a -> highlights.add(a));
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
			boolean clearDiagonal = false;
			
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
						int indexX = kingX;
						int indexY = kingY;
						while (indexX >= 0 && indexX <= 7 && indexY >= 0 && indexY <= 7 && !(chessBoard[indexX][indexY] != null 
								&& chessBoard[indexX][indexY].isWhitePiece() != whiteToMove)) {
							indexX += coefficientX;
							indexY += coefficientY;
							if ((chessBoard[indexX][indexY] == null || chessBoard[indexX][indexY].isWhitePiece() != whiteToMove) &&
								threatBoard.get(indexX).get(indexY).contains(chessPiece) && (!(chessPiece instanceof Pawn) || chessBoard[indexX][indexY] != null 
								&& chessBoard[indexX][indexY].isWhitePiece() != whiteToMove))
							{
								highlights.add(new int[] {indexX,indexY});	
							}
						}
						if (chessPiece instanceof Pawn) {
							pawnMoves.get(chessPiece).stream().filter(a -> Math.abs(a[0] - kingX) 
							== Math.abs(a[1] - kingY) && (a[0] - kingX > 0 ? 1 : -1) == coefficientX && 
							(a[1] - kingY > 0 ? 1 : -1) == coefficientY).forEach(c -> highlights.add(c));
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
				if (threatBoard.get(x).get(y).contains(chessPiece) && (chessBoard[x][y] == null 
					|| chessBoard[x][y].isWhitePiece() != whiteToMove) && 
					(!(chessPiece instanceof Pawn) || chessBoard[x][y] != null
					&& chessBoard[x][y].isWhitePiece() != whiteToMove))
				{
					highlights.add(new int[]{x,y});
				}	
			}
		}
		if (chessPiece instanceof Pawn) {
			pawnMoves.get(chessPiece).stream().forEach(a -> highlights.add(a));
		}
	}
			
			
	protected boolean surrounded(boolean isWhiteKing) {		
		chessPiece king = isWhiteKing ? whiteKing : blackKing;
		int kingX = king.getXCoordinate();
		int kingY = king.getYCoordinate();
		for (int x = kingX - 1; x <= kingX + 1; x++) {
			if(x < 0 || x > 7) {continue;}
			yloop:
			for (int y = kingY - 1; y <= kingY + 1; y++) {
				if (y < 0 || y > 7) {continue yloop;}
				if ((chessBoard[x][y] == null || chessBoard[x][y].isWhitePiece() != isWhiteKing)
					&& (threatBoard.get(x).get(y).stream().allMatch(p -> p.isWhitePiece() == isWhiteKing))) {
					int enemyX = 0;
					int enemyY = 0;
					int enemyX2 = 0;
					int enemyY2 = 0;
					chessPiece enemyNrTwo = null;
					if (checkingEnemy != null) {
						enemyX = checkingEnemy.getXCoordinate();
						enemyY = checkingEnemy.getYCoordinate();
						
						if (twoEnemies) {
							enemyNrTwo = threatBoard.get(kingX).get(kingY).stream().filter(a -> a != checkingEnemy && a.isWhitePiece() != king.isWhitePiece()).findFirst().get();
							enemyX2 = enemyNrTwo.getXCoordinate();
							enemyY2 = enemyNrTwo.getYCoordinate();
						}
					}
					else {
						return false;
					}
					
					if (((x == kingX && enemyX == kingX && y != enemyY) ||
						Math.abs(enemyX - kingX) == Math.abs(enemyY - kingY)
						&& Math.abs(enemyX - x) == Math.abs(enemyY - y)
						&& !(x == enemyX && y == enemyY) && !(checkingEnemy instanceof Pawn)) || (enemyNrTwo != null 
						&& ((y == kingY && enemyY2 == kingY && x != enemyX2) ||
						(x == kingX && enemyX2 == kingX && y != enemyY2) ||
						Math.abs(enemyX2 - kingX) == Math.abs(enemyY2 - kingY)
						&& Math.abs(enemyX2 - x) == Math.abs(enemyY2 - y)
						&& !(x == enemyX2 && y == enemyY2) && !(enemyNrTwo instanceof Pawn)))) 
					{
						continue yloop;	
					}
					return false;
				}
			}
		}
		return true;
	}
			
			
	protected boolean mustMoveKing(boolean isWhiteKing, chessPiece[][] board) {
		chessPiece king = isWhiteKing ? whiteKing : blackKing;
		Set<chessPiece> kingPosition = threatBoard.get(king.getXCoordinate()).get(king.getYCoordinate());
			
		switch(kingPosition.stream().filter(d -> d.isWhitePiece() != isWhiteKing).mapToInt(c -> 1).sum()) {
			case 0 : return false;
			case 2 : twoEnemies = true; inCheck = true; this.checkingEnemy = kingPosition.stream().filter(d -> d.isWhitePiece() != isWhiteKing).findFirst().get(); return true;
		}
		inCheck = true;
		chessPiece enemy = kingPosition.stream().filter(d -> d.isWhitePiece() != isWhiteKing).findFirst().get();		
		this.checkingEnemy = enemy;
		
		if (threatBoard.get(enemy.getXCoordinate()).get(enemy.getYCoordinate()).stream()
			.anyMatch(d -> d.isWhitePiece() == isWhiteKing && !(d instanceof King))) 
		{return false;}
		
		if(Math.abs(king.getXCoordinate() - enemy.getXCoordinate()) <= 1 && Math.abs(king.getYCoordinate() 
			- enemy.getYCoordinate()) <= 1 && enemy != longMovedPawn
			|| enemy instanceof Knight) {return true;}
		
		if (enemy == longMovedPawn) {
			final int finalEnemyX = enemy.getXCoordinate();
			final int finalEnemyY = enemy.getYCoordinate();
			return !pawnMoves.entrySet().stream().filter(a -> a.getKey().isWhitePiece() == isWhiteKing)
				.map(Entry::getValue).anyMatch(b -> b.stream().anyMatch(l ->  l[0] == finalEnemyX &&
				(finalEnemyX + 1 < 8 && chessBoard[finalEnemyX +1][finalEnemyY] instanceof Pawn && pawnMoves.get(chessBoard[finalEnemyX +1][finalEnemyY]).contains(l)
				|| finalEnemyX - 1 >= 0 && chessBoard[finalEnemyX -1][finalEnemyY] instanceof Pawn 
				&& pawnMoves.get(chessBoard[finalEnemyX -1][finalEnemyY]).contains(l))));
		}
		
		int kingX = king.getXCoordinate();
		int kingY = king.getYCoordinate();
		int enemyX = enemy.getXCoordinate();
		int enemyY = enemy.getYCoordinate();
		if (kingX == enemyX || kingY == enemyY) {
			int dynamicValue = enemyX != kingX ? enemyX : enemyY;
			boolean dynamicValueWasX = enemyX != kingX;
			int coefficient = dynamicValueWasX && enemyX - kingX > 0 || !dynamicValueWasX && enemyY - kingY > 0 ? -1 : 1;
			while (dynamicValueWasX && Math.abs(kingX - dynamicValue) != 1 ||
					!dynamicValueWasX && Math.abs(kingY - dynamicValue) != 1) {
				dynamicValue += coefficient;
				final int finalX = dynamicValueWasX ? dynamicValue : kingX;
				final int finalY = dynamicValueWasX ? kingY : dynamicValue;
				
				if (dynamicValueWasX && savingPawnAt(finalX,finalY, isWhiteKing) || savingPieceAt(finalX, finalY, isWhiteKing)) {
					return false;
				}
			}			
		} 
		else {
			int coefficientX = enemyX - kingX > 0 ? -1 : 1;
			int coefficientY = enemyY - kingY > 0 ? -1 : 1;
			while (Math.abs(kingX - enemyX) != 1) {
				enemyX += coefficientX;
				enemyY += coefficientY;
				if (savingPawnAt(enemyX,enemyY, isWhiteKing) || savingPieceAt(enemyX, enemyY, isWhiteKing)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public Collection<int[]> getHighlightsFor(chessPiece piece, chessPiece[][] board) {
		updateThreatBoard(board);
		saveHighlights(piece, board);
		Collection<int[]> returnCollection = new ArrayList<int[]>(this.highlights);
		highlights.clear();
		return returnCollection;
	}
	
	public void addToPieceStack(chessPiece piece) {
		movedPieceStack.add(piece);
	}
	
	public void saveMustMoveKing(chessPiece piece, chessPiece[][] board) {
		mustMoveKing(piece.isWhitePiece(), board);
	}
	
	public void resetThreatBoard() {
		updateThreatBoard(this.chessBoard);
	}
	
	public void setEngineAs(boolean isWhite) {
		engineIsWhite  = isWhite;
		this.chessEngine = new chessEngine(this);
		if (isWhite == whiteToMove) {
			Object[] move = chessEngine.findMove(4, this.chessBoard, whiteToMove);
			updateBoard((chessPiece)move[0], (int)move[1], (int)move[2]);
			if (listener != null) {
				listener.fireEventListener();
			}
		}
	}
	
	public void setEngineListener(engineListener listener) {
		this.listener = listener;
	}
	
	private boolean savingPawnAt(int x, int y, boolean isWhiteKing) {
		return pawnMoves.entrySet().stream().filter(e -> e.getKey().isWhitePiece()
			== isWhiteKing).map(Entry::getValue).anyMatch(m -> m.stream().anyMatch(a-> a[0] == x && a[1] == y));
	}
	
	private boolean savingPieceAt(int x, int y, boolean isWhiteKing) {
		return threatBoard.get(x).get(y).stream().anyMatch(d -> d.isWhitePiece() == isWhiteKing 
			&& !(d instanceof King) && !(d instanceof Pawn && chessBoard[x][y] == null));
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
	
	public void updatePawnMoves(Pawn piece, Collection<int[]> coordinates) {
		pawnMoves.put(piece, coordinates);
	}
	
	public Collection<int[]> getHighlights() {
		return this.highlights;
	}
	
	public int[] getHighlightedPiece() {
		return highlightedPiece != null ? new int[] {highlightedPiece.getXCoordinate(),
			highlightedPiece.getYCoordinate()} : null;
	}
	
	public chessPiece[][] getBoard() {
		return this.chessBoard;
	}
	
	public chessPiece[][] undo() {
		this.chessBoard = history.pop();
		inCheck = checkStack.pop();
		twoEnemies = twoEnemiesStack.pop();
		mustMoveKing = mustMoveStack.pop();
		checkingEnemy = enemyStack.pop();
		longMovedPawn = longMovedStack.pop();
		try {			
			movedPieceStack.pop().undoMove();
		} catch (NullPointerException e) {
			System.out.println("nullPointerexception linje 681");
		}
		return this.chessBoard;
	}
	
	public void saveBoard() {
		twoEnemiesStack.add(twoEnemies);
		history.add(cloneArray(this.chessBoard));
		checkStack.add(inCheck);
		mustMoveStack.add(mustMoveKing);
		enemyStack.add(checkingEnemy);
		longMovedStack.add(longMovedPawn);
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
		updateThreatBoard(this.chessBoard);
		history.add(cloneArray(this.chessBoard));
		mustMoveStack.add(false);
		checkStack.add(false);
		enemyStack.add(null);
		movedPieceStack.add(null);
		longMovedStack.add(null);
		twoEnemiesStack.add(false);
	}
	
}
