package stargame.android.model.jobs.mage;

import android.content.res.Resources;
import android.os.Bundle;

import java.util.Vector;

import stargame.android.model.Battle;
import stargame.android.model.BattleAction;
import stargame.android.model.BattleUnit;
import stargame.android.model.Unit;
import stargame.android.model.UnitJob;
import stargame.android.model.jobs.IJobCreator;
import stargame.android.model.jobs.JobType;
import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;

public class UnitJobMage extends UnitJob
{
    private UnitJobMage()
    {
        super();
    }

    public UnitJobMage( Unit oUnit, Resources oResources )
    {
        super( oUnit );

        mJobType = JobType.TYPE_MAGE;
        LoadAttributes( oResources );
    }

    @Override
    public Vector< BattleAction > GetJobBattleActions( Battle oBattle, BattleUnit oUnit )
    {
        Vector< BattleAction > vecActions = new Vector< BattleAction >();
        vecActions.add( new BattleActionFireball( oBattle, oUnit ) );
        return vecActions;
    }

    public static IJobCreator GetCreator()
    {
        return new UnitJobMageCreator();
    }

    public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
    {
        super.SaveUnitJobData( oObjectMap, oGlobalMap );
    }

    public static UnitJobMage loadState( Bundle oGlobalMap, String strObjKey )
    {
        Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey,
                                                             UnitJobMage.class.getName() );

        if ( oObjectBundle == null )
        {
            return null;
        }

        UnitJobMage oUnitJob = new UnitJobMage();

        oUnitJob.LoadUnitJobData( oObjectBundle, oGlobalMap );

        return oUnitJob;
    }

    public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
    {
        return loadState( oGlobalMap, strObjKey );
    }

    /**
     * The job factory instance
     */
    private static class UnitJobMageCreator implements IJobCreator
    {
        public UnitJob JobCreate( Unit oUnit, JobType eType, Resources oResources )
        {
            if ( eType == JobType.TYPE_MAGE )
            {
                return new UnitJobMage( oUnit, oResources );
            }
            return null;
        }
    }
}
