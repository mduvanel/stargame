package stargame.android.model;

import stargame.android.model.jobs.archer.UnitJobArcher;
import stargame.android.model.jobs.barbarian.UnitJobBarbarian;
import stargame.android.model.jobs.mage.UnitJobMage;
import stargame.android.model.jobs.oracle.UnitJobOracle;
import stargame.android.model.jobs.priest.UnitJobPriest;
import stargame.android.model.jobs.rogue.UnitJobRogue;
import stargame.android.model.jobs.soldier.UnitJobSoldier;
import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.util.Logger;

class UnitJobFactory implements ISavable
{
    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        // Nothing to do here
    }

    public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
    {
        return loadState( oGlobalStore, strObjKey );
    }

    public UnitJob loadState( IStorage oGlobalStore, String strObjKey )
    {
        // attempt to create a UnitJob out of all known UnitJob subclasses
        UnitJob oJob;

        if ( ( oJob = UnitJobArcher.loadState( oGlobalStore, strObjKey ) ) != null )
        {
            return oJob;
        }
        if ( ( oJob = UnitJobBarbarian.loadState( oGlobalStore, strObjKey ) ) != null )
        {
            return oJob;
        }
        if ( ( oJob = UnitJobMage.loadState( oGlobalStore, strObjKey ) ) != null )
        {
            return oJob;
        }
        if ( ( oJob = UnitJobPriest.loadState( oGlobalStore, strObjKey ) ) != null )
        {
            return oJob;
        }
        if ( ( oJob = UnitJobRogue.loadState( oGlobalStore, strObjKey ) ) != null )
        {
            return oJob;
        }
        if ( ( oJob = UnitJobSoldier.loadState( oGlobalStore, strObjKey ) ) != null )
        {
            return oJob;
        }
        if ( ( oJob = UnitJobOracle.loadState( oGlobalStore, strObjKey ) ) != null )
        {
            return oJob;
        }

        // Log the event, it is probably happening because a factory has been forgotten...
        Logger.e( String.format( "Failed to create UnitJob '%s' from Bundle", strObjKey ) );
        return null;
    }
}
