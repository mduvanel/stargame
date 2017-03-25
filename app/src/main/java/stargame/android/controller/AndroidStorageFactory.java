package stargame.android.controller;

import android.os.Bundle;

import stargame.android.storage.IStorage;
import stargame.android.storage.IStorageFactory;

/**
 * IStorageFactory implementation for production
 * author: Duduche
 */

public class AndroidStorageFactory implements IStorageFactory
{
    @Override
    public IStorage buildStorage()
    {
        return new AndroidStorage(new Bundle());
    }
}
