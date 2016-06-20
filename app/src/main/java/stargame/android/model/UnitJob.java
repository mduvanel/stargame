package stargame.android.model;

import java.util.Vector;

import android.content.res.Resources;
import android.os.Bundle;

import stargame.android.model.jobs.JobType;
import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;

/**
 *  Class representing a unit with a job.
 * @author Duduche
 */
public abstract class UnitJob implements ISavable
{
	/** The unit that has this job instance */
	protected Unit mUnit;

	private static final String M_UNIT = "Unit";

	/** The type of job */
	protected JobType mJobType;

	private static final String M_JOB_TYPE = "JobType";
	
	/** The passive increase in stats (just for having the job) */
	protected Attributes mAttributes;

	private static final String M_ATTRIBS = "Attributes";

	/** 
	 * The bonuses linked to the level-up (actual increase in stats 
	 * at level-up, when levelling up in a specific job)
	 */
	protected Attributes mLevelUpAttributes;

	private static final String M_LU_ATTRIBS = "LUAttributes";

	protected UnitJob() {}

	protected UnitJob( Unit unit )
	{
		mUnit = unit;
		mAttributes = new Attributes();
		mLevelUpAttributes = new Attributes();
	}

	public JobType GetJobType()
	{
		return mJobType;
	}

	public void LoadAttributes( Resources oResources )
	{
		mAttributes.LoadFromResources( oResources, mJobType.toString() + "_base_stats" );
		mLevelUpAttributes.LoadFromResources( oResources, mJobType.toString() + "_levelup_stats" );
	}

	public Attributes GetAttributes()
	{
		return mAttributes;
	}

	void LevelUp()
	{
		mUnit.mAttributes.AddAttributes( mLevelUpAttributes );
		mUnit.mLevel += 1;
	}

	void AddXP( int iXPAmount )
	{
		mUnit.mExperience += iXPAmount;

		if ( mUnit.mExperience >= 100 )
		{
			LevelUp();
			mUnit.mExperience -= 100;
		}
	}

	protected void SaveUnitJobData( Bundle oObjectMap, Bundle oGlobalMap )
	{
		oObjectMap.putInt( M_JOB_TYPE, mJobType.ordinal() );

		String strObjKey = SavableHelper.saveInMap( mUnit, oGlobalMap );
		oObjectMap.putString( M_UNIT, strObjKey );

		strObjKey = SavableHelper.saveInMap( mAttributes, oGlobalMap );
		oObjectMap.putString( M_ATTRIBS, strObjKey );

		strObjKey = SavableHelper.saveInMap( mLevelUpAttributes, oGlobalMap );
		oObjectMap.putString( M_LU_ATTRIBS, strObjKey );
	}

	protected void LoadUnitJobData( Bundle oObjectMap, Bundle oGlobalMap )
	{
		mJobType = JobType.values()[ oObjectMap.getInt( M_JOB_TYPE ) ];

		String strKey = oObjectMap.getString( M_UNIT );
		mUnit = Unit.loadState( oGlobalMap, strKey );

		strKey = oObjectMap.getString( M_ATTRIBS );
		mAttributes = Attributes.loadState( oGlobalMap, strKey );

		strKey = oObjectMap.getString( M_LU_ATTRIBS );
		mLevelUpAttributes = Attributes.loadState( oGlobalMap, strKey );
	}

	public abstract Vector< BattleAction > GetJobBattleActions( Battle oBattle, BattleUnit oUnit );
}
