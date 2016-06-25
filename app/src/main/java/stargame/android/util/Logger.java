package stargame.android.util;

import android.util.Log;
import android.view.MotionEvent;

public class Logger
{
    private static Logger ms_theLogger = null;

    private boolean m_bLog;

    private Logger()
    {
        m_bLog = true;
    }

    static private Logger GetInstance()
    {
        if ( ms_theLogger == null )
        {
            ms_theLogger = new Logger();
        }

        return ms_theLogger;
    }

    // not clean... should offer generic formatting methods to hide actual
    // logging from caller
    static public boolean isLogging()
    {
        return GetInstance().m_bLog;
    }

    static public void setLogging( boolean bLog )
    {
        GetInstance().m_bLog = bLog;
    }

    static public void log( int priority, String strMessage )
    {
        if ( !GetInstance().m_bLog )
        {
            return;
        }

        Log.println( priority, "StarGame.Logger", strMessage );
    }

    static public void v( String strMessage )
    {
        log( Log.VERBOSE, strMessage );
    }

    static public void a( String strMessage )
    {
        log( Log.ASSERT, strMessage );
    }

    static public void e( String strMessage )
    {
        log( Log.ERROR, strMessage );
    }

    static public void i( String strMessage )
    {
        log( Log.INFO, strMessage );
    }

    static public void d( String strMessage )
    {
        log( Log.DEBUG, strMessage );
    }

    static public void w( String strMessage )
    {
        log( Log.WARN, strMessage );
    }

    /**
     * Advanced logging function to debug MotionEvents
     *
     * @param oEvent the MotionEvent to log
     */
    static public void logEventInfo( MotionEvent oEvent )
    {
        final int iHistorySize = oEvent.getHistorySize();
        final int iPointerCount = oEvent.getPointerCount();
        String strMessage = String.format(
                "Entering handleTouchEvent() with a event history of depth %d and %d pointers",
                iHistorySize,
                iPointerCount );
        Logger.v( strMessage );

        // Look for up/down events in history
        for ( int h = 0; h < iHistorySize; h++ )
        {
            strMessage = String.format( "Event history %d", h );
            Logger.v( strMessage );

            for ( int p = 0; p < iPointerCount; p++ )
            {
                strMessage = String.format(
                        " pointer %d: (%f, %f)",
                        oEvent.findPointerIndex( p ),
                        oEvent.getHistoricalX( p, h ),
                        oEvent.getHistoricalY( p, h ) );
                Logger.v( strMessage );
            }
        }

        strMessage = String.format( "Main event of type %d", oEvent.getAction() );
        Logger.v( strMessage );

        for ( int p = 0; p < iPointerCount; p++ )
        {
            strMessage = String.format(
                    " pointer %d: (%f, %f)",
                    oEvent.findPointerIndex( p ),
                    oEvent.getX( p ),
                    oEvent.getY( p ) );
            Logger.v( strMessage );
        }
    }
}
