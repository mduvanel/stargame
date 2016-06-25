package stargame.android.model.jobs.rogue;

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

public class UnitJobRogue extends UnitJob
{
    private UnitJobRogue()
    {
        super();
    }

    public UnitJobRogue( Unit oUnit, Resources oResources )
    {
        super( oUnit );

        mJobType = JobType.TYPE_ROGUE;
        LoadAttributes( oResources );
    }

    @Override
    public Vector< BattleAction > GetJobBattleActions( Battle oBattle, BattleUnit oUnit )
    {
        Vector< BattleAction > vecActions = new Vector< BattleAction >();
        vecActions.add( new BattleActionVanish( oBattle, oUnit ) );
        return vecActions;
    }

    public static IJobCreator GetCreator()
    {
        return new UnitJobRogueCreator();
    }

    public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
    {
        super.SaveUnitJobData( oObjectMap, oGlobalMap );
    }

    public static UnitJobRogue loadState( Bundle oGlobalMap, String strObjKey )
    {
        Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey,
                                                             UnitJobRogue.class.getName() );

        if ( oObjectBundle == null )
        {
            return null;
        }

        UnitJobRogue oUnitJob = new UnitJobRogue();

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
    private static class UnitJobRogueCreator implements IJobCreator
    {
        public UnitJob JobCreate( Unit oUnit, JobType eType, Resources oResources )
        {
            if ( eType == JobType.TYPE_ROGUE )
            {
                return new UnitJobRogue( oUnit, oResources );
            }
            return null;
        }
    }
}
