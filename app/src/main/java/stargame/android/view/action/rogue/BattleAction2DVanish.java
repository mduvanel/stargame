package stargame.android.view.action.rogue;

import stargame.android.R;
import stargame.android.model.BattleAction;
import stargame.android.view.Battle2D;
import stargame.android.view.action.BattleAction2D;
import stargame.android.view.action.IBattleAction2DCreator;

public class BattleAction2DVanish extends BattleAction2D
{
	BattleAction2DVanish( BattleAction oAction, Battle2D oBattle )
	{
		super();
	}

	public static IBattleAction2DCreator GetCreator()
	{
		return new BattleAction2DVanishCreator();
	}

	/** The BattleAction2D factory instance */
	private static class BattleAction2DVanishCreator implements IBattleAction2DCreator
	{
		public BattleAction2D BattleAction2DCreate( BattleAction oAction, Battle2D oBattle )
		{
			if ( oAction.GetActionType() == R.string.vanish_action )
			{
				return new BattleAction2DVanish( oAction, oBattle );
			}
			return null;
		}	
	}
}
