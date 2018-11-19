package application;

import java.awt.Point;
import java.util.ArrayList;

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
	
	public static ArrayList<Point> getMoves(Field[][] fields, int row, int column)
	{
		return getMoves(fieldsToStatus(fields), row, column);
	}

	private static ArrayList<Point> getMoves(Status[][] board, int row, int column) 
	{
		ArrayList<Point> possibleMove = new ArrayList<Point>();
		int range[] = getRange(board, row, column);
			
		// KTORE POLA MOZE ZAJAC KOLEJNO W POZIOMIE, PIONIE, SKOS (ROSNACO), SKOS (MALEJACO)
		// DRUGI WARUNEK W IFACH TO KONTROLA CZY NEI SKACZEMY SWOIM PIONKIEM NA SWOJEGO
		if((column - range[0] >= 0) && (board[row][column] != board[row][column - range[0]]))
			possibleMove.add(new Point(row, column - range[0]));
		if((column + range[0] <= 7) && (board[row][column] != board[row][column + range[0]]))
			possibleMove.add(new Point(row, column + range[0]));
		
		if((row - range[1] >= 0) && (board[row][column] != board[row - range[1]][column]))
			possibleMove.add(new Point(row - range[1], column));
		if((row + range[1] <= 7) && (board[row][column] != board[row + range[1]][column]))
			possibleMove.add(new Point(row + range[1], column));
		
		if((column - range[2] >= 0 && row - range[2] >= 0) && (board[row][column] != board[row - range[2]][column - range[2]]))
			possibleMove.add(new Point(row - range[2], column - range[2]));
		if((column + range[2] <= 7 && row + range[2] <= 7) && (board[row][column] != board[row + range[2]][column + range[2]]))
			possibleMove.add(new Point(row + range[2], column + range[2]));
		
		
		if((row - range[3] >= 0 && column + range[3] <= 7) && (board[row][column] != board[row - range[3]][column + range[3]]))
			possibleMove.add(new Point(row - range[3], column + range[3]));
		if((row + range[3] <= 7 && column - range[3] >= 0) && (board[row][column] != board[row + range[3]][column - range[3]]))
			possibleMove.add(new Point(row + range[3], column - range[3]));
		
		// TU TRZEBA DODAC FILTROWANIE:
		// NIE MOZNA PRZZESKAKIWAC WROGICH PIONKOW
		
		//MOZNA ZRZOBIC TAK JAK TUTAJ PONIZEJ CHYBA ZE MACIE LEPSZY POMYSL, ITERUJEMY OD TYLU BO OD PRZODU JEST ZLE XD JAK CHCECIE WIEDZIEC DLACZEGO TO NAPISZCIE NA FB
		
		//for(int i = possibleMove.size() ; i >= 0 ; -i)
		//	if(!isValidMove(board, possibleMove.get(i))) possibleMove.remove(i);
		
		
		return possibleMove;
	}
	
	private static int[] getRange (Status board[][], int row, int column)
	{
		//tablica zasiegow 
		int range[] = {0, 0, 0, 0};
		
		// zasieg ruchu w poziomie
		for(int c = 0 ; c < 8 ; ++c)
			if(board[row][c] != Status.EMPTY) range[0]++;
		
		// zasieg ruchu w pionie
		for(int r = 0 ; r < 8 ; ++r)
			if(board[r][column] != Status.EMPTY) range[1]++;
		
		// zasieg ruchu po skosie "funkcja rosnaca
		for(int i = 0; row + i < 8 && column + i < 8 ; ++i)
			if (board[row+i][column + i] != Status.EMPTY) range[2]++;

		for (int i = 1; row - i >= 0 && column - i >= 0; ++i)
			if (board[row - i][column - i] != Status.EMPTY) range[2]++;
		
		// zasieg ruchu po skosie "funkcja malejaca
		for(int i = 0; row + i < 8 && column - i >= 0 ; ++i)
			if (board[row + i][column - i] != Status.EMPTY) range[3]++;

		for(int i = 1; row - i >= 0 && column + i < 8; ++i)
			if (board[row - i][column + i] != Status.EMPTY) range[3]++;

		return range;
	}
}
