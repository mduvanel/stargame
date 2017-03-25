package stargame.android.model.jobs.archer;

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

public class UnitJobArcher extends UnitJob
{
    private UnitJobArcher()
    {
        super();
    }

    private UnitJobArcher( Unit oUnit, Resources oResources )
    {
        super( oUnit );

        mJobType = JobType.TYPE_ARCHER;
        LoadAttributes( oResources );
    }

    public Vector< BattleAction > GetJobBattleActions( Battle oBattle,
                                                       BattleUnit oUnit )
    {
        return new Vector< BattleAction >();
    }

    public static IJobCreator GetCreator()
    {
        return new UnitJobArcherCreator();
    }

    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        super.SaveUnitJobData( oObjectStore, oGlobalStore );
    }

    public static UnitJobArcher loadState( IStorage oGlobalStore,
                                           String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey, UnitJobArcher.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        UnitJobArcher oUnitJob = new UnitJobArcher();

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
    private static class UnitJobArcherCreator implements IJobCreator
    {
        public UnitJob JobCreate( Unit oUnit, JobType eType,
                                  Resources oResources )
        {
            if ( eType == JobType.TYPE_ARCHER )
            {
                return new UnitJobArcher( oUnit, oResources );
            }
            return null;
        }
    }
}
