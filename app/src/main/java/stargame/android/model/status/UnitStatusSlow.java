package stargame.android.model.status;

import stargame.android.R;
import stargame.android.model.BattleUnit;
import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.storage.SavableHelper;

public class UnitStatusSlow extends UnitStatus
{
    /**
     * How much the unit is slowed down
     */
    private int mSlowPercentage;

    private final static String M_SLOW_PERCENT = "SlowPercent";

    public int GetSlowPercentage()
    {
        return mSlowPercentage;
    }

    /**
     * Empty constructor for loadState() method
     */
    private UnitStatusSlow()
    {
        super();
    }

    public UnitStatusSlow( int iTurns, BattleUnit oUnit, int iSlowPercentage )
    {
        super( iTurns, oUnit );
        mType = R.string.status_slow;
        mSlowPercentage = iSlowPercentage;
    }

    @Override
    public boolean TurnPassed()
    {
        // Nothing special to do when starting the turn
        return super.TurnPassed();
    }

    @Override
    protected void MergeStatusInternal( UnitStatus oStatus )
    {
        UnitStatusSlow oStatusSlow = ( UnitStatusSlow ) oStatus;

        // Pick the longest duration and Slow %
        if ( oStatusSlow.mTurnsLeft > mTurnsLeft )
        {
            mTurnsLeft = oStatusSlow.mTurnsLeft;
        }

        if ( oStatusSlow.mSlowPercentage > mSlowPercentage )
        {
            mSlowPercentage = oStatusSlow.mSlowPercentage;
        }
    }


    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        // Save parent info
        SaveUnitStatusData( oObjectStore, oGlobalStore );

        oObjectStore.putInt( M_SLOW_PERCENT, mSlowPercentage );
    }

    public static UnitStatusSlow loadState( IStorage oGlobalStore,
                                            String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey, UnitStatusSlow.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        UnitStatusSlow oStatus = new UnitStatusSlow();

        // Load parent info
        oStatus.LoadUnitStatusData( oObjectStore, oGlobalStore );

        oStatus.mSlowPercentage = oObjectStore.getInt( M_SLOW_PERCENT );

        return oStatus;
    }

    public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
    {
        return loadState( oGlobalStore, strObjKey );
    }
}
