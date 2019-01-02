package ai;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import javafx.util.Pair;

public class Bot 
{
	private int depth; 							 // glebokosc drzewa(ile ruchow w przod analizuje)
	private ArrayList<Point> myPawns, enemyPawns;// pionki
	private int size;                            // rozmiar planszy(gra odbywa sie tylko na planszy kwadratowej)
	private Status[][] board;                    // plansza
	private final MoveListener listener;         // listener do ktorego wyslemy zdarzenie obliczenia ruchu
	private Pair<Point, Point> move;             // ruch jaki wybral bot po wywolaniu alg alphaBeta	
	private final Status myColour, enemyColour;  // nasz kolor oraz kolor przeciwnika
	private float comW;                          // waga srodka masy
	private float uniW;                          // waga jednosci
	private float centW;                         // waga centralizacji
	private int[] minDistanceSum;                // minimalna suma ruchow potrzebnych do dotarcia do srodka masy; indeks=ilosc pionkow
	private int[][] positionValue;               // wartosci poszczególnych pól planszy
	private int[] maxPosValue;					 // maksymalna mozliwa wartosc sumy wszystki wag pozycji dla index=ilosc pionkow

	public Bot(MoveListener listener, Status myColour, int depth)
	{
        readConfig();
		this.listener = listener;
		this.myColour = myColour;
		this.depth = depth;
		enemyColour = myColour == Status.RED ? Status.BLACK : Status.RED;
		move = null;
		initPawns();
		initBoard();
	}

