package stargame.android.view.action;

import android.content.res.Resources;
import android.graphics.RectF;

import java.util.Vector;

import stargame.android.R;
import stargame.android.model.BattleAction;
import stargame.android.util.Orientation;
import stargame.android.util.Position;
import stargame.android.view.AnimatedItem2D;
import stargame.android.view.BattleDrawable;
import stargame.android.view.BattleField2D;
import stargame.android.view.BattleThread2D;
import stargame.android.view.DisplayConstants2D;
import stargame.android.view.ZOrderPosition;

/**
 * BattleActionTarget2D wraps a BattleAction to be able to display its targets.
 * It loads the target animations for all BattleActions, and the same
 * BattleActionTarget2D can be reused for a different BattleAction.
 */
public class BattleActionTarget2D
{
    /**
     * The battle action from the model
     */
    private BattleAction mBattleAction;

    /**
     * The animations for the actions
     */
    private AnimatedItem2D mAnimations;

    /**
     * The drawables associated to this BattleAction
     */
    private Vector< BattleDrawable > mVecDrawables;

    public BattleActionTarget2D( BattleAction oAction, Resources oRes )
    {
        mBattleAction = oAction;
        mVecDrawables = new Vector< BattleDrawable >();
        mAnimations = new AnimatedItem2D( "action_animations", oRes );
    }

    static public String GetAnimationStringFromAction( BattleAction oAction )
    {
        switch ( oAction.GetActionType() )
        {
            case R.string.move_action:
                return "Move";
            case R.string.attack_action:
            case R.string.stun_action:
                return "Attack";
            default:
                return "Target";
        }
    }

    /**
     * Update the BattleDrawables for the given BattleAction.
     * If the given action is null, then the current action is
     * used (to force an update).
     */
    public void UpdateDrawables( BattleAction oAction )
    {
        // Lock on Drawing Lock to avoid changing Action during display
        synchronized ( BattleThread2D.mDrawingLock )
        {
            if ( mBattleAction != null )
            {
                // Remove old drawables
                for ( BattleDrawable oDrawable : mVecDrawables )
                {
                    BattleField2D.GetInstance().GetDrawableList().RemoveDrawable( oDrawable );
                }
                mVecDrawables.clear();
            }

            mBattleAction = oAction;
            if ( mBattleAction != null )
            {
                mAnimations.SetBaseAnimation( GetAnimationStringFromAction( mBattleAction ),
                                              Orientation.NONE );

                // Create new drawables
                Vector< Position > oVecTargetableCells = mBattleAction.GetTargetableCells();
                for ( Position oPos : oVecTargetableCells )
                {
                    RectF oDestRect = new RectF( 0, 0, DisplayConstants2D.GetCellWidth(),
                                                 DisplayConstants2D.GetCellHeight() );
                    Position oDestPos = new Position();
                    BattleField2D.GetInstance().FillCellAbsDrawingPosition( oPos, oDestPos );
                    oDestRect.offsetTo( oDestPos.mPosX, oDestPos.mPosY );
                    int iZOrder = BattleField2D.GetInstance().GetCell2D(
                            oPos ).GetZOrder() + ZOrderPosition.TARGET_ANIM.Value();
                    BattleDrawable oDrawable = new BattleDrawable(
                            mAnimations.GetCurrentAnimation().Clone(), null, oDestRect, iZOrder );

                    // Add drawables
                    mVecDrawables.add( oDrawable );
                    BattleField2D.GetInstance().GetDrawableList().AddDrawable( oDrawable );
                }
            }
        }
    }

    /**
     * Change dynamically the BattleAction
     */
    public void SetBattleAction( BattleAction oAction )
    {
        // Don't bother taking the lock if the action is the same...
        if ( oAction == mBattleAction )
        {
            return;
        }

        // Update the drawables on action change
        UpdateDrawables( oAction );
    }
}
