package stargame.android.model.jobs.soldier;

import android.content.res.Resources;

import java.util.Vector;

import stargame.android.model.Battle;
import stargame.android.model.BattleAction;
import stargame.android.model.BattleUnit;
import stargame.android.model.Unit;
import stargame.android.model.UnitJob;
import stargame.android.model.jobs.IJobCreator;
import stargame.android.model.jobs.JobType;
import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.storage.SavableHelper;

public class UnitJobSoldier extends UnitJob
{
    private UnitJobSoldier()
    {
        super();
    }

    private UnitJobSoldier( Unit oUnit, Resources oResources )
    {
        super( oUnit );

        mJobType = JobType.TYPE_SOLDIER;
        LoadAttributes( oResources );
    }

    @Override
    public Vector< BattleAction > GetJobBattleActions( Battle oBattle,
                                                       BattleUnit oUnit )
    {
        return new Vector< BattleAction >();
    }

    public static IJobCreator GetCreator()
    {
        return new UnitJobSoldierCreator();
    }

    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        super.SaveUnitJobData( oObjectStore, oGlobalStore );
    }

    public static UnitJobSoldier loadState( IStorage oGlobalStore,
                                            String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey, UnitJobSoldier.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        UnitJobSoldier oUnitJob = new UnitJobSoldier();

        oUnitJob.LoadUnitJobData( oObjectStore, oGlobalStore );

        return oUnitJob;
    }

    public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
    {
        return loadState( oGlobalStore, strObjKey );
    }

    /**
     * The job factory instance
     */
    private static class UnitJobSoldierCreator implements IJobCreator
    {
        public UnitJob JobCreate( Unit oUnit, JobType eType,
                                  Resources oResources )
        {
            if ( eType == JobType.TYPE_SOLDIER )
            {
                return new UnitJobSoldier( oUnit, oResources );
            }
            return null;
        }
    }
}
