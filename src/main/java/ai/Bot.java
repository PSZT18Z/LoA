package main.java.ai;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import javafx.util.Pair;

public class Bot 
{
	private int depth; // glebokosc drzewo
	private ArrayList<Point> myPawns, enemyPawns;// pionki
	private int size;                            // rozmiar planszy(gra odbywa sie tylko na planszy kwadratowej)
	private Status[][] board;                    // plansza
	private final MoveListener listener;         // listener do ktorego wyslemy zdarzenie obliczenia ruchu
	private Pair<Point, Point> move;             // ostatnio obliczony ruch
	private final Status myColour, enemyColour;  // nasz kolor oraz kolor przeciwnika
	private float comW;                          // waga srodka masy
	private float uniW;                          // waga jednosci
	private float centW;                         // waga centralizacji
	private int[] minMoves;                      // minima suma odleglosci pionkow od srodka masy
	private int[][] positionValue;               // wartosci poszczególnych pól planszy
	private int[] maxPosValue;

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

	//czytuje konfiguracje bota z plikow konfiguracyjnych
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
        this.minMoves = myConfig.minMoves;
        this.positionValue = myConfig.positionValue;
        this.maxPosValue = myConfig.maxPosValue;

    }

	public void moveMade(Point startPoint, Point endPoint, Status moveMaker)
	{
		if(moveMaker == myColour)
			moveMade(board, myPawns, enemyPawns, startPoint, endPoint, moveMaker);
		else
			moveMade(board, enemyPawns, myPawns, startPoint, endPoint, moveMaker);
	}
	
	private void moveMade(Status board[][], ArrayList<Point> moveMakerPawns, ArrayList<Point> oppositePawns, Point startPoint, Point endPoint, Status moveMaker)
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
	
	private float h(ArrayList<Point> pawns)
	{
		int centerRow = 0, centerColumn = 0;
		int diffX, diffY;
		int minX =  1000, minY =  1000;
		int maxX = -1000, maxY = -1000;
		
		float comV = 0f; // center of mass value
		float uniV;      // unity value
		float centV = 0f;// centralisation value
		
		for(Point p : pawns)
		{
			centerRow += p.x;
			centerColumn += p.y;
			centV += positionValue[p.x][p.y];
			minX = Math.min(minX, p.x);
			minY = Math.min(minY, p.y);
			maxX = Math.max(maxX, p.x);
			maxY = Math.max(maxY, p.y);
		}
		
		centerRow /= pawns.size();
		centerColumn /= pawns.size();
		
		for(Point p : pawns)
		{
			diffX = Math.abs(p.x - centerRow);
			diffY = Math.abs(p.y - centerColumn);
			
			comV += diffX >= diffY ? diffX : diffY;
		}
		
		centV = centV/(float)maxPosValue[pawns.size()];
		comV = 1/(comV - minMoves[pawns.size()]);
		uniV = (float)(pawns.size())/(float)((maxX - minX + 1)*(maxY - minY + 1));
		
		return comW*comV + centW*centV + uniW*uniV;
	}
	
	private float alphaBeta(Status board[][], ArrayList<Point> movingPawns, ArrayList<Point> waitingPawns, Status movingPlayer, int depth, float alpha, float beta)
	{
		
		if(depth <= 0 || BoardManager.checkWin(board, movingPawns, movingPlayer))
			return h(movingPlayer == myColour ? movingPawns : waitingPawns);
		
		boolean hadMoves = false;
		for(Point p : movingPawns)
		{
			ArrayList<Point> moves = BoardManager.getMoves(board, p.x, p.y, movingPlayer);
			for(Point m : moves)
			{
				hadMoves = true;
				Status[][] boardClone = copyBoard(board);
				ArrayList<Point> movingPawnsClone = copyPawns(movingPawns);
				ArrayList<Point> waitingPawnsClone = copyPawns(waitingPawns);
				
				moveMade(boardClone, movingPawnsClone, waitingPawnsClone, p, m, movingPlayer);
					
				if(movingPlayer == myColour)
				{
					float prev_alpha = alpha;
					alpha = Math.max(alpha, alphaBeta(boardClone, waitingPawnsClone, movingPawnsClone, enemyColour, depth-1, alpha, beta));
					if(depth == this.depth && alpha > prev_alpha)
						move = new Pair<>(p, m);
				}
				else
					beta  = Math.min(beta, alphaBeta(boardClone, waitingPawnsClone, movingPawnsClone, myColour, depth-1, alpha, beta));
					
				if(alpha >= beta) return movingPlayer == myColour ? beta : alpha;
			}
		}
		
		if(!hadMoves) return h(movingPlayer == myColour ? movingPawns : waitingPawns);
		
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
	
	public void makeMove()
	{
		alphaBeta(board, myPawns, enemyPawns, myColour, depth, -1000, 1000);
		listener.moveReceived(move, myColour);
	}
}
