package stargame.android.model.jobs.oracle;

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

public class UnitJobOracle extends UnitJob
{
    private UnitJobOracle()
    {
        super();
    }

    private UnitJobOracle( Unit oUnit, Resources oResources )
    {
        super( oUnit );

        mJobType = JobType.TYPE_ORACLE;
        LoadAttributes( oResources );
    }

    @Override
    public Vector< BattleAction > GetJobBattleActions( Battle oBattle, BattleUnit oUnit )
    {
        return new Vector< BattleAction >();
    }

    public static IJobCreator GetCreator()
    {
        return new UnitJobOracleCreator();
    }

    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        super.SaveUnitJobData( oObjectStore, oGlobalStore );
    }

    public static UnitJobOracle loadState( IStorage oGlobalStore,
                                           String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey, UnitJobOracle.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        UnitJobOracle oUnitJob = new UnitJobOracle();

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
    private static class UnitJobOracleCreator implements IJobCreator
    {
        public UnitJob JobCreate( Unit oUnit, JobType eType,
                                  Resources oResources )
        {
            if ( eType == JobType.TYPE_ORACLE )
            {
                return new UnitJobOracle( oUnit, oResources );
            }
            return null;
        }
    }
}
