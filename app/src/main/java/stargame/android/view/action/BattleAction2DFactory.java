package stargame.android.view.action;

import java.util.Vector;

import stargame.android.model.BattleAction;
import stargame.android.view.Battle2D;
import stargame.android.view.action.mage.BattleAction2DFireball;
import stargame.android.view.action.priest.BattleAction2DBarrier;
import stargame.android.view.action.priest.BattleAction2DHeal;
import stargame.android.view.action.barbarian.BattleAction2DStun;
import stargame.android.view.action.barbarian.BattleAction2DWhirlWind;
import stargame.android.view.action.rogue.BattleAction2DVanish;

public class BattleAction2DFactory
{
	private Vector< IBattleAction2DCreator > mCreators;

	private static BattleAction2DFactory mInstance = null;

	private BattleAction2DFactory()
	{
		mCreators = new Vector< IBattleAction2DCreator >();
	}

	public static BattleAction2DFactory GetInstance()
	{
		if ( null == mInstance )
		{
			mInstance = new BattleAction2DFactory();
			// Register all known BattleAction2DCreators

			// Base
			mInstance.RegisterBattleAction2D( BattleAction2DMove.GetCreator() );
			mInstance.RegisterBattleAction2D( BattleAction2DAttack.GetCreator() );

			// Mage
			mInstance.RegisterBattleAction2D( BattleAction2DFireball.GetCreator() );

			// Barbarian
			mInstance.RegisterBattleAction2D( BattleAction2DWhirlWind.GetCreator() );
			mInstance.RegisterBattleAction2D( BattleAction2DStun.GetCreator() );

			// Priest
			mInstance.RegisterBattleAction2D( BattleAction2DHeal.GetCreator() );
			mInstance.RegisterBattleAction2D( BattleAction2DBarrier.GetCreator() );

			// Rogue
			mInstance.RegisterBattleAction2D( BattleAction2DVanish.GetCreator() );
		}
		return mInstance;
	}

	private void RegisterBattleAction2D( IBattleAction2DCreator oCreator )
	{
		mCreators.add( oCreator );
	}

	public BattleAction2D BattleAction2DCreate( BattleAction oAction, Battle2D oBattle )
	{
		BattleAction2D oAction2D = null;
		for ( IBattleAction2DCreator oFactory : mCreators )
		{
			oAction2D = oFactory.BattleAction2DCreate( oAction, oBattle );
			if ( null != oAction2D )
			{
				break;
			}
		}

		return oAction2D;
	}
}
