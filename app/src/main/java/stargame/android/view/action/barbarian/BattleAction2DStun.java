package stargame.android.view.action.barbarian;

import stargame.android.R;
import stargame.android.model.BattleAction;
import stargame.android.view.Battle2D;
import stargame.android.view.action.BattleAction2D;
import stargame.android.view.action.BattleAction2DAttack;
import stargame.android.view.action.IBattleAction2DCreator;

public class BattleAction2DStun extends BattleAction2DAttack
{
	public BattleAction2DStun( BattleAction oAction, Battle2D oBattle )
	{
		// Same as a regular attack animation-wise
		super( oAction, oBattle );
	}

	public static IBattleAction2DCreator GetCreator()
	{
		return new BattleAction2DStunCreator();
	}

	/** The BattleAction2D factory instance */
	private static class BattleAction2DStunCreator implements IBattleAction2DCreator
	{
		public BattleAction2D BattleAction2DCreate( BattleAction oAction, Battle2D oBattle )
		{
			if ( oAction.GetActionType() == R.string.stun_action )
			{
				return new BattleAction2DStun( oAction, oBattle );
			}
			return null;
		}	
	}
}
