package stargame.android.storage;

import stargame.android.storage.IStorage;

/**
 * ISavable is the interface implemented by Objects that can be
 * saved to / loaded from an IStorage
 *
 * @author Duduche
 */
public interface ISavable
{
    public abstract void saveState( IStorage oObjectStorage, IStorage oGlobalStorage );

    public abstract ISavable createInstance( IStorage oGlobalStorage, String strObjKey );
}
