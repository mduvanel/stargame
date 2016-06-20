package stargame.android.view;

import java.io.IOException;
import java.util.Observable;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParserException;

import stargame.android.util.Logger;
import stargame.android.util.Position;

import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Single Animation for an AnimatedItem
 */
public class Animation2D extends Observable implements IDrawable
{
	/** The bitmap resources that are used for the animation */
	private Bitmap mBitmapResources[];

	/** Total Number of animation steps */
	private int mNbAnimationSteps;

	/** Current animation step */
	private int mCurrentAnimationStep;
	private final int ANIMATION_FINISHED = -2;
	private final int ANIMATION_NOT_STARTED = -1;

	/** Count of frames drawn */
	private int mFrameCount;

	/** Tells whether this animation will automatically loop or not */
	private boolean mLoop;

	private Animation2DStep mAnimationSteps[];

	private boolean mDebugLog;
	private String mStrID;

	private Position mNextPositionOffset;
	private RectF mDrawingRect;

	/** Private dummy constructor for Clone() method */
	private Animation2D()
	{
		mNbAnimationSteps = 0;
		mCurrentAnimationStep = ANIMATION_NOT_STARTED;
		mLoop = false;

		mBitmapResources = null;
		mAnimationSteps = null;

		Init();
	}

	/**
	 * Simple constructor with a vector of Bitmaps and a vector
	 * of Animation2DSteps.
	 */
	public Animation2D( Vector< Bitmap > vecBitmaps, Vector< Animation2DStep > vecSteps, boolean bLoop )
	{
		mNbAnimationSteps = vecSteps.size();
		mLoop = bLoop;

		mBitmapResources = new Bitmap[ mNbAnimationSteps ];
		mAnimationSteps = new Animation2DStep[ mNbAnimationSteps ];

		for ( int i = 0; i < mNbAnimationSteps; ++i )
		{
			mBitmapResources[ i ] = vecBitmaps.get( i );
			mAnimationSteps[ i ] = vecSteps.get( i );
		}

		Init();
	}

	/**
	 * Simple constructor with a vector of Bitmaps and a unique
	 * duration for all steps.
	 */
	public Animation2D( Vector< Bitmap > vecBitmaps, int iDuration, boolean bLoop )
	{
		mNbAnimationSteps = vecBitmaps.size();
		mLoop = bLoop;

		mBitmapResources = new Bitmap[ mNbAnimationSteps ];
    	mAnimationSteps = new Animation2DStep[ mNbAnimationSteps ];

    	for ( int i = 0; i < mNbAnimationSteps; ++i )
    	{
    		mBitmapResources[ i ] = vecBitmaps.get( i );
    		mAnimationSteps[ i ] = new Animation2DStep();
    		mAnimationSteps[ i ].mDuration = iDuration;
    		mAnimationSteps[ i ].mResourceIndex = i;
    	}

    	Init();
	}

	/**
	 * Constructor loading from XML file
	 */
	public Animation2D( XmlResourceParser oParser )
	{
		try 
		{
		    int eventType = oParser.getEventType();
		    int iBitmapsNb = 0;
		    boolean bLoadBitmaps = false, bLoadFrames = false;

		    while ( eventType != XmlResourceParser.END_DOCUMENT )
		    {
		    	if ( bLoadBitmaps )
	        	{
				    for ( int iBitmap = 0; iBitmap < iBitmapsNb; ++iBitmap )
		    		{
				    	eventType = oParser.nextTag(); // Skip to the "Bitmap" tag
				    	eventType = oParser.nextTag(); // Skip the "Bitmap" tag

				    	assert oParser.getName().equals( "Index" );
				    	eventType = oParser.next();

				    	int iBitmapIndex = Integer.parseInt( oParser.getText() ) - 1;

				    	eventType = oParser.nextTag(); // Skip to the "Name" tag

				    	assert oParser.getName().equals( "Name" );
				    	eventType = oParser.next();

				    	mBitmapResources[ iBitmapIndex ] = BitmapRepository.GetInstance().GetBitmap( oParser.nextText() ); 

				    	eventType = oParser.nextTag(); // Skip the end tag
		    		}

				    // We finished loading bitmaps, continue
				    bLoadBitmaps = false;
	        	}
		    	else if ( bLoadFrames )
	        	{
				    for ( int iFrames = 0; iFrames < mNbAnimationSteps; ++iFrames )
		    		{
				    	mAnimationSteps[ iFrames ] = new Animation2DStep();

				    	eventType = oParser.nextTag(); // Skip to the "Frame" tag
				    	eventType = oParser.nextTag(); // Skip the "Frame" tag

				    	assert oParser.getName().equals( "BitmapIndex" );

				    	eventType = oParser.next();
				    	mAnimationSteps[ iFrames ].mResourceIndex = Integer.parseInt( oParser.getText() ) - 1;
				    	eventType = oParser.nextTag(); // Skip to the "OffsetX" tag

				    	assert oParser.getName().equals( "OffsetX" );

				    	eventType = oParser.next();
				    	mAnimationSteps[ iFrames ].mXOffset = Integer.parseInt( oParser.nextText() );
				    	eventType = oParser.nextTag(); // Skip to the "OffsetY" tag

				    	assert oParser.getName().equals( "OffsetY" );

				    	mAnimationSteps[ iFrames ].mYOffset = Integer.parseInt( oParser.nextText() );
				    	eventType = oParser.nextTag(); // Skip to the "Duration" tag

				    	assert oParser.getName().equals( "Duration" );

				    	mAnimationSteps[ iFrames ].mDuration = Integer.parseInt( oParser.nextText() );
				    	eventType = oParser.nextTag(); // Skip the end tag
		    		}

				    // We finished loading bitmaps, continue
				    bLoadFrames = false;
	        	}
		    	else if ( eventType == XmlResourceParser.START_TAG )
		        {
		    		if ( oParser.getName().equals( "Bitmaps" ) )
		            {
		                iBitmapsNb = Integer.parseInt( oParser.nextText() );
		                mBitmapResources = new Bitmap[ iBitmapsNb ];
		            }
		            else if ( oParser.getName().equals( "BitmapList" ) ) 
		            {
		                bLoadBitmaps = true;
		                continue;
		            }
		            else if ( oParser.getName().equals( "TotalFrames" ) ) 
		            {
		            	mNbAnimationSteps = Integer.parseInt( oParser.nextText() );
		            	mAnimationSteps = new Animation2DStep[ mNbAnimationSteps ];
		            }
		            else if ( oParser.getName().equals( "FrameList" ) ) 
		            {
		                bLoadFrames = true;
		                continue;
		            }
		            else if ( oParser.getName().equals( "Loop" ) ) 
		            {
		                mLoop = Boolean.parseBoolean( oParser.nextText() );
		            }
		        }
		        eventType = oParser.next();
		    }
		}
		catch ( XmlPullParserException e )
		{
			Logger.e( String.format( "XML Error while loading: %s", e.getMessage() ) );
		    e.printStackTrace();
		}
		catch ( IOException e )
		{
			Logger.e( String.format( "XML Error while loading: %s", e.getMessage() ) );
		    e.printStackTrace();
		}

		Init();
	}