	// wczytuje konfiguracje bota z plikow konfiguracyjnych
	private void readConfig()
    {
        BotConfig myConfig = null;
        try
        {
            myConfig = BotConfigReader.readBotConfig(myColour);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        this.size = myConfig.size;
        this.comW = myConfig.comW;
        this.uniW = myConfig.uniW;
        this.centW = myConfig.centW;
        this.minDistanceSum = myConfig.minDistanceSum;
        this.positionValue = myConfig.positionValue;
        this.maxPosValue = myConfig.maxPosValue;

    }

	// metoda wywolywana przez controlle aby poinformowac bota o ruchu gracza
	public void moveMade(Point startPoint, Point endPoint, Status moveMaker)
	{
		if(moveMaker == myColour)
			moveMade(board, myPawns, enemyPawns, startPoint, endPoint, moveMaker);
		else
			moveMade(board, enemyPawns, myPawns, startPoint, endPoint, moveMaker);
	}
	
	// metoda akutalizajaca plansze oraz pionki(otrzymane w arg) na podstawie wykonanego ruchu
	public static void moveMade(Status board[][], ArrayList<Point> moveMakerPawns, ArrayList<Point> oppositePawns, Point startPoint, Point endPoint, Status moveMaker)
	{
		Status oppositePlayer = moveMaker == Status.RED ? Status.BLACK : Status.RED;
		
		board[startPoint.x][startPoint.y] = Status.EMPTY;
		
		int i = moveMakerPawns.indexOf(startPoint);
		moveMakerPawns.set(i, endPoint);
		
		if(board[endPoint.x][endPoint.y] == oppositePlayer)
			oppositePawns.remove(endPoint);
			
		board[endPoint.x][endPoint.y] = moveMaker;
	}
	
	private void initPawns()
	{
		ArrayList<Point> redPawns = new ArrayList<>();
		ArrayList<Point> blackPawns = new ArrayList<>();
		
		for(int i = 1 ; i < size-1 ; ++i)
		{
			redPawns.add(new Point(i, 0));
			redPawns.add(new Point(i, size-1));
			blackPawns.add(new Point(0, i));
			blackPawns.add(new Point(size-1, i));
		}
		
		if(myColour == Status.RED) 
		{
			myPawns = redPawns;
			enemyPawns = blackPawns;
		}
		else
		{
			myPawns = blackPawns;
			enemyPawns = redPawns;
		}
	}

	private void initBoard()
    {
        board = new Status[size][size];
        for(int row = 0 ; row < size ; ++row)
           for(int column = 0 ; column < size ; ++column)
           {
                if((row == 0 || row == size-1) && (column != 0 && column != size-1))
                    board[row][column] = Status.BLACK;
                else if((column == 0 || column == size-1) && (row != 0 && row != size-1))
                   board[row][column] = Status.RED;
                else
                    board[row][column] = Status.EMPTY;
           }
    }
	
	// funkcja heurystyczna obliczająca jak dobre jest danie usawienie planszy dla bota
	float  h(ArrayList<Point> pawns)
	{
		
		float comV, centV, uniV;
		
		HeuristicStatisticCounter stats = new HeuristicStatisticCounter(pawns, positionValue);
	
		// dzielimy sume wartosci wszystkich pozycji pionek przez maksymalna sume wartosci mozliwa do uzyskania dla takiej ilosci pionkow
		centV = (float)stats.positionValueSum/(float)maxPosValue[pawns.size()];
		// im mniejsza suma odlglosc od srodka masy tym lepiej dlatego odwracamy wartosc sumy odleglosci ()^-1
		// od sumy odleglosci odejmujemy najmniejsza sume odleglosci mozliwa do uzaskania dla takiej ilosci pionkow
		// bez tego bot preferowalby plansze z mniejsza iloscia pionkow
		comV = 1/(float)(stats.distanceSum - minDistanceSum[pawns.size()]);
		// liczymy pole prostokąta zawierającego wsyzstkie punkty, im mniejszez tym lepiej
		// dodajemy 1 ponieważ juz sam pojedynczy pionek tworzy prostokat o polu 1
		// dzielimy najmniejszy mozliwy prostokat dla danej ilosic pionkow(czyli ilosc pionkow) przez otrzymany z naszych obliczen prostokat
		uniV = (float)(pawns.size())/(float)((stats.maxX - stats.minX + 1)*(stats.maxY - stats.minY + 1));
		
		return comW*comV + centW*centV + uniW*uniV;
	}
	
	// alg obliczjacy optymalny ruch
	private float alphaBeta(Status board[][], ArrayList<Point> movingPawns, ArrayList<Point> waitingPawns, Status movingPlayer, int depth, float alpha, float beta)
	{
		if(depth <= 0 || BoardManager.checkWin(board, movingPawns, movingPlayer))
			return h(movingPlayer == myColour ? movingPawns : waitingPawns);
		
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
					
				// jezeli nasz ruch to maksymalizujemy
				if(movingPlayer == myColour)
				{
					float prev_alpha = alpha;
					alpha = Math.max(alpha, alphaBeta(boardClone, waitingPawnsClone, movingPawnsClone, enemyColour, depth-1, alpha, beta));
					// jezeli jestesmy w korzeniu to akutalizujemy odpowiednio optymalny ruch
					if(depth == this.depth && alpha > prev_alpha)
						move = new Pair<>(p, m);
				}
				// gracz minimalizujacy
				else
					beta  = Math.min(beta, alphaBeta(boardClone, waitingPawnsClone, movingPawnsClone, myColour, depth-1, alpha, beta));
					
				// jezeli afla >= nie analizujemy dalszych ruchów potomków tylko zwracamy odpowiednio
				// dla gracza maxymalizujacego(bota) bete
				// dla gracza minimalizujacego alfe
				if(alpha >= beta) return movingPlayer == myColour ? beta : alpha;
			}
		}
		
		// jezeli gracz nie mial zadnych ruchów
		if(!hadMoves) return h(movingPlayer == myColour ? movingPawns : waitingPawns);
		
		// jezei gracz mial ruchy to zwracamy odpowiednio
		// dla gracza maxymalizujacego(bota) alfe
		// dla gracza minimalizujacego bete
		return movingPlayer == myColour ? alpha : beta;
	}
	
	private Status[][] copyBoard(Status[][] board)
	{
		Status boardClone[][] = new Status[board.length][];
		for(int i = 0 ; i < board.length ; ++i) boardClone[i] = board[i].clone();
		
		return boardClone;
	}
	
	private ArrayList<Point> copyPawns(ArrayList<Point> pawns)
	{
		ArrayList<Point> pawnsClone = new ArrayList<>(pawns.size());
		for(Point p : pawns) pawnsClone.add((Point) p.clone());
		
		return pawnsClone;
	}
	
	// wywołanie metody powoduję obliczenie przez bota następnego ruchu i wysłanie go do listenera
	public void makeMove()
	{
		alphaBeta(board, myPawns, enemyPawns, myColour, depth, -1000, 1000);
		listener.moveReceived(move, myColour);
	}
}
