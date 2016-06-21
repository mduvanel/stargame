package stargame.android.controller;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;

import java.util.Locale;

import stargame.android.controller.DPadController.DPadAction;
import stargame.android.model.Battle;
import stargame.android.storage.SavableHelper;
import stargame.android.util.Logger;
import stargame.android.util.Orientation;
import stargame.android.view.BattleField2D;
import stargame.android.view.BattleView2D;
import stargame.android.view.BattleThread2D;
import stargame.android.view.BitmapRepository;
import stargame.android.R;


/**
 * This is the Battle Activity, Handling a Battle display and behavior
 * <ul>
 * <li>animating by calling invalidate() from draw()
 * <li>loading and drawing resources
 * <li>handling onPause() in an animation
 * </ul>
 */
public class BattleActivity extends Activity implements IDPadPeeker
{
	/** A handle to the thread that's actually running the animation. */
	private BattleThread2D mBattleThread;

	/** The Battle taking place. */
	private Battle mBattle;

	private static final String M_BATTLE_ID = "BattleID";

	/** The currently selected zone when move is in progress */ 
	private DPadZone mCurrentZone;

	/** The DPAD controller Runnable */
	private ControllerRunnable mController;

	private static final String M_CONTROLLER = "Controller";

	/** Thread that runs the Controller */
	private Thread mControllerThread;

	/**
	 * Initial move position
	 */
	private float mClickPosX;
	private float mClickPosY;

	/**
	 * Initial move position for second pointer (zoom)
	 */
	private float mClickPosXZoom;
	private float mClickPosYZoom;

	public DPadZone PeekZone()
	{
		return mCurrentZone;
	}

	/**
	 * Invoked when the Activity is created.
	 * 
	 * @param savedInstanceState a Bundle containing state saved from a previous
	 *        execution, or null if this is a new execution
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		Log.i( this.getClass().getName(), "onCreate() called!" );
		if ( null != savedInstanceState )
		{
			Logger.w( "With saved Bundle from previous Activity" );
		}

	    super.onCreate( savedInstanceState );

	    // turn off the window's title bar
	    requestWindowFeature( Window.FEATURE_NO_TITLE );

	    // Init the BitmapRepository first
	    BitmapRepository.Init( getResources() );

    	// This creates the Battle object (model)
    	mBattle = CreateBattle( savedInstanceState );

		// tell system to use the layout defined in our XML file
		setContentView( R.layout.battle );

		// get handles to the BattleView from XML
		BattleView2D oBattleView = ( BattleView2D ) findViewById( R.id.battle );
		oBattleView.Init( this );
		mBattleThread = oBattleView.GetBattleThread();

		mController = CreateController( savedInstanceState );
		mControllerThread = new Thread( mController );
		mControllerThread.start();
		mCurrentZone = DPadZone.NONE;
		mBattleThread.GetInterfaceDrawer().SetPeeker( this );

		mClickPosX = -1;
		mClickPosY = -1;
		mClickPosXZoom = -1;
		mClickPosYZoom = -1;
	}

	/**
	 * Invoked when the Activity loses user focus.
	 */
	@Override
	protected void onPause()
	{
		Log.i( this.getClass().getName(), "onPause() called!" );
		super.onPause();
		mBattleThread.pause(); // pause game when Activity pauses
	}

	protected void onResume()
	{
		Log.i( this.getClass().getName(), "onResume() called!" );
		super.onResume();
		mBattleThread.unpause(); // pause game when Activity pauses
	}

	protected void onStart()
	{
		Log.i( this.getClass().getName(), "onStart() called!" );
		super.onStart();
	}

	protected void onRestart()
	{
		Log.i( this.getClass().getName(), "onRestart() called!" );
		super.onRestart();
	}

	protected void onStop()
	{
		Log.i( this.getClass().getName(), "onStop() called!" );
		super.onStop();
	}

	protected void onDestroy()
	{
		Log.i( this.getClass().getName(), "onDestroy() called!" );

		// Stop the threads
		if ( mBattleThread != null )
		{
			mBattleThread.doStop();
		}

		try
		{
			mController.doStop();
			mControllerThread.join();
		}
		catch ( InterruptedException e )
		{
			Logger.e( String.format( "Caught exception %s when exiting Controller Thread", e.getMessage() ) );
		}

		// Release references
		mBattle = null;
		mBattleThread = null;
		mCurrentZone = null;
		mController = null;
		mControllerThread = null;

		// Release singletons
		BattleField2D.Reset();
	    BitmapRepository.Release();

		super.onDestroy();
	}

	Battle CreateBattle( Bundle oMap )
	{
		Battle oBattle;
		if ( oMap != null )
		{
			String strBattleKey = oMap.getString( M_BATTLE_ID );
			oBattle = Battle.loadState( oMap, strBattleKey );
		}
		else
		{
	        //First Extract the bundle from intent
	        Bundle oBundle = getIntent().getExtras();

			//Next extract the values using the key as
			String strName = oBundle.getString( "BattleName" );

			oBattle = Battle.createBattle( getResources(), strName );
		}

		return oBattle;
	}

