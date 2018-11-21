package application;

import java.awt.Point;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class LoAController
{
	@FXML
	GridPane grid;
	@FXML
	Label label;
	
	private Field fields[][];
	private Field selectedField;
	private Status currentPlayer, waitingPlayer;
	private ArrayList<Point> redPawns, blackPawns;
	private Bot bot;
	
	@FXML private void initialize()
	{	
		initFields();
		initPawns();
		selectedField = null;
		currentPlayer = Status.RED;
		waitingPlayer = Status.BLACK;
		bot = new Bot(MoveCounter.fieldsToStatus(fields));
	}
	
	private void initFields()
	{
		fields = new Field[8][8];
		
		for(int row = 0 ; row < 8 ; ++row)
			for(int column = 0 ; column < 8 ; ++column) 
				fields[row][column] = new Field(row, column, grid, this);
	}
	
	public void buttonClicked(int row, int column) 
	{
		//przypadek wybrania swojego pionka
		if(selectedField == null && fields[row][column].getStatus() == currentPlayer)
		{
			selectedField = fields[row][column];
			selectedField.setType(Type.SELECTED);
			showMoves(row, column);
						
			return;
		}
		
		// wybranie dostepnego miejsca ruchu po wybraniu pionka
		if(selectedField != null && fields[row][column].getType() == Type.MOVE)
		{	
			moveSelected(row, column);
			//checkWin();
			changePlayer();
		}
		
		clearBoard();
		selectedField = null;
	}
	//usuniecie pionka
	private void removePawn(int row, int column)
	{
		ArrayList<Point> pawns = waitingPlayer == Status.RED ? redPawns : blackPawns;
		pawns.remove(new Point(row, column));
	}
	//przeniesienie pionka po wybraniu wlasciwego pola do przeniesienia
	private void moveSelected(int row, int column)
	{
		ArrayList<Point> pawns = currentPlayer == Status.RED ? redPawns : blackPawns;
		
		for(int i = 0 ; i < pawns.size() ; ++i) 
			if(pawns.get(i).x == selectedField.getRow() && pawns.get(i).y == selectedField.getColumn()) 
			{
				pawns.get(i).move(row, column);
				break;
			}
		
		if(fields[row][column].getStatus() == waitingPlayer )
			removePawn(row, column);
		
		fields[row][column].setStatus(currentPlayer);
		selectedField.setStatus(Status.EMPTY);
	}
	
	private void clearBoard() 
	{
		for(int row = 0 ; row < 8 ; ++row)
			for( int column = 0 ; column < 8 ; ++column)
				fields[row][column].setType(Type.CLEAR);
	}
	
	private void changePlayer()
	{
		waitingPlayer = currentPlayer;
		currentPlayer = currentPlayer == Status.RED ? Status.BLACK : Status.RED;
		label.setText("Current Player: " + currentPlayer);
	}
	
	private void showMoves(int row, int column) 
	{
		ArrayList<Point> moves = MoveCounter.getMoves(fields, row, column, currentPlayer);
		
		moves.forEach((p)-> fields[p.x][p.y].setType(Type.MOVE));
	}
		
	private void initPawns()
	{
		redPawns = new ArrayList<Point>();
		blackPawns = new ArrayList<Point>();
		
		for(int i = 1 ; i < 7 ; ++i) 
		{
			fields[i][0].setStatus(Status.RED);
			fields[i][7].setStatus(Status.RED);
			fields[0][i].setStatus(Status.BLACK);
			fields[7][i].setStatus(Status.BLACK);
			
			redPawns.add(new Point(i, 0));
			redPawns.add(new Point(i, 7));
			blackPawns.add(new Point(0, i));
			blackPawns.add(new Point(7, i));
		}
	}
}
