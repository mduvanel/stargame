package stargame.android.model;

import android.os.Bundle;

import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;
import stargame.android.util.FieldType;
import stargame.android.util.Position;

public class BattleCell implements ISavable
{
    /**
     * Type of terrain
     */
    private FieldType mType;

    private final static String M_TYPE = "Type";

    /**
     * Elevation
     */
    private int mElevation;

    private final static String M_ELEVATION = "Elevation";

    /**
     * Unit (if relevant)
     */
    private BattleUnit mUnit;

    private final static String M_UNIT = "Unit";

    /**
     * Position of the cell
     */
    private Position mPos;

    private final static String M_POSITION = "Position";

    BattleCell()
    {
        mUnit = null;
        mPos = new Position( 0, 0 );
        mElevation = 0;
        mType = FieldType.TYPE_NONE;
    }

    BattleCell( int iX, int iY )
    {
        mUnit = null;
        mPos = new Position( iX, iY );
        mElevation = 0;
        mType = FieldType.TYPE_NONE;
    }

    protected FieldType fieldFromString( String strType )
    {
        FieldType eType = FieldType.TYPE_NONE;

        if ( strType.compareTo( "GRASS" ) == 0 )
        {
            eType = FieldType.TYPE_GRASS;
        }
        else if ( strType.compareTo( "WATER" ) == 0 )
        {
            eType = FieldType.TYPE_WATER;
        }
        else if ( strType.compareTo( "LAVA" ) == 0 )
        {
            eType = FieldType.TYPE_LAVA;
        }
        else if ( strType.compareTo( "ROCK" ) == 0 )
        {
            eType = FieldType.TYPE_ROCK;
        }
        else if ( strType.compareTo( "PLAIN" ) == 0 )
        {
            eType = FieldType.TYPE_PLAIN;
        }
        else if ( strType.compareTo( "FOREST" ) == 0 )
        {
            eType = FieldType.TYPE_FOREST;
        }
        else if ( strType.compareTo( "NONE" ) == 0 )
        {
            eType = FieldType.TYPE_NONE;
        }

        return eType;
    }

    public void SetType( FieldType mType )
    {
        this.mType = mType;
    }

    public void SetType( String strType )
    {
        this.mType = fieldFromString( strType );
    }

    public FieldType GetType()
    {
        return mType;
    }

    public void SetElevation( int mElevation )
    {
        this.mElevation = mElevation;
    }

    public int GetElevation()
    {
        return mElevation;
    }

    public void SetPos( int iX, int iY )
    {
        mPos.mPosX = iX;
        mPos.mPosY = iY;
    }

    public Position GetPos()
    {
        return mPos;
    }

    public void SetUnit( BattleUnit unit )
    {
        this.mUnit = unit;
    }

    public BattleUnit GetUnit()
    {
        return mUnit;
    }

    public static BattleCell loadState( Bundle oMap, int i, int j )
    {
        String strCellDesc = String.format( "BattleCell_%d_%d_", i, j );
        BattleCell oCell = new BattleCell( i, j );

        oCell.mType = FieldType.valueOf( oMap.getString( strCellDesc.concat( "FieldType" ) ) );
        oCell.mElevation = oMap.getInt( strCellDesc.concat( "Elevation" ) );
        return oCell;
    }

    public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
    {
        oObjectMap.putInt( M_ELEVATION, mElevation );
        oObjectMap.putString( M_TYPE, mType.toString() );

        String strObjKey = SavableHelper.saveInMap( mPos, oGlobalMap );
        oObjectMap.putString( M_POSITION, strObjKey );

        if ( mUnit != null )
        {
            strObjKey = SavableHelper.saveInMap( mUnit, oGlobalMap );
            oObjectMap.putString( M_UNIT, strObjKey );
        }
    }

    public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
    {
        return loadState( oGlobalMap, strObjKey );
    }

    public static BattleCell loadState( Bundle oGlobalMap, String strObjKey )
    {
        Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey,
                                                             BattleCell.class.getName() );

        if ( oObjectBundle == null )
        {
            return null;
        }

        BattleCell oCell = new BattleCell();

        oCell.mElevation = oObjectBundle.getInt( M_ELEVATION );
        oCell.mType = FieldType.values()[ oObjectBundle.getInt( M_TYPE ) ];

        String strKey = oObjectBundle.getString( M_POSITION );
        oCell.mPos = Position.loadState( oGlobalMap, strKey );

        return oCell;
    }
}
