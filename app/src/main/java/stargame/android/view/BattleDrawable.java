package stargame.android.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class BattleDrawable implements Comparable< BattleDrawable >
{
    private static RectF msScreenSize;

    public static void SetScreenSize( RectF oRect )
    {
        msScreenSize = oRect;
    }

    /**
     * The IDrawable
     */
    private IDrawable mDrawable;

    public IDrawable GetDrawable()
    {
        return mDrawable;
    }

    public void SetDrawable( IDrawable oDrawable )
    {
        mDrawable = oDrawable;
    }

    /**
     * The paint to use for this drawable
     */
    public Paint mPaint;

    public Paint GetPaint()
    {
        return mPaint;
    }

    public void SetPaint( Paint oPaint )
    {
        mPaint = oPaint;
    }

    /**
     * The location where to draw this bitmap
     */
    public RectF mDestRect;

    public RectF GetDestRect()
    {
        return mDestRect;
    }

    public void SetDestRect( RectF oDestRect )
    {
        mDestRect = oDestRect;
    }

    public void MoveDestRect( int iNewX, int iNewY )
    {
        mDestRect.offsetTo( iNewX, iNewY );
    }

    /**
     * The Z-Order of this Bitmap
     */
    public int mZOrder;

    public int GetZOrder()
    {
        return mZOrder;
    }

    /**
     * Set method returns whether the value changed or not
     */
    public boolean SetZOrder( int iZOrder )
    {
        if ( mZOrder != iZOrder )
        {
            mZOrder = iZOrder;
            return true;
        }

        return false;
    }

    private RectF mDrawingRect;

    public BattleDrawable( IDrawable oDrawable, Paint oPaint, RectF oDestRect, int iZOrder )
    {
        mDrawable = oDrawable;
        mPaint = oPaint;
        mDestRect = oDestRect;
        mZOrder = iZOrder;
        mDrawingRect = new RectF();
    }

    public int compareTo( BattleDrawable oComp )
    {
        return mZOrder - oComp.mZOrder;
    }

    public void doDraw( Canvas oCanvas, int iOffsetX, int iOffsetY, float fZoomFactor )
    {
        // Compute offset rect
        mDrawingRect.set( mDestRect );
        if ( fZoomFactor != 1.0 )
        {
            // Scale rect
            mDrawingRect.bottom *= fZoomFactor;
            mDrawingRect.top *= fZoomFactor;
            mDrawingRect.left *= fZoomFactor;
            mDrawingRect.right *= fZoomFactor;
        }

        mDrawingRect.offset( iOffsetX, iOffsetY );

        // Draw only if destination Rect is at least partially visible
        if ( msScreenSize.contains( mDrawingRect ) || RectF.intersects( msScreenSize,
                                                                        mDrawingRect ) )
        {
            mDrawable.doDraw( oCanvas, mDrawingRect, fZoomFactor, mPaint );
        }
    }
}
