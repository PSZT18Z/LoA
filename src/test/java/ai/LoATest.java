package ai;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.Point;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


class LoATest {
	
	Status e = Status.EMPTY;
	Status r = Status.RED;
	Status b = Status.BLACK;
	
	Status[][] board = {	
			{e, e, e, e, e, b, e, e},
			{r, e, b, e, e, e, e, e},
			{e, e, r, b, b, r, b, e},
			{e, e, e, e, r, e, r, e},
			{e, b, e, b, r, b, e, e},
			{e, e, r, r, b, e, e, e},
			{e, e, b, e, e, r, e, e},
			{e, r, e, b, e, e, e, e}
			};
	ArrayList<Point> moveMakerPawns = new ArrayList<>();
	ArrayList<Point> oppositePawns = new ArrayList<>();
	
	private void createBoard(Status[][] board)
	{		
		for(int row = 0 ; row < 8 ; ++row)
			for(int column = 0 ; column < 8 ; ++column)
				if(board[row][column] == r)
				{
					moveMakerPawns.add(new Point(row,column));
				}
				else if(board[row][column] == b)
				{
					oppositePawns.add(new Point(row,column));
				}
	}
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		System.out.println("Testing of LoA project");
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		System.out.println("End");
	}
	
	@Test
	void testNotWinRed() {
		createBoard(board);
		assertFalse(BoardManager.checkWin(board, moveMakerPawns, Status.RED ));
	}
	
	@Test
	void testNotWinBlack() {
		createBoard(board);
		assertFalse(BoardManager.checkWin(board, oppositePawns, Status.BLACK ));
	}
	
	@Test
	void testWin() {
		Status[][] board = {	
				{e, e, e, e, e, b, e, e},
				{e, e, b, e, e, e, e, e},
				{e, e, r, b, b, r, b, e},
				{e, e, e, r, r, e, r, e},
				{e, b, e, b, r, b, r, e},
				{e, e, r, r, b, e, e, e},
				{e, r, b, e, e, e, e, e},
				{e, e, e, b, e, e, e, e}
				};
		createBoard(board);
		assertTrue(BoardManager.checkWin(board, moveMakerPawns, Status.RED ));
		
	}
	
	@Test
	void testOnePawn() {
		Status[][] board = {	
				{e, e, e, e, e, b, e, e},
				{e, e, b, e, e, e, e, e},
				{e, e, e, b, b, e, b, e},
				{e, e, e, r, e, e, e, e},
				{e, b, e, b, e, b, e, e},
				{e, e, e, e, b, e, e, e},
				{e, e, b, e, e, e, e, e},
				{e, e, e, b, e, e, e, e}
				};
		createBoard(board);
		assertTrue(BoardManager.checkWin(board, moveMakerPawns, Status.RED ));
		
	}

	@Test
	void tesMove() {
		createBoard(board);
		Point startPoint = new Point(7,1);
		Point endPoint = new Point(5,1);
		
		Bot.moveMade(board, moveMakerPawns, oppositePawns, startPoint , endPoint, Status.RED);
		assertEquals(board[startPoint.x][startPoint.y],Status.EMPTY);
		assertEquals(board[endPoint.x][endPoint.y], Status.RED);
		assertTrue(moveMakerPawns.contains(endPoint));
		assertFalse(moveMakerPawns.contains(startPoint));
	
	}
	
	@Test
	void testBeating() {
		createBoard(board);
		Point startPoint = new Point(1,0);
		Point endPoint = new Point(1,2);
		
		Bot.moveMade(board, moveMakerPawns, oppositePawns, startPoint , endPoint, Status.RED);
		
		assertEquals(board[startPoint.x][startPoint.y],Status.EMPTY);
		assertEquals(board[endPoint.x][endPoint.y], Status.RED);
		assertTrue(moveMakerPawns.contains(endPoint));
		assertFalse(moveMakerPawns.contains(startPoint));
		assertFalse(oppositePawns.contains(endPoint));
	
	}
	
	@Test
	void testJumpOverPawns() {
		createBoard(board);
		Point startPoint = new Point(5,3);
		Point endPoint = new Point(5,0);
		
		Bot.moveMade(board, moveMakerPawns, oppositePawns, startPoint , endPoint, Status.RED);
		assertEquals(board[startPoint.x][startPoint.y],Status.EMPTY);
		assertEquals(board[endPoint.x][endPoint.y], Status.RED);
		assertTrue(moveMakerPawns.contains(endPoint));
		assertFalse(moveMakerPawns.contains(startPoint));
	
	}
	
	@Test
	void testGetRange() {
		int[] expRange = {2,2,3,3,0,5,2,2};
		int[] range = BoardManager.getRange(board, 0, 1, Status.RED, 8);
		assertArrayEquals(expRange, range);
	}
	@Test
	void testPossibleMoves() {
		ArrayList<Point> possibleMoves = new ArrayList<Point>();
		possibleMoves.add(new Point(7, 3));
		possibleMoves.add(new Point(5, 1));
		possibleMoves.add(new Point(6, 0));
		ArrayList<Point> x = BoardManager.getMoves(board, 7, 1, Status.RED);
		assertEquals(x, possibleMoves);
	}

}
