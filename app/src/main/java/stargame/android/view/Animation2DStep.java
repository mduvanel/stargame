package stargame.android.view;

public class Animation2DStep
{
    protected int mResourceIndex; // resource used for this animation step
    protected int mXOffset; // Offset in X direction for this animation's bitmap for this step
    protected int mYOffset; // Offset in Y direction for this animation's bitmap for this step
    protected int mDuration; // duration for this step, in frames
    protected int mZOrderOffset; // Offset in the ZOrder for this step

    protected Animation2DStep()
    {
        mResourceIndex = -1;
        mXOffset = 0;
        mYOffset = 0;
        mDuration = -1;
        mZOrderOffset = 0;
    }

    public Animation2DStep( int iResourceIndex, int iXOffset, int iYOffset, int iDuration,
                            int iZOrderOffset )
    {
        mResourceIndex = iResourceIndex;
        mXOffset = iXOffset;
        mYOffset = iYOffset;
        mDuration = iDuration;
        mZOrderOffset = iZOrderOffset;
    }

    public Animation2DStep Clone()
    {
        Animation2DStep oClone = new Animation2DStep();
        oClone.mResourceIndex = this.mResourceIndex;
        oClone.mXOffset = this.mXOffset;
        oClone.mYOffset = this.mYOffset;
        oClone.mDuration = this.mDuration;
        oClone.mZOrderOffset = this.mZOrderOffset;
        return oClone;
    }

    public boolean DisplayChange( Animation2DStep oCompareStep )
    {
        return ( mZOrderOffset != oCompareStep.mZOrderOffset ||
                mXOffset != oCompareStep.mXOffset ||
                mYOffset != oCompareStep.mYOffset );
    }

    public boolean DisplayChange()
    {
        return ( mZOrderOffset != 0 || mXOffset != 0 || mYOffset != 0 );
    }
}
