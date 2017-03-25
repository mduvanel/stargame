package stargame.android.model;

import stargame.android.model.jobs.priest.BattleActionBarrier;
import stargame.android.model.jobs.priest.BattleActionHeal;
import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.util.Logger;

class BattleActionFactory implements ISavable
{
    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        // Nothing to do here
    }

    public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
    {
        return loadState( oGlobalStore, strObjKey );
    }

    public BattleAction loadState( IStorage oGlobalStore, String strObjKey )
    {
        // attempt to create a BattleAction out of all known BattleAction subclasses
        BattleAction oAction;

        // Base actions
        if ( ( oAction = BattleActionAttack.loadState( oGlobalStore, strObjKey ) ) != null )
        {
            return oAction;
        }
        if ( ( oAction = BattleActionMove.loadState( oGlobalStore, strObjKey ) ) != null )
        {
            return oAction;
        }

        // Priest actions
        if ( ( oAction = BattleActionBarrier.loadState( oGlobalStore, strObjKey ) ) != null )
        {
            return oAction;
        }
        if ( ( oAction = BattleActionHeal.loadState( oGlobalStore, strObjKey ) ) != null )
        {
            return oAction;
        }

        // TODO: finish

        // Log the event, it is probably happening because a factory has been forgotten...
        Logger.e( String.format( "Failed to create BattleAction '%s' from Bundle", strObjKey ) );
        return null;
    }
}
