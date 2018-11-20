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
		
	}
}
