package stargame.android.view.action;

import android.graphics.Color;

import stargame.android.R;
import stargame.android.model.BattleAction;
import stargame.android.model.BattleActionAttack;
import stargame.android.model.BattleUnit;
import stargame.android.util.Orientation;
import stargame.android.view.Battle2D;
import stargame.android.view.IDrawable;
import stargame.android.view.TextAnimation2D;
import stargame.android.view.action.BattleAction2D;
import stargame.android.view.action.IBattleAction2DCreator;

public class BattleAction2DAttack extends BattleAction2D
{
	protected BattleAction2DAttack( BattleAction oAction, Battle2D oBattle )
	{
		super();

		BattleActionAttack oActionAttack = ( BattleActionAttack )oAction;
		BattleUnit oUnit = oAction.GetTargets().get( 0 ).mCell.GetUnit();

		String strText = null;
		switch ( oAction.GetTargets().get( 0 ).mActionStatus )
		{
		case STATUS_MISS:
			strText = oBattle.GetContext().getString( R.string.missed );
			break;
		case STATUS_CRITICAL:
		case STATUS_SUCCESS:
			strText = String.valueOf( oActionAttack.GetDamage() ); 
			break;
		case STATUS_PENDING:
			// Should not happen
		}

		Orientation eActionOrientation = oAction.GetSourceUnit().GetCell().GetPos().GetOrientation( 
				oAction.GetTargets().get( 0 ).mCell.GetPos() );
		oBattle.GetBattleUnit2D( oAction.GetSourceUnit() ).SetNextAnimation( 
				BattleActionTarget2D.GetAnimationStringFromAction( oAction ),
				eActionOrientation );

		IDrawable oAnimation = new TextAnimation2D( strText, Color.LTGRAY );
		AddDrawable( CreateBattleDrawable( oUnit, oAnimation ), true );
	}

	public static IBattleAction2DCreator GetCreator()
	{
		return new BattleAction2DAttackCreator();
	}

	/** The BattleAction2D factory instance */
	private static class BattleAction2DAttackCreator implements IBattleAction2DCreator
	{
		public BattleAction2D BattleAction2DCreate( BattleAction oAction, Battle2D oBattle )
		{
			if ( oAction.GetActionType() == R.string.attack_action )
			{
				return new BattleAction2DAttack( oAction, oBattle );
			}
			return null;
		}	
	}
}
