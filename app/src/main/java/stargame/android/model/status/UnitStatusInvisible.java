package stargame.android.model.status;

import stargame.android.R;
import stargame.android.model.BattleUnit;
import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.storage.SavableHelper;

public class UnitStatusInvisible extends UnitStatus
{
    /**
     * Empty constructor for loadState() method
     */
    private UnitStatusInvisible()
    {
        super();
    }

    public UnitStatusInvisible( int iTurns, BattleUnit oUnit )
    {
        super( iTurns, oUnit );
        mType = R.string.status_invisible;
    }

    @Override
    public boolean TurnPassed()
    {
        // Nothing special to do when starting the turn
        return super.TurnPassed();
    }

    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        // Save parent info
        SaveUnitStatusData( oObjectStore, oGlobalStore );
    }

    public static UnitStatusInvisible loadState( IStorage oGlobalStore,
                                                 String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey, UnitStatusInvisible.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        UnitStatusInvisible oStatus = new UnitStatusInvisible();

        // Load parent info
        oStatus.LoadUnitStatusData( oObjectStore, oGlobalStore );

        return oStatus;
    }

    public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
    {
        return loadState( oGlobalStore, strObjKey );
    }

    @Override
    protected void MergeStatusInternal( UnitStatus oStatus )
    {
        UnitStatusInvisible oStatusInv = ( UnitStatusInvisible ) oStatus;

        // Pick the longest duration
        if ( oStatusInv.mTurnsLeft > mTurnsLeft )
        {
            mTurnsLeft = oStatusInv.mTurnsLeft;
        }
    }
}
