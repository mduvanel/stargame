package stargame.android.view;

import android.graphics.RectF;

import java.util.Vector;

import stargame.android.model.BattleCell;

public class BattleCell2D
{
    /**
     * The BattleCell that is represented by this object
     */
    @SuppressWarnings( "unused" )
    private BattleCell mCell;

    private BattleDrawableList mDrawableList;

    /**
     * Cell's basic ZOrder
     */
    private int mZOrder;

    public int GetZOrder()
    {
        return mZOrder;
    }

    /**
     * The drawables to be drawn in the following order:
     */
    private Vector< BattleDrawable > mVecDrawables;

    public BattleCell2D( BattleCell oCell, BattleDrawableList oDrawableList, int iZOrder )
    {
        mZOrder = iZOrder;
        mDrawableList = oDrawableList;
        mCell = oCell;
        mVecDrawables = new Vector< BattleDrawable >();
    }

    public void AddTile( IDrawable oDrawable, RectF oDestRect, boolean bFront )
    {
        int iZOrderOffset = ZOrderPosition.CELL_BACK.Value();
        if ( bFront )
        {
            iZOrderOffset = ZOrderPosition.CELL_FRONT.Value();
        }

        BattleDrawable oBattleDrawable = new BattleDrawable( oDrawable, null,
                                                             new RectF( oDestRect ),
                                                             mZOrder + iZOrderOffset );
        mVecDrawables.add( oBattleDrawable );
        mDrawableList.AddDrawable( oBattleDrawable );
    }

    public void Reset( int iZOrder )
    {
        // We have to remove the previous elements because we will insert new ones
        for ( BattleDrawable oDrawable : mVecDrawables )
        {
            mDrawableList.RemoveDrawable( oDrawable );
        }
        mVecDrawables.clear();
        mZOrder = iZOrder;
    }
}
