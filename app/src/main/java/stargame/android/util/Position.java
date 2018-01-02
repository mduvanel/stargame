package stargame.android.util;

import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.storage.SavableHelper;

public class Position implements ISavable
{
    public int mPosX;
    public int mPosY;

    private static final String M_POS_X = "PosX";
    private static final String M_POS_Y = "PosY";

    public Position()
    {
        mPosX = -1;
        mPosY = -1;
    }

    public Position( IStorage oStore )
    {
        mPosX = oStore.getInt( M_POS_X );
        mPosY = oStore.getInt( M_POS_Y );
    }

    public Position( int iSourceX, int iSourceY )
    {
        mPosX = iSourceX;
        mPosY = iSourceY;
    }

    public Position( Position oPos )
    {
        mPosX = oPos.mPosX;
        mPosY = oPos.mPosY;
    }

    public Position( Position oPos, int iXOffset, int iYOffset )
    {
        mPosX = oPos.mPosX + iXOffset;
        mPosY = oPos.mPosY + iYOffset;
    }

    /**
     * Copy
     */
    public void SetPos( Position oPos )
    {
        mPosX = oPos.mPosX;
        mPosY = oPos.mPosY;
    }

    public void SetPos( int iPosX, int iPosY )
    {
        mPosX = iPosX;
        mPosY = iPosY;
    }

    public boolean Equals( Position oPos )
    {
        return ( oPos.mPosX == mPosX ) && ( oPos.mPosY == mPosY );
    }

    public Position Sub( Position oPos )
    {
        return new Position( mPosX - oPos.mPosX, mPosY - oPos.mPosY );
    }

    public Position Add( Position oPos )
    {
        return new Position( mPosX + oPos.mPosX, mPosY + oPos.mPosY );
    }

    public Position Div( int iDiv )
    {
        return new Position( mPosX / iDiv, mPosY / iDiv );
    }

    public Position Mul( int iMulX, int iMulY )
    {
        return new Position( mPosX * iMulX, mPosY * iMulY );
    }

    public boolean IsInside( int left, int top, int right, int bottom )
    {
        return ( mPosX >= left ) && ( mPosX <= right ) && ( mPosY >= top ) && ( mPosY <= bottom );
    }

    public Position Offset( int iOffsetX, int iOffsetY )
    {
        return new Position( this, iOffsetX, iOffsetY );
    }

    /**
     * Given another position, returns the orientation that a unit standing
     * on this position would have if looking at the given position.
     */
    public Orientation GetOrientation( Position oTargetPosition )
    {
        int iDiffPosX = this.mPosX - oTargetPosition.mPosX;
        int iDiffPosY = this.mPosY - oTargetPosition.mPosY;

        if ( iDiffPosY > Math.abs( iDiffPosX ) )
        {
            return Orientation.NORTH;
        }
        else if ( iDiffPosY < -Math.abs( iDiffPosX ) )
        {
            return Orientation.SOUTH;
        }
        else if ( iDiffPosX > Math.abs( iDiffPosY ) )
        {
            return Orientation.WEST;
        }
        else
        {
            return Orientation.EAST;
        }
    }

    /**
     * Generate new position with Offset with respect to the drawing orientation
     * oPos: absolute initial position
     * oOrientation: drawing orientation
     * iOffsetX: absolute X offset
     * iOffsetY: absolute Y offset
     *
     * @return Offset position
     */
    static public Position OffsetPositionAbsolute( Position oPos, Orientation oOrientation,
                                                   int iOffsetX, int iOffsetY )
    {
        int iTmp;
        switch ( oOrientation )
        {
            case SOUTH:
                iOffsetX = -iOffsetX;
                iOffsetY = -iOffsetY;
                break;
            case EAST:
                iTmp = iOffsetX;
                //noinspection SuspiciousNameCombination
                iOffsetX = iOffsetY;
                iOffsetY = -iTmp;
                break;
            case WEST:
                iTmp = iOffsetX;
                iOffsetX = -iOffsetY;
                iOffsetY = iTmp;
                break;
            case NORTH:
            case NONE:
                // Nothing to change
        }

        return oPos.Offset( iOffsetX, iOffsetY );
    }

    /**
     * Generate new position with Offset with respect to the drawing orientation
     * oPos: absolute initial position
     * oOrientation: drawing orientation
     * iOffsetX: X offset relative to Drawing Orientation
     * iOffsetY: X offset relative to Drawing Orientation
     *
     * @return Offset position
     */
    static public Position OffsetPositionRelative( Position oPos, Orientation oOrientation,
                                                   int iOffsetX, int iOffsetY )
    {
        int iTmp;
        switch ( oOrientation )
        {
            case SOUTH:
                iOffsetX = -iOffsetX;
                iOffsetY = -iOffsetY;
                break;
            case EAST:
                iTmp = iOffsetX;
                iOffsetX = -iOffsetY;
                iOffsetY = iTmp;
                break;
            case WEST:
                iTmp = iOffsetX;
                //noinspection SuspiciousNameCombination
                iOffsetX = iOffsetY;
                iOffsetY = -iTmp;
                break;
            case NORTH:
            case NONE:
                // Nothing to change
        }

        return oPos.Offset( iOffsetX, iOffsetY );
    }

    public String toString()
    {
        return String.format( "[%d:%d]", mPosX, mPosY );
    }

    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        oObjectStore.putInt( M_POS_X, mPosX );
        oObjectStore.putInt( M_POS_Y, mPosY );
    }

    public static Position loadState( IStorage oGlobalStore, String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey,
                Position.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        Position oPos = new Position();

        oPos.mPosX = oObjectStore.getInt( M_POS_X );
        oPos.mPosY = oObjectStore.getInt( M_POS_Y );

        return oPos;
    }

    public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
    {
        return loadState( oGlobalStore, strObjKey );
    }
}
