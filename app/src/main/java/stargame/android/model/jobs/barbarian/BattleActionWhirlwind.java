package stargame.android.model.jobs.barbarian;

import java.util.Vector;

import stargame.android.R;
import stargame.android.model.Battle;
import stargame.android.model.BattleAction;
import stargame.android.model.BattleCell;
import stargame.android.model.BattleUnit;
import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.storage.SavableHelper;
import stargame.android.util.Position;
import stargame.android.util.RandomGenerator;

/**
 * Class BattleActionWhirlwind implements the Whirlwind action for barbarian
 *
 * @author Duduche
 */
public class BattleActionWhirlwind extends BattleAction implements ISavable
{
    /**
     * The respective damage for each ActionTarget
     */
    private Vector< Integer > mVecDamage;

    private static final String M_VEC_DAMAGE = "Damages";

    private static final int INVALID_TARGET = -1;

    private BattleActionWhirlwind()
    {
        super();
        mVecDamage = new Vector< Integer >();
    }

    BattleActionWhirlwind( Battle oBattle, BattleUnit oUnit )
    {
        super( oBattle, oUnit );
        mActionType = R.string.whirlwind_action;
        mVecDamage = new Vector< Integer >();
    }

    public boolean SetTarget( BattleCell oCell )
    {
        Position oPos = oCell.GetPos();
        if ( !oPos.Equals( mSourceUnit.GetCell().GetPos() ) )
        {
            return false;
        }

        // Set the targets (no check if there is a unit)
        BattleCell oTargetCell = mBattle.GetBattleField().GetCell( oPos.mPosX - 1, oPos.mPosY );
        if ( oTargetCell != null && IsAttackHeightPossible( oTargetCell.GetPos() ) )
        {
            mVecTargets.add( new ActionTarget( oTargetCell ) );
        }

        oTargetCell = mBattle.GetBattleField().GetCell( oPos.mPosX, oPos.mPosY - 1 );
        if ( oTargetCell != null && IsAttackHeightPossible( oTargetCell.GetPos() ) )
        {
            mVecTargets.add( new ActionTarget( oTargetCell ) );
        }

        oTargetCell = mBattle.GetBattleField().GetCell( oPos.mPosX + 1, oPos.mPosY );
        if ( oTargetCell != null && IsAttackHeightPossible( oTargetCell.GetPos() ) )
        {
            mVecTargets.add( new ActionTarget( oTargetCell ) );
        }

        oTargetCell = mBattle.GetBattleField().GetCell( oPos.mPosX, oPos.mPosY + 1 );
        if ( oTargetCell != null && IsAttackHeightPossible( oTargetCell.GetPos() ) )
        {
            mVecTargets.add( new ActionTarget( oTargetCell ) );
        }

        return true;
    }

    public boolean CanExecuteAction()
    {
        return ( mVecTargets.size() > 0 );
    }

    public void ExecuteAction()
    {
        mVecDamage.clear();
        for ( ActionTarget oTarget : mVecTargets )
        {
            if ( oTarget.mCell.GetUnit() != null )
            {
                // Check if attack lands: compute chance of hitting the target
                if ( RandomGenerator.GetInstance().GetRandom( 1, 100 ) <=
                        mSourceUnit.GetHitChance( oTarget.mCell.GetUnit() ) )
                {
                    // Get the attack information from the attacking unit
                    double dFinalDamage = Math.max(
                            1, mSourceUnit.GetUnit().GetResultingAttributes().GetStrength() -
                                    oTarget.mCell.GetUnit().GetUnit().GetResultingAttributes().GetPhysicalDef() );

                    // Check critical chance
                    if ( RandomGenerator.GetInstance().GetRandom( 1, 100 ) <=
                            mSourceUnit.GetCritChance() )
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
                            dFinalDamage - ( dFinalDamage / 10 ),
                            dFinalDamage + ( dFinalDamage / 10 ) );
                    mVecDamage.add( oTarget.mCell.GetUnit().ApplyDamage(
                            dFinalDamage ) );
                }
                else
                {
                    mVecDamage.add( 0 );
                    oTarget.mActionStatus = ActionStatus.STATUS_MISS;
                }
            }
            else
            {
                mVecDamage.add( INVALID_TARGET );
            }
        }

        NotifyActionUpdate();
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
        mVecTargets.clear();
        mVecDamage.clear();
        mVecTargetableCells.clear();
        mVecTargetableCells.add( mSourceUnit.GetCell().GetPos() );
    }

    public int GetDamage( Position oPos )
    {
        for ( int i = 0; i < mVecTargets.size(); ++i )
        {
            if ( mVecTargets.get( i ).mCell.GetPos().Equals( oPos ) )
            {
                return mVecDamage.get( i );
            }
        }

        return -1;
    }

    public boolean IsValidTarget( Position oPos )
    {
        return ( oPos == mSourceUnit.GetCell().GetPos() );
    }

    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        // Save parent info
        super.SaveBattleActionState( oObjectStore, oGlobalStore );

        int[] aDamages = new int[ mVecDamage.size() ];
        int iCounter = 0;
        for ( int iDamage : mVecDamage )
        {
            aDamages[ iCounter++ ] = iDamage;
        }

        oObjectStore.putIntArray( M_VEC_DAMAGE, aDamages );
    }

    public static BattleActionWhirlwind loadState( IStorage oGlobalStore,
                                                   String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey, BattleActionWhirlwind.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        BattleActionWhirlwind oAction = new BattleActionWhirlwind();

        // Load parent info
        oAction.LoadBattleActionState( oObjectStore, oGlobalStore );

        int[] aDamages = oObjectStore.getIntArray( M_VEC_DAMAGE );
        for ( int damage : aDamages )
        {
            oAction.mVecDamage.add( damage );
        }

        return oAction;
    }

    public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
    {
        return loadState( oGlobalStore, strObjKey );
    }
}
