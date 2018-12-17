package ai;

import ui.Field;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

// obliczanie mozliwych ruchow oraz sprawdzanie warunkow zwyciestwa. Korzysta z niej zarowno bot jak i controller gry
public class BoardManager 
{
	// Zmiana reprezentacji planszy która posluguje się controller na wersje prostsza która posługuje się bot
	// Field[][] -> Status[][]
	public static Status[][] fieldsToStatus(Field[][] fields)
	{
        //obliczenie rozmiaru planszu
		int size = fields.length;
		Status output[][] = new Status[size][size];
		
		for(int row = 0 ; row < size ; ++row)
			for(int column = 0 ; column < size ; ++column)
				output[row][column] = fields[row][column].getStatus();

		return output;
	}
	
	// metoda wywolana przez controller
	public static ArrayList<Point> getMoves(Field[][] fields, int row, int column, Status currentPlayer)
	{
		return getMoves(fieldsToStatus(fields), row, column, currentPlayer);
	}

	// metoda wywolana przez bota
	static ArrayList<Point> getMoves(Status[][] board, int row, int column, Status currentPlayer)
	{
	    //obliczenie rozmiaru planszu
		int size = board.length;

		ArrayList<Point> possibleMove = new ArrayList<>();
		// pobranie zasiegu ruchu w konkretnych kierunkach
		// 0-Prawo, 1-Lewo, 2-Gora, 3-Dol, 4-GoraPrawo, 5-DolLewo, 6-GoraLewo, 7-DolPrawo
		int range[] = getRange(board, row, column, currentPlayer, size);
			
		// sprwadzenie czy nie wychodzimy danym ruchem za plaszne, jesli nie to dodajemy go do mozliwych ruchow
		// drugi warunek w ifach to sprawdzenie czy nie skaczemy swoim pionkiem na swojego
		if((column + range[0] < size) && (board[row][column] != board[row][column + range[0]]))
			possibleMove.add(new Point(row, column + range[0]));
		if((column - range[1] >= 0) && (board[row][column] != board[row][column - range[1]]))
			possibleMove.add(new Point(row, column - range[1]));
		
		if((row + range[2] < size) && (board[row][column] != board[row + range[2]][column]))
			possibleMove.add(new Point(row + range[2], column));
		if((row - range[3] >= 0) && (board[row][column] != board[row - range[3]][column]))
			possibleMove.add(new Point(row - range[3], column));
		
		if((column + range[4] < size && row + range[4] <= 7) && (board[row][column] != board[row + range[4]][column + range[4]]))
			possibleMove.add(new Point(row + range[4], column + range[4]));
		if((column - range[5] >= 0 && row - range[5] >= 0) && (board[row][column] != board[row - range[5]][column - range[5]]))
			possibleMove.add(new Point(row - range[5], column - range[5]));
		
		if((row + range[6] < size && column - range[6] >= 0) && (board[row][column] != board[row + range[6]][column - range[6]]))
			possibleMove.add(new Point(row + range[6], column - range[6]));
		if((row - range[7] >= 0 && column + range[7] <= 7) && (board[row][column] != board[row - range[7]][column + range[7]]))
			possibleMove.add(new Point(row - range[7], column + range[7]));
		
		return possibleMove;
	}
	
	static int[] getRange(Status board[][], int row, int column, Status currentPlayer, int size)
	{
		Status enemy = currentPlayer == Status.BLACK ? Status.RED : Status.BLACK;
		
		// 0-Prawo, 1-Lewo, 2-Gora, 3-Dol, 4-GoraPrawo, 5-DolLewo, 6-GoraLewo, 7-DolPrawo
		
		// tablica zasiegow teoretycznie zasieg w gore i w dol jest taki sam,ale
		// mozliwe jest ze np. ruch w dol jest blokowany przez przeciwny pionek
		int range[] = new int[size];
		
		// pozycja najblizszego przeciwnego pionak w danym kierunku
		int enemyPawn[] = new int[size];
		
		Arrays.fill(range, 1);       // 1 na start bo pionek dla ktorego liczymy ruchy zapewnia 1 mozliwy ruch w kazda strone
		Arrays.fill(enemyPawn, 100); // 100 = nieskonczonosc
		
	// LICZYMY RUCHY OD DANEGO PIONKA W KOLEJNE MOZLIWE KIERUNKI:
	// drugi warunek w kazdej petli oblicza pozycja najblizszego przeciwnego pionka w danym kierunku
		
		// W PRAWO
		for(int i = 1 ; column + i < size ; ++i)
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
		for(int i = 1 ; row + i < size ; ++i)
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
		for(int i = 1; row + i < size && column + i < size ; ++i)
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
		for(int i = 1; row + i < size && column - i >= 0 ; ++i)
		{
			if(board[row + i][column - i] != Status.EMPTY) range[6]++;
			if(board[row + i][column - i] == enemy && enemyPawn[6] == 100) enemyPawn[6] = i;
		}

		// DOL PRAWO
		for(int i = 1; row - i >= 0 && column + i < size; ++i)
		{
			if(board[row - i][column + i] != Status.EMPTY) range[6]++;
			if(board[row - i][column + i] == enemy && enemyPawn[7] == 100) enemyPawn[7] = i;
		}
		
		range[1] = range[0]; range[3] = range[2]; range[5] = range[4]; range[7] = range[6];
		
		// oceniamy czy dany ruch jest blokowany przez przeciwny pionek
		for(int i = 0 ; i < 8 ; ++i)
			if(enemyPawn[i] < range[i]) range[i] = 0;

		return range;
	}

	// metoda wywolywana przez controller
	public static boolean checkWin(Field[][] fields, ArrayList<Point> pawns, Status currentPlayer)
	{
		return checkWin(fieldsToStatus(fields), pawns, currentPlayer);
	}
	
	// metoda wywolywana przez bota
	// sprawdzamy czy spelnione zostaly warunki zwyciestwa
	// przeszukiwanie wszerz
	static boolean checkWin(Status[][] board, ArrayList<Point> pawns, Status currentPlayer)
	{
	    int size = board.length;
		ArrayList<Point> connected = new ArrayList<>();
		
		//dodajemy jednego pionka do tablicy stanow polaczonych
		connected.add(pawns.get(0));
		
		// przechodzimy po tablicy stanow polaczonych i dodajemy do niej sasiadów pionków znajdujacych się w tablicy
		for(int i = 0 ; i < connected.size() ; ++i)
		{
			Point p = connected.get(i);
			int row = p.x, column = p.y; 
			
			for(int j = -1 ; j < 2 ; ++j)
				for(int k = -1; k < 2 ; ++k)
					if(row + j >= 0 && row + j < size && column + k >= 0 && column + k < size // unikamy wyjscia za plansze
					&& board[row + j][column + k] == currentPlayer		   // dodajemy sąsiadów tylko o tym samym kolorze
					&& !connected.contains(new Point(row + j, column +k))) // unikamy duplikacji
					    	connected.add(new Point(row + j, column + k));
		}
		
		// jezeli tablica stanow odwiedzonych posiada wyszstkie pionki danego koloru na mapie, tzn ze sa one polaczone w jedna grp
		return connected.size() == pawns.size();
	}
}
