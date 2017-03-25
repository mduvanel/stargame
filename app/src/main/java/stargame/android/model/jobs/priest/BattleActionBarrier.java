package stargame.android.model.jobs.priest;

import stargame.android.R;
import stargame.android.model.Battle;
import stargame.android.model.BattleCell;
import stargame.android.model.BattleUnit;
import stargame.android.model.RangeBattleAction;
import stargame.android.model.status.UnitStatusBarrier;
import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.storage.SavableHelper;


/**
 * Class BattleActionBarrier implements the Barrier cast for priest
 *
 * @author Duduche
 */
public class BattleActionBarrier extends RangeBattleAction implements ISavable
{
    private int mBarrierDuration;

    private static final String M_DURATION = "Duration";

    private static final int M_DEFAULT_DURATION = 4;

    private BattleActionBarrier()
    {
        super();
    }

    BattleActionBarrier( Battle oBattle, BattleUnit oUnit )
    {
        super( oBattle, oUnit );
        mActionType = R.string.barrier_action;
        mBarrierDuration = M_DEFAULT_DURATION;
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
        // Get the target unit
        BattleUnit oTargetUnit = mVecTargets.get( 0 ).mCell.GetUnit();

        // Cast the barrier...
        oTargetUnit.SetStatus( new UnitStatusBarrier(
                mBarrierDuration,
                oTargetUnit,
                mSourceUnit.GetUnit().GetResultingAttributes().GetMagicPower() ) );

        // That's it!
        NotifyActionUpdate();
        ResetValidArray();
        mSourceUnit.SetActionPerformed();
    }

    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        // Save parent info
        super.SaveBattleActionState( oObjectStore, oGlobalStore );

        oObjectStore.putInt( M_DURATION, mBarrierDuration );
    }

    public static BattleActionBarrier loadState( IStorage oGlobalStore,
                                                 String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey, BattleActionBarrier.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        BattleActionBarrier oAction = new BattleActionBarrier();

        // Load parent info
        oAction.LoadBattleActionState( oObjectStore, oGlobalStore );

        oAction.mBarrierDuration = oObjectStore.getInt( M_DURATION );

        return oAction;
    }

    public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
    {
        return loadState( oGlobalStore, strObjKey );
    }
}
