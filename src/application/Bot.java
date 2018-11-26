package application;

import java.awt.Point;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.util.Pair;

public class Bot 
{
	// glebokosc drzewa(ile ruchow w przod analizuje)
	private final int depth = 6;
	
	private ArrayList<Point> myPawns, enemyPawns;
	private Status board[][];
	private LoAController controller;
	
	// ruch jaki wybral bot po wywolaniu alg alphaBeta
	private Pair<Point, Point> move;
	
	// minimalna suma ruchow potrzebnych do dotarcia do srodka masy  indeks=ilosc pionkow
	private final int minMoves[] = {0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 14};
	
	// wagi poszegolnych pozycji na planszy
	private final int positionValue[][] = 
					  {
							{-2, -1, -1, -1, -1, -1, -1, -2},
							{-1, 0 , 0 , 0 , 0 , 0 , 0 , -1},
							{-1, 0 , 1 , 1 , 1 , 1 , 0 , -1},
							{-1, 0 , 1 , 2 , 2 , 1 , 0 , -1},
							{-1, 0 , 1 , 2 , 2 , 1 , 0 , -1},
							{-1, 0 , 1 , 1 , 1 , 1 , 0 , -1},
							{-1, 0 , 0 , 0 , 0 , 0 , 0 , -1},
							{-2, -1, -1, -1, -1, -1, -1, -2},
					  };
	
	// maksymalna mozliwa wartosc sumy wszystki wag pozycji dla index=ilosc pionkow
	private final int maxPosValue[] = {0, 2, 4, 6, 8, 9, 10, 11, 12, 13, 14, 15, 16};
	
	public Bot(Status board[][], LoAController controller)
	{
		this.board = board;
		this.controller = controller;
		move = null;
		initPawns();
	}
	
	// metoda wywolywana przez controlle aby poinformowac bota o ruchu gracza
	public void moveMade(Point startPoint, Point endPoint, Status moveMaker)
	{
		if(moveMaker == Status.RED)
			moveMade(board, myPawns, enemyPawns, startPoint, endPoint, moveMaker);
		else
			moveMade(board, enemyPawns, myPawns, startPoint, endPoint, moveMaker);
	}
	
	// metoda akutalizajaca plansze oraz pionki(otrzymane w arg) na podstawie wykonanego ruchu
	private void moveMade(Status board[][], ArrayList<Point> moveMakerPawns, ArrayList<Point> oppositePawns, Point startPoint, Point endPoint, Status moveMaker)
	{
		Status oppositePlayer = moveMaker == Status.RED ? Status.BLACK : Status.RED;
		
		board[startPoint.x][startPoint.y] = Status.EMPTY;
		
		int i = moveMakerPawns.indexOf(startPoint);
		moveMakerPawns.set(i, endPoint); // zmieniamy wspolrzedne pionka ktory sie ruszyl
		
		if(board[endPoint.x][endPoint.y] == oppositePlayer) // jezeli wystapilo zbicie to usuwamy przeciwny pionek
			oppositePawns.remove(endPoint);
			
		board[endPoint.x][endPoint.y] = moveMaker;
	}
	
	private void initPawns()
	{
		myPawns = new ArrayList<Point>();
		enemyPawns = new ArrayList<Point>();
		
		for(int i = 1 ; i < 7 ; ++i) 
		{
			myPawns.add(new Point(i, 0)); // czerwone bota
			myPawns.add(new Point(i, 7));
			enemyPawns.add(new Point(0, i)); // czarne przeciwnika
			enemyPawns.add(new Point(7, i));
		}
	}
	
	// funkcja heurystyczna obliczająca jak dobre jest danie usawienie planszy dla bota
	private float h(Status board[][], ArrayList<Point> pawns)
	{
		int centerRow = 0, centerColumn = 0;
		int diffX, diffY;
		int minX =  1000, minY =  1000;
		int maxX = -1000, maxY = -1000;
		
		float comV = 0f; // wartosc srodka masy
		float centV = 0f;// wartosc centralizacji
		float uniV = 0f; // wartosc jednosci
		
		
		float comW = 1f; // waga srodka masy
		float centW = 0.3f;// waga centralizacji
		float uniW = 1f; // waga jednosci
		
		for(Point p : pawns)
		{
			centerRow += p.x;    // suma wszystkich rzedow pionkow
			centerColumn += p.y; // suma wyszstkich kolumn pionkow
			
			centV += positionValue[p.x][p.y]; // liczymy sume wag pozycji pionkow
			
			minX = Math.min(minX, p.x);// obliczamy graniczne wspolrzedne aby utworzyc z nich
			minY = Math.min(minY, p.y);// prostokat a nastepnie obliczyc jego pole
			maxX = Math.max(maxX, p.x);
			maxY = Math.max(maxY, p.y);
		}
		
		centerRow /= pawns.size(); // srodek rzedów
		centerColumn /= pawns.size(); // srodek kolumn
		
		for(Point p : pawns)
		{
			diffX = Math.abs(p.x - centerRow);
			diffY = Math.abs(p.y - centerColumn);
			
			comV += diffX >= diffY ? diffX : diffY; // obliczamy sume odleglosci pionkow od srodka masy(liczba ruchow jakie trzeba wykonac
		}	 										// danym pionkiem aby dojsc do srodka masy)
		
		
		// dzielimy sume wag pozycji pionkow przez maxymalna wartosc sumy wag jakie daloby sie otrzymac dla danej ilosci pionkow
		centV = centV/(float)maxPosValue[pawns.size()];
		
		// obliczenie wartosci srodka masy
		// od sumy odleglosci odejmujemy najmniejsa odleglosc mozliwa dla danej ilosci pionkow
		// podnosimy do potegi -1, im wieksza suma odleglosci tym gorsza dla bota plansza
		comV = 1/(float)(comV - minMoves[pawns.size()]);
		
		// im mniejsze pole prostokata utworzonego z granicznych wspolrzednych tym lepsze pole dla bota
		// minimalne mozliwe pole dla x pionkow wynosi x, dlatego dzielimy ilosc pionkow przez pole obliczonego prostokata
		// dodajemy 1 poniewaz kazdy pionek sam zajmuje prostokat o polu = 1
		uniV = (float)(pawns.size())/(float)((maxX - minX + 1)*(maxY - minY + 1));
		
		return comW*comV + centW*centV + uniW*uniV;
	}
	
