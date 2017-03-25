package stargame.android.model;

import android.content.res.Resources;

import java.util.Vector;

import stargame.android.model.jobs.JobType;
import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.storage.SavableHelper;

/**
 * Class representing a unit with a job.
 *
 * @author Duduche
 */
public abstract class UnitJob implements ISavable
{
    /**
     * The unit that has this job instance
     */
    private Unit mUnit;

    private static final String M_UNIT = "Unit";

    /**
     * The type of job
     */
    protected JobType mJobType;

    private static final String M_JOB_TYPE = "JobType";

    /**
     * The passive increase in stats (just for having the job)
     */
    private Attributes mAttributes;

    private static final String M_ATTRIBS = "Attributes";

    /**
     * The bonuses linked to the level-up (actual increase in stats
     * at level-up, when levelling up in a specific job)
     */
    private Attributes mLevelUpAttributes;

    private static final String M_LU_ATTRIBS = "LUAttributes";

    protected UnitJob()
    {
    }

    protected UnitJob( Unit unit )
    {
        mUnit = unit;
        mAttributes = new Attributes();
        mLevelUpAttributes = new Attributes();
    }

    JobType GetJobType()
    {
        return mJobType;
    }

    protected void LoadAttributes( Resources oResources )
    {
        mAttributes.LoadFromResources( oResources, mJobType.toString() + "_base_stats" );
        mLevelUpAttributes.LoadFromResources( oResources, mJobType.toString() + "_levelup_stats" );
    }

    Attributes GetAttributes()
    {
        return mAttributes;
    }

    private void LevelUp()
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

    protected void SaveUnitJobData( IStorage oObjectStore, IStorage oGlobalStore )
    {
        oObjectStore.putInt( M_JOB_TYPE, mJobType.ordinal() );

        String strObjKey = SavableHelper.saveInStore( mUnit, oGlobalStore );
        oObjectStore.putString( M_UNIT, strObjKey );

        strObjKey = SavableHelper.saveInStore( mAttributes, oGlobalStore );
        oObjectStore.putString( M_ATTRIBS, strObjKey );

        strObjKey = SavableHelper.saveInStore( mLevelUpAttributes,
                                               oGlobalStore );
        oObjectStore.putString( M_LU_ATTRIBS, strObjKey );
    }

    protected void LoadUnitJobData( IStorage oObjectStore, IStorage oGlobalStore )
    {
        mJobType = JobType.values()[ oObjectStore.getInt( M_JOB_TYPE ) ];

        String strKey = oObjectStore.getString( M_UNIT );
        mUnit = Unit.loadState( oGlobalStore, strKey );

        strKey = oObjectStore.getString( M_ATTRIBS );
        mAttributes = Attributes.loadState( oGlobalStore, strKey );

        strKey = oObjectStore.getString( M_LU_ATTRIBS );
        mLevelUpAttributes = Attributes.loadState( oGlobalStore, strKey );
    }

    public abstract Vector< BattleAction > GetJobBattleActions( Battle oBattle, BattleUnit oUnit );
}