	ControllerRunnable CreateController( Bundle oMap )
	{
		DPadController oController = new DPadController( 
				mBattle, 
				mBattleThread.GetInterfaceDrawer(), 
				getApplicationContext() );

		if ( oMap != null )
		{
			Bundle oControllerMap = oMap.getBundle( M_CONTROLLER );
			oController.LoadState( oControllerMap );
		}

		oController.NotifyInterfaceDrawer();
		return new ControllerRunnable( oController );
	}

	public Battle GetBattle()
	{
		return mBattle;
	}

	/**
	 * Notification that something is about to happen, to give the Activity a
	 * chance to save state.
	 * 
	 * @param outState a Bundle into which this Activity should save its state
	 */
	@Override
	protected void onSaveInstanceState( Bundle outState )
	{
		super.onSaveInstanceState( outState );

		String strObjKey = SavableHelper.saveInMap( mBattle, outState );
		outState.putString( M_BATTLE_ID, strObjKey );

		Bundle oControllerMap = new Bundle();
		mController.SaveState( oControllerMap );
		outState.putBundle( M_CONTROLLER, oControllerMap );

		Log.w( this.getClass().getName(), "SIS called" );
	}

	/**
	 * Orientation change handled without reconstruction of application.
	 * 
	 * @param newConfig the new configuration
	 */
	@Override
	public void onConfigurationChanged( Configuration newConfig )
	{ 
	    super.onConfigurationChanged( newConfig ); 
	    mBattleThread.onConfigurationChanged( newConfig );
	    Log.i( this.getClass().getName(), "onConfigurationChanged() called" );
	}

	public boolean doKeyUp( int iKeyCode, Orientation eOrientation )
	{
		BattleView2D oBattleView;

		switch ( iKeyCode )
		{
		case KeyEvent.KEYCODE_O:
			oBattleView = ( BattleView2D ) findViewById( R.id.battle );
			oBattleView.ZoomIn();
			return true;
		case KeyEvent.KEYCODE_I:
			oBattleView = ( BattleView2D ) findViewById( R.id.battle );
			oBattleView.ZoomOut();
	    	return true;
		case KeyEvent.KEYCODE_L:
	    	Logger.setLogging( !Logger.isLogging() );
	    	return true;
		case KeyEvent.KEYCODE_MENU:
			// TODO : display application menu
			break;
		case KeyEvent.KEYCODE_BACK:
			mController.AddCommand( DPadAction.DPAD_BACK, Orientation.NONE );
			return true;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			mController.AddCommand( DPadAction.DPAD_LEFT, eOrientation );
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			mController.AddCommand( DPadAction.DPAD_RIGHT, eOrientation );
			return true;
		case KeyEvent.KEYCODE_DPAD_UP:
			mController.AddCommand( DPadAction.DPAD_UP, eOrientation );
			return true;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			mController.AddCommand( DPadAction.DPAD_DOWN, eOrientation );
			return true;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			mController.AddCommand( DPadAction.DPAD_CENTER, Orientation.NONE );
			return true;
	    }

	    return false;
	}

	private DPadZone GetClickZone( float fClickX, float fClickY )
	{
		// This was a click, check in which part of the screen
		int iCenterX = mBattleThread.GetSurfaceWidth() / 2;
		int iCenterY = mBattleThread.GetSurfaceHeight() / 2;

		// Check for center first
		BattleView2D oBattleView = ( BattleView2D ) findViewById( R.id.battle );
		if ( oBattleView.GetCenterZoneRect().contains( ( int )fClickX, ( int )fClickY ) )
		{
    		return DPadZone.CENTER;
		}
		else
		{
			if ( Math.abs( iCenterX - fClickX ) > Math.abs( iCenterY - fClickY ) )
			{
				// Horizontal
				if ( iCenterX < fClickX )
				{
					return DPadZone.RIGHT;
				}
				else
				{
					return DPadZone.LEFT;
				}
			}
			else
			{
				// Vertical
				if ( iCenterY < fClickY )
				{
					return DPadZone.DOWN;
				}
				else
				{
					return DPadZone.UP;
				}
			}
		}
	}

