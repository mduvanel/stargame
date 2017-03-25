package stargame.android.model.status;

import stargame.android.R;
import stargame.android.model.BattleUnit;
import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.storage.SavableHelper;


public class UnitStatusBarrier extends UnitStatus
{
    private int mTotalAbsorbCapacity;

    private final static String M_TOTAL_ABSORB = "Total";

    private int mCurrentAbsorbCapacity;

    private final static String M_CURRENT_ABSORB = "Current";

    /**
     * Empty constructor for loadState() method
     */
    private UnitStatusBarrier()
    {
        super();
    }

    public UnitStatusBarrier( int iTurns, BattleUnit oUnit, int iAbsorbCapacity )
    {
        super( iTurns, oUnit );
        mType = R.string.status_barrier;
        mTotalAbsorbCapacity = iAbsorbCapacity;
        mCurrentAbsorbCapacity = iAbsorbCapacity;
    }

    /**
     * Calculates how much damage is absorbed and returns the rest
     */
    public int Absorb( int iDamage )
    {
        if ( iDamage > mCurrentAbsorbCapacity )
        {
            int iResult = iDamage - mCurrentAbsorbCapacity;
            mCurrentAbsorbCapacity = 0;
            return iResult;
        }
        else
        {
            mCurrentAbsorbCapacity -= iDamage;
            return 0;
        }
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

        oObjectStore.putInt( M_TOTAL_ABSORB, mTotalAbsorbCapacity );
        oObjectStore.putInt( M_CURRENT_ABSORB, mCurrentAbsorbCapacity );
    }

    public static UnitStatusBarrier loadState( IStorage oGlobalStore,
                                               String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey, UnitStatusBarrier.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        UnitStatusBarrier oStatus = new UnitStatusBarrier();

        // Load parent info
        oStatus.LoadUnitStatusData( oObjectStore, oGlobalStore );

        oStatus.mTotalAbsorbCapacity = oObjectStore.getInt( M_TOTAL_ABSORB );
        oStatus.mCurrentAbsorbCapacity = oObjectStore.getInt( M_CURRENT_ABSORB );

        return oStatus;
    }

    public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
    {
        return loadState( oGlobalStore, strObjKey );
    }

    @Override
    protected void MergeStatusInternal( UnitStatus oStatus )
    {
        UnitStatusBarrier oStatusBarrier = ( UnitStatusBarrier ) oStatus;

        // Two barriers are merged by taking the longest duration AND highest protection
        if ( oStatusBarrier.mCurrentAbsorbCapacity > mCurrentAbsorbCapacity )
        {
            mCurrentAbsorbCapacity = oStatusBarrier.mCurrentAbsorbCapacity;
        }

        if ( oStatusBarrier.mTurnsLeft > mTurnsLeft )
        {
            mTurnsLeft = oStatusBarrier.mTurnsLeft;
        }
    }
}
