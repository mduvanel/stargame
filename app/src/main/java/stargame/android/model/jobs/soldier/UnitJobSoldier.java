package stargame.android.model.jobs.soldier;

import java.util.Vector;

import android.content.res.Resources;
import android.os.Bundle;

import stargame.android.model.Battle;
import stargame.android.model.BattleAction;
import stargame.android.model.BattleUnit;
import stargame.android.model.Unit;
import stargame.android.model.UnitJob;
import stargame.android.model.jobs.IJobCreator;
import stargame.android.model.jobs.JobType;
import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;

public class UnitJobSoldier extends UnitJob
{
	private UnitJobSoldier()
	{
		super();
	}

	public UnitJobSoldier( Unit oUnit, Resources oResources )
	{
		super( oUnit );
		
		mJobType = JobType.TYPE_SOLDIER;
		LoadAttributes( oResources );
	}

	@Override
	public Vector< BattleAction > GetJobBattleActions( Battle oBattle, BattleUnit oUnit )
	{
		return new Vector< BattleAction >();
	}

	public static IJobCreator GetCreator()
	{
		return new UnitJobSoldierCreator();
	}

	public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
	{
		super.SaveUnitJobData( oObjectMap, oGlobalMap );
	}

	public static UnitJobSoldier loadState( Bundle oGlobalMap, String strObjKey )
	{
		Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey, UnitJobSoldier.class.getName() );

		if ( oObjectBundle == null )
		{
			return null;
		}

		UnitJobSoldier oUnitJob = new UnitJobSoldier();

		oUnitJob.LoadUnitJobData( oObjectBundle, oGlobalMap );

		return oUnitJob;
	}

	public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
	{
		return loadState( oGlobalMap, strObjKey );
	}

	/** The job factory instance */
	private static class UnitJobSoldierCreator implements IJobCreator
	{
		public UnitJob JobCreate( Unit oUnit, JobType eType, Resources oResources )
		{
			if ( eType == JobType.TYPE_SOLDIER )
			{
				return new UnitJobSoldier( oUnit, oResources );
			}
			return null;
		}	
	}
}
