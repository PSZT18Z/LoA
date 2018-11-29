package ai;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// klasa odpowiedzielna za zczytanie konfiguracja botów z plików konfiguracyjnych
public class BotConfigReader
{
    public static BotConfig readBotConfig(Status colour) throws IOException
    {
        Properties prop = new Properties();

        String configFileName = colour == Status.BLACK ? "blackBot.config.properties" : "redBot.config.properties";

        InputStream inputStream = BotConfigReader.class.getClassLoader().getResourceAsStream(configFileName);

        // sprawdzamy czy plik instnieje
        if(inputStream != null)
        {
            prop.load(inputStream);
            inputStream.close();
        }
        else
            throw new FileNotFoundException("bot config file: " + configFileName + " not found");

        //czytamy wartosci a nastepnie zamieniamy je na odpowiednie typy
        float comW = Float.valueOf(prop.getProperty("centerOfMassWeight"));   // czytamy wage srodka masy
        float uniW = Float.valueOf(prop.getProperty("unityWeight"));          // czytamy wage jednosci
        float centW = Float.valueOf(prop.getProperty("centralisationWeight"));// czytamy wage centralizacji
        int size = Integer.valueOf(prop.getProperty("size"));                 // czytamy rozmiar

        // tablice intow zwracane sa jako jedne string z intami oddzielonymi przecinkami
        String minDistanceSumString = prop.getProperty("minDistanceSum");
        String maxPosValueString = prop.getProperty("maxPosValue");
        String[] rows = new String[size];
        for(int i = 0 ; i<size ; ++i) rows[i] = prop.getProperty("row"+i);

        // zamieniami tablice stringow na tablice intow
        int minDistanceSum[] = parseStringToIntArray(minDistanceSumString);
        int maxPosValue[] = parseStringToIntArray(maxPosValueString);
        int positionValue[][] = parsePositionValues(rows);



        return new BotConfig(minDistanceSum, positionValue, maxPosValue, comW, uniW, centW, size);
    }

    // zamieniamy tablice stringow w ktorej kazdy string to inty oddzielone przecinkami
    // na dwu wymiraowa tablice intow
    private static int[][] parsePositionValues(String[] rows)
    {
        int[][] positionValue = new int[rows.length][rows.length];

        for(int row = 0 ; row < rows.length ; ++row)
            positionValue[row] = parseStringToIntArray(rows[row]);

        return positionValue;
    }

    // zamieniamy string zawierajacy w sobie inty oddzielone przecinkami na tablice intow
    private static int[] parseStringToIntArray(String string)
    {
        String[] singleInts =  string.split(",");
        int[] ints = new int[singleInts.length];

        for(int i = 0 ; i < singleInts.length ; ++i) ints[i] = Integer.valueOf(singleInts[i].trim());

        return ints;
    }
}
