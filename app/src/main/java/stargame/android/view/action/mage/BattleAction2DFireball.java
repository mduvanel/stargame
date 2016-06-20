package stargame.android.view.action.mage;

import android.graphics.Color;
import stargame.android.R;
import stargame.android.model.BattleAction;
import stargame.android.model.BattleUnit;
import stargame.android.model.jobs.mage.BattleActionFireball;
import stargame.android.view.Battle2D;
import stargame.android.view.IDrawable;
import stargame.android.view.TextAnimation2D;
import stargame.android.view.action.BattleAction2D;
import stargame.android.view.action.IBattleAction2DCreator;

public class BattleAction2DFireball extends BattleAction2D
{
	BattleAction2DFireball( BattleAction oAction, Battle2D oBattle )
	{
		super();

		BattleActionFireball oActionFireball = ( BattleActionFireball )oAction;
		BattleUnit oUnit = oAction.GetTargets().get( 0 ).mCell.GetUnit();

		String strText = null;
		switch ( oAction.GetTargets().get( 0 ).mActionStatus )
		{
		case STATUS_MISS:
			strText = oBattle.GetContext().getString( R.string.missed );
			break;
		case STATUS_CRITICAL:
		case STATUS_SUCCESS:
			strText = String.valueOf( oActionFireball.GetDamage() ); 
			break;
		case STATUS_PENDING:
			// Should not happen
		}

		IDrawable oAnimation = new TextAnimation2D( strText, Color.LTGRAY );
		AddDrawable( CreateBattleDrawable( oUnit, oAnimation ), true );		
	}

	public static IBattleAction2DCreator GetCreator()
	{
		return new BattleAction2DFireballCreator();
	}

	/** The BattleAction2D factory instance */
	private static class BattleAction2DFireballCreator implements IBattleAction2DCreator
	{
		public BattleAction2D BattleAction2DCreate( BattleAction oAction, Battle2D oBattle )
		{
			if ( oAction.GetActionType() == R.string.fireball_action )
			{
				return new BattleAction2DFireball( oAction, oBattle );
			}
			return null;
		}	
	}
}
