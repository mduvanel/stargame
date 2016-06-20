package stargame.android.view;

import java.util.Timer;
import java.util.TimerTask;

import stargame.android.controller.BattleActivity;
import stargame.android.util.Logger;
import stargame.android.util.Orientation;
import stargame.android.view.action.BattleAction2D;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;

public class BattleThread2D extends Thread
{
	/**
	 * State-tracking constants
	 */
	public static final int STATE_NODISPLAY = 1;
	public static final int STATE_RUNNING = 2;

	/** The state of the game. One of RUNNING or NODISPLAY */
	private int mMode;

	/** Surface holder */
	private SurfaceHolder mSurfaceHolder;

	/** The Battle containing all the logic */
	private Battle2D mBattle;

	/** The View that holds some display values */
	private BattleView2D mBattleView;

	private boolean mRun;

	/** Class responsible for drawing the interface */
	private InterfaceDrawer mInterfaceDrawer;

	public InterfaceDrawer GetInterfaceDrawer()
	{
		return mInterfaceDrawer;
	}

	/** Link to the controller (Activity) */
	private BattleActivity mController;

	/** Local storage of Orientation value */
	private int mOrientationIntValue;

	/** Surface size */
	private int mSurfaceWidth;
	private int mSurfaceHeight;

	public int GetSurfaceWidth()
	{
		return mSurfaceWidth;
	}

	public int GetSurfaceHeight()
	{
		return mSurfaceHeight;
	}

	/** Timer that pulse's the draw event */
	private Timer mDisplayTimer;

	/** TimerTask that releases the drawing lock */
	private DisplayTimerTask mDisplayTimerTask;

	/**
	 * The current drawing orientation. This object owns the orientation and
	 * updates the Battle2D of changes. BattleView also reads this field.
	 */
	private Orientation mDrawingOrientation;

	public Orientation GetDrawingOrientation()
	{
		return mDrawingOrientation;
	}

	/**
	 * The global object used as a lock for protecting objects that affect
	 * behavior of doDraw() methods
	 */
	public static Object mDrawingLock = new Object();

	private static class DisplayTimerTask extends TimerTask
	{
		private Object mObject;
		boolean mRun;

		public DisplayTimerTask( Object oObj )
		{
			mObject = oObj;
			mRun = true;
		}

		@Override
		public void run()
		{
			while ( mRun )
			{
				synchronized ( mObject )
				{
					mObject.notifyAll();
				}
			}

			// Cleanup
			cancel();
			mObject = null;
		}

		public void doStop()
		{
			mRun = false;
		}
	}

	public BattleThread2D( BattleView2D oView, BattleActivity oController,
			Battle2D oBattle, SurfaceHolder oSurfaceHolder, Context oContext )
	{
		mBattleView = oView;
		mController = oController;
		mSurfaceHolder = oSurfaceHolder;
		mInterfaceDrawer = new InterfaceDrawer( oContext, mBattleView, oBattle );

		mBattle = oBattle;

		mOrientationIntValue = 0;
		mDrawingOrientation = Orientation.NORTH;
		mBattle.SetDrawingOrientation( mDrawingOrientation );

		mDisplayTimer = new Timer( "DrawingTimer", true );
		mDisplayTimerTask = new DisplayTimerTask( mDrawingLock );
		mDisplayTimer.schedule( mDisplayTimerTask, 0, 40 );
	}

	/**
	 * Used to signal the thread the next action to activate
	 * 
	 * @param b
	 *            true to run, false to shut down
	 */
	public void setRunning( boolean b )
	{
		mRun = b;
	}

	/**
	 * Starts the display thread
	 */
	public void doStart()
	{
		if ( !mRun )
		{
			setRunning( true );
			start();
		}
		setState( STATE_RUNNING );
	}

	/**
	 * Stops the display thread
	 */
	public void doStop()
	{
		setRunning( false );
		try
		{
			// Stop Timer and release
			mDisplayTimerTask.doStop();
			mDisplayTimer = null;

			// Exit drawing thread
			join();
		} catch ( InterruptedException ex )
		{
			Logger.e( String.format(
					"Caught exception %s when joining display thread",
					ex.getMessage() ) );
		}

		// Clear other resources
		mBattle = null;
		mBattleView = null;
		mInterfaceDrawer = null;
		mController = null;
	}

	/**
	 * Pauses the animation.
	 */
	public void pause()
	{
		setState( STATE_NODISPLAY );
	}

	/**
	 * Resumes from a pause.
	 */
	public void unpause()
	{
		setState( STATE_RUNNING );
	}

