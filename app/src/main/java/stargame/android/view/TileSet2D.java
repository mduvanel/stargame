package stargame.android.view;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import stargame.android.util.FieldType;
import stargame.android.util.Logger;

public class TileSet2D
{
    /**
     * Defines the type of a given tile
     */
    public enum TileType
    {
        PLAIN,
        CORNER,
        TOP,
        BOTTOM,
        LEFT,
        RIGHT,
        ANGLE;

        public String toString()
        {
            switch ( this )
            {
                case PLAIN:
                    return "plain";
                case CORNER:
                    return "corner";
                case TOP:
                    return "top";
                case BOTTOM:
                    return "bottom";
                case LEFT:
                    return "left";
                case RIGHT:
                    return "right";
                case ANGLE:
                    return "angle";
            }

            return "";
        }
    }

    /**
     * Defines the quadrant of a given tile
     */
    public enum TilePos
    {
        UPPER_LEFT,
        UPPER_RIGHT,
        LOWER_LEFT,
        LOWER_RIGHT,
        MIDDLE_LEFT,
        MIDDLE_RIGHT; // MIDDLE ones are for vertical tilesets with odd height

        public String toString()
        {
            switch ( this )
            {
                case UPPER_LEFT:
                    return "ul";
                case UPPER_RIGHT:
                    return "ur";
                case LOWER_LEFT:
                    return "ll";
                case LOWER_RIGHT:
                    return "lr";
                case MIDDLE_LEFT:
                    return "ml";
                case MIDDLE_RIGHT:
                    return "mr";
            }

            return "";
        }
    }

    private final static int msTileHeight = 16;
    private final static int msTileWidth = 16;

    public static String msGrass1String = "grass1";
    public static String msGrass2String = "grass2";
    public static String msDirtString = "dirt";
    public static String msWaterString = "water";
    public static String msVerticalGrassRock = "vertical_grass";
    public static String msVerticalDirtRock = "vertical_dirt";

    /**
     * The Bitmaps that are used in this TileSet2D
     */
    private HashMap< TilePos, HashMap< TileType, IDrawable > > mBitmaps;

    /**
     * Name of the tileset
     */
    private String mSetName;

    private static int TilesetBitmapIdFromParams( TileType eType, TilePos ePos, String strName,
                                                  int iStep, Resources oRes )
    {
        String strFullname = String.format( "%s_%s_%s", strName, ePos.toString(),
                                            eType.toString() );
        if ( iStep != -1 )
        {
            strFullname = strFullname.concat( String.format( "_step%d", iStep ) );
        }

        return oRes.getIdentifier( strFullname, "drawable", "stargame.android" );
    }

    public TileSet2D( String strName, Resources oResources )
    {
        mSetName = strName;

        mBitmaps = new HashMap< TilePos, HashMap< TileType, IDrawable > >();
        for ( TilePos ePos : TilePos.values() )
        {
            HashMap< TileType, IDrawable > oMap = new HashMap< TileType, IDrawable >();

            // Brute-force loading of tilesets...
            for ( TileType eType : TileType.values() )
            {
                int index = TilesetBitmapIdFromParams( eType, ePos, mSetName, -1, oResources );
                if ( index > 0 )
                {
                    try
                    {
                        Bitmap oBitmap = BitmapRepository.GetInstance().GetBitmap( index );
                        oMap.put( eType, new BitmapDrawable( oBitmap ) );
                    }
                    catch ( Exception ex )
                    {
                        Logger.e( String.format(
                                "Error loading bitmap %s, %s of tileset %s, resource missing!",
                                eType.toString(), ePos.toString(), mSetName ) );
                    }
                }
            }
            mBitmaps.put( ePos, oMap );
        }
    }

    public TileSet2D( XmlResourceParser oParser, Resources oResources )
    {
        @SuppressWarnings( "unused" )
        boolean bVertical = false;
        int iSteps = -1;
        @SuppressWarnings( "unused" )
        FieldType eFieldType;

        try
        {
            int eventType = oParser.getEventType();
            while ( eventType != XmlResourceParser.END_DOCUMENT )
            {
                if ( eventType == XmlResourceParser.START_TAG )
                {
                    if ( oParser.getName().equals( "Name" ) )
                    {
                        mSetName = oParser.nextText();
                    }
                    else if ( oParser.getName().equals( "Vertical" ) )
                    {
                        bVertical = Boolean.parseBoolean( oParser.nextText() );
                    }
                    else if ( oParser.getName().equals( "Steps" ) )
                    {
                        iSteps = Integer.parseInt( oParser.nextText() );
                    }
                    else if ( oParser.getName().equals( "FieldType" ) )
                    {
                        eFieldType = FieldType.valueOf( oParser.nextText() );
                    }
                }
                eventType = oParser.next();
            }

            Logger.i( "Loading Complete!" );
        }
        catch ( XmlPullParserException e )
        {
            Logger.e( String.format( "XML Error while loading: %s", e.getMessage() ) );
            e.printStackTrace();
            return;
        }
        catch ( IOException e )
        {
            Logger.e( String.format( "IO Error while loading: %s", e.getMessage() ) );
            e.printStackTrace();
            return;
        }

        mBitmaps = new HashMap< TilePos, HashMap< TileType, IDrawable > >();
        for ( TilePos ePos : TilePos.values() )
        {
            HashMap< TileType, IDrawable > oMap = new HashMap< TileType, IDrawable >();

            // Brute-force loading of tilesets...
            for ( TileType eType : TileType.values() )
            {
                try
                {
                    if ( iSteps == 1 )
                    {
                        // Single Bitmap
                        int index = TilesetBitmapIdFromParams( eType, ePos, mSetName, -1,
                                                               oResources );
                        if ( index > 0 )
                        {
                            Bitmap oBitmap = BitmapRepository.GetInstance().GetBitmap( index );
                            oMap.put( eType, new BitmapDrawable( oBitmap ) );
                        }
                    }
                    else
                    {
                        // Animation2D
                        Vector< Bitmap > oBitmaps = new Vector< Bitmap >();
                        for ( int i = 0; i < iSteps; ++i )
                        {
                            int index = TilesetBitmapIdFromParams( eType, ePos, mSetName, i + 1,
                                                                   oResources );
                            if ( index > 0 )
                            {
                                oBitmaps.add( BitmapRepository.GetInstance().GetBitmap( index ) );
                            }
                        }

                        oMap.put( eType, new Animation2D( oBitmaps, 5, true ) );
                    }
                }
                catch ( Exception ex )
                {
                    Logger.e( String.format(
                            "Error loading bitmap %s, %s of tileset %s, resource missing!",
                            eType.toString(), ePos.toString(), mSetName ) );
                }
            }
            mBitmaps.put( ePos, oMap );
        }
    }

    public IDrawable GetDrawable( TileType eType, TilePos ePos )
    {
        // HACK: we duplicate the IDrawable in case it is an Animation2D
        IDrawable oDrawable = mBitmaps.get( ePos ).get( eType );
        if ( oDrawable instanceof Animation2D )
        {
            return ( ( Animation2D ) oDrawable ).Clone();
        }
        else
        {
            return oDrawable;
        }
    }

    public static int GetTileHeight()
    {
        return msTileHeight;
    }

    public static int GetTileWidth()
    {
        return msTileWidth;
    }
}
