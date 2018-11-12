package application;

import java.awt.Point;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class LoAController {

	@FXML
	GridPane grid;
	
	@FXML
	Label label;
	
	//BUTTONY UZYWAMY ICH DALEJ W KODZIE JAK CHCEMY POKOLORWAC JAKAS PLYTKE ALBO NANIESC NA NIA PIONEK
	private Button cells[][];
	
	//POLOZENIE PIONKOW 0 puste, 1 czerwony pionek, -1 niebieski pionek
	private int pawns[][];
	
	// nie trzeba tlumaczyc xd
	private Point selectedPoint;
	
	// true oznacza ze wybrany pionek moze sie ruszyc na to miejsce, false ze nie
	private boolean possibleMoves[][];
	
	// TO NAM MOWI CZY JAKIS PIONEK JEST WYBRANY TERAZ CZY NIE
	private boolean isAnySelected;
	
	//TO POPRAWIA CZYELNOSC KODU
	private enum Player{BLUE, RED}
	
	// TO NAM MOWI KTORY GRACZ TERAZ MA TURE
	private Player currentPlayer;
	
	@FXML private void initialize(){	
		initButtons();
		initPawns();
		paintBoard();
		selectedPoint = new Point(); //  domyslnie jest 0,0 jak checie zmienic to uzywacie .move(x, y)
		possibleMoves = new boolean[8][8]; // domyslnie wszedzie na false
		isAnySelected = false;
		currentPlayer = Player.BLUE;
	}
	
	private void initButtons(){
		cells = new Button[8][8];
		
		for(int row = 0 ; row < 8 ; ++row)
			for( int column = 0 ; column < 8 ; ++column) {
				
				cells[row][column] = new Button();
				final int i = row, j = column;
				cells[row][column].setOnAction(e -> buttonClicked(i, j));
				cells[row][column].setPrefSize(80, 80);
				grid.add(cells[row][column], column, row);
			}
	}
	
	private void buttonClicked(int row, int column) {
		
	}
	
	private void initPawns() {
		
		pawns = new int[8][8];
		
		Image blackPawn = new Image(getClass().getResourceAsStream("black.png"));
		Image redPawn   = new Image(getClass().getResourceAsStream("red.png"));
		
		for(int i = 1 ; i < 7 ; ++i) {
			cells[i][0].setGraphic(new ImageView(redPawn));
			cells[i][7].setGraphic(new ImageView(redPawn));
			cells[0][i].setGraphic(new ImageView(blackPawn));
			cells[7][i].setGraphic(new ImageView(blackPawn));
			
			pawns[i][0] = pawns[i][7] = 1;
			pawns[0][i] = pawns[7][i] = -1;
		}
	}
	
	private void paintBoard() {
		
		for(int row = 0 ; row < 8 ; ++row)
			for( int column = 0 ; column < 8 ; ++column) 
				if( (row+column)%2 == 0) 
					cells[row][column].setStyle("-fx-background-color: #ffffff; ");
				else
					cells[row][column].setStyle("-fx-background-color: #D3D3D3; ");
			
	}
	
}
