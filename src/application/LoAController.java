package application;

import java.awt.Point;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

//Kontroluje okienko gry
public class LoAController
{
	@FXML
	GridPane grid;
	@FXML
	Label label;
	
	private Field fields[][]; // plansza gry
	private Field selectedField; // obecnie wybrane pole, jesli zadno nie jest wybrane to null
	private Status currentPlayer, waitingPlayer;
	private ArrayList<Point> redPawns, blackPawns;
	private Bot bot;
	
	@FXML private void initialize()
	{	
		initFields();
		initPawns();
		selectedField = null;
		currentPlayer = Status.BLACK; // czarny zaczyna
		waitingPlayer = Status.RED;
		bot = new Bot(BoardManager.fieldsToStatus(fields), this); // Stworzenie bota, podajemu mu ten controller oraz plansze gry skonwertowana na prostsza postac
	}
	
	//inicjalizacja planszy
	private void initFields()
	{
		fields = new Field[8][8];
		
		for(int row = 0 ; row < 8 ; ++row)
			for(int column = 0 ; column < 8 ; ++column) 
				fields[row][column] = new Field(row, column, grid, this);
	}
	
	//inicjaliza pionków
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
	
	//wyczyszczenie koloru wszystkich pól planszy na domyślne(biale/szare)
	private void clearBoard() 
	{
		for(int row = 0 ; row < 8 ; ++row)
			for( int column = 0 ; column < 8 ; ++column)
				fields[row][column].setType(Type.CLEAR);
	}
	
	// pokazanie pól na które może ruszyc sie obecnie wybrany pionek
	private void showMoves(int row, int column) 
	{
		ArrayList<Point> moves = BoardManager.getMoves(fields, row, column, currentPlayer);
		
		moves.forEach((p)-> fields[p.x][p.y].setType(Type.MOVE));
	}
	
	//usuniecie pionka(gdy zostal zbity przez przeciwnika)
	private void removePawn(int row, int column)
	{
		ArrayList<Point> pawns = waitingPlayer == Status.RED ? redPawns : blackPawns;
		pawns.remove(new Point(row, column));
	}
	
	//przeniesienie obecnie wybranego pionka po wybraniu wlasciwego pola do przeniesienia
	private void moveSelected(int row, int column)
	{
		ArrayList<Point> pawns = currentPlayer == Status.RED ? redPawns : blackPawns;
		
		//szukamy pionka który wlasnie sie ruszyl a nastepnie zmieniamy jego wspolrzedne na nowe
		for(int i = 0 ; i < pawns.size() ; ++i) 
			if(pawns.get(i).x == selectedField.getRow() && pawns.get(i).y == selectedField.getColumn()) 
			{
				pawns.get(i).move(row, column);//zmienie wspolrzednych na nowe
				break;
			}
		
		//jezeli na docelowym polu znajdowal sie pionek przeciwnika to go zbijamy
		if(fields[row][column].getStatus() == waitingPlayer )
			removePawn(row, column);
		
		fields[row][column].setStatus(currentPlayer);
		selectedField.setStatus(Status.EMPTY);
	}
	
	// zmiana obecnego gracza
	private void changePlayer()
	{
		waitingPlayer = currentPlayer;
		currentPlayer = currentPlayer == Status.RED ? Status.BLACK : Status.RED;
		
		if(currentPlayer == Status.BLACK)
			label.setText("Your Turn (" + currentPlayer + ")");
		else
			label.setText("Computer Turn (" + currentPlayer + ")");
	}
	
	// skonczenie gry, jezeli
	// arg = true to gracz wygral normalna metoda(utworzenie grupy pionkow)
	// arg = false to gracz wygral poprzez to ze przeciwny gracz nie mial mozliwych ruchów
	private void endGame(boolean isNormalVictory)
	{
		label.setText(currentPlayer + " has Won!");
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Game has ended");
		alert.setHeaderText(currentPlayer + " has Won!");
		
		if(isNormalVictory)
			alert.setContentText(currentPlayer + " formed a group with his pawns!");
		else 
			alert.setContentText(waitingPlayer + " has no moves!");
		
		alert.showAndWait();
		System.exit(0);
	}
	
	// sprawdzanie czy przeciwnik ma wogole mozliwy ruch
	private boolean enemyHasMoves()
	{
		ArrayList<Point> pawns = waitingPlayer == Status.RED ? redPawns : blackPawns;
		
		for(Point p : pawns)
			if(BoardManager.getMoves(fields, p.x, p.y, currentPlayer).size() != 0) return true;
		
		return false;
	}
	
	//uruchomienie wątku odpowiadającego za ruch bota
	private void runBotMoveThread()
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				bot.makeMove();
			}
		}).start();
	}
	
	// Funkcja wywolywana za kazdym razem jak zostalo klikniete pole
	public void buttonClicked(int row, int column) 
	{
		//wybranie swojego pionka
		if(selectedField == null && fields[row][column].getStatus() == currentPlayer)
		{
			selectedField = fields[row][column];
			selectedField.setType(Type.SELECTED);
			showMoves(row, column); // pokazujemy na planszy mozliwe ruchy
						
			return;
		}
		
		// wybranie jednego z mozliwych ruchow
		if(selectedField != null && fields[row][column].getType() == Type.MOVE)
		{	
			// przesuwamy wybranego pionka w docelowe miejsce
			moveSelected(row, column); 
			
			// przekazujemy botowi informacje o ruchu jaki wykonal gracz
			bot.moveMade(new Point(selectedField.getRow(), selectedField.getColumn()), new Point(row, column), currentPlayer);
			
			// sprawdzamy czy po wykonanym ruchu nie spelnione zostaly warunki zwyciestwa
			if(BoardManager.checkWin(fields, currentPlayer == Status.BLACK ? blackPawns : redPawns, currentPlayer))
				endGame(true);
			
			// sprawdzamy czy przecinwik ma mozliwy ruch
			if(!enemyHasMoves())
				endGame(false);
			
			changePlayer();
		}
		
		clearBoard();
		selectedField = null;
		
		if(currentPlayer == Status.RED)
			runBotMoveThread();
	}
	
	public Status getCurrentPlayer()
	{
		return currentPlayer;
	}
}
