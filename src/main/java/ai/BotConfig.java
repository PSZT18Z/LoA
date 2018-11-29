package ai;

public class BotConfig
{
    public float comW;// center of mass weight
    public float uniW; // unity weight
    public float centW; // centralisation weight
    public int size;
    public int[] minDistanceSum;
    public int[][] positionValue;
    public int[] maxPosValue;

    public BotConfig(int[] minDistanceSum, int[][] positionValue, int[] maxPosValue, float comW, float uniW, float centW, int size)
    {
        this.minDistanceSum = minDistanceSum;
        this.positionValue = positionValue;
        this.maxPosValue = maxPosValue;
        this.comW = comW;
        this.uniW = uniW;
        this.centW = centW;
        this.size = size;
    }
}
