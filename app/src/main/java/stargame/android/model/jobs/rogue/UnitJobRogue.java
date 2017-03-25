package stargame.android.model.jobs.rogue;

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

public class UnitJobRogue extends UnitJob
{
    private UnitJobRogue()
    {
        super();
    }

    private UnitJobRogue( Unit oUnit, Resources oResources )
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

    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        super.SaveUnitJobData( oObjectStore, oGlobalStore );
    }

    public static UnitJobRogue loadState( IStorage oGlobalStore, String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey, UnitJobRogue.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        UnitJobRogue oUnitJob = new UnitJobRogue();

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
    private static class UnitJobRogueCreator implements IJobCreator
    {
        public UnitJob JobCreate( Unit oUnit, JobType eType,
                                  Resources oResources )
        {
            if ( eType == JobType.TYPE_ROGUE )
            {
                return new UnitJobRogue( oUnit, oResources );
            }
            return null;
        }
    }
}
