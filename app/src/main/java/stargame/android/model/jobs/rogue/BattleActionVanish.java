package stargame.android.model.jobs.rogue;

import stargame.android.R;
import stargame.android.model.Battle;
import stargame.android.model.BattleAction;
import stargame.android.model.BattleUnit;
import stargame.android.model.status.UnitStatusInvisible;
import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.storage.SavableHelper;
import stargame.android.util.Position;

/**
 * Class BattleActionVanish implements the Vanish action for rogues
 *
 * @author Duduche
 */
class BattleActionVanish extends BattleAction implements ISavable
{
    private int mVanishDuration;

    private static final String M_DURATION = "Duration";

    private static final int M_DEFAULT_DURATION = 3;

    private BattleActionVanish()
    {
        super();
    }

    BattleActionVanish( Battle oBattle, BattleUnit oUnit )
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
        mSourceUnit.SetStatus( new UnitStatusInvisible(
                mVanishDuration, mSourceUnit ) );
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

    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        // Save parent info
        super.SaveBattleActionState( oObjectStore, oGlobalStore );

        oObjectStore.putInt( M_DURATION, mVanishDuration );
    }

    public static BattleActionVanish loadState( IStorage oGlobalStore,
                                                String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey, BattleActionVanish.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        BattleActionVanish oAction = new BattleActionVanish();

        // Load parent info
        oAction.LoadBattleActionState( oObjectStore, oGlobalStore );

        oAction.mVanishDuration = oObjectStore.getInt( M_DURATION );

        return oAction;
    }

    public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
    {
        return loadState( oGlobalStore, strObjKey );
    }
}
