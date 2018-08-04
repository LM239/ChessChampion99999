package chessProject;

import java.util.ArrayList;
import java.util.Collection;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class GUIController_v2 {
	@FXML ImageView field;
	@FXML TextArea summaryField;
	@FXML AnchorPane chessGame;
	
	@FXML Button highlightToggle;
	
	private Image nothing;
	private ChessBoard_v2 game;
	
	private boolean highlight = true;
	private Collection<int[]> currentHighlights = new ArrayList<>();

	public void initialize() {
		game = new ChessBoard_v2();
		nothing = new Image(GUIController_v2.class.getResource("/nothing.png").toExternalForm());
		
		for (int y = 0; y < 8; y++) {
			AnchorPane yColumn = (AnchorPane) chessGame.lookup("#" + String.valueOf(y));
			for (int x = 0; x< 8; x++) {
				ImageView imageField = ((ImageView) yColumn.lookup("#" + String.valueOf(x)
				+ String.valueOf(y)).lookup("#i" + String.valueOf(x) + String.valueOf(y)));
				
				System.out.println(String.valueOf(x) + String.valueOf(y));
				imageField.setOnMouseClicked(e -> sendInput(imageField));
			}
		}
		update();
	}
	
	public void update() {
		summaryField.appendText(game.getSummary());
		summaryField.setScrollTop(Double.MAX_VALUE);
		clearHighlights();
		
		if (highlight) {
			placeHighlights(game.getHighlights());
		} 
		else {
			placeHighlightedPiece();
		}
		updateUserBoard(game.getBoard());
	}

	private void clearHighlights() {
		for (int[] xyTuple : currentHighlights) {
			int x = xyTuple[0];
			int y = xyTuple[1];
			chessGame.lookup("#" + String.valueOf(y))
			.lookup("#" + String.valueOf(x) + String.valueOf(y))
			.setStyle((x + y + 2)%2 == 0 ? "-fx-background-color: GRAY" : "-fx-background-color: WHITE" );
		}
	}

	private void placeHighlights(Collection<int[]> highlights) {
		for (int[] xyTuple : highlights) {
			currentHighlights.add(xyTuple);
			int x = xyTuple[0];
			int y = xyTuple[1];
			chessGame.lookup("#" + String.valueOf(y))
			.lookup("#" + String.valueOf(x) + String.valueOf(y)).setStyle("-fx-background-color: LIGHTBLUE");
		}
		placeHighlightedPiece();
	}

	@FXML
	public void sendInput(ImageView field){
		int xCoor = Character.getNumericValue(field.getId().charAt(1));
		int yCoor = Character.getNumericValue(field.getId().charAt(2));
		
		System.out.println("game.getInput(" + Integer.toString(xCoor) + "," + Integer.toString(yCoor) + ");");
		game.getInput(xCoor,yCoor);
		update();
	}
	
	
	private void updateUserBoard(chessPiece[][] board) {
		for (int y = 0; y < 8; y++) {
			AnchorPane yColumn = (AnchorPane) chessGame.lookup("#" + String.valueOf(y));
			for (int x = 0; x < 8; x++) {
				chessPiece piece = board[x][y];
			
				((ImageView) yColumn
				.lookup("#" + String.valueOf(x) + String.valueOf(y))
				.lookup("#i" + String.valueOf(x) + String.valueOf(y)))
				.setImage(piece == null ? nothing : piece.getImage());	
			}
		}
	}
	
	public void toggleHighlights() {
		highlight = !highlight;
		highlightToggle.setText(highlight ? "Switch highlight[OFF]" : "Switch highlight[ON]");
		if (highlight) {
			placeHighlights(game.getHighlights());
		}
		else {
			clearHighlights();
			placeHighlightedPiece();
		}
	}

	private void placeHighlightedPiece() {
		int[] piece = game.getHighlightedPiece();
		if (piece != null) {
			currentHighlights.add(piece);
			chessGame.lookup("#" + String.valueOf(piece[1]))
			.lookup("#" + String.valueOf(piece[0]) + String.valueOf(piece[1]))
			.setStyle("-fx-background-color: LIGHTPINK");
		}
	}
}