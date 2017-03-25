package stargame.android.model.status;

import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.util.Logger;

public class UnitStatusFactory implements ISavable
{
    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        // Nothing to do here
    }

    public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
    {
        return loadState( oGlobalStore, strObjKey );
    }

    public UnitStatus loadState( IStorage oGlobalStore, String strObjKey )
    {
        // attempt to create a UnitStatus out of all known UnitStatus subclasses
        UnitStatus oStatus;

        if ( ( oStatus = UnitStatusBarrier.loadState( oGlobalStore, strObjKey ) ) != null )
        {
            return oStatus;
        }
        if ( ( oStatus = UnitStatusInvisible.loadState( oGlobalStore, strObjKey ) ) != null )
        {
            return oStatus;
        }
        if ( ( oStatus = UnitStatusPoison.loadState( oGlobalStore, strObjKey ) ) != null )
        {
            return oStatus;
        }

        // Log the event, it is probably happening because a factory has been forgotten...
        String strError = String.format(
                "Failed to create UnitStatus '%s' from Bundle", strObjKey );
        Logger.e( strError );
        throw new UnknownError( strError );
    }
}