	public boolean onTouchEvent( MotionEvent oEvent, Orientation eOrientation )
	{
		boolean bHandled = false;

		final int iPointerCount = oEvent.getPointerCount();

		if ( Logger.isLogging() )
		{
			Logger.logEventInfo( oEvent );
		}

		switch( oEvent.getAction() & MotionEvent.ACTION_MASK )
		{
		case MotionEvent.ACTION_DOWN:

			// New move action started
			mClickPosX = oEvent.getX();
			mClickPosY = oEvent.getY();
			// Set the click zone
			mCurrentZone = GetClickZone( mClickPosX, mClickPosY );
			bHandled = true;
			break;

		case MotionEvent.ACTION_POINTER_DOWN:

			bHandled = true;
			if ( iPointerCount > 2 )
			{
				break; // ignore this new pointer
			}

			// Store initial pos of both pointers
			for ( int p = 0; p < iPointerCount; ++p )
			{
				int iPointerIndex = oEvent.findPointerIndex( p );
				if ( iPointerIndex == 0 )
				{
					mClickPosX = oEvent.getX( iPointerIndex );
					mClickPosY = oEvent.getY( iPointerIndex );

					// Set the click zone
					mCurrentZone = GetClickZone( mClickPosX, mClickPosY );
					if ( Logger.isLogging() )
					{
						Logger.v( String.format(Locale.ENGLISH,
                                  " initial pointer 0: (%f, %f)",
                                  mClickPosX,
								  mClickPosY ) );
					}
				}
				else if ( iPointerIndex == 1 )
				{
					mClickPosXZoom = oEvent.getX( iPointerIndex );
					mClickPosYZoom = oEvent.getY( iPointerIndex );
					if ( Logger.isLogging() )
					{
						Logger.v( String.format(Locale.ENGLISH,
								  " initial pointer 1: (%f, %f)",
								  mClickPosXZoom,
								  mClickPosYZoom ) );
					}
				}
			}
			break;

		case MotionEvent.ACTION_POINTER_UP:

			// Check that pointer going up is one of the two handling the zoom
			int iPointerUp = ( oEvent.getAction() & MotionEvent.ACTION_POINTER_ID_MASK ) >> MotionEvent.ACTION_POINTER_ID_SHIFT; 
			if ( iPointerUp < 2 )
			{
				double dNewDist = Math.pow( oEvent.getX( 0 ) - oEvent.getX( 1 ), 2 ) + Math.pow( oEvent.getY( 0 ) - oEvent.getY( 1 ), 2 );
				double dInitialDist = Math.pow( mClickPosX - mClickPosXZoom, 2 ) + Math.pow( mClickPosY - mClickPosYZoom, 2 );
				BattleView2D oBattleView = ( BattleView2D ) findViewById( R.id.battle );

				if ( dNewDist / dInitialDist > 1.2 )
				{
					oBattleView.ZoomIn();
				}
				else if ( dInitialDist / dNewDist > 1.2 )
				{
					oBattleView.ZoomOut();
				}
				bHandled = true;

				mClickPosX = -1;
				mClickPosY = -1;
				mClickPosXZoom = -1;
				mClickPosYZoom = -1;
			}
			break;

		case MotionEvent.ACTION_UP:

			bHandled = true;

			// Early exit if we just zoomed in or out
			if ( mClickPosX == -1 && mClickPosY == -1 )
			{
				mCurrentZone = DPadZone.NONE;
				break;
			}

			if ( !mBattle.IsDialogFinished() )
			{
				// Resume dialog and go to next one if current is done
				if ( mBattleThread.GetDialogDrawer().ResumeTextDisplay() )
				{
					try
					{
						mBattle.NextDialog();
					}
					catch ( Exception e )
					{
						Logger.e( "Exception caught when calling Battle.NextDialog()" );
					}
				}
			}
			else
			{
				switch ( mCurrentZone )
				{
				case UP:
					mController.AddCommand( DPadAction.DPAD_UP, eOrientation );
					break;
				case DOWN:
					mController.AddCommand( DPadAction.DPAD_DOWN, eOrientation );
					break;
				case LEFT:
					mController.AddCommand( DPadAction.DPAD_LEFT, eOrientation );
					break;
				case RIGHT:
					mController.AddCommand( DPadAction.DPAD_RIGHT, eOrientation );
					break;
				case CENTER:
					mController.AddCommand( DPadAction.DPAD_CENTER, eOrientation );
					break;
				case NONE:
				default:
					break;
				}
			}

			mCurrentZone = DPadZone.NONE;

			// Fallthrough
		case MotionEvent.ACTION_CANCEL:

			bHandled = true;
			break;

		case MotionEvent.ACTION_MOVE:

			// Get current pressure state to display highlighted direction
			float fNewPosX = oEvent.getX();
			float fNewPosY = oEvent.getY();

			if ( mClickPosXZoom == -1 && mClickPosYZoom == -1 )
			{
				// Check movement amplitude
				double dDistanceSq = Math.pow( mClickPosX - fNewPosX, 2 ) + Math.pow( mClickPosY - fNewPosY, 2 );
	
				if ( dDistanceSq > 100 ) // more than 10 pixels move
				{
					// This was a movement, find the direction
					if ( Math.abs( fNewPosX - mClickPosX ) > Math.abs( fNewPosY - mClickPosY ) )
					{
						// lateral movement
						if ( fNewPosX > mClickPosX )
						{
				    		mCurrentZone = DPadZone.RIGHT;
						}
						else
						{
				    		mCurrentZone = DPadZone.LEFT;
						}
					}
					else
					{
						// Vertical movement
						if ( fNewPosY > mClickPosY )
						{
				    		mCurrentZone = DPadZone.DOWN;
						}
						else
						{
				    		mCurrentZone = DPadZone.UP;
						}
					}
				}
				else
				{
					mCurrentZone = GetClickZone( fNewPosX, fNewPosY );
				}
			}
			else
			{
				mCurrentZone = DPadZone.NONE;
			}

			bHandled = true;
			break;
		}

		return bHandled;
	}
}
