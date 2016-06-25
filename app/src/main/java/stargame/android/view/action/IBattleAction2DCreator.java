package stargame.android.view.action;

import stargame.android.model.BattleAction;
import stargame.android.view.Battle2D;

public interface IBattleAction2DCreator
{
    public BattleAction2D BattleAction2DCreate( BattleAction oAction, Battle2D oBattle );
}
