package stargame.android.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public interface IDrawable
{
    public void doDraw( Canvas oCanvas, RectF oSourceRect, float fZoomFactor, Paint oPaint );

    public boolean isFinished();
}
