package stargame.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.NinePatchDrawable;

import stargame.android.R;
import stargame.android.controller.IDPadPeeker;
import stargame.android.controller.IDPadPeeker.DPadZone;
import stargame.android.model.BattleCell;
import stargame.android.model.BattleUnit;
import stargame.android.util.Logger;

public class InterfaceDrawer
{
    /**
     * Tells whether or not to draw the 5 zone separation
     */
    private boolean mDrawOverlay;

    /**
     * The strings corresponding to the actions of all 5 directions
     */
    private String mTopString;
    private String mBottomString;
    private String mLeftString;
    private String mRightString;
    private String mCenterString;

    /**
     * The paint for the text
     */
    private Paint mPaint;

    /**
     * The paint for the center rectangle
     */
    private Paint mCenterPaint;

    /**
     * The paint for the lifebar
     */
    private Paint mLifebarPaint;

    /**
     * The bitmaps used for the interface
     */
    private Bitmap mLifebar;

    private NinePatchDrawable mButton;
    private NinePatchDrawable mSelectedButton;

    /**
     * The peeker to display currently selected direction
     */
    private IDPadPeeker mDPadPeeker;

    private BattleView2D mView;

    /**
     * Rect used to gather info from font
     */
    private Rect mClipRect;

    /**
     * Rect used for drawing
     */
    private Rect mDrawRect;

    private Battle2D mBattle;

    /**
     * Surface dimensions
     */
    private int mWidth;
    private int mHeight;

    /**
     * Text size ratio due to DIP behavior...
     */
    private float mTextSizeRatio;

    /**
     * float array used for tet drawing
     */
    private float[] mTextDrawArray;

    private static final int MAX_TEXT_LENGTH = 50;

    public InterfaceDrawer( Context oContext, BattleView2D oBattleView, Battle2D oBattle )
    {
        mBattle = oBattle;
        mView = oBattleView;
        mDrawOverlay = true;
        mTopString = "";
        mBottomString = "";
        mLeftString = "";
        mRightString = "";
        mCenterString = "";
        mWidth = -1;
        mHeight = -1;
        mClipRect = new Rect();
        mDrawRect = new Rect();
        mDPadPeeker = null;
        mTextDrawArray = new float[ 2 * MAX_TEXT_LENGTH ];
        mTextSizeRatio = oContext.getResources().getDisplayMetrics().density;

        // Paint settings
        mPaint = new Paint();
        mPaint.setTextAlign( Align.CENTER );
        mPaint.setColor( Color.BLACK );
        mPaint.setAlpha( 192 );
        mPaint.setTypeface( Typeface.DEFAULT_BOLD );
        mPaint.setAntiAlias( true );
        mPaint.setTextSize( mTextSizeRatio * 15f );

        mCenterPaint = new Paint();
        mCenterPaint.setColor( Color.RED );
        mCenterPaint.setAlpha( 85 );
        mCenterPaint.setStyle( Paint.Style.FILL_AND_STROKE );
        mCenterPaint.setStrokeCap( Paint.Cap.ROUND );
        mCenterPaint.setStrokeWidth( 2 );

        mLifebarPaint = new Paint();

        // load bitmaps
        Bitmap oButtonBitmap = BitmapRepository.GetInstance().GetBitmap( R.drawable.button_raw );
        byte[] aChunk = oButtonBitmap.getNinePatchChunk();
        mButton = new NinePatchDrawable( oContext.getResources(), oButtonBitmap, aChunk,
                                         new Rect( 7, 7, 17, 17 ), null );

        oButtonBitmap = BitmapRepository.GetInstance().GetBitmap( R.drawable.button_selected_raw );
        aChunk = oButtonBitmap.getNinePatchChunk();
        mSelectedButton = new NinePatchDrawable( oContext.getResources(), oButtonBitmap, aChunk,
                                                 new Rect( 7, 7, 17, 17 ), null );
        mLifebar = BitmapRepository.GetInstance().GetBitmap( R.drawable.lifebar );
    }

    public void SetPeeker( IDPadPeeker oPeeker )
    {
        mDPadPeeker = oPeeker;
    }

    public void SetStrings( String oTopString, String oBottomString, String oLeftString,
                            String oRightString, String oCenterString )
    {
        synchronized ( BattleThread2D.mDrawingLock )
        {
            mTopString = oTopString;
            mBottomString = oBottomString;
            mLeftString = oLeftString;
            mRightString = oRightString;
            mCenterString = oCenterString;
            //Logger.d( String.format( "Setting strings: Up=%s, Left=%s, Right=%s, Down=%s", mTopString, mLeftString, mRightString, mBottomString ) );
        }
    }

