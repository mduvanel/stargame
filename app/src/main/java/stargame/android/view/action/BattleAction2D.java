package stargame.android.view.action;

import android.graphics.RectF;

import java.util.Vector;

import stargame.android.model.BattleAction;
import stargame.android.model.BattleUnit;
import stargame.android.util.Position;
import stargame.android.view.BattleCell2D;
import stargame.android.view.BattleDrawable;
import stargame.android.view.BattleField2D;
import stargame.android.view.DisplayConstants2D;
import stargame.android.view.IDrawable;
import stargame.android.view.ZOrderPosition;

/**
 * BattleAction2D is used to handle all the synchronization of
 * the animations related to the actual Execution of a BattleAction.
 */
public abstract class BattleAction2D
{
    protected static class ActionBattleDrawable
    {
        public BattleDrawable mDrawable;
        public boolean mAdd;

        public ActionBattleDrawable( BattleDrawable oDrawable, boolean bAdd )
        {
            mDrawable = oDrawable;
            mAdd = bAdd;
        }
    }

    /**
     * Vector containing all the IDrawables that should be elapsed before resuming normal display
     */
    protected Vector< ActionBattleDrawable > mDrawables;

    protected BattleAction2D()
    {
        mDrawables = new Vector< ActionBattleDrawable >();
    }

    public void AddDrawable( BattleDrawable oDrawable, boolean bNew )
    {
        mDrawables.add( new ActionBattleDrawable( oDrawable, bNew ) );
    }

    public void EnableDrawables()
    {
        for ( ActionBattleDrawable oDrawable : mDrawables )
        {
            if ( oDrawable.mAdd )
            {
                BattleField2D.GetInstance().GetDrawableList().AddDrawable( oDrawable.mDrawable );
            }
        }
    }

    /**
     * Tells whether the current animation for this action is finished or not
     */
    public boolean isFinished()
    {
        for ( ActionBattleDrawable oDrawable : mDrawables )
        {
            if ( oDrawable.mDrawable.GetDrawable() != null && !oDrawable.mDrawable.GetDrawable().isFinished() )
            {
                return false;
            }
        }

        return true;
    }

    public static BattleDrawable CreateBattleDrawable( BattleUnit oUnit, IDrawable oDrawable )
    {
        Position oDrawingPos = new Position();
        BattleField2D.GetInstance().FillCellAbsDrawingPosition( oUnit.GetCell().GetPos(),
                                                                oDrawingPos );
        oDrawingPos.mPosY -= oUnit.GetCell().GetElevation() * DisplayConstants2D.GetCellStackHeight();
        RectF oDestRect = new RectF( 0, 0, DisplayConstants2D.GetCellWidth(),
                                     DisplayConstants2D.GetUnitHeight() );
        oDestRect.offsetTo( oDrawingPos.mPosX, oDrawingPos.mPosY );
        BattleCell2D oCell2D = BattleField2D.GetInstance().GetCell2D( oUnit.GetCell().GetPos() );
        return new BattleDrawable(
                oDrawable,
                null,
                oDestRect,
                oCell2D.GetZOrder() + ZOrderPosition.FRONT_UNIT.Value() );
    }

    public void Update( BattleAction oAction )
    {
        // Basic implementation: no update expected
    }
}
