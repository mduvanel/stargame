package stargame.android.model;

import android.os.Bundle;
import stargame.android.model.jobs.priest.BattleActionBarrier;
import stargame.android.model.jobs.priest.BattleActionHeal;
import stargame.android.storage.ISavable;
import stargame.android.util.Logger;

public class BattleActionFactory implements ISavable
{
	public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
	{
		// Nothing to do here
	}

	public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
	{
		return loadState( oGlobalMap, strObjKey );
	}

	public BattleAction loadState( Bundle oGlobalMap, String strObjKey )
	{
		// attempt to create a BattleAction out of all known BattleAction subclasses
		BattleAction oAction = null;

		// Base actions
		if ( ( oAction = BattleActionAttack.loadState( oGlobalMap, strObjKey ) ) != null ) { return oAction; }
		if ( ( oAction = BattleActionMove.loadState( oGlobalMap, strObjKey ) ) != null ) { return oAction; }

		// Priest actions
		if ( ( oAction = BattleActionBarrier.loadState( oGlobalMap, strObjKey ) ) != null ) { return oAction; }
		if ( ( oAction = BattleActionHeal.loadState( oGlobalMap, strObjKey ) ) != null ) { return oAction; }

		// TODO: finish

		// Log the event, it is probably happening because a factory has been forgotten...
		Logger.e( String.format( "Failed to create BattleAction '%s' from Bundle", strObjKey ) );
		return oAction;
	}
}
