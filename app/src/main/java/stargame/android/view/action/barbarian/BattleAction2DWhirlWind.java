package stargame.android.view.action.barbarian;

import android.graphics.Color;

import stargame.android.R;
import stargame.android.model.BattleAction;
import stargame.android.model.BattleAction.ActionTarget;
import stargame.android.model.jobs.barbarian.BattleActionWhirlwind;
import stargame.android.util.Orientation;
import stargame.android.util.Position;
import stargame.android.view.Battle2D;
import stargame.android.view.BattleUnit2D;
import stargame.android.view.IDrawable;
import stargame.android.view.TextAnimation2D;
import stargame.android.view.action.BattleAction2D;
import stargame.android.view.action.IBattleAction2DCreator;

public class BattleAction2DWhirlWind extends BattleAction2D
{
    BattleAction2DWhirlWind( BattleAction oAction, Battle2D oBattle )
    {
        super();

        BattleActionWhirlwind oActionWW = ( BattleActionWhirlwind ) oAction;

        String strText = null;
        IDrawable oAnimation;
        for ( ActionTarget oTarget : oAction.GetTargets() )
        {
            BattleUnit2D oUnit = oBattle.GetBattleUnit2D( oTarget.mCell.GetUnit() );

            if ( null != oUnit )
            {
                Position oPos = oTarget.mCell.GetPos();
                switch ( oTarget.mActionStatus )
                {
                    case STATUS_MISS:
                        strText = oBattle.GetContext().getString( R.string.missed );
                        break;
                    case STATUS_CRITICAL:
                    case STATUS_SUCCESS:
                        strText = String.valueOf( oActionWW.GetDamage( oPos ) );
                        break;
                    case STATUS_PENDING:
                        // Should not happen
                }

                oAnimation = new TextAnimation2D( strText, Color.LTGRAY );
                AddDrawable( CreateBattleDrawable( oTarget.mCell.GetUnit(), oAnimation ), true );
            }
        }

        oBattle.GetBattleUnit2D( oAction.GetSourceUnit() ).SetNextAnimation(
                oAction.GetName( oBattle.GetContext() ),
                Orientation.NONE );
    }

    public static IBattleAction2DCreator GetCreator()
    {
        return new BattleAction2DWhirlWindCreator();
    }

    /**
     * The BattleAction2D factory instance
     */
    private static class BattleAction2DWhirlWindCreator implements IBattleAction2DCreator
    {
        public BattleAction2D BattleAction2DCreate( BattleAction oAction, Battle2D oBattle )
        {
            if ( oAction.GetActionType() == R.string.whirlwind_action )
            {
                return new BattleAction2DWhirlWind( oAction, oBattle );
            }
            return null;
        }
    }
}
