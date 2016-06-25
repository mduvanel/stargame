package stargame.android.model.jobs.priest;

import android.os.Bundle;

import stargame.android.R;
import stargame.android.model.Battle;
import stargame.android.model.BattleCell;
import stargame.android.model.BattleUnit;
import stargame.android.model.RangeBattleAction;
import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;
import stargame.android.util.RandomGenerator;


/**
 * Class BattleActionHeal implements the Heal action for priest
 *
 * @author Duduche
 */
public class BattleActionHeal extends RangeBattleAction implements ISavable
{
    private int mHeal;

    private static final String M_HEAL = "Heal";

    BattleActionHeal()
    {
        super();
    }

    BattleActionHeal( Battle oBattle, BattleUnit oUnit )
    {
        super( oBattle, oUnit );
        mActionType = R.string.heal_action;
        mHeal = 0;

        InitValidArray();
    }

    @Override
    public boolean SetTarget( BattleCell oCell )
    {
        if ( IsValidTarget( oCell.GetPos() ) && oCell.GetUnit() != null )
        {
            mVecTargets.set( 0, new ActionTarget( oCell ) );
            return true;
        }

        return false;
    }

    public boolean CanExecuteAction()
    {
        return ( mVecTargets.get( 0 ) != null );
    }

    @Override
    public void ExecuteAction()
    {
        // Get the target
        ActionTarget oTarget = mVecTargets.get( 0 );

        // Get heal amount
        double dHeal = 20 + mSourceUnit.GetUnit().GetResultingAttributes().GetMagicPower();

        // Heal always lands (for now?), check for critical heal
        if ( RandomGenerator.GetInstance().GetRandom( 1, 100 ) > 100 - mCritChance )
        {
            dHeal *= 1.5;
            oTarget.mActionStatus = ActionStatus.STATUS_CRITICAL;
        }
        else
        {
            oTarget.mActionStatus = ActionStatus.STATUS_SUCCESS;
        }

        // Add random portion to damage (10% of base damage)
        dHeal = RandomGenerator.GetInstance().GetRandom( dHeal - ( dHeal / 10.0 ),
                                                         dHeal + ( dHeal / 10.0 ) );
        mHeal = oTarget.mCell.GetUnit().HealResult( dHeal );

        NotifyActionUpdate();
        ResetValidArray();
        mSourceUnit.SetActionPerformed();
    }

    public int GetHealAmount()
    {
        return mHeal;
    }

    public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
    {
        // Save parent info
        super.SaveBattleActionState( oObjectMap, oGlobalMap );

        oObjectMap.putInt( M_HEAL, mHeal );
    }

    public static BattleActionHeal loadState( Bundle oGlobalMap, String strObjKey )
    {
        Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey,
                                                             BattleActionHeal.class.getName() );

        if ( oObjectBundle == null )
        {
            return null;
        }

        BattleActionHeal oAction = new BattleActionHeal();

        // Load parent info
        oAction.LoadBattleActionState( oObjectBundle, oGlobalMap );

        oAction.mHeal = oObjectBundle.getInt( M_HEAL );

        return oAction;
    }

    public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
    {
        return loadState( oGlobalMap, strObjKey );
    }
}
