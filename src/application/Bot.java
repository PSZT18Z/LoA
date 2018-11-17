package application;

import java.awt.Point;
import java.util.ArrayList;

public class Bot 
{
	private final int depth = 5;
	
	private ArrayList<Point> myPawns, enemyPawns;
	private Status board[][];
	
	public Bot()
	{
		initBoard();
		initPawns();
	}
	
	private void initBoard()
	{
		board = new Status[8][8];
		
		for(int row = 0 ; row < 8 ; ++row)
			for(int column = 0 ; column < 8 ; ++column)
					board[row][column] = Status.EMPTY;
		
	    for(int i = 1 ; i < 7 ; ++i) 
		{
			board[i][0] = Status.RED;
			board[i][7] = Status.RED;
			board[0][i] = Status.BLACK;
			board[7][i] = Status.BLACK;
		}	
	}
	
	private void initPawns()
	{
		myPawns = new ArrayList<Point>();
		enemyPawns = new ArrayList<Point>();
		
		for(int i = 1 ; i < 7 ; ++i) 
		{
			enemyPawns.add(new Point(i, 0));
			enemyPawns.add(new Point(i, 7));
			myPawns.add(new Point(0, i));
			myPawns.add(new Point(7, i));
		}	
	}
}
