package stargame.android.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

/**
 * TextAnimtion2D is used to display any textual animation, like
 * damage dealt, miss, absorb, etc.
 */
public class TextAnimation2D implements IDrawable
{
	/** The Paint used to draw the text */
	private Paint mPaint;

	/** The Paint used to draw the stroke around the text */
	private Paint mStrokePaint;

	/** The text to be displayed */
	private String mText;

	/** The duration of the move text display */
	private static int DISPLAY_MOVE_FRAMES = 20;

	/** The duration of the static text display */
	private static int DISPLAY_STATIC_FRAMES = 7;

	/** Displayed frames count */
	private int mFrameCount;

	/** Tells whether the animation is elapsed */
	private boolean mElapsed;

	public TextAnimation2D( String strText, int iColor )
	{
		mText = strText;
		mFrameCount = 0;
		mElapsed = false;

		mPaint = new Paint();
		mPaint.setColor( iColor );
		mPaint.setTextAlign( Paint.Align.CENTER );
		mPaint.setTypeface( Typeface.DEFAULT_BOLD );
		mPaint.setTextSize( 20f );

		mStrokePaint = new Paint();
		mStrokePaint.setColor( Color.BLACK );
		mStrokePaint.setTextAlign( Paint.Align.CENTER );
		mStrokePaint.setTypeface( Typeface.DEFAULT_BOLD );
		mStrokePaint.setStyle( Paint.Style.STROKE );
		mStrokePaint.setStrokeWidth( 2 );
	}

	public boolean isFinished()
	{
		return mElapsed;
	}

	public void doDraw( Canvas oCanvas, RectF oDestRect, float fZoomFactor, Paint oPaint )
	{
		if ( mElapsed )
		{
			return;
		}

		if ( mFrameCount >= DISPLAY_STATIC_FRAMES + DISPLAY_MOVE_FRAMES )
		{
			mElapsed = true;
		}
		else if ( mFrameCount < DISPLAY_MOVE_FRAMES )
		{
			// Interpolate growing
			float fRatio = ( DISPLAY_MOVE_FRAMES - mFrameCount ) / ( float )DISPLAY_MOVE_FRAMES;
			mStrokePaint.setStrokeWidth( 3.f );
			mPaint.setTextScaleX( 1.f + fRatio );
			mStrokePaint.setTextScaleX( 1.f + fRatio );

			float fRatioSquared = ( 1.f - fRatio ) * ( 1.f - fRatio );
			float fHeight = fRatioSquared * ( oDestRect.centerY() + mPaint.getTextSize() ) + ( 1.f - fRatioSquared ) * ( oDestRect.top + mPaint.getTextSize() );

			oCanvas.drawText( mText, oDestRect.centerX(), fHeight, mStrokePaint );
			oCanvas.drawText( mText, oDestRect.centerX(), fHeight, mPaint );
		}
		else
		{
			mStrokePaint.setStrokeWidth( 3.f );
			mPaint.setTextScaleX( 1.f );
			mStrokePaint.setTextScaleX( 1.f );

			oCanvas.drawText( mText, oDestRect.centerX(), oDestRect.centerY() + mPaint.getTextSize(), mStrokePaint );
			oCanvas.drawText( mText, oDestRect.centerX(), oDestRect.centerY() + mPaint.getTextSize(), mPaint );
		}

		mFrameCount++;
	}
}
