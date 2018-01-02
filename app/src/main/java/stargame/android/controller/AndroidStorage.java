package stargame.android.controller;

import android.os.Bundle;

import java.util.Set;

import stargame.android.storage.IStorage;

/**
 * author: Duduche
 */

class AndroidStorage implements IStorage
{
    private Bundle mBundle;

    AndroidStorage( Bundle oBundle ) {
        mBundle = oBundle;
    }

    @Override
    public IStorage getStore( String strKey )
    {
        Bundle oBundle = mBundle.getBundle( strKey );
        if ( null == oBundle )
        {
            return null;
        }
        return new AndroidStorage( oBundle );
    }

    @Override
    public void putStore( String strKey, IStorage oStore )
    {
        AndroidStorage oStorage = ( AndroidStorage )oStore;
        mBundle.putBundle( strKey, oStorage.mBundle );
    }

    @Override
    public void putBoolean( String strKey, boolean bValue )
    {
        mBundle.putBoolean( strKey, bValue );
    }

    @Override
    public void putInt( String strKey, int iValue )
    {
        mBundle.putInt( strKey, iValue );
    }

    @Override
    public void putDouble( String strKey, double dValue )
    {
        mBundle.putDouble( strKey, dValue );
    }

    @Override
    public void putIntArray( String strKey, int[] aiValues )
    {
        mBundle.putIntArray( strKey, aiValues );
    }

    @Override
    public void putString( String strKey, String strValue )
    {
        mBundle.putString( strKey, strValue );
    }

    @Override
    public void putStringArray( String strKey, String[] astrValues )
    {
        mBundle.putStringArray( strKey, astrValues );
    }

    @Override
    public String[] getStringArray( String strKey )
    {
        return mBundle.getStringArray( strKey );
    }

    @Override
    public String getString( String strKey )
    {
        return mBundle.getString( strKey );
    }

    @Override
    public boolean getBoolean( String strKey )
    {
        return mBundle.getBoolean( strKey );
    }

    @Override
    public int getInt( String strKey )
    {
        return mBundle.getInt( strKey );
    }

    @Override
    public int[] getIntArray( String strKey )
    {
        return mBundle.getIntArray( strKey );
    }

    @Override
    public double getDouble( String strKey )
    {
        return mBundle.getDouble( strKey );
    }

    @Override
    public boolean containsKey( String strKey )
    {
        return mBundle.containsKey( strKey );
    }

    @Override
    public Set< String > keySet()
    {
        return mBundle.keySet();
    }
}