	/** Internal common Init method for all ctors */
	private void Init()
	{
		mCurrentAnimationStep = ANIMATION_NOT_STARTED;
		mNextPositionOffset = new Position();
		mDrawingRect = new RectF();
		mFrameCount = -1;

		// Debug stuff
		mDebugLog = false;
		mStrID = "";
	}

	public void SetDebugLog( boolean bDebug, String strID )
	{
		mDebugLog = bDebug;
		mStrID = strID;
	}

	/**
	 * Clone this instance
	 */
	public Animation2D Clone()
	{
		Animation2D oClone = new Animation2D();

		oClone.mNbAnimationSteps = this.mNbAnimationSteps;
		oClone.mCurrentAnimationStep = this.mCurrentAnimationStep;
		oClone.mFrameCount = this.mFrameCount;
		oClone.mLoop = this.mLoop;
		oClone.mNextPositionOffset.SetPos( this.mNextPositionOffset );
		oClone.mDrawingRect.set( this.mDrawingRect );

		oClone.mBitmapResources = new Bitmap[ mNbAnimationSteps ];
		oClone.mAnimationSteps = new Animation2DStep[ mNbAnimationSteps ];

    	for ( int i = 0; i < mNbAnimationSteps; ++i )
    	{
    		oClone.mBitmapResources[ i ] = this.mBitmapResources[ i ];
    		oClone.mAnimationSteps[ i ] = this.mAnimationSteps[ i ].Clone();
    	}

    	oClone.mDebugLog = this.mDebugLog;
    	oClone.mStrID = this.mStrID; 
		return oClone;
	}

	public boolean IsAnimationRunning()
	{
		return ( mCurrentAnimationStep != ANIMATION_FINISHED );
	}

	public boolean IsNotificationEndOfAnimation()
	{
		return !( IsAnimationRunning() && mCurrentAnimationStep < mNbAnimationSteps - 1 );
	}
	
	public int GetNextZOrderOffset()
	{
		if ( IsAnimationRunning() && mCurrentAnimationStep < mNbAnimationSteps - 1 )
		{
			return mAnimationSteps[ mCurrentAnimationStep + 1 ].mZOrderOffset;
		}
		else
		{
			return 0;
		}
	}

	public Position GetNextPositionOffset()
	{
		if ( IsAnimationRunning() && mCurrentAnimationStep < mNbAnimationSteps - 1 )
		{
			mNextPositionOffset.SetPos(
					mAnimationSteps[ mCurrentAnimationStep + 1 ].mXOffset, 
					mAnimationSteps[ mCurrentAnimationStep + 1 ].mYOffset );
		}
		else
		{
			mNextPositionOffset.SetPos( 0, 0 );
		}

		return mNextPositionOffset;
	}

	private void DebugLogInfo( String strMessage )
	{
		if ( mDebugLog )
		{
			Logger.d( String.format( "Animation %s: %s.", mStrID, strMessage ) );
		}
	}

