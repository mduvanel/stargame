package stargame.android.util;

import java.util.Random;

/*
 * Singleton for generating random numbers
 */
public class RandomGenerator
{
    Random mRandom;

    static RandomGenerator msGenerator = null;

    private RandomGenerator()
    {
        mRandom = new Random();
        mRandom.setSeed( System.currentTimeMillis() );
    }

    static public RandomGenerator GetInstance()
    {
        if ( msGenerator == null )
        {
            msGenerator = new RandomGenerator();
        }

        return msGenerator;
    }

    public double GetRandom( double dMinValue, double dMaxValue )
    {
        double dValue = mRandom.nextDouble();
        return dValue * ( dMaxValue - dMinValue ) + dMinValue;
    }

    public int GetRandom( int iMinValue, int iMaxValue )
    {
        int iValue = mRandom.nextInt( iMaxValue - iMinValue + 1 );
        return iValue + iMinValue;
    }
}
