package stargame.android.view.action.priest;

import android.graphics.Color;

import stargame.android.R;
import stargame.android.model.BattleAction;
import stargame.android.model.BattleUnit;
import stargame.android.model.jobs.priest.BattleActionHeal;
import stargame.android.view.Battle2D;
import stargame.android.view.IDrawable;
import stargame.android.view.TextAnimation2D;
import stargame.android.view.action.BattleAction2D;
import stargame.android.view.action.IBattleAction2DCreator;

public class BattleAction2DHeal extends BattleAction2D
{
    BattleAction2DHeal( BattleAction oAction, Battle2D oBattle )
    {
        super();

        BattleActionHeal oActionHeal = ( BattleActionHeal ) oAction;
        BattleUnit oUnit = oAction.GetTargets().get( 0 ).mCell.GetUnit();

        IDrawable oAnimation = new TextAnimation2D(
                String.valueOf( oActionHeal.GetHealAmount() ),
                Color.GREEN );
        AddDrawable( CreateBattleDrawable( oUnit, oAnimation ), true );
    }

    public static IBattleAction2DCreator GetCreator()
    {
        return new BattleAction2DHealCreator();
    }

    /**
     * The BattleAction2D factory instance
     */
    private static class BattleAction2DHealCreator implements IBattleAction2DCreator
    {
        public BattleAction2D BattleAction2DCreate( BattleAction oAction, Battle2D oBattle )
        {
            if ( oAction.GetActionType() == R.string.heal_action )
            {
                return new BattleAction2DHeal( oAction, oBattle );
            }
            return null;
        }
    }
}
