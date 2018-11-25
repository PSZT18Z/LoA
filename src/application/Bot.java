package application;

import java.awt.Point;
import java.util.ArrayList;

import javafx.util.Pair;

public class Bot 
{
	private final int depth = 6;
	
	private ArrayList<Point> myPawns, enemyPawns;
	private Status board[][];
	private LoAController controller;
	private Pair<Point, Point> move;
	private int minMoves[] = {0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 14};
	
	public Bot(Status board[][], LoAController controller)
	{
		this.board = board;
		this.controller = controller;
		move = null;
		initPawns();
	}
	
	public void moveMade(Point startPoint, Point endPoint, Status moveMaker)
	{
		if(moveMaker == Status.RED)
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
		myPawns = new ArrayList<Point>();
		enemyPawns = new ArrayList<Point>();
		
		for(int i = 1 ; i < 7 ; ++i) 
		{
			myPawns.add(new Point(i, 0));
			myPawns.add(new Point(i, 7));
			enemyPawns.add(new Point(0, i));
			enemyPawns.add(new Point(7, i));
		}
	}
	
	private float h(Status board[][], ArrayList<Point> pawns)
	{
		int centerRow = 0, centerColumn = 0;
		int distanceSum = 0;
		int diffX, diffY;
		
		for(Point p : pawns)
		{
			centerRow += p.x;
			centerColumn += p.y;
		}
		
		centerRow /= pawns.size();
		centerColumn /= pawns.size();
		
		for(Point p : pawns)
		{
			diffX = Math.abs(p.x - centerRow);
			diffY = Math.abs(p.y - centerColumn);
			
			distanceSum += diffX >= diffY ? diffX : diffY;
		}
		
		return 1/(float)(distanceSum - minMoves[pawns.size()]);
	}
	
	public void makeMove()
	{
		alphaBeta(board, myPawns, enemyPawns, Status.RED, depth, -1000, 1000);
		
		controller.buttonClicked(move.getKey().x, move.getKey().y);
		controller.buttonClicked(move.getValue().x, move.getValue().y);
	}
	
	private float alphaBeta(Status board[][], ArrayList<Point> movingPawns, ArrayList<Point> waitingPawns, Status movingPlayer, int depth, float alpha, float beta)
	{
		if(depth <= 0 || BoardManager.checkWin(board, movingPawns, movingPlayer))
			return h(board, movingPlayer == Status.RED ? movingPawns : waitingPawns);
		
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
					
				if(movingPlayer == Status.RED)
				{
					float prev_alpha = alpha;
					alpha = Math.max(alpha, alphaBeta(boardClone, waitingPawnsClone, movingPawnsClone, Status.BLACK, depth-1, alpha, beta));
					if(depth == this.depth && alpha > prev_alpha)
						move = new Pair<Point, Point>(p, m);
				}
				else
					beta  = Math.min(beta, alphaBeta(boardClone, waitingPawnsClone, movingPawnsClone, Status.RED, depth-1, alpha, beta));
					
				if(alpha >= beta) return movingPlayer == Status.RED ? beta : alpha;
			}
		}
		
		if(!hadMoves) return h(board, movingPlayer == Status.RED ? movingPawns : waitingPawns);
		
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
