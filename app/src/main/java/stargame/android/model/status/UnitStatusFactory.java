package stargame.android.model.status;

import android.os.Bundle;

import stargame.android.storage.ISavable;
import stargame.android.util.Logger;

public class UnitStatusFactory implements ISavable
{
    public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
    {
        // Nothing to do here
    }

    public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
    {
        return loadState( oGlobalMap, strObjKey );
    }

    public UnitStatus loadState( Bundle oGlobalMap, String strObjKey )
    {
        // attempt to create a UnitStatus out of all known UnitStatus subclasses
        UnitStatus oStatus;

        if ( ( oStatus = UnitStatusBarrier.loadState( oGlobalMap, strObjKey ) ) != null )
        {
            return oStatus;
        }
        if ( ( oStatus = UnitStatusInvisible.loadState( oGlobalMap, strObjKey ) ) != null )
        {
            return oStatus;
        }
        if ( ( oStatus = UnitStatusPoison.loadState( oGlobalMap, strObjKey ) ) != null )
        {
            return oStatus;
        }

        // Log the event, it is probably happening because a factory has been forgotten...
        Logger.e( String.format( "Failed to create UnitStatus '%s' from Bundle", strObjKey ) );
        return null;
    }
}