	public void resetCurrentAnimation()
	{
		mCurrentAnimationStep = ANIMATION_NOT_STARTED;
		mFrameCount = 0;
		CheckFrameDifference( 0, mCurrentAnimationStep );
		DebugLogInfo( "animation reset" );
	}

	private void CheckFrameDifference( int iStep1, int iStep2 )
	{
		boolean bChange;
		if ( ANIMATION_NOT_STARTED == iStep1 || ANIMATION_FINISHED == iStep1 )
		{
			bChange = mAnimationSteps[ iStep2 ].DisplayChange();
		}
		else if ( ANIMATION_NOT_STARTED == iStep2 || ANIMATION_FINISHED == iStep2 )
		{
			bChange = mAnimationSteps[ iStep1 ].DisplayChange();
		}
		else
		{
			bChange = mAnimationSteps[ iStep1 ].DisplayChange( mAnimationSteps[ iStep2 ] );
		}

		if ( bChange )
		{
			DebugLogInfo( "ZOrder or Position difference detected for next step" );

			// notify ZOrder change
			setChanged();
			notifyObservers();
		}
	}

	private void nextStep()
	{
		if ( ANIMATION_NOT_STARTED == mCurrentAnimationStep )
		{
			DebugLogInfo( "nextStep called. Starting animation at step 0" );
			mCurrentAnimationStep = 0;
			mFrameCount = 0;
		}
		else
		{
			DebugLogInfo( String.format( "nextStep called. Current step: %d, frame count: %d", mCurrentAnimationStep, mFrameCount ) );
			mFrameCount++;
		}

		if ( mFrameCount == mAnimationSteps[ mCurrentAnimationStep ].mDuration - 1 )
		{
			// Compute next step differences
			int iCompareAnimationStep;

			if ( mCurrentAnimationStep == mNbAnimationSteps - 1 )
			{
				if ( mLoop )
				{
					iCompareAnimationStep = 0;
				}
				else
				{
					// Animation is finished, notify update
					iCompareAnimationStep = ANIMATION_FINISHED;
				}
			}
			else
			{
				iCompareAnimationStep = mCurrentAnimationStep + 1;
			}

			CheckFrameDifference( mCurrentAnimationStep, iCompareAnimationStep );
		}
		else if ( mFrameCount == mAnimationSteps[ mCurrentAnimationStep ].mDuration )
		{
			// Change step
			DebugLogInfo( String.format( "nextStep: step change" ) );

			if ( mCurrentAnimationStep == mNbAnimationSteps - 1 )
			{
				if ( mLoop )
				{
					mCurrentAnimationStep = 0;
					mFrameCount = 0;
					DebugLogInfo( "nextStep: looping" );
				}
				else
				{
					// Animation is finished, notify update
					mCurrentAnimationStep = ANIMATION_FINISHED;
					mFrameCount = -1;

					// notify change
					DebugLogInfo( "Animation ended" );
					setChanged();
					notifyObservers();
				}
			}
			else
			{
				mCurrentAnimationStep++;
				mFrameCount = 0;
				DebugLogInfo( "nextStep: incrementing step" );
			}
		}
	}

	/**
	 * This method will set the current animation step and frame count directly
	 * from the given Animation2D object.
	 * WARNING: No check is done to validate that step or frame count are valid
	 * for this animation!
	 */
	protected void CloneState( Animation2D oAnimation2D )
	{
		mCurrentAnimationStep = oAnimation2D.mCurrentAnimationStep;
		mFrameCount = oAnimation2D.mFrameCount;
	}

	public boolean isFinished()
	{
		if ( ANIMATION_FINISHED == mCurrentAnimationStep )
		{
			return true;
		}
		else if ( mLoop || ANIMATION_NOT_STARTED == mCurrentAnimationStep )
		{
			return false;
		}
		else
		{
			return ( ( mCurrentAnimationStep == mNbAnimationSteps - 1 ) && 
					mFrameCount == mAnimationSteps[ mCurrentAnimationStep ].mDuration -1 );
		}
	}

	public void doDraw( Canvas oCanvas, RectF oSourceRect, float fZoomFactor, Paint oPaint )
	{
		if ( mNbAnimationSteps == 0 )
		{
			// This is for special non-drawn animations (invisible)
			DebugLogInfo( "doDraw: 0-steps animation" );
			return;
		}

		if ( ANIMATION_FINISHED == mCurrentAnimationStep )
		{
			DebugLogInfo( "doDraw: animation is finished" );
			return;
		}

		// Increase animation steps
		nextStep();

		if ( IsAnimationRunning() )
		{
			// Actual drawing
			Animation2DStep oCurrentStep = mAnimationSteps[ mCurrentAnimationStep ];
			if ( oCurrentStep.mResourceIndex > -1 ) // Resource index of -1 means do not display anything
			{
				mDrawingRect.set( oSourceRect );
				mDrawingRect.offset( oCurrentStep.mXOffset * fZoomFactor, oCurrentStep.mYOffset * fZoomFactor );
				oCanvas.drawBitmap( mBitmapResources[ oCurrentStep.mResourceIndex ], null, mDrawingRect, oPaint );
			}
			else
			{
				DebugLogInfo( "doDraw: resource index invalid, nothing to draw" );
			}
		}
	}
}
