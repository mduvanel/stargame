package stargame.android.model.jobs.barbarian;

import android.os.Bundle;

import stargame.android.R;
import stargame.android.model.Battle;
import stargame.android.model.BattleActionAttack;
import stargame.android.model.BattleUnit;
import stargame.android.model.status.UnitStatusSlow;
import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;
import stargame.android.util.RandomGenerator;


/**
 * Class BattleActionStun implements the Stun action for barbarian
 *
 * @author Duduche
 */
public class BattleActionStun extends BattleActionAttack implements ISavable
{
    private final static int SLOW_TURN_DURATION = 3;

    private final static int SLOW_PERCENTAGE = 50;

    public BattleActionStun()
    {
        super();
    }

    public BattleActionStun( Battle oBattle, BattleUnit oUnit )
    {
        super( oBattle, oUnit );
        mActionType = R.string.stun_action;
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

            // Add Slow status to target
            oTarget.mCell.GetUnit().SetStatus( new UnitStatusSlow(
                    SLOW_TURN_DURATION,
                    oTarget.mCell.GetUnit(),
                    SLOW_PERCENTAGE ) );

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

    public static BattleActionStun loadState( Bundle oGlobalMap, String strObjKey )
    {
        Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey,
                                                             BattleActionStun.class.getName() );

        if ( oObjectBundle == null )
        {
            return null;
        }

        BattleActionStun oAction = new BattleActionStun();

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