	// metoda wywolywana przez controller, wykonuje ruch bota
	public void makeMove()
	{
		// wywolujemy alg alfa-beta aby otrzymac najbardziej optymalny ruch
		alphaBeta(board, myPawns, enemyPawns, Status.RED, depth, -1000, 1000);
		
		// bot symuluje klikniecie dwoch pól
		controller.buttonClicked(move.getKey().x, move.getKey().y);
		Platform.runLater(() -> controller.buttonClicked(move.getValue().x, move.getValue().y));
	}
	
	// alg obliczjacy optymalny ruch
	private float alphaBeta(Status board[][], ArrayList<Point> movingPawns, ArrayList<Point> waitingPawns, Status movingPlayer, int depth, float alpha, float beta)
	{
		if(depth <= 0 || BoardManager.checkWin(board, movingPawns, movingPlayer))
			return h(board, movingPlayer == Status.RED ? movingPawns : waitingPawns);
		
		// boolean sluzacy do sprawdzania czy gracz ruszajacy sie teraz mial chociaz 1 mozliwy ruch
		boolean hadMoves = false;
		
		// dla kazdego pionka
		for(Point p : movingPawns)
		{
			// pobieramy ruchy
			ArrayList<Point> moves = BoardManager.getMoves(board, p.x, p.y, movingPlayer);
			
			// a nastepnie dla kazdego ruchu wykonujemy alg 
			for(Point m : moves)
			{
				hadMoves = true;
				
				// kopiujemy stan gry
				Status[][] boardClone = copyBoard(board);
				ArrayList<Point> movingPawnsClone = copyPawns(movingPawns);
				ArrayList<Point> waitingPawnsClone = copyPawns(waitingPawns);
				
				// zmieniamy stan gry wykonujacy ruch obecnej iteracji
				moveMade(boardClone, movingPawnsClone, waitingPawnsClone, p, m, movingPlayer);
					
				// red = bot, gracz maksymalizujacy
				if(movingPlayer == Status.RED)
				{
					float prev_alpha = alpha;
					alpha = Math.max(alpha, alphaBeta(boardClone, waitingPawnsClone, movingPawnsClone, Status.BLACK, depth-1, alpha, beta));
					// jezeli estesmy w korzeniu to akutalizujemy odpowiednio optymalny ruch
					if(depth == this.depth && alpha > prev_alpha)
						move = new Pair<Point, Point>(p, m);
				}
				// gracz minimalizujacy
				else
					beta  = Math.min(beta, alphaBeta(boardClone, waitingPawnsClone, movingPawnsClone, Status.RED, depth-1, alpha, beta));
					
				// jezeli afla >= nie analizujemy dalszych ruchów potomków tylko zwracamy odpowiednio
				// dla gracza maxymalizujacego(bota) bete
				// dla gracza minimalizujacego alfe
				if(alpha >= beta) return movingPlayer == Status.RED ? beta : alpha;
			}
		}
		
		// jezeli gracz nie mial zadnych ruchów
		if(!hadMoves) return h(board, movingPlayer == Status.RED ? movingPawns : waitingPawns);
		
		// jezei gracz mial ruchy to zwracamy odpowiednio
		// dla gracza maxymalizujacego(bota) alfe
		// dla gracza minimalizujacego bete
		return movingPlayer == Status.RED ? alpha : beta;
	}
	
	private Status[][] copyBoard(Status[][] board)
	{
		Status boardClone[][] = new Status[8][];
		for(int i = 0 ; i < board.length ; ++i) boardClone[i] = board[i].clone();
		
		return boardClone;
	}
	
	private ArrayList<Point> copyPawns(ArrayList<Point> pawns)
	{
		ArrayList<Point> pawnsClone = new ArrayList<Point>(pawns.size());
		for(Point p : pawns) pawnsClone.add((Point) p.clone());
		
		return pawnsClone;
	}
}
