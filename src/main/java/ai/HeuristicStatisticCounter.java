package ai;

import java.awt.Point;
import java.util.ArrayList;

public class HeuristicStatisticCounter 
{
	public int maxX, maxY, minX, minY;
	public int distanceSum;
	public int positionValueSum;
	
	public HeuristicStatisticCounter(ArrayList<Point> pawns, int[][] positionValue)
	{
		maxX = maxY = -100;
		minX = minY = 100;
		int centerRow = 0;
		int centerColumn = 0;
		
		for(Point p : pawns)
		{
			centerRow += p.x;
			centerColumn += p.y;
			positionValueSum += positionValue[p.x][p.y];
			minX = Math.min(minX, p.x);
			minY = Math.min(minY, p.y);
			maxX = Math.max(maxX, p.x);
			maxY = Math.max(maxY, p.y);
		}
		
		centerRow /= pawns.size();
		centerColumn /= pawns.size();
		
		for(Point p : pawns)
		{
			int diffX = Math.abs(p.x - centerRow);
			int diffY = Math.abs(p.y - centerColumn);
			
			distanceSum += diffX >= diffY ? diffX : diffY;
		}
			
	}
}
