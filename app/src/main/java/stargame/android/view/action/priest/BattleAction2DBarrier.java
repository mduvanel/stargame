package stargame.android.view.action.priest;

import stargame.android.R;
import stargame.android.model.BattleAction;
import stargame.android.view.Battle2D;
import stargame.android.view.action.BattleAction2D;
import stargame.android.view.action.IBattleAction2DCreator;

public class BattleAction2DBarrier extends BattleAction2D
{
	BattleAction2DBarrier( BattleAction oAction, Battle2D oBattle )
	{
		super();
	}

	public static IBattleAction2DCreator GetCreator()
	{
		return new BattleAction2DBarrierCreator();
	}

	/** The BattleAction2D factory instance */
	private static class BattleAction2DBarrierCreator implements IBattleAction2DCreator
	{
		public BattleAction2D BattleAction2DCreate( BattleAction oAction, Battle2D oBattle )
		{
			if ( oAction.GetActionType() == R.string.barrier_action )
			{
				return new BattleAction2DBarrier( oAction, oBattle );
			}
			return null;
		}	
	}
}
