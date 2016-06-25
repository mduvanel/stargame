package stargame.android.view;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;

import stargame.android.util.Logger;

public class BitmapRepository
{
    /**
     * The map
     */
    private HashMap< Integer, Bitmap > mBitmaps;

    /**
     * The resources to load from
     */
    private static Resources msResources;

    /**
     * The singleton instance
     */
    private static BitmapRepository msTheInstance = null;

    BitmapRepository()
    {
        mBitmaps = new HashMap< Integer, Bitmap >();
    }

    public static BitmapRepository GetInstance()
    {
        if ( msTheInstance == null )
        {
            Logger.w( "BitmapRepository singleton created" );
            msTheInstance = new BitmapRepository();
        }
        return msTheInstance;
    }

    public static void Init( Resources oResources )
    {
        msResources = oResources;
    }

    public static void Release()
    {
        if ( null != msTheInstance )
        {
            // release all Bitmaps
            for ( Bitmap oBitmap : msTheInstance.mBitmaps.values() )
            {
                if ( null != oBitmap )
                {
                    oBitmap.recycle();
                }
            }
            msTheInstance.mBitmaps = null;
        }

        // Release static refs
        msTheInstance = null;
        msResources = null;
    }

    /**
     * Get Bitmap from raw resource index
     */
    public Bitmap GetBitmap( int index )
    {
        Bitmap oBitmap = mBitmaps.get( index );

        if ( null == oBitmap )
        {
            if ( null == msResources )
            {
                Logger.e( "NULL Resources pointer when trying to get a Bitmap!" );
            }
            else
            {
                oBitmap = BitmapFactory.decodeResource( msResources, index );
                if ( null == oBitmap )
                {
                    Logger.e( String.format( "Failed to load Bitmap with ID %d!", index ) );
                }
                mBitmaps.put( index, oBitmap );
            }
        }

        return oBitmap;
    }

    /**
     * Get Bitmap from resource name
     */
    public Bitmap GetBitmap( String strName )
    {
        if ( null == msResources )
        {
            Logger.e( "NULL Resources pointer when trying to get a Bitmap!" );
            return null;
        }
        else
        {
            int index = msResources.getIdentifier( strName, "drawable", "stargame.android" );
            return GetBitmap( index );
        }
    }
}
