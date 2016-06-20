package stargame.android.model.status;

import java.util.Observable;

import stargame.android.model.BattleUnit;
import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;

import android.content.Context;
import android.os.Bundle;

public abstract class UnitStatus extends Observable implements ISavable
{
	int mType;

	private final static String M_TYPE = "Type";

	/** The unit having this status */
	BattleUnit mUnit;

	private final static String M_UNIT = "Unit";

	/** Remaining turns for this status */
	int mTurnsLeft;

	private final static String M_TURNS = "Turns";

	/** Name of this status */
	String mName;

	private final static String M_NAME = "Name";

	public UnitStatus()
	{
		mTurnsLeft = -1;
		mUnit = null;
	}

	public UnitStatus( int iTurns, BattleUnit oUnit )
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
		if ( mTurnsLeft == 0 )
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	protected void SaveUnitStatusData( Bundle oObjectMap, Bundle oGlobalMap )
	{
		oObjectMap.putInt( M_TYPE, mType );
		oObjectMap.putInt( M_TURNS, mTurnsLeft );
		oObjectMap.putString( M_NAME, mName );

		String strObjKey = SavableHelper.saveInMap( mUnit, oGlobalMap );
		oObjectMap.putString( M_UNIT, strObjKey );
	}

	protected void LoadUnitStatusData( Bundle oObjectMap, Bundle oGlobalMap )
	{
		mType = oObjectMap.getInt( M_TYPE );
		mTurnsLeft = oObjectMap.getInt( M_TURNS );
		mName = oObjectMap.getString( M_NAME );

		String strKey = oObjectMap.getString( M_UNIT );
		mUnit = BattleUnit.loadState( oGlobalMap, strKey );
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

	/** Abstract method allowing 2 Status of the same type to be merged */
	protected abstract void MergeStatusInternal( UnitStatus oStatus );
}
