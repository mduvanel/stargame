package stargame.android.view;

public class DisplayConstants2D
{
	/** Text-related values */
	static final private int mDamageTextYOffset = 20;

	/** Cell-related values */
	static final private int mCellWidth = 32;
	static final private int mCellHeight = 32;
	static final private int mCellStackHeight = 16;

	/** Unit-related values */
	static final private int mUnitHeight = 48;

    /** Zoom constants */
    static final private float mZoomStep = 0.5f;
    static final private float mZoomMin = 1.0f;
    static final private float mZoomMax = 2.0f;

    /** Interface-related values */
	static final private int mInterfaceMargin = 10;
	static final private int mTopTextOffset = 6;
	static final private int mBottomTextOffset = 24;
	static final private int mLeftTextOffset = 29;
	static final private int mRightTextOffset = 30;
	static final private int mLifebarMargin = 2;

    /* For multi-density bitmaps...
    public static int ConvertDensity( int iDensity )
    {
    	if ( iDensity == TypedValue.DENSITY_DEFAULT )
    	{
    		return DisplayMetrics.DENSITY_DEFAULT;
    	}
    	else
    	{
    		return iDensity;
    	}
    }*/

    public static int GetUnitHeight()
    {
    	return mUnitHeight;
    }
   
	public static int GetCellWidth()
	{
		return mCellWidth;
	}

	public static int GetCellHeight()
	{
		return mCellHeight;
	}

	public static int GetCellStackHeight()
	{
		return mCellStackHeight;
	}

	public static float GetZoomStep()
	{
		return mZoomStep;
	}

	public static float GetZoomMin()
	{
		return mZoomMin;
	}

	public static float GetZoomMax()
	{
		return mZoomMax;
	}

	public static int GetInterfaceMargin()
	{
		return mInterfaceMargin;
	}

	public static int GetTopTextOffset()
	{
		return mTopTextOffset;
	}

	public static int GetBottomTextOffset()
	{
		return mBottomTextOffset;
	}

	public static int GetLeftTextOffset()
	{
		return mLeftTextOffset;
	}

	public static int GetRightTextOffset()
	{
		return mRightTextOffset;
	}

	public static int GetDamageTextYOffset()
	{
		return mDamageTextYOffset;
	}

	public static int GetLifebarMargin()
	{
		return mLifebarMargin;
	}
}
