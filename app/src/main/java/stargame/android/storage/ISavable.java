package stargame.android.storage;

import android.os.Bundle;

/**
 * ISavable is the interface implemented by Objects that can be 
 * saved to / loaded from a Bundle
 * 
 * @author Duduche
 */
public interface ISavable
{
	public abstract void saveState( Bundle oObjectMap, Bundle oGlobalMap );

	public abstract ISavable createInstance( Bundle oGlobalMap, String strObjKey );
}
