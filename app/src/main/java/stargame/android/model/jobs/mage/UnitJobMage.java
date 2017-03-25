package stargame.android.model.jobs.mage;

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

public class UnitJobMage extends UnitJob
{
    private UnitJobMage()
    {
        super();
    }

    private UnitJobMage( Unit oUnit, Resources oResources )
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

    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        super.SaveUnitJobData( oObjectStore, oGlobalStore );
    }

    public static UnitJobMage loadState( IStorage oGlobalStore,
                                         String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey, UnitJobMage.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        UnitJobMage oUnitJob = new UnitJobMage();

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
    private static class UnitJobMageCreator implements IJobCreator
    {
        public UnitJob JobCreate( Unit oUnit, JobType eType,
                                  Resources oResources )
        {
            if ( eType == JobType.TYPE_MAGE )
            {
                return new UnitJobMage( oUnit, oResources );
            }
            return null;
        }
    }
}
