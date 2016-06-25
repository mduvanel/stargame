package stargame.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class StarView extends View
{
    Button mStart;

    Button mExit;

    public StarView( Context oContext )
    {
        super( oContext );

        setFocusable( true ); // make sure we get key events
    }

    public StarView( Context oContext, AttributeSet oAttrSet )
    {
        super( oContext, oAttrSet );

        setFocusable( true ); // make sure we get key events
    }

    /**
     * Standard override to get key-press events.
     */
    @Override
    public boolean onKeyDown( int iKeyCode, KeyEvent oMsg )
    {
        return dispatchKeyEvent( oMsg );
    }

    /**
     * Standard override for key-up. We actually care about these, so we can
     * turn off the engine or stop rotating.
     */
    @Override
    public boolean onKeyUp( int iKeyCode, KeyEvent oMsg )
    {
        return dispatchKeyEvent( oMsg );
    }
}
