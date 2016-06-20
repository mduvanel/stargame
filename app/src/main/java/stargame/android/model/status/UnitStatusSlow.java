package stargame.android.model.status;

import stargame.android.R;
import stargame.android.model.BattleUnit;
import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;
import android.os.Bundle;

public class UnitStatusSlow extends UnitStatus
{
	/** How much the unit is slowed down */
	private int mSlowPercentage;

	private final static String M_SLOW_PERCENT = "SlowPercent";

	public int GetSlowPercentage()
	{
		return mSlowPercentage;
	}

	/** Empty constructor for loadState() method */
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
		UnitStatusSlow oStatusSlow = ( UnitStatusSlow )oStatus;

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


	public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
	{
		// Save parent info
		SaveUnitStatusData( oObjectMap, oGlobalMap );

		oObjectMap.putInt( M_SLOW_PERCENT, mSlowPercentage );
	}

	public static UnitStatusSlow loadState( Bundle oGlobalMap, String strObjKey )
	{
		Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey, UnitStatusSlow.class.getName() );

		if ( oObjectBundle == null )
		{
			return null;
		}

		UnitStatusSlow oStatus = new UnitStatusSlow();

		// Load parent info
		oStatus.LoadUnitStatusData( oObjectBundle, oGlobalMap );

		oStatus.mSlowPercentage = oObjectBundle.getInt( M_SLOW_PERCENT );

		return oStatus;
	}

	public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
	{
		return loadState( oGlobalMap, strObjKey );
	}
}
