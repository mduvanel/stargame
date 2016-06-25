package stargame.android.model;

import android.os.Bundle;

import stargame.android.R;
import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;
import stargame.android.util.Position;
import stargame.android.util.RandomGenerator;

/**
 * Class AttackBattleAction implements the default Attack action for all jobs
 *
 * @author Duduche
 */
public class BattleActionAttack extends BattleAction implements ISavable
{
    private static class AttackStruct implements ISavable
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

        public static AttackStruct loadState( Bundle oGlobalMap, String strObjKey )
        {
            Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey,
                                                                 AttackStruct.class.getName() );

            if ( oObjectBundle == null )
            {
                return null;
            }

            AttackStruct oStruct = new AttackStruct();

            oStruct.mAvailable = oObjectBundle.getBoolean( M_AVAILABLE );

            return oStruct;
        }
    }

    private AttackStruct mAttackArray[][];

    private static final String M_ATTACK_ARRAY = "AttackArray";

    protected int mDamage;

    private static final String M_DAMAGE = "Damage";

    protected BattleActionAttack()
    {
        super();
    }

    public BattleActionAttack( Battle oBattle, BattleUnit oUnit )
    {
        super( oBattle, oUnit );
        mActionType = R.string.attack_action;
        mDamage = 0;

        InitAttackArray();
    }

    public boolean SetTarget( BattleCell oCell )
    {
        if ( IsValidTarget( oCell.GetPos() ) &&
                oCell.GetUnit() != null &&
                oCell.GetUnit().IsTargetable() ) // invisible units can't be attacked
        {
            mVecTargets.set( 0, new ActionTarget( oCell ) );
            return true;
        }

        return false;
    }

    protected void InitAttackArray()
    {
        mAttackArray = new AttackStruct[ mBattle.GetBattleField().GetWidth() ][ mBattle.GetBattleField().GetHeight() ];
        for ( int i = 0; i < mBattle.GetBattleField().GetWidth(); ++i )
        {
            for ( int j = 0; j < mBattle.GetBattleField().GetHeight(); ++j )
            {
                mAttackArray[ i ][ j ] = new AttackStruct();
            }
        }
    }

    protected void ResetAttackArray()
    {
        for ( int i = 0; i < mBattle.GetBattleField().GetWidth(); ++i )
        {
            for ( int j = 0; j < mBattle.GetBattleField().GetHeight(); ++j )
            {
                mAttackArray[ i ][ j ].mAvailable = false;
            }
        }
        mVecTargetableCells.clear();
    }

    public boolean CanExecuteAction()
    {
        return ( mVecTargets.get( 0 ) != null );
    }

    public void ExecuteAction()
    {
        // Get the target unit
        ActionTarget oTarget = mVecTargets.get( 0 );

        // Check if attack lands: compute chance of hitting the target
        if ( RandomGenerator.GetInstance().GetRandom( 1, 100 ) <= mSourceUnit.GetHitChance(
                oTarget.mCell.GetUnit() ) )
        {
            // Get the attack information from the attacking unit
            double dFinalDamage = Math.max( 1,
                                            mSourceUnit.GetUnit().GetResultingAttributes().GetStrength() -
                                                    oTarget.mCell.GetUnit().GetUnit().GetResultingAttributes().GetPhysicalDef() );

            // Check critical chance
            if ( RandomGenerator.GetInstance().GetRandom( 1, 100 ) <= mSourceUnit.GetCritChance() )
            {
                dFinalDamage *= 1.5;
                oTarget.mActionStatus = ActionStatus.STATUS_CRITICAL;
            }
            else
            {
                oTarget.mActionStatus = ActionStatus.STATUS_SUCCESS;
            }

            // Add random portion to damage (10% of base damage)
            dFinalDamage = RandomGenerator.GetInstance().GetRandom(
                    dFinalDamage - ( dFinalDamage / 10 ), dFinalDamage + ( dFinalDamage / 10 ) );
            mDamage = oTarget.mCell.GetUnit().ApplyDamage( dFinalDamage );
        }
        else
        {
            mDamage = 0;
            oTarget.mActionStatus = ActionStatus.STATUS_MISS;
        }

        NotifyActionUpdate();
        ResetAttackArray();
        mSourceUnit.SetActionPerformed();
    }

    protected boolean IsAttackHeightPossible( Position oTargetPos )
    {
        return Math.abs(
                mSourceUnit.GetCell().GetElevation() - mBattle.GetBattleField().GetElevation(
                        oTargetPos ) ) < 3;
    }

    public void ComputeTargets()
    {
        mVecTargets.set( 0, null );
        ResetAttackArray();
        int iUnitRange = mSourceUnit.GetUnit().GetResultingAttributes().GetAttackRange();
        Position oUnitPos = mSourceUnit.GetCell().GetPos();
        Position oNewPosition;

        for ( int i = 0; i < iUnitRange; ++i )
        {
            // Go in all 4 directions
            oNewPosition = oUnitPos.Offset( -( i + 1 ), 0 );
            if ( oNewPosition.mPosX >= 0 && IsAttackHeightPossible( oNewPosition ) )
            {
                mAttackArray[ oNewPosition.mPosX ][ oNewPosition.mPosY ].mAvailable = true;
                mVecTargetableCells.add( oNewPosition );
            }

            oNewPosition = oUnitPos.Offset( i + 1, 0 );
            if ( oNewPosition.mPosX < mBattle.GetBattleField().GetWidth() && IsAttackHeightPossible(
                    oNewPosition ) )
            {
                mAttackArray[ oNewPosition.mPosX ][ oNewPosition.mPosY ].mAvailable = true;
                mVecTargetableCells.add( oNewPosition );
            }

            oNewPosition = oUnitPos.Offset( 0, -( i + 1 ) );
            if ( oNewPosition.mPosY >= 0 && IsAttackHeightPossible( oNewPosition ) )
            {
                mAttackArray[ oNewPosition.mPosX ][ oNewPosition.mPosY ].mAvailable = true;
                mVecTargetableCells.add( oNewPosition );
            }

            oNewPosition = oUnitPos.Offset( 0, i + 1 );
            if ( oNewPosition.mPosY < mBattle.GetBattleField().GetHeight() && IsAttackHeightPossible(
                    oNewPosition ) )
            {
                mAttackArray[ oNewPosition.mPosX ][ oNewPosition.mPosY ].mAvailable = true;
                mVecTargetableCells.add( oNewPosition );
            }
        }
    }

    public int GetDamage()
    {
        return mDamage;
    }

    public boolean IsValidTarget( Position oPos )
    {
        return mAttackArray[ oPos.mPosX ][ oPos.mPosY ].mAvailable;
    }

    public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
    {
        // Save parent info
        super.SaveBattleActionState( oObjectMap, oGlobalMap );

        oObjectMap.putInt( M_DAMAGE, mDamage );

        Bundle oAttackBundle = SavableHelper.saveBidimensionalArrayInMap( mAttackArray,
                                                                          oGlobalMap );
        oObjectMap.putBundle( M_ATTACK_ARRAY, oAttackBundle );
    }

    protected void LoadBattleActionAttackState( Bundle oObjectMap, Bundle oGlobalMap )
    {
        mDamage = oObjectMap.getInt( M_DAMAGE );

        InitAttackArray();
        Bundle oArrayBundle = oObjectMap.getBundle( M_ATTACK_ARRAY );
        SavableHelper.loadBidimensionalArrayFromMap( mAttackArray, oArrayBundle, oGlobalMap,
                                                     new AttackStruct() );
    }

    public static BattleActionAttack loadState( Bundle oGlobalMap, String strObjKey )
    {
        Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey,
                                                             BattleActionAttack.class.getName() );

        if ( oObjectBundle == null )
        {
            return null;
        }

        BattleActionAttack oAction = new BattleActionAttack();

        // Load parent info
        oAction.LoadBattleActionState( oObjectBundle, oGlobalMap );

        oAction.LoadBattleActionAttackState( oObjectBundle, oGlobalMap );

        return oAction;
    }

    public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
    {
        return loadState( oGlobalMap, strObjKey );
    }
}
