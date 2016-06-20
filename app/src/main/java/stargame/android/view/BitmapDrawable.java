package stargame.android.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class BitmapDrawable implements IDrawable
{
	private Bitmap mBitmap;

	public BitmapDrawable( Bitmap oBitmap )
	{
		mBitmap = oBitmap;
	}

	public void doDraw( Canvas oCanvas, RectF oDestRect, float fZoomFactor, Paint oPaint )
	{
		oCanvas.drawBitmap( mBitmap, null, oDestRect, oPaint );
	}

	public boolean isFinished()
	{
		return false;
	}
}
