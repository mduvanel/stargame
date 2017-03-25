package stargame.android.storage;

/**
 * IStorageFactory is an abstract factory that build IStorage instances
 * author: Duduche
 */

public interface IStorageFactory
{
    IStorage buildStorage();
}