    public void DrawHorizontal9Patch( Canvas oCanvas, NinePatchDrawable o9Patch, String strText,
                                      int iHeightBitmap )
    {
        if ( strText.length() > 0 )
        {
            // First get dimensions for the text
            mPaint.getTextBounds( strText, 0, strText.length(), mClipRect );

            mDrawRect.set(
                    ( mWidth - ( o9Patch.getMinimumWidth() + mClipRect.width() ) ) / 2,
                    iHeightBitmap,
                    ( mWidth + ( o9Patch.getMinimumWidth() + mClipRect.width() ) ) / 2,
                    iHeightBitmap + o9Patch.getMinimumHeight() + mClipRect.height() );
            o9Patch.setBounds( mDrawRect );
            o9Patch.draw( oCanvas );

            oCanvas.drawText(
                    strText,
                    mWidth / 2,
                    iHeightBitmap + o9Patch.getMinimumHeight() / 2 - mClipRect.top,
                    mPaint );
        }
    }

    public void DrawVertical9Patch( Canvas oCanvas, NinePatchDrawable o9Patch, String strText,
                                    int iWidthBitmap )
    {
        if ( strText.length() > 0 )
        {
            // First get dimensions for the text
            int iTextVerticalSpace = 4;
            GetTextBounds( strText );
            int iAverageWidth = mClipRect.width() / strText.length();

            // Compute the position for every letter
            if ( strText.length() > MAX_TEXT_LENGTH )
            {
                Logger.e( String.format( "Text too long to draw! (%s)", strText ) );
            }

            int iBaseWidth = iWidthBitmap + ( o9Patch.getMinimumWidth() + iAverageWidth ) / 2;
            int iHeight = 0;
            for ( int i = 0; ( i < strText.length() ) && ( i < MAX_TEXT_LENGTH ); i++ )
            {
                mPaint.getTextBounds( strText, i, i + 1, mClipRect );

                // Compute correct Y position
                iHeight += mClipRect.height() - mClipRect.bottom;
                if ( i > 0 )
                {
                    iHeight += iTextVerticalSpace;
                }
                mTextDrawArray[ 2 * i ] = iBaseWidth;
                mTextDrawArray[ 2 * i + 1 ] = iHeight;
                iHeight += mClipRect.bottom;
            }

            // Now we know the button's total height, we can offset properly the Y coordinate
            int iTotalHeight = iHeight;
            for ( int i = 0; i < strText.length(); i++ )
            {
                mTextDrawArray[ 2 * i + 1 ] += ( mHeight - iTotalHeight ) / 2;
                iHeight += mClipRect.bottom;
            }

            mDrawRect.set(
                    iWidthBitmap,
                    ( mHeight - ( o9Patch.getMinimumHeight() + iTotalHeight ) ) / 2,
                    iWidthBitmap + o9Patch.getMinimumWidth() + iAverageWidth,
                    ( mHeight + ( o9Patch.getMinimumHeight() + iTotalHeight ) ) / 2 );
            o9Patch.setBounds( mDrawRect );
            o9Patch.draw( oCanvas );

            // Draw all the text
            oCanvas.drawPosText(
                    strText,
                    mTextDrawArray,
                    mPaint );
        }
    }

    public void DrawHorizontalButton( Canvas oCanvas, Bitmap oBitmap, String strText,
                                      int iHeightBitmap, int iHeightText )
    {
        if ( strText.length() > 0 )
        {
            oCanvas.drawBitmap(
                    oBitmap,
                    ( mWidth - oBitmap.getWidth() ) / 2,
                    iHeightBitmap,
                    mPaint );
            mPaint.getTextBounds( strText, 0, strText.length(), mClipRect );
            oCanvas.drawText(
                    strText,
                    mWidth / 2,
                    iHeightText,
                    mPaint );
        }
    }

    protected Rect GetTextBounds( String strText )
    {
        mPaint.getTextBounds( strText, 0, strText.length(), mClipRect );
        return mClipRect;
    }

    public void DrawVerticalButton( Canvas oCanvas, Bitmap oBitmap, String strText,
                                    int iWidthBitmap, int iWidthText )
    {
        if ( strText.length() > 0 )
        {
            int iHeightCounter = 0, iTextSpace = 4;
            GetTextBounds( strText );
            int iTextHeight = mClipRect.bottom - mClipRect.top;
            oCanvas.drawBitmap(
                    oBitmap,
                    iWidthBitmap,
                    ( mHeight - oBitmap.getHeight() ) / 2,
                    mPaint );
            int iBaseHeight = ( mHeight - strText.length() * ( iTextHeight + iTextSpace ) ) / 2;
            for ( int i = 0; i < strText.length(); i++ )
            {
                mPaint.getTextBounds( strText, i, i + 1, mClipRect );
                iHeightCounter += iTextSpace + mClipRect.bottom - mClipRect.top;
                oCanvas.drawText(
                        strText.substring( i, i + 1 ),
                        iWidthText,
                        iBaseHeight + iHeightCounter - mClipRect.bottom,
                        mPaint );
            }
        }
    }

