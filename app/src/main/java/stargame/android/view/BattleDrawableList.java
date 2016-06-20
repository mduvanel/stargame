package stargame.android.view;

import java.util.Collections;
import java.util.Vector;

import android.graphics.Canvas;

public class BattleDrawableList
{
	private Vector< BattleDrawable > mVecDrawables;

	private boolean mChanged;

	public BattleDrawableList()
	{
		mChanged = false;
		mVecDrawables = new Vector< BattleDrawable >();
	}

	public void AddDrawable( BattleDrawable oStruct )
	{
		mVecDrawables.add( oStruct );
		mChanged = true;
	}

	public void RemoveDrawable( BattleDrawable oStruct )
	{
		if ( mVecDrawables.remove( oStruct ) )
		{
			mChanged = true;
		}
	}

	public void Clear()
	{
		mVecDrawables.clear();
	}

	public void Changed()
	{
		mChanged = true;
	}

	public void DrawBattle( Canvas oCanvas, int iOffsetX, int iOffsetY, float fZoomFactor )
	{
		if ( mChanged )
		{
			Collections.sort( mVecDrawables );
			mChanged = false;
		}

		for ( BattleDrawable oDrawable : mVecDrawables )
		{
			// Do not draw drawables with ZOrder < 0
			if ( oDrawable.mZOrder > -1 )
			{
				oDrawable.doDraw( oCanvas, iOffsetX, iOffsetY, fZoomFactor );
			}
		}
	}
}
