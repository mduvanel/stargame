package stargame.android.model.jobs.rogue;

import android.os.Bundle;

import stargame.android.R;
import stargame.android.model.Battle;
import stargame.android.model.BattleAction;
import stargame.android.model.BattleUnit;
import stargame.android.model.status.UnitStatusInvisible;
import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;
import stargame.android.util.Position;

/**
 * Class BattleActionVanish implements the Vanish action for rogues
 *
 * @author Duduche
 */
public class BattleActionVanish extends BattleAction implements ISavable
{
    int mVanishDuration;

    private static final String M_DURATION = "Duration";

    private static final int M_DEFAULT_DURATION = 3;

    public BattleActionVanish()
    {
        super();
    }

    public BattleActionVanish( Battle oBattle, BattleUnit oUnit )
    {
        super( oBattle, oUnit );
        mActionType = R.string.vanish_action;
        mVanishDuration = M_DEFAULT_DURATION;
    }

    public boolean CanExecuteAction()
    {
        return true;
    }

    public void ExecuteAction()
    {
        mSourceUnit.SetStatus( new UnitStatusInvisible( mVanishDuration, mSourceUnit ) );
        mSourceUnit.SetActionPerformed();
    }

    @Override
    public void ComputeTargets()
    {
        mVecTargetableCells.clear();
        mVecTargetableCells.add( mSourceUnit.GetCell().GetPos() );
    }

    @Override
    public boolean IsValidTarget( Position oPos )
    {
        // Only castable on self
        return oPos.Equals( mSourceUnit.GetCell().GetPos() );
    }

    public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
    {
        // Save parent info
        super.SaveBattleActionState( oObjectMap, oGlobalMap );

        oObjectMap.putInt( M_DURATION, mVanishDuration );
    }

    public static BattleActionVanish loadState( Bundle oGlobalMap, String strObjKey )
    {
        Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey,
                                                             BattleActionVanish.class.getName() );

        if ( oObjectBundle == null )
        {
            return null;
        }

        BattleActionVanish oAction = new BattleActionVanish();

        // Load parent info
        oAction.LoadBattleActionState( oObjectBundle, oGlobalMap );

        oAction.mVanishDuration = oObjectBundle.getInt( M_DURATION );

        return oAction;
    }

    public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
    {
        return loadState( oGlobalMap, strObjKey );
    }
}
