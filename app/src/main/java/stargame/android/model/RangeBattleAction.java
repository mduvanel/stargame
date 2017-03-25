package stargame.android.model;

import android.os.Bundle;

import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.storage.SavableHelper;
import stargame.android.util.Position;

/**
 * Class RangeBattleAction is a more specialized abstract class that can be used by Range Battle Action.
 *
 * @author Duduche
 */
public abstract class RangeBattleAction extends BattleAction
{
    private static class ValidStruct implements ISavable
    {
        public boolean mAvailable;

        private static final String M_AVAILABLE = "Available";

        public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
        {
            oObjectStore.putBoolean( M_AVAILABLE, mAvailable );
        }

        public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
        {
            return loadState( oGlobalStore, strObjKey );
        }

        public static ValidStruct loadState( IStorage oGlobalStore,
                                             String strObjKey )
        {
            IStorage oObjectStore = SavableHelper.retrieveStore(
                    oGlobalStore, strObjKey, ValidStruct.class.getName() );

            if ( oObjectStore == null )
            {
                return null;
            }

            ValidStruct oStruct = new ValidStruct();

            oStruct.mAvailable = oObjectStore.getBoolean( M_AVAILABLE );

            return oStruct;
        }
    }

    private ValidStruct mValidArray[][];

    private static final String M_VALID_ARRAY = "ValidArray";

    /**
     * Critical effect chance for ability (%)
     */
    protected int mCritChance = 5;

    private static final String M_CRIT = "Crit";

    /**
     * Ability range (cells)
     */
    private int mRange = 4;

    private static final String M_RANGE = "Range";

    /**
     * Ability max height distance
     */
    private int mHeight = 4;

    private static final String M_HEIGHT = "Height";

    /**
     * Action radius
     */
    private int mRadius = 0;

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

    private boolean IsActionPossible( Position oPos )
    {
        boolean bTargetOK = oPos.IsInside( 0, 0, mBattle.GetBattleField().GetWidth() - 1,
                                           mBattle.GetBattleField().GetHeight() - 1 );
        return bTargetOK && ( Math.abs(
                mSourceUnit.GetCell().GetElevation() - mBattle.GetBattleField().GetElevation(
                        oPos ) ) < mHeight );
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

    public void SaveBattleActionState( IStorage oObjectStore,
                                       IStorage oGlobalStore )
    {
        super.SaveBattleActionState( oObjectStore, oGlobalStore );

        oObjectStore.putInt( M_CRIT, mCritChance );
        oObjectStore.putInt( M_RANGE, mRange );
        oObjectStore.putInt( M_HEIGHT, mHeight );
        oObjectStore.putInt( M_RADIUS, mRadius );

        IStorage oValidStore = SavableHelper.saveBidimensionalArrayInStore(
                mValidArray, oGlobalStore );
        oObjectStore.putStore( M_VALID_ARRAY, oValidStore );
    }

    public void LoadBattleActionState( IStorage oObjectStore,
                                       IStorage oGlobalStore )
    {
        super.LoadBattleActionState( oObjectStore, oGlobalStore );

        mCritChance = oObjectStore.getInt( M_CRIT );
        mRange = oObjectStore.getInt( M_RANGE );
        mHeight = oObjectStore.getInt( M_HEIGHT );
        mRadius = oObjectStore.getInt( M_RADIUS );

        InitValidArray();
        IStorage oArrayStore = oObjectStore.getStore( M_VALID_ARRAY );
        SavableHelper.loadBidimensionalArrayFromStore(
                mValidArray, oArrayStore, oGlobalStore, new ValidStruct() );
    }

    public boolean IsValidTarget( Position oPos )
    {
        return mValidArray[ oPos.mPosX ][ oPos.mPosY ].mAvailable;
    }
}
