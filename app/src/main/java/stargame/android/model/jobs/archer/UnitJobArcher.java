package stargame.android.model.jobs.archer;

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

public class UnitJobArcher extends UnitJob
{
	private UnitJobArcher()
	{
		super();
	}

	public UnitJobArcher( Unit oUnit, Resources oResources )
	{
		super( oUnit );

		mJobType = JobType.TYPE_ARCHER;
		LoadAttributes( oResources );
	}

	public Vector< BattleAction > GetJobBattleActions( Battle oBattle, BattleUnit oUnit )
	{
		return new Vector< BattleAction >();
	}

	public static IJobCreator GetCreator()
	{
		return new UnitJobArcherCreator();
	}

	public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
	{
		super.SaveUnitJobData( oObjectMap, oGlobalMap );
	}

	public static UnitJobArcher loadState( Bundle oGlobalMap, String strObjKey )
	{
		Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey, UnitJobArcher.class.getName() );

		if ( oObjectBundle == null )
		{
			return null;
		}

		UnitJobArcher oUnitJob = new UnitJobArcher();

		oUnitJob.LoadUnitJobData( oObjectBundle, oGlobalMap );

		return oUnitJob;
	}

	public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
	{
		return loadState( oGlobalMap, strObjKey );
	}

	/** The job factory instance */
	private static class UnitJobArcherCreator implements IJobCreator
	{
		public UnitJob JobCreate( Unit oUnit, JobType eType, Resources oResources )
		{
			if ( eType == JobType.TYPE_ARCHER )
			{
				return new UnitJobArcher( oUnit, oResources );
			}
			return null;
		}	
	}
}
