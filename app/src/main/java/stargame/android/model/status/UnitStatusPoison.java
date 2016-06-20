package stargame.android.model.status;

import stargame.android.R;
import stargame.android.model.BattleUnit;
import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;
import android.os.Bundle;

public class UnitStatusPoison extends UnitStatus
{
	int mDamagePerTurn;

	private final static String M_DPT = "DPT";

	/** Empty constructor for loadState() method */
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
		UnitStatusPoison oStatusPoison = ( UnitStatusPoison )oStatus;

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

	public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
	{
		// Save parent info
		SaveUnitStatusData( oObjectMap, oGlobalMap );

		oObjectMap.putInt( M_DPT, mDamagePerTurn );
	}

	public static UnitStatusPoison loadState( Bundle oGlobalMap, String strObjKey )
	{
		Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey, UnitStatusPoison.class.getName() );

		if ( oObjectBundle == null )
		{
			return null;
		}

		UnitStatusPoison oStatus = new UnitStatusPoison();

		// Load parent info
		oStatus.LoadUnitStatusData( oObjectBundle, oGlobalMap );

		oStatus.mDamagePerTurn = oObjectBundle.getInt( M_DPT );

		return oStatus;
	}

	public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
	{
		return loadState( oGlobalMap, strObjKey );
	}
}
