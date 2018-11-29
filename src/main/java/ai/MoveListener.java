package ai;

import java.awt.Point;

import javafx.util.Pair;

public interface MoveListener 
{
	void moveReceived(Pair<Point, Point> move, Status color);
}