    public void DrawPortrait( Canvas oCanvas )
    {
        BattleCell oCell = BattleField2D.GetInstance().GetSelectedCell();
        BattleUnit oCellUnit = oCell.GetUnit();
        if ( oCellUnit != null )
        {
            int iMargin = DisplayConstants2D.GetInterfaceMargin();
            Bitmap oPortrait = mBattle.GetBattleUnit2D( oCellUnit ).GetPortraitBitmap();

            // Draw the portrait
            mDrawRect.set( 0, 0, oPortrait.getWidth(), oPortrait.getHeight() );
            mDrawRect.offset( iMargin + ( mLifebar.getWidth() - oPortrait.getWidth() ) / 2,
                              mHeight - oPortrait.getHeight() - mLifebar.getHeight() - iMargin );
            oCanvas.drawBitmap( oPortrait, null, mDrawRect, null );

            // Draw the bar
            mDrawRect.set( 0, 0, mLifebar.getWidth(), mLifebar.getHeight() );
            mDrawRect.offset( iMargin, mHeight - mLifebar.getHeight() - iMargin );
            oCanvas.drawBitmap( mLifebar, null, mDrawRect, null );

            // Draw the life
            float fLifeRatio = ( float ) oCellUnit.GetCurrentHitPoints() / ( float ) oCellUnit.GetUnit().GetResultingAttributes().GetHitPoints();
            int iColorRatio = ( int ) ( 511 * fLifeRatio );
            mLifebarPaint.setARGB( 192, Math.min( 511 - iColorRatio, 255 ),
                                   Math.min( 255, iColorRatio ), 0 );

            mDrawRect.left += ( int ) ( DisplayConstants2D.GetLifebarMargin() * mTextSizeRatio );
            mDrawRect.top += ( int ) ( DisplayConstants2D.GetLifebarMargin() * mTextSizeRatio );
            mDrawRect.right -= ( int ) ( DisplayConstants2D.GetLifebarMargin() * mTextSizeRatio );
            mDrawRect.bottom -= ( int ) ( DisplayConstants2D.GetLifebarMargin() * mTextSizeRatio );

            // Fill the bar with the life ratio
            mDrawRect.right = ( int ) ( mDrawRect.left + ( float ) mDrawRect.width() * fLifeRatio );

            oCanvas.drawRect( mDrawRect, mLifebarPaint );
        }
    }

    public void doDraw( Canvas oCanvas )
    {
        DPadZone oZone = DPadZone.NONE;
        if ( mDPadPeeker != null )
        {
            oZone = mDPadPeeker.PeekZone();
        }

        //Logger.d( String.format( "Displaying interface with strings: Up=%s, Left=%s, Right=%s, Down=%s", mTopString, mLeftString, mRightString, mBottomString ) );

        DrawHorizontal9Patch(
                oCanvas,
                ( oZone == DPadZone.UP ? mSelectedButton : mButton ),
                mTopString,
                DisplayConstants2D.GetInterfaceMargin() );

        GetTextBounds( mBottomString );
        DrawHorizontal9Patch(
                oCanvas,
                ( oZone == DPadZone.DOWN ? mSelectedButton : mButton ),
                mBottomString,
                mHeight - DisplayConstants2D.GetInterfaceMargin() - mButton.getMinimumHeight() - mClipRect.height() );

        DrawVertical9Patch(
                oCanvas,
                ( oZone == DPadZone.LEFT ? mSelectedButton : mButton ),
                mLeftString,
                DisplayConstants2D.GetInterfaceMargin() );

        DrawVertical9Patch(
                oCanvas,
                ( oZone == DPadZone.RIGHT ? mSelectedButton : mButton ),
                mRightString,
                mWidth - DisplayConstants2D.GetInterfaceMargin() - mButton.getMinimumWidth() );

        DrawPortrait( oCanvas );

        if ( mDrawOverlay && mCenterString.length() > 0 )
        {
            // Draw the center zone
            oCanvas.drawRect( mView.GetCenterZoneRect(), mCenterPaint );
        }
    }

    public void SetSurfaceSize( int iWidth, int iHeight )
    {
        mWidth = iWidth;
        mHeight = iHeight;
    }
}
