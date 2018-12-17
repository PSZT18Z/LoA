package ui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Optional;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import ai.BoardManager;
import ai.Bot;
import ai.MoveListener;
import ai.Status;

//Kontroluje okienko gry
public class LoAController
{
	@FXML
	GridPane grid;
	@FXML
	Label label;
	
	private enum Player{HUMAN, AI};
	Player blackPlayer, redPlayer;
	private Field fields[][]; // plansza gry
	private Field selectedField; // obecnie wybrane pole, jesli zadno nie jest wybrane to null
	private Status currentPlayer, waitingPlayer;
	private ArrayList<Point> redPawns, blackPawns;
	private Bot redBot, blackBot;
	
	@FXML private void initialize()
	{
	    initSettings();
		initFields();
		initPawns();
		selectedField = null;
		currentPlayer = Status.BLACK; // czarny zaczyna
		waitingPlayer = Status.RED;

		if(blackPlayer == Player.AI)
			runBotMoveThread(Status.BLACK);
	}

	// ustawiamy czy gracz bedzie botem(o jakiej glebokosci drzewa) czy czlowiekiem
	private void initSettings()
    {
       Pair<Integer, Integer> settings = getSettingsFromUser();
       ControllerMoveListener listener = new ControllerMoveListener();
       
       if(settings.getKey() == 0) redPlayer = Player.HUMAN;
       else
       {
    	   redPlayer = Player.AI;
    	   redBot = new Bot(listener, Status.RED, settings.getKey()); // Stworzenie bota podajemy mu naszego listenera
       }															  // oraz okreslamy kolor i poziom trudnosci bota
       
       if(settings.getValue() == 0) blackPlayer = Player.HUMAN;
       else
       {
    	   blackPlayer = Player.AI;
    	   blackBot = new Bot(listener, Status.BLACK, settings.getValue());
       }
    }
	
	// Wyswietlamy opcje wyboru opcji uzytownikowi
	private Pair<Integer, Integer> getSettingsFromUser()
	{
		Optional<Pair<Integer, Integer>> result = new OpeningDialog().showAndWait();
		if(!result.isPresent()) System.exit(0);
		
		return result.get();
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
		redPawns = new ArrayList<>();
		blackPawns = new ArrayList<>();
		
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
		for(Point p : pawns)
			if(p.x == selectedField.getRow() && p.y == selectedField.getColumn())
			{
				p.move(row, column);//zmienie wspolrzednych na nowe
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
		
		label.setText("Current Turn: " + currentPlayer);
	}
	
	// skonczenie gry, jezeli
	// arg = true to gracz wygral normalna metoda(utworzenie grupy pionkow)
	// arg = false to gracz wygral poprzez to ze przeciwny gracz nie mial mozliwych ruchów
	private void endGame(boolean isNormalVictory, Status whoWon)
	{
		clearBoard();
	    Status whoLost = whoWon == Status.RED ? Status.BLACK : Status.RED;

		label.setText(whoWon + " has Won!");
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Game has ended");
		alert.setHeaderText(whoWon + " has Won!");
		
		if(isNormalVictory)
			alert.setContentText(whoWon + " formed a group with his pawns!");
		else 
			alert.setContentText(whoLost + " has no moves!");
		
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
	private void runBotMoveThread(Status colour)
	{
		if(colour == Status.RED)
			new Thread(() -> redBot.makeMove()).start();
		else
			new Thread(() -> blackBot.makeMove()).start();
	}
	
	
	// Funkcja wywolywana za kazdym razem jak zostalo klikniete pole
    void buttonClicked(int row, int column)
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
			
			// sprawdzamy czy po wykonanym ruchu nie spelnione zostaly warunki zwyciestwa
			if(BoardManager.checkWin(fields, currentPlayer == Status.BLACK ? blackPawns : redPawns, currentPlayer))
				endGame(true, currentPlayer);
			else if(BoardManager.checkWin(fields, waitingPlayer == Status.BLACK ? blackPawns : redPawns, waitingPlayer))
                endGame(true, waitingPlayer);

						
			// sprawdzamy czy przecinwik ma mozliwy ruch
			if(!enemyHasMoves())
				endGame(false, currentPlayer);
			
			// przekazujemy botowi informacje o ruchu jaki wykonal gracz
			if(redPlayer == Player.AI)
				redBot.moveMade(new Point(selectedField.getRow(), selectedField.getColumn()), new Point(row, column), currentPlayer);
			
			if(blackPlayer == Player.AI)
				blackBot.moveMade(new Point(selectedField.getRow(), selectedField.getColumn()), new Point(row, column), currentPlayer);
			
			changePlayer();
		}
		
		clearBoard();
		selectedField = null;
		
		if(currentPlayer == Status.RED && redPlayer == Player.AI)
			runBotMoveThread(Status.RED);
		else if(currentPlayer == Status.BLACK && blackPlayer == Player.AI)
			runBotMoveThread(Status.BLACK);
	}
	
	public boolean isHumanTurn()
	{
		return (currentPlayer == Status.RED && redPlayer == Player.HUMAN)
			   || (currentPlayer == Status.BLACK && blackPlayer == Player.HUMAN);
	}
	
	
	public class ControllerMoveListener implements MoveListener
	{
		@Override
		public void moveReceived(Pair<Point, Point> move, Status colour)
		{
			System.out.println(colour + " bot move received");
            System.out.println("FROM:("+move.getKey().x+","+move.getKey().y+") " +
                               "TO:("+move.getValue().x+"," + move.getValue().y+")\n");
            	
			buttonClicked(move.getKey().x, move.getKey().y);
			Platform.runLater(() -> buttonClicked(move.getValue().x, move.getValue().y));
		}
	}
	

}
