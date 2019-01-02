package ai;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


class LoATest 
{
	
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
	static void setUpBeforeClass() throws Exception
	{
		System.out.println("Testing of LoA project");
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception
	{
		System.out.println("End");
	}
	
	//test funkcji sprawdzajacej warunki zwyciestwa w razie braku stowrzenia jednej grupy
	@Test
	void testNotWinRed() 
	{
		createBoard(board);
		assertFalse(BoardManager.checkWin(board, moveMakerPawns, Status.RED ));	//brak spelnienia warunkow zwyciestwa dla gracza RED, funkcja powinna zwrocic false
	}
	//test funkcji sprawdzajacej warunki zwyciestwa w razie braku stowrzenia jednej grupy
	@Test
	void testNotWinBlack() 
	{
		createBoard(board);
		assertFalse(BoardManager.checkWin(board, oppositePawns, Status.BLACK ));	//brak spelnienia warunkow zwyciestwa dla gracza BLACK, funkcja powinna zwrocic false
	}
	//test funkcji checkWin w razie spelniania warunkow zwyciestwa
	@Test
	void testWin() 
	{
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
		assertTrue(BoardManager.checkWin(board, moveMakerPawns, Status.RED ));	//gracz RED spelnia warunki zwyciestwa, funkcja zwaraca true
		
	}
	
	//sprawdzanie warunku zwyciestwa przy jednym pionku
	@Test
	void testOnePawn() 
	{
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
		assertTrue(BoardManager.checkWin(board, moveMakerPawns, Status.RED )); //gracz RED ma jeden pionek, funkcja zwyciestwa zwraca true
		
	}
	//test wykonania ruchu bez bicia i przeskakiwania swojego pionka
	@Test
	void tesMove() 
	{
		createBoard(board);
		Point startPoint = new Point(7,1);
		Point endPoint = new Point(5,1);
		
		Bot.moveMade(board, moveMakerPawns, oppositePawns, startPoint , endPoint, Status.RED); 
		assertEquals(board[startPoint.x][startPoint.y],Status.EMPTY);	//pole z ktorego startujemy po skoku ma status EMPTY
		assertEquals(board[endPoint.x][endPoint.y], Status.RED);		//pole na ktore skaczemy zmienia status na status gracza wykonujacego ruch RED
		assertTrue(moveMakerPawns.contains(endPoint));					//arraylist z  pionkami gracza RED zawiera pole na ktore skaczemy
		assertFalse(moveMakerPawns.contains(startPoint));				//arraylist z  pionkami gracza RED nie zawierac pola z ktorego startowalismy
	
	}
	//test wykonania ruchu z biciem
	@Test
	void testBeating() 
	{
		createBoard(board);
		Point startPoint = new Point(1,0);
		Point endPoint = new Point(1,2);
		
		Bot.moveMade(board, moveMakerPawns, oppositePawns, startPoint , endPoint, Status.RED);
		
		assertEquals(board[startPoint.x][startPoint.y],Status.EMPTY);	//pole startu zmienia status na EMPTY
		assertEquals(board[endPoint.x][endPoint.y], Status.RED);		//pole konca zmienia satus na status gracza wykonujacego ruch RED
		assertTrue(moveMakerPawns.contains(endPoint));					//arraylist z pionkami gracza RED musi zawierac pole na ktore skaczemy
		assertFalse(moveMakerPawns.contains(startPoint));				//arraylist z pionkami gracza RED nie zawiera pola startu
		assertFalse(oppositePawns.contains(endPoint));					//arraylist gracza BLACK nie zawiera pola na ktorym zbilismy jego pion
	
	}
	//test wykonania ruchu z przeskoczeniem swojego pionka
	@Test
	void testJumpOverPawns()
{
		createBoard(board);
		Point startPoint = new Point(5,3);
		Point endPoint = new Point(5,0);
		
		Bot.moveMade(board, moveMakerPawns, oppositePawns, startPoint , endPoint, Status.RED);
		assertEquals(board[startPoint.x][startPoint.y],Status.EMPTY);	//pole startu zmienia status na EMPTY
		assertEquals(board[endPoint.x][endPoint.y], Status.RED);		//pole konca zmienia satus na status gracza wykonujacego ruch RED
		assertTrue(moveMakerPawns.contains(endPoint));					//arraylist z pionkami gracza RED musi zawierac pole na ktore skaczemy
		assertFalse(moveMakerPawns.contains(startPoint));				//arraylist z pionkami gracza RED nie zawiera pola startu
	
	}
	//test zakresu ruchu danego pionka
	@Test
	void testGetRange()
	{
		int[] expRange = {2,2,3,3,0,5,2,2};
		int[] range = BoardManager.getRange(board, 0, 1, Status.RED, 8);
		assertArrayEquals(expRange, range);	//porownanie wyliczonego zakresu z zakresem zwroconym przez funkcje getRange
	}
	//test moï¿½liwych ruchï¿½w dla pionka
	@Test
	void testPossibleMoves() 
	{
		ArrayList<Point> possibleMoves = new ArrayList<Point>();
		possibleMoves.add(new Point(7, 3));
		possibleMoves.add(new Point(5, 1));
		possibleMoves.add(new Point(6, 0));
		ArrayList<Point> x = BoardManager.getMoves(board, 7, 1, Status.RED);
		assertEquals(x, possibleMoves);	//porownanie wyliczonych mooliwych do ruchu pol ze zwroconymi przez funkcje getMoves
	}

	//test funkcji ustalajacej statystki danego ukladu
	@Test
	void testHeuristicStatisticCounter () 
	{
		BotConfig myConfig = null;
        try
        {
            myConfig = BotConfigReader.readBotConfig(Status.RED);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            fail("Exception reading Bot config");
        }
		createBoard(board);
		HeuristicStatisticCounter  counter = new HeuristicStatisticCounter(moveMakerPawns, myConfig.positionValue);
		//porownanie oczekiwanych wartosci statystyk z wyliczonymi przez konstruktor HeuristicStatisticCounter
		assertTrue(counter.maxX==7);
		assertTrue(counter.maxY==6);
		assertTrue(counter.minX==1);
		assertTrue(counter.minY==0);
		assertTrue(counter.distanceSum==22);
		assertTrue(counter.positionValueSum==6);
				  
	}
	//test funkcji heurystycznej
	@Test
	void testHeuristic() 
	{
		createBoard(board);
		MoveListener listener = null;
		Bot bot = new Bot(listener, Status.RED, 6);
		BotConfig myConfig = null;
        try
        {
            myConfig = BotConfigReader.readBotConfig(Stastus.RED);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            fail("Exception reading Bot config");
        }
       
        double result = myConfig.centW*(6.0/14.0)+myConfig.comW*(1.0/12.0)+myConfig.uniW*(10.0/49.0);    
    
        assertTrue(Math.abs(bot.h(moveMakerPawns)-result) < 0.000001);	//sprawdzanie co do dokladnosci 0.000001 wyniku dzialania funkcji heurystycznej (ze wzgledu na dzielenie)
	}
		
}
