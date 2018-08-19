package chessProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Stack;

public class chessEngine {
	private ChessBoard_v2 game;
	private Map<Character, Double[][]> posValues = new HashMap<>();
	private Stack<Boolean> kingMoved = new Stack<>();
	
	public chessEngine(ChessBoard_v2 game) {
		this.game = game;
		String s = "BKNPQR";
		for (int i = 0; i < s.length(); i++){
		char c = s.charAt(i);
		File file = new File(chessEngine.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "/" + c + ".txt");
	 
	Scanner scanner = null;
			try {
				scanner = new Scanner(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
	String[] values = scanner.next().trim().split(",");
	Double[][] result = new Double[8][8];
	for (int y = 0; y < 8; y++) {
		for (int x = 0; x < 8; x++) {
			result[y][x] = Double.valueOf(values[7*y + x].trim());
		}
	}
	scanner.close();
	posValues.put(c, result);
		}
	}
	
	public Object[] findMove(int depth, chessPiece[][] board, boolean whiteToMove) {
		Map<chessPiece, Collection<int[]>> possibleMoves = getPossibleMoves(board, whiteToMove);
		double bestMoveValue = -9999;
		Object[] bestMoveFound = new Object[3];
		for (Entry<chessPiece, Collection<int[]>> pieceEntry : possibleMoves.entrySet()) {
			for (int[] move : pieceEntry.getValue()) {
				chessPiece piece = pieceEntry.getKey();
				makeMove(board, piece, move);
				double value = alphaBetaMax(-10000, 10000, depth, board, whiteToMove);
				board = game.undo();
				if (piece instanceof King) {
					((King)piece).setHasMovedTest(kingMoved.pop());
				}
				if(value >= bestMoveValue) {
					bestMoveValue = value;
					bestMoveFound[0] = pieceEntry.getKey();
					bestMoveFound[1] = move[0];
					bestMoveFound[2] = move[1];
				}
			} 
		}
		game.resetThreatBoard();
		return bestMoveFound;
	}
	
	private double alphaBetaMax(double alpha, double beta, int depthleft, chessPiece[][] board, boolean playAsWhite) {
		if (depthleft == 0) { 
			return evaluateBoard(board, !playAsWhite);
		}
		Map<chessPiece, Collection<int[]>> possibleMoves = getPossibleMoves(board, !playAsWhite);
		for (Entry<chessPiece, Collection<int[]>> pieceEntry : possibleMoves.entrySet()) {
			for (int[] move : pieceEntry.getValue()) {
				chessPiece piece = pieceEntry.getKey();
				makeMove(board, piece, move);
				double score = alphaBetaMin(alpha, beta, depthleft - 1, board, !playAsWhite);
				board = game.undo();
				if (piece instanceof King) {
					((King)piece).setHasMovedTest(kingMoved.pop());
				}
				if( score >= beta ) {					
					return beta;// fail hard beta-cutoff
				}
				if( score > alpha ) {					
					alpha = score; // alpha acts like max in MiniMax
				}
			} 
		}
		return alpha;
	}
	
	private double alphaBetaMin( double alpha, double beta, int depthleft, chessPiece[][] board, boolean playAsWhite) {
		if (depthleft == 0) {
			return -evaluateBoard(board, playAsWhite);
		}
		Map<chessPiece, Collection<int[]>> possibleMoves = getPossibleMoves(board, !playAsWhite);
		for (Entry<chessPiece, Collection<int[]>> pieceEntry : possibleMoves.entrySet()) {
			for (int[] move : pieceEntry.getValue()) {
				chessPiece piece = pieceEntry.getKey();
				makeMove(board, piece, move);
				double score = alphaBetaMax(alpha, beta, depthleft - 1, board, playAsWhite);
				board = game.undo();
				if (piece instanceof King) {
					((King)piece).setHasMovedTest(kingMoved.pop());
				}
				if(score <= alpha) {					
					return alpha; // fail hard alpha-cutoff
				}
				if(score < beta) {					
					beta = score; // beta acts like min in MiniMax
				}
			} 
		}
		return beta;
	}

	private void makeMove(chessPiece[][] board, chessPiece piece, int[] move) {
		game.saveMustMoveKing(piece, board);
		game.saveBoard();
		game.addToPieceStack(piece);
		if (piece instanceof Pawn && (move[1] == 0 || move[1] == 7)) {
			board[move[0]][move[1]] = new Queen(move[0],move[1], piece.isWhitePiece());
		}
		else {
			board[move[0]][move[1]] = piece;	
			if (piece instanceof King) {
				this.kingMoved.add(((King)piece).hasMovedTest());
				((King)piece).setHasMovedTest(true);
			}
		}
		board[piece.getXCoordinate()][piece.getYCoordinate()] = null;
		piece.setCoordinates(move[0], move[1]);
		game.updateThreatBoard(board);
	}
	

	private double evaluateBoard(chessPiece[][] board, boolean playAsWhite) {
		double totalEvaluation = 0;
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				totalEvaluation += getPieceValue(board[x][y], x ,y);
			}
		}
		return totalEvaluation;
	}

	private Map<chessPiece, Collection<int[]>> getPossibleMoves(chessPiece[][] chessBoard, boolean whiteToMove) {
		Map<chessPiece, Collection<int[]>> possibleMoves = new HashMap<>();
		for (chessPiece[] column : chessBoard) {
			for (chessPiece piece : column) {
				if (piece != null && piece.isWhitePiece() == whiteToMove) {
					possibleMoves.put(piece, game.getHighlightsFor(piece, chessBoard));
				}
			}
		}
		return possibleMoves;
	}
	
	private double getPieceValue(chessPiece piece, int x, int y) {
		if (piece == null) {
			return 0;
		}
		boolean isWhite = piece.isWhitePiece();
		double returnValue = 0;

		if (piece instanceof Pawn) {
			returnValue = 10 + ( isWhite ? posValues.get('P')[y][x] : posValues.get('P')[7 - y][x] );
		} else if (piece instanceof Rook) {
			returnValue = 50 + ( isWhite ? posValues.get('R')[y][x] : posValues.get('R')[7 - y][x] );
		} else if (piece instanceof Knight) {
			returnValue = 30 + posValues.get('N')[y][x];
		} else if (piece instanceof Bishop) {
			returnValue = 30 + ( isWhite ? posValues.get('B')[y][x] : posValues.get('B')[7 - y][x] );
		} else if (piece instanceof Queen) {
			returnValue = 90 + posValues.get('Q')[y][x];
		} else if (piece instanceof King) {
			returnValue = 900 + ( isWhite ? posValues.get('K')[y][x] : posValues.get('K')[7 - y][x] );
		}
		return isWhite ? returnValue : -returnValue;
	}
}
