package stargame.android.controller;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;

import stargame.android.controller.DPadController.DPadAction;
import stargame.android.util.Logger;
import stargame.android.util.Orientation;

/**
 * Class ControllerThread handles threaded execution of DPadController actions.
 * 
 * @author Duduche
 */
public class ControllerRunnable implements Runnable
{
	private DPadController mControler;

	private boolean mRun;

	static private class Action
	{
		public DPadAction mAction;

		public Orientation mOrientation;

		public Action( DPadAction eAction, Orientation eOrientation )
		{
			mOrientation = eOrientation;
			mAction = eAction;
		}
	}

	private ArrayList< Action > mActionsQueue;

	public ControllerRunnable( DPadController oControler )
	{
		mRun = true;
		mControler = oControler;
		mActionsQueue = new ArrayList< Action >();
	}

	public void AddCommand( DPadAction eAction, Orientation eOrientation )
	{
		synchronized ( mActionsQueue )
		{
			mActionsQueue.add( new Action( eAction, eOrientation ) );
		}
	}

	public void run()
	{
		Action oAction = null;
		boolean bActionAvailable;

		try
		{
			while ( mRun )
			{
				bActionAvailable = false;

				synchronized ( mActionsQueue )
				{
					if ( !mActionsQueue.isEmpty() )
					{
						bActionAvailable = true;
						oAction = mActionsQueue.get( 0 );
						mActionsQueue.remove( 0 );
					}
				}

				if ( bActionAvailable )
				{
					switch ( oAction.mAction )
					{
					case DPAD_UP:
						mControler.UpAction( oAction.mOrientation );
						break;
					case DPAD_DOWN:
						mControler.DownAction( oAction.mOrientation );
						break;
					case DPAD_LEFT:
						mControler.LeftAction( oAction.mOrientation );
						break;
					case DPAD_RIGHT:
						mControler.RightAction( oAction.mOrientation );
						break;
					case DPAD_CENTER:
						mControler.CenterAction();
						break;
					case DPAD_BACK:
						mControler.BackAction();
						break;
					}
				}
				else
				{
					Thread.sleep( 40 );
				}
			}
		}
		catch ( Exception e )
		{
			Logger.e( String.format( "Exception caught in Controler Thread: %s", Log.getStackTraceString( e ) ) );
		}
	}

	public void doStop()
	{
		mRun = false;
		mControler = null;
	}

	public void SaveState( Bundle oMap )
	{
		mControler.SaveState( oMap );
	}
}
