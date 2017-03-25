package stargame.android.model.status;

import android.content.Context;

import java.util.Observable;

import stargame.android.model.BattleUnit;
import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.storage.SavableHelper;

public abstract class UnitStatus extends Observable implements ISavable
{
    int mType;

    private final static String M_TYPE = "Type";

    /**
     * The unit having this status
     */
    BattleUnit mUnit;

    private final static String M_UNIT = "Unit";

    /**
     * Remaining turns for this status
     */
    int mTurnsLeft;

    private final static String M_TURNS = "Turns";

    /**
     * Name of this status
     */
    private String mName;

    private final static String M_NAME = "Name";

    UnitStatus()
    {
        mTurnsLeft = -1;
        mUnit = null;
    }

    UnitStatus( int iTurns, BattleUnit oUnit )
    {
        mTurnsLeft = iTurns;
        mUnit = oUnit;
    }

    public boolean TurnPassed()
    {
        // Notify observers that Status might have evolved
        setChanged();
        notifyObservers();

        if ( mTurnsLeft < 0 )
        {
            return false; // unlimited duration effect
        }

        mTurnsLeft--;
        return ( mTurnsLeft == 0 );
    }

    void SaveUnitStatusData( IStorage oObjectStore, IStorage oGlobalStore )
    {
        oObjectStore.putInt( M_TYPE, mType );
        oObjectStore.putInt( M_TURNS, mTurnsLeft );
        oObjectStore.putString( M_NAME, mName );

        String strObjKey = SavableHelper.saveInStore( mUnit, oGlobalStore );
        oObjectStore.putString( M_UNIT, strObjKey );
    }

    void LoadUnitStatusData( IStorage oObjectStore, IStorage oGlobalStore )
    {
        mType = oObjectStore.getInt( M_TYPE );
        mTurnsLeft = oObjectStore.getInt( M_TURNS );
        mName = oObjectStore.getString( M_NAME );

        String strKey = oObjectStore.getString( M_UNIT );
        mUnit = BattleUnit.loadState( oGlobalStore, strKey );
    }

    public int GetType()
    {
        return mType;
    }

    /**
     * Return the name of the status. If a valid context is provided,
     * it can be used to retrieve a string constant from it.
     */
    public String GetName( Context oContext )
    {
        if ( null != oContext && null == mName )
        {
            mName = oContext.getString( mType );
        }

        return mName;
    }

    public void MergeStatus( UnitStatus oStatus )
    {
        if ( oStatus.GetType() == this.GetType() )
        {
            MergeStatusInternal( oStatus );
        }
    }

    /**
     * Abstract method allowing 2 Status of the same type to be merged
     */
    protected abstract void MergeStatusInternal( UnitStatus oStatus );
}
