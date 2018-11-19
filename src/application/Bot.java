package application;

import java.awt.Point;
import java.util.ArrayList;

public class Bot 
{
	private final int depth = 5;
	
	private ArrayList<Point> myPawns, enemyPawns;
	private Status board[][];
	
	public Bot(Status[][] board)
	{
		this.board = board;
		initPawns();
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
