package stargame.android.model.jobs.barbarian;

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

public class UnitJobBarbarian extends UnitJob
{
    private UnitJobBarbarian()
    {
        super();
    }

    private UnitJobBarbarian( Unit oUnit, Resources oResources )
    {
        super( oUnit );

        mJobType = JobType.TYPE_BARBARIAN;
        LoadAttributes( oResources );
    }

    @Override
    public Vector< BattleAction > GetJobBattleActions( Battle oBattle,
                                                       BattleUnit oUnit )
    {
        Vector< BattleAction > vecActions = new Vector< BattleAction >();
        vecActions.add( new BattleActionWhirlwind( oBattle, oUnit ) );
        vecActions.add( new BattleActionStun( oBattle, oUnit ) );
        return vecActions;
    }

    public static IJobCreator GetCreator()
    {
        return new UnitJobBarbarianCreator();
    }

    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        super.SaveUnitJobData( oObjectStore, oGlobalStore );
    }

    public static UnitJobBarbarian loadState( IStorage oGlobalStore,
                                              String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey, UnitJobBarbarian.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        UnitJobBarbarian oUnitJob = new UnitJobBarbarian();

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
    private static class UnitJobBarbarianCreator implements IJobCreator
    {
        public UnitJob JobCreate( Unit oUnit, JobType eType, Resources oResources )
        {
            if ( eType == JobType.TYPE_BARBARIAN )
            {
                return new UnitJobBarbarian( oUnit, oResources );
            }
            return null;
        }
    }
}
