package chessProject;

import java.util.Collection;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class GUIController_v2 {
	StackPane[][] fields = new StackPane[8][8];
	ImageView[][] ifields = new ImageView[8][8];
	
	@FXML ImageView field;
	@FXML TextArea summaryField;
	
	@FXML ImageView izeroseven;
	@FXML ImageView ioneseven;
	@FXML ImageView itwoseven;
	@FXML ImageView ithreeseven;
	@FXML ImageView ifourseven;
	@FXML ImageView ifiveseven;
	@FXML ImageView isixseven;
	@FXML ImageView isevenseven;
	@FXML ImageView izerosix;
	@FXML ImageView ionesix;
	@FXML ImageView itwosix;
	@FXML ImageView ithreesix;
	@FXML ImageView ifoursix;
	@FXML ImageView ifivesix;
	@FXML ImageView isixsix;
	@FXML ImageView isevensix;
	@FXML ImageView izerofive;
	@FXML ImageView ionefive;
	@FXML ImageView itwofive;
	@FXML ImageView ithreefive;
	@FXML ImageView ifourfive;
	@FXML ImageView ifivefive;
	@FXML ImageView isixfive;
	@FXML ImageView isevenfive;
	@FXML ImageView izerofour;
	@FXML ImageView ionefour;
	@FXML ImageView itwofour;
	@FXML ImageView ithreefour;
	@FXML ImageView ifourfour;
	@FXML ImageView ifivefour;
	@FXML ImageView isixfour;
	@FXML ImageView isevenfour;
	@FXML ImageView izerothree;
	@FXML ImageView ionethree;
	@FXML ImageView itwothree;
	@FXML ImageView ithreethree;
	@FXML ImageView ifourthree;
	@FXML ImageView ifivethree;
	@FXML ImageView isixthree;
	@FXML ImageView iseventhree;
	@FXML ImageView izerotwo;
	@FXML ImageView ionetwo;
	@FXML ImageView itwotwo;
	@FXML ImageView ithreetwo;
	@FXML ImageView ifourtwo;
	@FXML ImageView ifivetwo;
	@FXML ImageView isixtwo;
	@FXML ImageView iseventwo;
	@FXML ImageView izeroone;
	@FXML ImageView ioneone;
	@FXML ImageView itwoone;
	@FXML ImageView ithreeone;
	@FXML ImageView ifourone;
	@FXML ImageView ifiveone;
	@FXML ImageView isixone;
	@FXML ImageView isevenone;
	@FXML ImageView izerozero;
	@FXML ImageView ionezero;
	@FXML ImageView itwozero;
	@FXML ImageView ithreezero;
	@FXML ImageView ifourzero;
	@FXML ImageView ifivezero;
	@FXML ImageView isixzero;
	@FXML ImageView isevenzero;
	
	@FXML StackPane zeroseven;
	@FXML StackPane oneseven;
	@FXML StackPane twoseven;
	@FXML StackPane threeseven;
	@FXML StackPane fourseven;
	@FXML StackPane fiveseven;
	@FXML StackPane sixseven;
	@FXML StackPane sevenseven;
	@FXML StackPane zerosix;
	@FXML StackPane onesix;
	@FXML StackPane twosix;
	@FXML StackPane threesix;
	@FXML StackPane foursix;
	@FXML StackPane fivesix;
	@FXML StackPane sixsix;
	@FXML StackPane sevensix;
	@FXML StackPane zerofive;
	@FXML StackPane onefive;
	@FXML StackPane twofive;
	@FXML StackPane threefive;
	@FXML StackPane fourfive;
	@FXML StackPane fivefive;
	@FXML StackPane sixfive;
	@FXML StackPane sevenfive;
	@FXML StackPane zerofour;
	@FXML StackPane onefour;
	@FXML StackPane twofour;
	@FXML StackPane threefour;
	@FXML StackPane fourfour;
	@FXML StackPane fivefour;
	@FXML StackPane sixfour;
	@FXML StackPane sevenfour;
	@FXML StackPane zerothree;
	@FXML StackPane onethree;
	@FXML StackPane twothree;
	@FXML StackPane threethree;
	@FXML StackPane fourthree;
	@FXML StackPane fivethree;
	@FXML StackPane sixthree;
	@FXML StackPane seventhree;
	@FXML StackPane zerotwo;
	@FXML StackPane onetwo;
	@FXML StackPane twotwo;
	@FXML StackPane threetwo;
	@FXML StackPane fourtwo;
	@FXML StackPane fivetwo;
	@FXML StackPane sixtwo;
	@FXML StackPane seventwo;
	@FXML StackPane zeroone;
	@FXML StackPane oneone;
	@FXML StackPane twoone;
	@FXML StackPane threeone;
	@FXML StackPane fourone;
	@FXML StackPane fiveone;
	@FXML StackPane sixone;
	@FXML StackPane sevenone;
	@FXML StackPane zerozero;
	@FXML StackPane onezero;
	@FXML StackPane twozero;
	@FXML StackPane threezero;
	@FXML StackPane fourzero;
	@FXML StackPane fivezero;
	@FXML StackPane sixzero;
	@FXML StackPane sevenzero;
	
	@FXML AnchorPane contentBox;
	
	Image nothing;

	ChessBoard_v2 game;

	public void initialize() {
		game = new ChessBoard_v2(this);
		fields[0][0] = zerozero;
		fields[0][1] = zeroone;
		fields[0][2] = zerotwo;
		fields[0][3] = zerothree;
		fields[0][4] = zerofour;
		fields[0][5] = zerofive;
		fields[0][6] = zerosix;
		fields[0][7] = zeroseven;
		fields[1][0] = onezero;
		fields[1][1] = oneone;
		fields[1][2] = onetwo;
		fields[1][3] = onethree;
		fields[1][4] = onefour;
		fields[1][5] = onefive;
		fields[1][6] = onesix;
		fields[1][7] = oneseven;
		fields[2][0] = twozero;
		fields[2][1] = twoone;
		fields[2][2] = twotwo;
		fields[2][3] = twothree;
		fields[2][4] = twofour;
		fields[2][5] = twofive;
		fields[2][6] = twosix;
		fields[2][7] = twoseven;
		fields[3][0] = threezero;
		fields[3][1] = threeone;
		fields[3][2] = threetwo;
		fields[3][3] = threethree;
		fields[3][4] = threefour;
		fields[3][5] = threefive;
		fields[3][6] = threesix;
		fields[3][7] = threeseven;
		fields[4][0] = fourzero;
		fields[4][1] = fourone;
		fields[4][2] = fourtwo;
		fields[4][3] = fourthree;
		fields[4][4] = fourfour;
		fields[4][5] = fourfive;
		fields[4][6] = foursix;
		fields[4][7] = fourseven;
		fields[5][0] = fivezero;
		fields[5][1] = fiveone;
		fields[5][2] = fivetwo;
		fields[5][3] = fivethree;
		fields[5][4] = fivefour;
		fields[5][5] = fivefive;
		fields[5][6] = fivesix;
		fields[5][7] = fiveseven;
		fields[6][0] = sixzero;
		fields[6][1] = sixone;
		fields[6][2] = sixtwo;
		fields[6][3] = sixthree;
		fields[6][4] = sixfour;
		fields[6][5] = sixfive;
		fields[6][6] = sixsix;
		fields[6][7] = sixseven;
		fields[7][0] = sevenzero;
		fields[7][1] = sevenone;
		fields[7][2] = seventwo;
		fields[7][3] = seventhree;
		fields[7][4] = sevenfour;
		fields[7][5] = sevenfive;
		fields[7][6] = sevensix;
		fields[7][7] = sevenseven;
		
		ifields[0][0] = izerozero;
		ifields[0][1] = izeroone;
		ifields[0][2] = izerotwo;
		ifields[0][3] = izerothree;
		ifields[0][4] = izerofour;
		ifields[0][5] = izerofive;
		ifields[0][6] = izerosix;
		ifields[0][7] = izeroseven;
		ifields[1][0] = ionezero;
		ifields[1][1] = ioneone;
		ifields[1][2] = ionetwo;
		ifields[1][3] = ionethree;
		ifields[1][4] = ionefour;
		ifields[1][5] = ionefive;
		ifields[1][6] = ionesix;
		ifields[1][7] = ioneseven;
		ifields[2][0] = itwozero;
		ifields[2][1] = itwoone;
		ifields[2][2] = itwotwo;
		ifields[2][3] = itwothree;
		ifields[2][4] = itwofour;
		ifields[2][5] = itwofive;
		ifields[2][6] = itwosix;
		ifields[2][7] = itwoseven;
		ifields[3][0] = ithreezero;
		ifields[3][1] = ithreeone;
		ifields[3][2] = ithreetwo;
		ifields[3][3] = ithreethree;
		ifields[3][4] = ithreefour;
		ifields[3][5] = ithreefive;
		ifields[3][6] = ithreesix;
		ifields[3][7] = ithreeseven;
		ifields[4][0] = ifourzero;
		ifields[4][1] = ifourone;
		ifields[4][2] = ifourtwo;
		ifields[4][3] = ifourthree;
		ifields[4][4] = ifourfour;
		ifields[4][5] = ifourfive;
		ifields[4][6] = ifoursix;
		ifields[4][7] = ifourseven;
		ifields[5][0] = ifivezero;
		ifields[5][1] = ifiveone;
		ifields[5][2] = ifivetwo;
		ifields[5][3] = ifivethree;
		ifields[5][4] = ifivefour;
		ifields[5][5] = ifivefive;
		ifields[5][6] = ifivesix;
		ifields[5][7] = ifiveseven;
		ifields[6][0] = isixzero;
		ifields[6][1] = isixone;
		ifields[6][2] = isixtwo;
		ifields[6][3] = isixthree;
		ifields[6][4] = isixfour;
		ifields[6][5] = isixfive;
		ifields[6][6] = isixsix;
		ifields[6][7] = isixseven;
		ifields[7][0] = isevenzero;
		ifields[7][1] = isevenone;
		ifields[7][2] = iseventwo;
		ifields[7][3] = iseventhree;
		ifields[7][4] = isevenfour;
		ifields[7][5] = isevenfive;
		ifields[7][6] = isevensix;
		ifields[7][7] = isevenseven;
		
		nothing = new Image(GUIController_v2.class.getResource("/nothing.png").toExternalForm());
		
		for (ImageView[] fieldRow : ifields) {
			for (ImageView field : fieldRow) {
				field.setOnMouseClicked(e -> sendInput(field));
			}
		}
		update();
	}
	
	public void update() {
		summaryField.appendText(game.getSummary());
		summaryField.appendText("");
		summaryField.setScrollTop(Double.MAX_VALUE);
		
		clearHighlights();
		placeHiglights(game.getHighlights());
		updateUserBoard(game.getBoard());
	}

	private void clearHighlights() {
		boolean whiteSquare = true;
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				fields[x][y].setStyle(whiteSquare ? "-fx-background-color: WHITE" 
						: "-fx-background-color: GRAY" );
				whiteSquare = whiteSquare ? false : true;
			}
			whiteSquare = whiteSquare ? false : true;
		}
	}

	private void placeHiglights(Collection<int[]> highlights) {
		for (int[] xyTuple : highlights) {
			fields[xyTuple[0]][xyTuple[1]].setStyle("-fx-background-color: LIGHTBLUE");
		}
		int[] piece = game.getHighlightedPiece();
		if (piece != null) {
			fields[piece[0]][piece[1]].setStyle("-fx-background-color: LIGHTPINK");
		}
	}

	@FXML
	public void sendInput(ImageView field){
		int xCoor = 0;
		int yCoor = 0;
		
		xyloop:
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y< 8; y++) {
				if (ifields[x][y] == field) {
					xCoor = x;
					yCoor = y;
					break xyloop;
				}
			}
		}
		System.out.println(Integer.toString(xCoor) + "," + Integer.toString(yCoor));
		game.getInput(new int[] {xCoor,yCoor});
		update();
	}
	
	
	private void updateUserBoard(chessPiece[][] board) {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				chessPiece piece = board[x][y];
				if (piece == null) {
					ifields[x][y].setImage(nothing);
				} 
				else {
					ifields[x][y].setImage(piece.getImage());
				}
			}
		}
	}
}