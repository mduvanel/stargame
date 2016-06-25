package stargame.android.util;

// Orientation of an unit
public enum Orientation
{
    NORTH,
    WEST,
    SOUTH,
    EAST,
    NONE;

    /**
     * Returns the resulting Orientation when this Orientation is drawn from
     * eDrawingOrientation point of view
     */
    public Orientation GetResultingOrientation( Orientation eDrawingOrientation )
    {
        if ( this == NONE || eDrawingOrientation == NONE )
        {
            return NONE;
        }

        return Orientation.values()[ modulo( this.ordinal() - eDrawingOrientation.ordinal(), 4 ) ];
    }

    /**
     * Returns this Orientation transformed by the given eDrawingOrientation
     */
    public Orientation TransformOrientation( Orientation eDrawingOrientation )
    {
        if ( eDrawingOrientation == NONE )
        {
            return NONE;
        }

        switch ( eDrawingOrientation )
        {
            case NORTH:
            case SOUTH:
            case EAST:
            case WEST:
                //Logger.w(  String.format( "Drawing orientation: %d, Direction: %d", eDrawingOrientation.ordinal(), this.ordinal() ) );
                return Orientation.values()[ modulo( eDrawingOrientation.ordinal() + this.ordinal(),
                                                     4 ) ];
            case NONE:
            default:
                return NONE;
        }
    }

    public Orientation RotateClockwise()
    {
        return Orientation.values()[ modulo( this.ordinal() + 1, 4 ) ];
    }

    static public int modulo( int iValue, int iModulo )
    {
        if ( iValue > -1 )
        {
            return iValue % iModulo;
        }

        int iTmp = iModulo - ( ( -iValue ) % iModulo );
        if ( iTmp == iModulo )
        {
            return 0;
        }

        return iTmp;
    }
}
