package stargame.android.view;

import java.util.Vector;

import stargame.android.R;
import stargame.android.model.status.UnitStatus;
import stargame.android.util.Orientation;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * UnitStatus2D wraps a vector of UnitStatus to be able to display the 
 * status ailments of a unit. It loads the animations for all UnitStatus,  
 * and the same can be reused for a different UnitStatus.
 */
public class UnitStatus2D implements IDrawable
{
	private Context mContext;

	/** The different UnitStatus to display */
	private Vector< UnitStatus > mVecUnitStatus;

	/** The current status to display */
	private UnitStatus mCurrentStatusToDisplay;

	/** Paint object linked to the status */
	private Paint mStatusPaint;

	/** The animations for the status */
	private AnimatedItem2D mAnimations;

	public UnitStatus2D( Vector< UnitStatus > vecStatus, Context oContext )
	{
		mContext = oContext;
		mStatusPaint = new Paint();
		mVecUnitStatus = vecStatus;
		mCurrentStatusToDisplay = null;
		mAnimations = new AnimatedItem2D( "status_animations", oContext.getResources() );
	}

	/** Change dynamically the UnitStatus */
	public void SetStatusVector( Vector< UnitStatus > vecStatus )
	{
		// Lock on Drawing Lock to avoid changing UnitStatus during animation
		synchronized ( BattleThread2D.mDrawingLock )
		{
			// Check if the currently displaying status is still in the
			// new vector
			mVecUnitStatus = vecStatus;
			if ( ( mVecUnitStatus != null ) && 
					!mVecUnitStatus.contains( mCurrentStatusToDisplay ) )
			{
				// Set to null, a status will be set in the next doDraw() call
				mCurrentStatusToDisplay = null;
			}
		}
	}

	/** Draw the current status's animation if needed */
	public void doDraw( Canvas oCanvas, RectF oSourceRect, float fZoomFactor, Paint oPaint )
	{
		if ( mVecUnitStatus != null && !mVecUnitStatus.isEmpty() )
		{
			if ( ( mCurrentStatusToDisplay == null ) )
			{
				// Get the first status in the vector
				mCurrentStatusToDisplay = mVecUnitStatus.get( 0 );
				mAnimations.SetBaseAnimation( mCurrentStatusToDisplay.GetName( mContext ), Orientation.NONE );
			}
			else if ( mAnimations.isFinished() )
			{
				mAnimations.ResetCurrentAnimation();

				// Get the next status in the vector
				int iPos = mVecUnitStatus.indexOf( mCurrentStatusToDisplay );
				int iNewPos = ( iPos + 1 ) % mVecUnitStatus.size();
				mCurrentStatusToDisplay = mVecUnitStatus.get( iNewPos );
				mAnimations.SetBaseAnimation( mCurrentStatusToDisplay.GetName( mContext ), Orientation.NONE );
			}

			mAnimations.doDraw( oCanvas, oSourceRect, fZoomFactor, oPaint );
		}
	}

	/** 
	 * The returned paint will be used to draw the unit, this
	 * can be used to alter the normal drawing of the unit
	 */
	public Paint GetStatusPaint()
	{
		boolean bInvisible = false;
		for ( UnitStatus oStatus : mVecUnitStatus )
		{
			if ( oStatus.GetType() == R.string.status_invisible )
			{
				bInvisible = true;
				mStatusPaint.setAlpha( 160 );
			}
		}

		if ( !bInvisible )
		{
			mStatusPaint.setAlpha( 255 );
		}

		return mStatusPaint;
	}

	public boolean isFinished()
	{
		// UnitStatus lifetime is not handled using this
		return false;
	}
}
