package stargame.android.model.status;

import stargame.android.R;
import stargame.android.model.BattleUnit;
import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.storage.SavableHelper;

class UnitStatusPoison extends UnitStatus
{
    private int mDamagePerTurn;

    private final static String M_DPT = "DPT";

    /**
     * Empty constructor for loadState() method
     */
    private UnitStatusPoison()
    {
        super();
    }

    public UnitStatusPoison( int iTurns, BattleUnit oUnit, int iDamagePerTurn )
    {
        super( iTurns, oUnit );
        mDamagePerTurn = iDamagePerTurn;
        mType = R.string.status_poison;
    }

    @Override
    public boolean TurnPassed()
    {
        // Apply damage to the unit
        mUnit.ApplyDamage( mDamagePerTurn );

        return super.TurnPassed();
    }

    @Override
    protected void MergeStatusInternal( UnitStatus oStatus )
    {
        UnitStatusPoison oStatusPoison = ( UnitStatusPoison ) oStatus;

        // Pick the longest duration and DPT
        if ( oStatusPoison.mTurnsLeft > mTurnsLeft )
        {
            mTurnsLeft = oStatusPoison.mTurnsLeft;
        }

        if ( oStatusPoison.mDamagePerTurn > mDamagePerTurn )
        {
            mDamagePerTurn = oStatusPoison.mDamagePerTurn;
        }
    }

    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        // Save parent info
        SaveUnitStatusData( oObjectStore, oGlobalStore );

        oObjectStore.putInt( M_DPT, mDamagePerTurn );
    }

    public static UnitStatusPoison loadState( IStorage oGlobalStore, String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey, UnitStatusPoison.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        UnitStatusPoison oStatus = new UnitStatusPoison();

        // Load parent info
        oStatus.LoadUnitStatusData( oObjectStore, oGlobalStore );

        oStatus.mDamagePerTurn = oObjectStore.getInt( M_DPT );

        return oStatus;
    }

    public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
    {
        return loadState( oGlobalStore, strObjKey );
    }
}
