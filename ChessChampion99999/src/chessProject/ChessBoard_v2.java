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
import java.util.function.Predicate;

public class ChessBoard_v2{	
	private boolean whiteToMove = true;
	private boolean highlighting  = true;
	private chessPiece whiteKing, blackKing;
	private String summary = "";
	private boolean inCheck = false;
	private boolean mustMoveKing = false;
	private boolean promotingPawn = false;
	private chessPiece checkingEnemy = null;
	private chessPiece promotingPiece = null;
	private chessPiece[][] chessBoard = new chessPiece[8][8];
	private chessPiece[][] copyOfBoard = new chessPiece[8][8];
	private final List<List<Set<chessPiece>>> threatBoard = new ArrayList<>();
	private Collection<int[]> highlights = new ArrayList<>();
	private Map<Pawn, Collection<int[]>> pawnMoves = new HashMap<>();
	private chessPiece highlightedPiece = null;
	private String moveString = "";

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
				
				chessBoard[rook.getXCoordinate()][rook.getYCoordinate()] = null;
				chessBoard[rook.getXCoordinate() + (currentX - x > 0 ? 3 : -2)][currentY] = rook;
				rook.setCoordinates(rook.getXCoordinate() + (currentX - x > 0 ? 3 : -2) ,currentY);
			} 
			
			chessBoard[piece.getXCoordinate()][piece.getYCoordinate()] = null;
			highlightedPiece = null;
			highlights.clear();
			
			if (piece instanceof Pawn) { 
				if (y == 7 || y == 0) {
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
		updateThreatBoard();
		
		boolean surrounded = surrounded();
		mustMoveKing = mustMoveKing();
		
		if (inCheck) {
			highlightedPiece = whiteToMove ? whiteKing : blackKing;
			moveString += surrounded && mustMoveKing ? "#" : "+";
		}
		
		summary += moveString + "\n" + (whiteToMove ? "White to move\n" : "Black to move\n") + this.toString() + "\n\n";
		
		if (surrounded) {
			if (mustMoveKing) {
				summary += "Check Mate!\n" + (whiteToMove ? "Black" : "White") + " wins!\nCongratulations!\n";
			} 
			else {
				for (chessPiece[] column : chessBoard) {
					for (chessPiece pieceAtXY : column) {
						if (pieceAtXY != null && pieceAtXY.isWhitePiece() == whiteToMove) {
							saveHiglights(pieceAtXY);
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
	
	public chessPiece[][] cloneArray(chessPiece[][] board) {
	    chessPiece[][] newBoard = new chessPiece[8][8];
	    for (int i = 0; i < 8; i++) {
	      newBoard[i] = board[i].clone();
	    }
	    return newBoard;
	}

			
	private String getMoveString(chessPiece piece, int x, int y) {
		String result = "";
		if (piece instanceof Pawn) {
			if (x != piece.getXCoordinate()) {
				result = Character.toString(("abcdefgh".charAt(piece.getXCoordinate()))) + "x";
			}
			result += Character.toString(("abcdefgh".charAt(x))) + Integer.toString(y+1);
			return result;
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
		} catch (NoSuchElementException e) {
		
		}
		if (chessBoard[x][y] != null) {
			result += "x";
		}
		
		return result + Character.toString(("abcdefgh".charAt(x))) + Integer.toString(y+1);
	}


	private void updateThreatBoard() {
		pawnMoves.entrySet().stream().filter(a -> a.getKey().isWhitePiece() == whiteToMove)
		.map(Entry::getKey).forEach(Pawn::setJustMovedFalse);
		
		pawnMoves.clear();
		threatBoard.stream().forEach(a -> a.stream().forEach(Set::clear));
		
		Arrays.stream(this.chessBoard).forEach(x -> Arrays.stream(x).filter(y -> y != null)
				.forEach(c -> c.placeThreats(this.chessBoard)));
	}
	
	public void getInput(int x, int y) {
		if (!promotingPawn &&!highlighting && highlights.stream().anyMatch(p -> p[0] == x && p[1] == y)) {
			updateBoard(highlightedPiece,x, y);
		}
		else if (!promotingPawn && chessBoard[x][y] != null && chessBoard[x][y].isWhitePiece() == whiteToMove) {
			highlights.clear();
			saveHiglights(chessBoard[x][y]);
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
	
	public void saveHiglights(chessPiece chessPiece) {
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
				for (int x = kingX - 1; x <= kingX + 1; x++) {
					if (x < 0 || x > 7 || (x == kingX && enemyX == kingX)) {continue;}
					yloop:
					for (int y = kingY - 1; y <= kingY + 1; y++) {
						if (y < 0 || y > 7 || (y == kingY && enemyY == kingY) ||
							Math.abs(enemyX - kingX) == Math.abs(enemyY - kingY)
							&& Math.abs(enemyX - x) == Math.abs(enemyY - y)
							&& !(x == enemyX && y == enemyY)) {continue yloop;}
						if ((chessBoard[x][y] == null || chessBoard[x][y].isWhitePiece() != chessPiece.isWhitePiece())
							&& threatBoard.get(x).get(y).stream().allMatch(p-> p.isWhitePiece() == whiteToMove))
						{
							highlights.add(new int[]{x,y});
						}
					}
				}
				return;
			}
			else {
				if (kingX == enemyX || kingY == enemyY) {
					int dynamicValue = enemyX != kingX ? enemyX : enemyY;
					boolean dynamicValueWasX = enemyX != kingX;
					int coefficient = dynamicValueWasX && enemyX - kingX > 0 || !dynamicValueWasX && enemyY - kingY > 0 ? -1 : 1;
					while (true) {
						if ((dynamicValueWasX ? kingX : kingY)-dynamicValue == 0) {break;}
						final int finalValueX = dynamicValueWasX ? dynamicValue : enemyX;
						final int finalValueY = dynamicValueWasX ? enemyY : dynamicValue;
						
						if (threatBoard.get(finalValueX).get(finalValueY).contains(chessPiece)
							&& (!(chessPiece instanceof Pawn) || (chessBoard[finalValueX][finalValueY] != null && chessBoard[finalValueX]
							[finalValueY].isWhitePiece() != whiteToMove)) || chessPiece instanceof Pawn 
							&& pawnMoves.get(chessPiece).stream().anyMatch(a -> a[0] == finalValueX && a[1] == finalValueY)) 
						{
							highlights.add(new int[]{finalValueX, finalValueY});
						}
						dynamicValue += coefficient;
					}
					if (checkingEnemy instanceof Pawn && chessPiece instanceof Pawn) {
						pawnMoves.get(chessPiece).stream().filter(a -> a[0] == checkingEnemy.getXCoordinate() 
						&& a[1] == checkingEnemy.getYCoordinate() - ((Pawn)chessPiece).getCoefficient())
						.forEach(a -> highlights.add(a));
					} 
				} 
				else if (Math.abs(enemyX - kingX) == Math.abs(enemyY - kingY)) {
					int coefficientX = enemyX - kingX > 0 ? -1 : 1;
					int coefficientY = enemyY - kingY > 0 ? -1 : 1;
						
					while (true) {
						if (enemyX-kingX == 0) {break;}
						final int finalValueX = enemyX;
						final int finalValueY = enemyY;
						
						if (threatBoard.get(enemyX).get(enemyY).contains(chessPiece) && (!(chessPiece instanceof Pawn) || 
							(chessBoard[enemyX][enemyY] != null && chessBoard[enemyX][enemyY].isWhitePiece() != whiteToMove))
							|| chessPiece instanceof Pawn && pawnMoves.get(chessPiece).stream()
							.anyMatch(a -> a[0] == finalValueX && a[1] == finalValueY))
						{
							highlights.add(new int[] {enemyX,enemyY});
						}
						enemyX += coefficientX;
						enemyY += coefficientY;
					}
					
					if (checkingEnemy instanceof Pawn && chessPiece instanceof Pawn) {
						pawnMoves.get(chessPiece).stream().filter(a -> a[0] == checkingEnemy.getXCoordinate()
						&& a[1] == checkingEnemy.getYCoordinate() - ((Pawn)chessPiece).getCoefficient())
						.forEach(b -> highlights.add(b));
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
				yloop:
				for (int y = kingY - 1; y <= kingY + 1; y++) {
					if (y < 0 || y > 7) {continue yloop;}
					if ((chessBoard[x][y] == null || chessBoard[x][y].isWhitePiece() != chessPiece.isWhitePiece())
						&& threatBoard.get(x).get(y).stream().allMatch(p-> p.isWhitePiece() == whiteToMove))
					{
						highlights.add(new int[]{x,y});
					}
				}
			}
			if (!((King)king).getHasMoved() && !inCheck) {
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
				final int finalX = dynamicValueWasX ? testValue : kingX;
				final int finalY = dynamicValueWasX ? kingY : testValue;
				if (chessBoard[finalX][finalY] != null) {
					if (chessBoard[finalX][finalY] == chessPiece) {
						clearHorisontal = true;
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
						Collection<int[]> threats = new ArrayList<>();
							
						int dynamicIndex = dynamicValueWasX ? kingX : kingY;
						while (dynamicIndex >= 0 && dynamicIndex <= 7) {
							dynamicIndex += coefficient;
							
							final int finalKingX = dynamicValueWasX ? dynamicIndex : kingX;
							final int finalKingY = dynamicValueWasX ?  kingY : dynamicIndex;
								
							if (chessBoard[finalKingX][finalKingY] == null || chessBoard[finalKingX][finalKingY].isWhitePiece() != whiteToMove) {
								threats.add(new int[]{finalKingX,finalKingY});
							} 
							if ((chessBoard[finalKingX][finalKingY] != null && chessBoard[finalKingX]
									[finalKingY].isWhitePiece() != whiteToMove)) {break;}
						}
						for (int[] threat : threats) {
							if (threatBoard.get(threat[0]).get(threat[1]).contains(chessPiece)) {
								if ((chessBoard[threat[0]][threat[1]] == null || chessBoard[threat[0]]
								[threat[1]].isWhitePiece() != chessPiece.isWhitePiece()) || !dynamicValueWasX &&
								(!(chessPiece instanceof Pawn) || chessBoard[threat[0]][threat[1]] != null 
								&& chessBoard[threat[0]][threat[1]].isWhitePiece() != chessPiece.isWhitePiece()))
								{
									highlights.add(threat);
								}
						
							}
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
						Collection<int[]> threats = new ArrayList<>();
						int indexX = kingX;
						int indexY = kingY;
						while (indexX >= 0 && indexX <= 7 && indexY >= 0 && indexY <= 7 && !(chessBoard[indexX][indexY] != null 
								&& chessBoard[indexX][indexY].isWhitePiece() != whiteToMove)) {
							indexX += coefficientX;
							indexY += coefficientY;
							if (chessBoard[indexX][indexY] == null || chessBoard[indexX][indexY].isWhitePiece() != whiteToMove) {
								threats.add(new int[]{indexX,indexY});
							}
						}
						for (int[] threat : threats) {
							if (threatBoard.get(threat[0]).get(threat[1]).contains(chessPiece) &&
								(chessBoard[threat[0]][threat[1]] == null || chessBoard[threat[0]][threat[1]].isWhitePiece() != chessPiece.isWhitePiece()) &&
								(!(chessPiece instanceof Pawn) || chessBoard[threat[0]][threat[1]] != null 
								&& chessBoard[threat[0]][threat[1]].isWhitePiece() != chessPiece.isWhitePiece()))
								{
									highlights.add(threat);
								}
						}
						if (chessPiece instanceof Pawn) {
							pawnMoves.get(chessPiece).stream().filter(a -> Math.abs(a[0] - kingX) 
							== Math.abs(a[1] - kingY)).filter(b -> (b[0] - kingX > 0 ? 1 : -1) == coefficientX && 
							(b[1] - kingY > 0 ? 1 : -1) == coefficientY).forEach(c -> highlights.add(c));
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
		
		if (chessPiece instanceof Pawn) {
			pawnMoves.get(chessPiece).stream().forEach(a -> highlights.add(a));
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
			case 2 : inCheck = true; return true;
		}
		inCheck = true;
		chessPiece enemy = kingPosition.stream().filter(d -> d.isWhitePiece() != whiteToMove).findFirst().get();		
		this.checkingEnemy = enemy;
		
		if (threatBoard.get(enemy.getXCoordinate()).get(enemy.getYCoordinate()).stream().anyMatch
				(d -> d.isWhitePiece() == whiteToMove && !(d instanceof King))) {return false;}
		if(Math.abs(king.getXCoordinate() - enemy.getXCoordinate()) <= 1 && Math.abs(king.getYCoordinate() 
				- enemy.getYCoordinate()) <= 1 && !(enemy instanceof Pawn && ((Pawn)enemy).getJustMovedLong())|| enemy instanceof Knight) 
		{
			return true;
		}
		if (enemy instanceof Pawn) {
			final int finalEnemyX = enemy.getXCoordinate();
			final int finalEnemyY = enemy.getYCoordinate();
			return pawnMoves.entrySet().stream().filter(a -> a.getKey().isWhitePiece() == whiteToMove)
				.map(Entry::getValue).anyMatch(b -> b.stream().anyMatch(l ->  l[0] == finalEnemyX &&
				(finalEnemyX + 1 < 8 && pawnMoves.get(chessBoard[finalEnemyX +1][finalEnemyY]).contains(l)
				|| finalEnemyX - 1 >= 0 && pawnMoves.get(chessBoard[finalEnemyX -1][finalEnemyY]).contains(l))));
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
		
		return !threatenedIndexes.stream().anyMatch(g -> threatBoard.get(g[0]).get(g[1]).stream()
			.anyMatch(d -> d.isWhitePiece() == whiteToMove && !(d instanceof King) && (!(d instanceof Pawn)
			|| chessBoard[g[0]][g[1]] != null || pawnMoves.get(d).stream().anyMatch(a -> a[0] == g[0] && a[1] == g[1]))));
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
}
