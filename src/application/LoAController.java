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
		
		if (selectedField == null && fields[row][column].getStatus() == currentPlayer)
		{
			fields[row][column].setType(Type.SELECTED);
			selectedField = fields[column][row];
			showMoves(row, column);
			//selectedField = null;
			//currentPlayer = (currentPlayer == Status.RED) ? Status.BLACK : Status.RED;
		}
		else if (selectedField != null && fields[column][row].getType() == Type.MOVE)
		{
			
			// kod wykonujacy ruch
			clearBoard();
			
		}	
	}
	
	private void clearBoard() {
		for(int r = 0 ; r < 8 ; ++r)
			for( int c = 0 ; c < 8 ; ++c)
				fields[r][c].setType(Type.CLEAR);
	}
	
	private void showMoves(int row, int column) {
		boolean tab[][] = setMoves(row,column);
		
		for(int r = 0 ; r < 8 ; ++r)
			for( int c = 0 ; c < 8 ; ++c)
				if(tab[r][c] == true)
				{
					fields[r][c].setType(Type.MOVE);
				}	
	}
	

	
	private void initPawns() {
		
		for(int i = 1 ; i < 7 ; ++i) {
			fields[i][0].setStatus(Status.RED);
			fields[i][7].setStatus(Status.RED);
			fields[0][i].setStatus(Status.BLACK);
			fields[7][i].setStatus(Status.BLACK);
		}
	}
	
	private int[] showRange (int row, int column) {
		//tablica zasiegow 
		int range[] = {0, 0, 0, 0};
		
		// zasieg ruchu w poziomie
		for (int c = 0 ; c < 8 ; ++c)
			if(fields[row][c].getStatus() != Status.EMPTY) range[0]++;
		
		// zasieg ruchu w pionie
		for (int r = 0 ; r < 8 ; ++r)
			if(fields[r][column].getStatus() != Status.EMPTY) range[1]++;
		
		// zasieg ruchu po skosie "funkcja rosnaca"
		for(int i = 1; row + i < 8 && column + i < 8 ; ++i)
			if (fields[row+i][column + i].getStatus() != Status.EMPTY) range[2]++;

		for (int i = 0; row - i >= 0 && column - i >= 0; ++i)
			if (fields[row - i][column - i].getStatus() != Status.EMPTY) range[2]++;
		
		// zasieg ruchu po skosie "funkcja malejaca"
		for(int i = 1; row + i < 8 && column - i >= 0 ; ++i)
			if (fields[row + i][column - i].getStatus() != Status.EMPTY) range[3]++;

		for (int i = 0; row - i >= 0 && column + i < 8; ++i)
			if (fields[row - i][column + i].getStatus() != Status.EMPTY) range[3]++;

		return range;
		
	}
	
	private boolean[][] setMoves(int row, int column) {
		int range[] = showRange(row, column);
		boolean possibleMove[][] = new boolean[8][8];
		
		// KTORE POLA MOZE ZAJAC KOLEJNO W POZIOMIE, PIONIE, SKOS (ROSNACO), SKOS (MALEJACO)
		if(column - range[0] >= 0) possibleMove[row][column - range[0]] = true;
		if(column + range[0] <= 7) possibleMove[row][column + range[0]] = true;
		
		if(row - range[1] >= 0) possibleMove[row - range[1]][column] = true;
		if(row + range[1] <= 7) possibleMove[row + range[1]][column] = true;
		
		if(column - range[2] >= 0 && row - range[2] >= 0) possibleMove[row - range[2]][column - range[2]] = true;
		if(column + range[2] <= 7 && row + range[2] <= 7) possibleMove[row + range[2]][column + range[2]] = true;
		
		
		if(row - range[3] >= 0 && column + range[3] <= 7) possibleMove[row - range[3]][column + range[3]] = true;
		if(row + range[3] <= 7 && column - range[3] >= 0) possibleMove[row + range[3]][column - range[3]] = true;
		
		
		return possibleMove;		
		
	}
	
	
}
