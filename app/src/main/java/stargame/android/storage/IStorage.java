package stargame.android.storage;

import java.util.Set;

/**
 * IStorage is an interface to hide dependency to Android's Bundle class
 * @author Duduche
 */

public interface IStorage
{
    IStorage getStore( String strKey );

    void putStore( String strKey, IStorage store );

    void putBoolean( String strKey, boolean bValue );

    void putInt( String strKey, int iValue );

    void putIntArray( String strKey,
                      int[] aiValues );

    void putDouble( String strKey, double dValue );

    void putString( String strKey,
                    String strValue );

    void putStringArray( String strKey,
                         String[] astrValues );

    String[] getStringArray( String strKey );

    String getString( String strKey );

    boolean getBoolean( String strKey );

    int getInt( String strKey );

    int[] getIntArray( String strKey );

    double getDouble( String strKey );

    boolean containsKey( String strKey );

    Set<String> keySet();
}
