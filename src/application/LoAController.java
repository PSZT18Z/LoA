package application;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class LoAController {

	@FXML
	GridPane grid;
	
	@FXML
	Label label;
	
	private Field fields[][];
	
	// nie trzeba tlumaczyc xd
	private Field selectedField;
	
	// TO NAM MOWI KTORY GRACZ TERAZ MA TURE
	private Status currentPlayer;
	
	@FXML private void initialize(){	
		initFields();
		initPawns();
		selectedField = null;
		currentPlayer = Status.RED;
	}
	
	private void initFields(){
		fields = new Field[8][8];
		
		for(int row = 0 ; row < 8 ; ++row)
			for( int column = 0 ; column < 8 ; ++column) 
				fields[row][column] = new Field(row, column, grid, this);
	}
	
	public void buttonClicked(int row, int column) {
		
	}
	
	private void initPawns() {
		
		for(int i = 1 ; i < 7 ; ++i) {
			fields[i][0].setStatus(Status.RED);
			fields[i][7].setStatus(Status.RED);
			fields[0][i].setStatus(Status.BLACK);
			fields[7][i].setStatus(Status.BLACK);
		}
	}
}
