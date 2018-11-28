package main.java.ai;

public class BotConfig
{
    public float comW;// center of mass weight
    public float uniW; // unity weight
    public float centW; // centralisation weight
    public int size;
    public int[] minMoves;
    public int[][] positionValue;
    public int[] maxPosValue;

    public BotConfig(int[] minMoves, int[][] positionValue, int[] maxPosValue, float comW, float uniW, float centW, int size)
    {
        this.minMoves = minMoves;
        this.positionValue = positionValue;
        this.maxPosValue = maxPosValue;
        this.comW = comW;
        this.uniW = uniW;
        this.centW = centW;
        this.size = size;
    }
}
