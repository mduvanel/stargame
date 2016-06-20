package stargame.android.model.status;

import stargame.android.R;
import stargame.android.model.BattleUnit;
import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;
import android.os.Bundle;

public class UnitStatusInvisible extends UnitStatus
{
	/** Empty constructor for loadState() method */
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

	public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
	{
		// Save parent info
		SaveUnitStatusData( oObjectMap, oGlobalMap );
	}

	public static UnitStatusInvisible loadState( Bundle oGlobalMap, String strObjKey )
	{
		Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey, UnitStatusInvisible.class.getName() );

		if ( oObjectBundle == null )
		{
			return null;
		}

		UnitStatusInvisible oStatus = new UnitStatusInvisible();

		// Load parent info
		oStatus.LoadUnitStatusData( oObjectBundle, oGlobalMap );

		return oStatus;
	}

	public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
	{
		return loadState( oGlobalMap, strObjKey );
	}

	@Override
	protected void MergeStatusInternal( UnitStatus oStatus )
	{
		UnitStatusInvisible oStatusInv = ( UnitStatusInvisible )oStatus;

		// Pick the longest duration
		if ( oStatusInv.mTurnsLeft > mTurnsLeft )
		{
			mTurnsLeft = oStatusInv.mTurnsLeft;
		}
	}
}
