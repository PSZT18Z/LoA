package application;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;


public class MoveCounter 
{
	public static Status[][] fieldsToStatus(Field[][] fields)
	{
		Status output[][] = new Status[8][8];
		
		for(int row = 0 ; row < 8 ; ++row)
			for(int column = 0 ; column < 8 ; ++column)
				output[row][column] = fields[row][column].getStatus();
		
		return output;
	}
	
	public static ArrayList<Point> getMoves(Field[][] fields, int row, int column, Status currentPlayer)
	{
		return getMoves(fieldsToStatus(fields), row, column, currentPlayer);
	}

	private static ArrayList<Point> getMoves(Status[][] board, int row, int column, Status currentPlayer) 
	{
		ArrayList<Point> possibleMove = new ArrayList<Point>();
		int range[] = getRange(board, row, column, currentPlayer);
			
		// KTORE POLA MOZE ZAJAC KOLEJNO W POZIOMIE, PIONIE, SKOS (ROSNACO), SKOS (MALEJACO)
		// DRUGI WARUNEK W IFACH TO KONTROLA CZY NEI SKACZEMY SWOIM PIONKIEM NA SWOJEGO
		if((column + range[0] <= 7) && (board[row][column] != board[row][column + range[0]]))
			possibleMove.add(new Point(row, column + range[0]));
		if((column - range[1] >= 0) && (board[row][column] != board[row][column - range[0]]))
			possibleMove.add(new Point(row, column - range[1]));
		
		if((row + range[2] <= 7) && (board[row][column] != board[row + range[2]][column]))
			possibleMove.add(new Point(row + range[2], column));
		if((row - range[3] >= 0) && (board[row][column] != board[row - range[3]][column]))
			possibleMove.add(new Point(row - range[3], column));
		
		if((column + range[4] <= 7 && row + range[4] <= 7) && (board[row][column] != board[row + range[4]][column + range[4]]))
			possibleMove.add(new Point(row + range[4], column + range[4]));
		if((column - range[5] >= 0 && row - range[5] >= 0) && (board[row][column] != board[row - range[5]][column - range[5]]))
			possibleMove.add(new Point(row - range[5], column - range[5]));
		
		if((row + range[6] <= 7 && column - range[6] >= 0) && (board[row][column] != board[row + range[6]][column - range[6]]))
			possibleMove.add(new Point(row + range[6], column - range[6]));
		if((row - range[7] >= 0 && column + range[7] <= 7) && (board[row][column] != board[row - range[7]][column + range[7]]))
			possibleMove.add(new Point(row - range[7], column + range[7]));
		
		return possibleMove;
	}
	
	private static int[] getRange (Status board[][], int row, int column, Status currentPlayer)
	{
		Status enemy = currentPlayer == Status.BLACK ? Status.RED : Status.BLACK;
		
		// tablica zasiegow 
		int range[] = new int[8];
		int enemyPawn[] = new int[8];
		Arrays.fill(range, 1);
		Arrays.fill(enemyPawn, 100); // 100 = nieskonczonosc
		
		// W PRAWO
		for(int i = 1 ; column + i < 8 ; ++i)
		{
			if(board[row][column + i] != Status.EMPTY) range[0]++;
			if(board[row][column + i] == enemy && enemyPawn[0] == 100) enemyPawn[0] = i;
		}
		
		// W LEWO
		for(int i = 1 ; column - i >= 0 ; ++i)
		{
			if(board[row][column - i] != Status.EMPTY) range[0]++;
			if(board[row][column - i] == enemy && enemyPawn[1] == 100) enemyPawn[1] = i;
		}
		
		// W GORE
		for(int i = 1 ; row + i < 8 ; ++i)
		{
			if(board[row + i][column] != Status.EMPTY) range[2]++;
			if(board[row + i][column] == enemy && enemyPawn[2] == 100) enemyPawn[2] = i;
		}
		
		// W DOL
		for(int i = 1 ; row - i >= 0 ; ++i)
		{
			if(board[row - i][column] != Status.EMPTY) range[2]++;
			if(board[row - i][column] == enemy && enemyPawn[3] == 100) enemyPawn[3] = i;
		}
		
		// GORA PRAWO
		for(int i = 1; row + i < 8 && column + i < 8 ; ++i)
		{
			if(board[row + i][column + i] != Status.EMPTY) range[4]++;
			if(board[row + i][column + i] == enemy && enemyPawn[4] == 100) enemyPawn[4] = i;
		}
		
		// DOL LEWO
		for (int i = 1; row - i >= 0 && column - i >= 0; ++i)
		{
			if(board[row - i][column - i] != Status.EMPTY) range[4]++;
			if(board[row - i][column - i] == enemy && enemyPawn[5] == 100) enemyPawn[5] = i;
		}
		
		// GORA LEWO
		for(int i = 1; row + i < 8 && column - i >= 0 ; ++i)
		{
			if(board[row + i][column - i] != Status.EMPTY) range[6]++;
			if(board[row + i][column - i] == enemy && enemyPawn[6] == 100) enemyPawn[6] = i;
		}

		// DOL PRAWO
		for(int i = 1; row - i >= 0 && column + i < 8; ++i)
		{
			if(board[row - i][column + i] != Status.EMPTY) range[6]++;
			if(board[row - i][column + i] == enemy && enemyPawn[7] == 100) enemyPawn[7] = i;
		}
		
		range[1] = range[0]; range[3] = range[2]; range[5] = range[4]; range[7] = range[6];
		
		for(int i = 0 ; i < 8 ; ++i)
			if(enemyPawn[i] < range[i]) range[i] = 0;

		return range;
	}
}
