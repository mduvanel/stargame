package stargame.android.view;

import java.util.HashMap;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import stargame.android.util.Logger;
import stargame.android.util.Orientation;

/**
 * Group of animations, either contains the 4 animations of an item (one for each
 * Orientation), or a single animation regardless of Orientation.
 */
public class OrientableAnimation2D implements IDrawable
{
	private HashMap< Orientation, Animation2D > mMapAnimations;

	private Orientation mCurrentOrientation;

	public OrientableAnimation2D()
	{
		mMapAnimations = new HashMap< Orientation, Animation2D >( 1 );
		mCurrentOrientation = Orientation.NONE;
	}

	public void SetAnimation( Animation2D oAnimation, Orientation eOrientation )
	{
		mMapAnimations.put( eOrientation, oAnimation );
	}

	public void ResetCurrentAnimation()
	{
		for ( Animation2D oAnimation : mMapAnimations.values() )
		{
			oAnimation.resetCurrentAnimation();
		}
	}

	public void SetCurrentOrientation( Orientation eOrientation )
	{
		Animation2D oCurrentAnimation = mMapAnimations.get( mCurrentOrientation );
		if ( oCurrentAnimation != null )
		{
			// Transfer current Animation2D state to new Animation2D
			mMapAnimations.get( eOrientation ).CloneState( oCurrentAnimation );
		}
		else
		{
			// Reset animation to start from beginning
			mMapAnimations.get( eOrientation ).resetCurrentAnimation();
		}

		// Update current Orientation
		mCurrentOrientation = eOrientation;
	}

	public Animation2D GetCurrentAnimation()
	{
		return mMapAnimations.get( mCurrentOrientation );
	}

	public boolean isFinished()
	{
		Animation2D oAnimation = GetCurrentAnimation();
		if ( oAnimation != null )
		{
			return oAnimation.isFinished();
		}
		else
		{
			return true;
		}
	}

	public void doDraw( Canvas oCanvas, RectF oSourceRect, float fZoomFactor,
			Paint oPaint )
	{
		Animation2D oAnimation = GetCurrentAnimation();
		if ( oAnimation != null )
		{
			oAnimation.doDraw( oCanvas, oSourceRect, fZoomFactor, oPaint );
		}
		else
		{
			Logger.e( String.format( "Attempting to draw NULL Animation (Orientation = %s)", mCurrentOrientation.toString() ) );
		}
	}
}
