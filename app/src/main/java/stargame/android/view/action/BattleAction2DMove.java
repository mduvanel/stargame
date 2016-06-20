package stargame.android.view.action;

import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import android.graphics.RectF;

import stargame.android.R;
import stargame.android.model.BattleAction;
import stargame.android.model.BattleActionMove;
import stargame.android.util.Logger;
import stargame.android.util.Orientation;
import stargame.android.util.Position;
import stargame.android.view.Animation2D;
import stargame.android.view.Battle2D;
import stargame.android.view.BattleDrawable;
import stargame.android.view.BattleUnit2D;
import stargame.android.view.IDrawable;
import stargame.android.view.MoveAnimation2DFactory;
import stargame.android.view.OrientableAnimation2D;
import stargame.android.view.action.BattleAction2D;
import stargame.android.view.action.IBattleAction2DCreator;

public class BattleAction2DMove extends BattleAction2D implements Observer
{
	private int mIndex;

	private BattleUnit2D mUnit;

	private Vector< Position > mTrajectory;

	private Vector< OrientableAnimation2D > mGroups;

	BattleAction2DMove( BattleAction oAction, Battle2D oBattle )
	{
		super();

		mUnit = oBattle.GetBattleUnit2D( oAction.GetSourceUnit() );
		BattleActionMove oActionMove = ( BattleActionMove )oAction;
		mTrajectory = oActionMove.GetTrajectory();
		mGroups = new Vector< OrientableAnimation2D >();
		mIndex = 0;

		// Create the move animations
		Position oStartPos = null;

		for ( Position oPos : mTrajectory )
		{
			if ( oStartPos != null )
			{
				Orientation eMoveOrientation = oStartPos.GetOrientation( oPos );
				OrientableAnimation2D oGroup = MoveAnimation2DFactory.Create( mUnit, oStartPos, oPos, this );
				if ( null != oGroup )
				{
					mUnit.SetNextAnimation( oGroup, eMoveOrientation );
					mGroups.add( oGroup );

					// Create a dummy BattleDrawable to know when animation is finished
					BattleDrawable oDrawable = new BattleDrawable( oGroup, null, new RectF( 0, 0, 0, 0 ), 0 );
					AddDrawable( oDrawable, false );
				}
			}
			oStartPos = oPos;
		}

		// Prepare initial Animation
		if ( mGroups.size() > 0 )
		{
			mGroups.get( 0 ).ResetCurrentAnimation();
		}
	}

	@Override
	public void Update( BattleAction oAction )
	{
		// Just increment index counter
		mIndex++;

		if ( mIndex >= mDrawables.size() )
		{
			// Reset ZOrder offset at the end of the animation
			Logger.i( "Resetting Drawing offset" );
			mUnit.UpdateDrawing( 0, 0, 0 );
		}
		mUnit.UpdateFromBattleCell();
	}

	/** This animation should tell if current step is finished */
	public boolean isFinished()
	{
		if ( mIndex > -1 && mIndex < mDrawables.size() )
		{
			IDrawable oCurrentDrawable = mDrawables.get( mIndex ).mDrawable.GetDrawable();
			return ( oCurrentDrawable.isFinished() );
		}
		else
		{
			return true;
		}
	}

	public static IBattleAction2DCreator GetCreator()
	{
		return new BattleAction2DMoveCreator();
	}

	/** The BattleAction2D factory instance */
	private static class BattleAction2DMoveCreator implements IBattleAction2DCreator
	{
		public BattleAction2D BattleAction2DCreate( BattleAction oAction, Battle2D oBattle )
		{
			if ( oAction.GetActionType() == R.string.move_action )
			{
				return new BattleAction2DMove( oAction, oBattle );
			}
			return null;
		}	
	}

	/** Get updates from an Animation */
	public void update( Observable oObs, Object oObj )
	{
		Animation2D oAnimation = ( Animation2D )oObs;

		if ( oAnimation != null )
		{
			// Notify ZOrder change to BattleUnit's drawable
			int iNextZOrderOffset = 0;
			int iNextPosX = 0, iNextPosY = 0;
			if ( oAnimation.IsNotificationEndOfAnimation() )
			{
				// Get info from the next animation
				if ( mIndex < mGroups.size() - 1 )
				{
					Logger.d( "Updating from next animation's initial values" );
					oAnimation = mGroups.get( mIndex + 1 ).GetCurrentAnimation();
					iNextZOrderOffset = oAnimation.GetNextZOrderOffset();
					iNextPosX = oAnimation.GetNextPositionOffset().mPosX;
					iNextPosY = oAnimation.GetNextPositionOffset().mPosY;
				}
				else
				{
					Logger.d( "End of animations: 0 offset and position" );
				}
			}
			else
			{
				Logger.d( "Standard update from Animation" );
				iNextZOrderOffset = oAnimation.GetNextZOrderOffset();
				iNextPosX = oAnimation.GetNextPositionOffset().mPosX;
				iNextPosY = oAnimation.GetNextPositionOffset().mPosY;
			}

			Logger.d( String.format( 
					"Updating Unit with Offsets: ZOrder = %d, Position = %d, %d", 
					iNextZOrderOffset, 
					iNextPosX, 
					iNextPosY ) );

			mUnit.UpdateDrawing( iNextZOrderOffset, iNextPosX, iNextPosY );
			mUnit.UpdateFromBattleCell();
		}
	}
}