	public void run()
	{
		while ( mRun )
		{
			synchronized ( mDrawingLock )
			{
				try
				{
					mDrawingLock.wait( 1000 );
					if ( mMode == STATE_RUNNING )
					{
						Canvas c = null;
						try
						{
							c = mSurfaceHolder.lockCanvas( null );
							doDraw( c );
						} catch ( Exception e )
						{
							Logger.e( String.format(
									"Exception caught during drawing: %s",
									Log.getStackTraceString( e ) ) );
						} finally
						{
							// do this in a finally so that if an exception is
							// thrown
							// during the above, we don't leave the Surface in
							// an
							// inconsistent state
							if ( c != null )
							{
								mSurfaceHolder.unlockCanvasAndPost( c );
							}
						}
					}
				} catch ( InterruptedException oEx )
				{
					// just loop
					Log.w( this.getClass().getName(),
							"wait() call was interrupted before notification or timeout." );
				}
			}
		}

		// release ref to surface holder
		mSurfaceHolder = null;
		Log.w( this.getClass().getName(), "Got out of run() loop." );
	}

	public void setOrientation( int iOrientation )
	{
		if ( iOrientation != OrientationEventListener.ORIENTATION_UNKNOWN )
		{
			mOrientationIntValue = iOrientation;
		}
	}

	public void onConfigurationChanged( Configuration newConfig )
	{
		Logger.w( "onConfigurationChanged() message received" );

		synchronized ( mDrawingLock )
		{
			Logger.i( String.format( "synchronized setOrientation(Angle = %d)",
					mOrientationIntValue ) );
			switch ( newConfig.orientation )
			{
			case Configuration.ORIENTATION_LANDSCAPE:
				if ( mOrientationIntValue < 180 )
				{
					mDrawingOrientation = Orientation.WEST;
				} else
				{
					mDrawingOrientation = Orientation.EAST;
				}
				break;
			case Configuration.ORIENTATION_PORTRAIT:
				// Portrait is always NORTH, no Orientation change notification
				// for SOUTH
				mDrawingOrientation = Orientation.NORTH;
				break;
			}

			UpdateOrientation();
		}
	}

	public DialogDrawer GetDialogDrawer()
	{
		return mBattle.GetDialogDrawer();
	}

	private void UpdateOrientation()
	{
		mBattle.SetDrawingOrientation( mDrawingOrientation );
		mBattleView.UpdateCenterCellConstraints();
		mBattleView.CenterOn( BattleField2D.GetInstance().GetSelectedCell()
				.GetPos() );
	}

	public void SetSurfaceSize( int iWidth, int iHeight )
	{
		mSurfaceWidth = iWidth;
		mSurfaceHeight = iHeight;
		BattleDrawable.SetScreenSize( new RectF( 0, 0, iWidth, iHeight ) );
		mInterfaceDrawer.SetSurfaceSize( iWidth, iHeight );
		mBattle.GetDialogDrawer().SetSurfaceSize( iWidth, iHeight );
	}

	/**
	 * Sets the game mode. That is, whether we are running, paused, in the
	 * failure state, in the victory state, etc.
	 * 
	 * @see #setState(int, CharSequence)
	 * @param mode
	 *            one of the STATE_* constants
	 */
	public void setState( int mode )
	{
		synchronized ( mDrawingLock )
		{
			mMode = mode;
		}
	}

	/**
	 * Handles a key-down event.
	 * 
	 * @param iKeyCode
	 *            the key that was pressed
	 * @param oMsg
	 *            the original event object
	 * @return false
	 */
	public boolean doKeyDown( int iKeyCode, KeyEvent oMsg )
	{
		return false;
	}

	/**
	 * Handles a key-up event.
	 * 
	 * @param iKeyCode
	 *            the key that was pressed
	 * @param oMsg
	 *            the original event object
	 * @return true if the key was handled and consumed, or else false
	 */
	public boolean doKeyUp( int iKeyCode, KeyEvent oMsg )
	{
		boolean bHandled = false;

		// Handles the user input for the View commands
		switch ( iKeyCode )
		{
		case KeyEvent.KEYCODE_SPACE:
			synchronized ( mDrawingLock )
			{
				mDrawingOrientation = mDrawingOrientation.RotateClockwise();
				UpdateOrientation();
			}
			bHandled = true;
			break;
		}

		if ( bHandled )
		{
			return true;
		}

		// If not handled yet, pass it to the controller
		synchronized ( mDrawingLock )
		{
			return mController.doKeyUp( iKeyCode, mDrawingOrientation );
		}
	}

	/**
	 * Handles a touch event.
	 */
	public boolean onTouchEvent( MotionEvent oEvent )
	{
		return mController.onTouchEvent( oEvent, mDrawingOrientation );
	}

	/**
	 * Draws the Battle Canvas.
	 */
	public void doDraw( Canvas oCanvas )
	{
		if ( mMode == STATE_RUNNING && mDrawingOrientation != null )
		{
			// Draw the Battle
			Logger.w( "begin doDraw()" );
			mBattle.doDraw( oCanvas, mBattleView.GetZoomFactor(),
					mBattleView.GetCenterCellCoords() );

			BattleAction2D oAction = mBattle.GetCurrentBattleAction();
			if ( oAction == null )
			{
				// draw interface only when no action is active
				mInterfaceDrawer.doDraw( oCanvas );
			}
			Logger.w( "end doDraw()" );
		}
	}
}
