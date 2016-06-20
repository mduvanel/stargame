package stargame.android.model;

import android.os.Bundle;
import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;
import stargame.android.util.Position;

/**
 * Class RangeBattleAction is a more specialized abstract class that can be used by Range Battle Action.
 * 
 * @author Duduche
 */
public abstract class RangeBattleAction extends BattleAction
{
	protected static class ValidStruct implements ISavable
	{
		public boolean mAvailable;

		private static final String M_AVAILABLE = "Available";

		public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
		{
			oObjectMap.putBoolean( M_AVAILABLE, mAvailable );
		}

		public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
		{
			return loadState( oGlobalMap, strObjKey );
		}

		public static ValidStruct loadState( Bundle oGlobalMap, String strObjKey )
		{
			Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey, ValidStruct.class.getName() );

			if ( oObjectBundle == null )
			{
				return null;
			}

			ValidStruct oStruct = new ValidStruct();

			oStruct.mAvailable = oObjectBundle.getBoolean( M_AVAILABLE );

			return oStruct;
		}
	}

	private ValidStruct mValidArray [][];

	private static final String M_VALID_ARRAY = "ValidArray";

	/** Critical effect chance for ability (%) */
	protected int mCritChance = 5;

	private static final String M_CRIT = "Crit";

	/** Ability range (cells) */
	protected int mRange = 4;

	private static final String M_RANGE = "Range";

	/** Ability max height distance */
	protected int mHeight = 4;

	private static final String M_HEIGHT = "Height";

	/** Action radius */
	protected int mRadius = 0;

	private static final String M_RADIUS = "Radius";

	public RangeBattleAction()
	{
		super();
	}

	public RangeBattleAction( Battle oBattle, BattleUnit oUnit )
	{
		super( oBattle, oUnit );
		mValidArray = null;
	}

	protected void InitValidArray()
	{
		mValidArray = new ValidStruct[ mBattle.GetBattleField().GetWidth() ][ mBattle.GetBattleField().GetHeight() ];
		for ( int i = 0; i < mBattle.GetBattleField().GetWidth(); ++i )
		{
			for ( int j = 0; j < mBattle.GetBattleField().GetHeight(); ++j )
			{
				mValidArray[ i ][ j ] = new ValidStruct();
			}
		}
	}

	protected void ResetValidArray()
	{
		for ( int i = 0; i < mBattle.GetBattleField().GetWidth(); ++i )
		{
			for ( int j = 0; j < mBattle.GetBattleField().GetHeight(); ++j )
			{
				mValidArray[ i ][ j ].mAvailable = false;
			}
		}
		mVecTargetableCells.clear();
	}

	protected boolean IsActionPossible( Position oPos )
	{
		boolean bTargetOK = oPos.IsInside( 0, 0, mBattle.GetBattleField().GetWidth() - 1, mBattle.GetBattleField().GetHeight() - 1 );
		return bTargetOK && ( Math.abs( mSourceUnit.GetCell().GetElevation() - mBattle.GetBattleField().GetElevation( oPos ) ) < mHeight );
	}

	public void ComputeTargets()
	{
		ResetValidArray();
		mVecTargets.set( 0, null );
		Position oPos = mSourceUnit.GetCell().GetPos();

		for ( int i = 1; i < mRange; ++i )
		{
			Position oCirclePos = new Position( oPos, -i, 0 );
			do
			{
				if ( IsActionPossible( oCirclePos ) )
				{
					mValidArray[ oCirclePos.mPosX ][ oCirclePos.mPosY ].mAvailable = true;
					mVecTargetableCells.add( oCirclePos );
				}
				oCirclePos = oCirclePos.Offset( 1, -1 );
			} while ( oCirclePos.mPosX < oPos.mPosX );

			do
			{
				if ( IsActionPossible( oCirclePos ) )
				{
					mValidArray[ oCirclePos.mPosX ][ oCirclePos.mPosY ].mAvailable = true;
					mVecTargetableCells.add( oCirclePos );
				}
				oCirclePos = oCirclePos.Offset( 1, 1 );
			} while ( oCirclePos.mPosX < oPos.mPosX + i );

			do
			{
				if ( IsActionPossible( oCirclePos ) )
				{
					mValidArray[ oCirclePos.mPosX ][ oCirclePos.mPosY ].mAvailable = true;
					mVecTargetableCells.add( oCirclePos );
				}
				oCirclePos = oCirclePos.Offset( -1, 1 );
			} while ( oCirclePos.mPosX > oPos.mPosX );

			do
			{
				if ( IsActionPossible( oCirclePos ) )
				{
					mValidArray[ oCirclePos.mPosX ][ oCirclePos.mPosY ].mAvailable = true;
					mVecTargetableCells.add( new Position( oCirclePos ) );
				}
				oCirclePos = oCirclePos.Offset( -1, -1 );
			} while ( oCirclePos.mPosX > oPos.mPosX - i );
		}
	}

	public void SaveBattleActionState( Bundle oObjectMap, Bundle oGlobalMap )
	{
		super.SaveBattleActionState( oObjectMap, oGlobalMap );

		oObjectMap.putInt( M_CRIT, mCritChance );
		oObjectMap.putInt( M_RANGE, mRange );
		oObjectMap.putInt( M_HEIGHT, mHeight );
		oObjectMap.putInt( M_RADIUS, mRadius );

		Bundle oValidBundle = SavableHelper.saveBidimensionalArrayInMap( mValidArray, oGlobalMap );
		oObjectMap.putBundle( M_VALID_ARRAY, oValidBundle );
	}

	public void LoadBattleActionState( Bundle oObjectMap, Bundle oGlobalMap )
	{
		super.LoadBattleActionState( oObjectMap, oGlobalMap );

		mCritChance = oObjectMap.getInt( M_CRIT );
		mRange = oObjectMap.getInt( M_RANGE );
		mHeight = oObjectMap.getInt( M_HEIGHT );
		mRadius = oObjectMap.getInt( M_RADIUS );

		InitValidArray();
		Bundle oArrayBundle = oObjectMap.getBundle( M_VALID_ARRAY );
		SavableHelper.loadBidimensionalArrayFromMap( mValidArray, oArrayBundle, oGlobalMap, new ValidStruct() );
	}

	public boolean IsValidTarget( Position oPos )
	{
		return mValidArray[ oPos.mPosX ][ oPos.mPosY ].mAvailable;
	}
}
