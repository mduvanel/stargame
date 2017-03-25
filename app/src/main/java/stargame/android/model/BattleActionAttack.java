package stargame.android.model;

import stargame.android.R;
import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
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
        boolean mAvailable;

        private static final String M_AVAILABLE = "Available";

        public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
        {
            oObjectStore.putBoolean( M_AVAILABLE, mAvailable );
        }

        public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
        {
            return loadState( oGlobalStore, strObjKey );
        }

        public static AttackStruct loadState( IStorage oGlobalStore,
                                              String strObjKey )
        {
            IStorage oObjectStore = SavableHelper.retrieveStore(
                    oGlobalStore, strObjKey, AttackStruct.class.getName() );

            if ( oObjectStore == null )
            {
                return null;
            }

            AttackStruct oStruct = new AttackStruct();

            oStruct.mAvailable = oObjectStore.getBoolean( M_AVAILABLE );

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

    private void InitAttackArray()
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

    private boolean IsAttackHeightPossible( Position oTargetPos )
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

    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        // Save parent info
        super.SaveBattleActionState( oObjectStore, oGlobalStore );

        oObjectStore.putInt( M_DAMAGE, mDamage );

        IStorage oAttackStore = SavableHelper.saveBidimensionalArrayInStore(
                mAttackArray, oGlobalStore );
        oObjectStore.putStore( M_ATTACK_ARRAY, oAttackStore );
    }

    protected void LoadBattleActionAttackState( IStorage oObjectStore,
                                                IStorage oGlobalStore )
    {
        mDamage = oObjectStore.getInt( M_DAMAGE );

        InitAttackArray();
        IStorage oArrayStore = oObjectStore.getStore( M_ATTACK_ARRAY );
        SavableHelper.loadBidimensionalArrayFromStore(
                mAttackArray, oArrayStore, oGlobalStore, new AttackStruct() );
    }

    public static BattleActionAttack loadState( IStorage oGlobalStore,
                                                String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey, BattleActionAttack.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        BattleActionAttack oAction = new BattleActionAttack();

        // Load parent info
        oAction.LoadBattleActionState( oObjectStore, oGlobalStore );

        oAction.LoadBattleActionAttackState( oObjectStore, oGlobalStore );

        return oAction;
    }

    public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
    {
        return loadState( oGlobalStore, strObjKey );
    }
}
