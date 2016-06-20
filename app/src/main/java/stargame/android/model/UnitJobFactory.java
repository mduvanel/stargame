package stargame.android.model;

import android.os.Bundle;
import stargame.android.model.jobs.archer.UnitJobArcher;
import stargame.android.model.jobs.barbarian.UnitJobBarbarian;
import stargame.android.model.jobs.mage.UnitJobMage;
import stargame.android.model.jobs.oracle.UnitJobOracle;
import stargame.android.model.jobs.priest.UnitJobPriest;
import stargame.android.model.jobs.rogue.UnitJobRogue;
import stargame.android.model.jobs.soldier.UnitJobSoldier;
import stargame.android.storage.ISavable;
import stargame.android.util.Logger;

public class UnitJobFactory implements ISavable
{
	public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
	{
		// Nothing to do here
	}

	public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
	{
		return loadState( oGlobalMap, strObjKey );
	}

	public UnitJob loadState( Bundle oGlobalMap, String strObjKey )
	{
		// attempt to create a UnitJob out of all known UnitJob subclasses
		UnitJob oJob = null;

		if ( ( oJob = UnitJobArcher.loadState( oGlobalMap, strObjKey ) ) != null ) { return oJob; }
		if ( ( oJob = UnitJobBarbarian.loadState( oGlobalMap, strObjKey ) ) != null ) { return oJob; }
		if ( ( oJob = UnitJobMage.loadState( oGlobalMap, strObjKey ) ) != null ) { return oJob; }
		if ( ( oJob = UnitJobPriest.loadState( oGlobalMap, strObjKey ) ) != null ) { return oJob; }
		if ( ( oJob = UnitJobRogue.loadState( oGlobalMap, strObjKey ) ) != null ) { return oJob; }
		if ( ( oJob = UnitJobSoldier.loadState( oGlobalMap, strObjKey ) ) != null ) { return oJob; }
		if ( ( oJob = UnitJobOracle.loadState( oGlobalMap, strObjKey ) ) != null ) { return oJob; }

		// Log the event, it is probably happening because a factory has been forgotten...
		Logger.e( String.format( "Failed to create UnitJob '%s' from Bundle", strObjKey ) );
		return oJob;
	}
}
