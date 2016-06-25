package stargame.android.model.jobs;

import android.content.res.Resources;

import java.util.Vector;

import stargame.android.model.Unit;
import stargame.android.model.UnitJob;
import stargame.android.model.jobs.archer.UnitJobArcher;
import stargame.android.model.jobs.barbarian.UnitJobBarbarian;
import stargame.android.model.jobs.mage.UnitJobMage;
import stargame.android.model.jobs.oracle.UnitJobOracle;
import stargame.android.model.jobs.priest.UnitJobPriest;
import stargame.android.model.jobs.rogue.UnitJobRogue;
import stargame.android.model.jobs.soldier.UnitJobSoldier;

public class JobFactory
{
    private Vector< IJobCreator > mCreators;

    private static JobFactory mInstance = null;

    private JobFactory()
    {
        mCreators = new Vector< IJobCreator >();
    }

    public static JobFactory GetInstance()
    {
        if ( null == mInstance )
        {
            mInstance = new JobFactory();
            // Register all known JobCreators
            mInstance.RegisterJob( UnitJobSoldier.GetCreator() );
            mInstance.RegisterJob( UnitJobArcher.GetCreator() );
            mInstance.RegisterJob( UnitJobMage.GetCreator() );
            mInstance.RegisterJob( UnitJobPriest.GetCreator() );
            mInstance.RegisterJob( UnitJobBarbarian.GetCreator() );
            mInstance.RegisterJob( UnitJobRogue.GetCreator() );
            mInstance.RegisterJob( UnitJobOracle.GetCreator() );
        }
        return mInstance;
    }

    private void RegisterJob( IJobCreator oJob )
    {
        mCreators.add( oJob );
    }

    public UnitJob JobCreate( Unit oUnit, JobType eJob, Resources oResources )
    {
        UnitJob oJob = null;
        for ( IJobCreator oFactory : mCreators )
        {
            oJob = oFactory.JobCreate( oUnit, eJob, oResources );
            if ( null != oJob )
            {
                break;
            }
        }

        return oJob;
    }
}
