package stargame.android.view;

public enum ZOrderPosition
{
    CELL_BACK( 0 ),
    CELL_FRONT( CELL_BACK.Value() + 1 ),
    TARGET_ANIM( CELL_FRONT.Value() + 1 ),
    SELECTED_ANIM( TARGET_ANIM.Value() + 1 ),
    UNIT( SELECTED_ANIM.Value() + 1 ),
    FRONT_UNIT( UNIT.Value() + 1 ),
    TEXT( FRONT_UNIT.Value() + 1 ),
    TOTAL( TEXT.Value() + 1 );

    private final int mZOrderValue;

    ZOrderPosition( int iPos )
    {
        mZOrderValue = iPos;
    }

    public int Value()
    {
        return mZOrderValue;
    }
}
