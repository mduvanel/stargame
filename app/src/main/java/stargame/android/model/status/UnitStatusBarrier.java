package stargame.android.model.status;

import stargame.android.R;
import stargame.android.model.BattleUnit;
import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;
import android.os.Bundle;


public class UnitStatusBarrier extends UnitStatus
{
	int mTotalAbsorbCapacity;

	private final static String M_TOTAL_ABSORB = "Total";

	int mCurrentAbsorbCapacity;

	private final static String M_CURRENT_ABSORB = "Current";

	/** Empty constructor for loadState() method */
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

	/** Calculates how much damage is absorbed and returns the rest */
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

	public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
	{
		// Save parent info
		SaveUnitStatusData( oObjectMap, oGlobalMap );

		oObjectMap.putInt( M_TOTAL_ABSORB, mTotalAbsorbCapacity );
		oObjectMap.putInt( M_CURRENT_ABSORB, mCurrentAbsorbCapacity );
	}

	public static UnitStatusBarrier loadState( Bundle oGlobalMap, String strObjKey )
	{
		Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey, UnitStatusBarrier.class.getName() );

		if ( oObjectBundle == null )
		{
			return null;
		}

		UnitStatusBarrier oStatus = new UnitStatusBarrier();

		// Load parent info
		oStatus.LoadUnitStatusData( oObjectBundle, oGlobalMap );

		oStatus.mTotalAbsorbCapacity = oObjectBundle.getInt( M_TOTAL_ABSORB );
		oStatus.mCurrentAbsorbCapacity = oObjectBundle.getInt( M_CURRENT_ABSORB );

		return oStatus;
	}

	public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
	{
		return loadState( oGlobalMap, strObjKey );
	}

	@Override
	protected void MergeStatusInternal( UnitStatus oStatus )
	{
		UnitStatusBarrier oStatusBarrier = ( UnitStatusBarrier )oStatus;

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
