package stargame.android.view;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Observable;
import java.util.Observer;

import stargame.android.controller.BattleActivity;
import stargame.android.model.BattleField;
import stargame.android.util.Logger;
import stargame.android.util.Orientation;
import stargame.android.util.Position;

/**
 * View that draws for a Battle.
 */
public class BattleView2D extends SurfaceView implements SurfaceHolder.Callback
{
    private class BattleFieldObserverProxy implements Observer
    {
        public BattleFieldObserverProxy()
        {
            BattleField2D.GetInstance().GetBattleField().addObserver( this );
        }

        public void update( Observable oObs, Object oObj )
        {
            BattleView2D.this.Update( oObs, oObj );
        }

        public void detach()
        {
            BattleField2D.GetInstance().GetBattleField().deleteObserver( this );
        }
    }

    ;

    /**
     * Proxy for observing the BattleField
     */
    private BattleFieldObserverProxy mObserver;

    /**
     * Handle to the application context, used to e.g. fetch Drawables.
     */
    private Context mContext;

    /** Pointer to the text view to display "Paused.." etc. */
    //private TextView mStatusText;

    /**
     * The Battle containing all the logic
     */
    private Battle2D mBattle;

    /**
     * The thread that actually draws the animation
     */
    private BattleThread2D mThread;

    /**
     * The handler for orientation changed
     */
    private OrientationEventListener mOrientationListener;

    /**
     * The Battlefield coords of the Cell in the center
     */
    private Position mCenterCellPos;

    /**
     * The Rect describing the Center Zone
     */
    private Rect mCenterZoneRect;

    /**
     * The Battlefield min and max values for the center
     */
    private int mMinCenterCellX;
    private int mMaxCenterCellX;
    private int mMinCenterCellY;
    private int mMaxCenterCellY;

    /**
     * The number of "full" cells displayed in both directions
     */
    private int mCellsDisplayedWidth;
    private int mCellsDisplayedHeight;

    /**
     * The current zoom factor for display
     */
    private float mZoomFactor;

    public BattleView2D( Context oContext, AttributeSet oAttrs )
    {
        super( oContext, oAttrs );

        mZoomFactor = 1;
        mContext = oContext.getApplicationContext();
        mBattle = null;
        mThread = null;
        mCenterCellPos = null;
        mObserver = null;

        mOrientationListener = new OrientationEventListener( mContext,
                                                             SensorManager.SENSOR_DELAY_UI )
        {
            @Override
            public void onOrientationChanged( int iOrientation )
            {
                if ( mThread != null )
                {
                    mThread.setOrientation( iOrientation );
                }
            }
        };

        setFocusable( true ); // make sure we get key events
        setFocusableInTouchMode( true );
    }

    /**
     * Passes the Battle object and creates the thread
     */
    public void Init( BattleActivity oController )
    {
        mBattle = new Battle2D( oController.GetBattle(), mContext );
        mCenterCellPos = new Position( 0, 0 );
        mCenterZoneRect = new Rect();

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback( this );

        // register our interest in hearing about changes to the BattleField2D
        mObserver = new BattleFieldObserverProxy();

        mThread = new BattleThread2D( this, oController, mBattle, holder, mContext );
        mContext = null;
    }

    /**
     * Fetches the animation thread corresponding to this BattleView.
     *
     * @return the animation thread
     */
    public BattleThread2D GetBattleThread()
    {
        return mThread;
    }

    /**
     * Standard override to get key-press events.
     */
    @Override
    public boolean onKeyDown( int iKeyCode, KeyEvent oMsg )
    {
        if ( mThread != null )
        {
            return mThread.doKeyDown( iKeyCode, oMsg );
        }

        return false;
    }

    /**
     * Standard override for key-up. We actually care about these, so we can
     * turn off the engine or stop rotating.
     */
    @Override
    public boolean onKeyUp( int iKeyCode, KeyEvent oMsg )
    {
        if ( mThread != null )
        {
            return mThread.doKeyUp( iKeyCode, oMsg );
        }

        return false;
    }

    /**
     * Standard window-focus override. Notice focus lost so we can pause on
     * focus lost. e.g. user switches to take a call.
     */
    @Override
    public void onWindowFocusChanged( boolean bHasWindowFocus )
    {
        if ( mThread != null )
        {
            if ( !bHasWindowFocus )
            {
                mThread.pause();
            }
            else
            {
                mThread.unpause();
            }
        }
    }

    /**
     * Installs a pointer to the text view used for messages.
     */
    //public void setTextView( TextView textView )
    //{
    //mStatusText = textView;
    //}

    /**
     * Callback invoked when the surface dimensions change.
     */
    public void surfaceChanged( SurfaceHolder oHolder, int iFormat, int iWidth,
                                int iHeight )
    {
        mThread.SetSurfaceSize( iWidth, iHeight );
        UpdateCenterCellConstraints();
        CenterOn( BattleField2D.GetInstance().GetSelectedCell().GetPos() );
    }

    public void UpdateCenterCellConstraints()
    {
        // The number of cells that can be represented in each dimension
        mCellsDisplayedWidth = ( int ) Math.floor( ( double ) mThread.GetSurfaceWidth() /
                                                           ( double ) ( DisplayConstants2D.GetCellWidth() * mZoomFactor ) );
        mCellsDisplayedHeight = ( int ) Math.floor( ( double ) mThread.GetSurfaceHeight() /
                                                            ( double ) ( DisplayConstants2D.GetCellHeight() * mZoomFactor ) );

        // "full" cells display is always an odd number
        if ( Orientation.modulo( mCellsDisplayedWidth, 2 ) == 0 )
        {
            mCellsDisplayedWidth -= 1;
        }
        if ( Orientation.modulo( mCellsDisplayedHeight, 2 ) == 0 )
        {
            mCellsDisplayedHeight -= 1;
        }

        Orientation oCurOrientation = mThread.GetDrawingOrientation();
        int iRemainingX = 0, iRemainingY = 0;

        // How many remaining cells can be fit
        switch ( oCurOrientation )
        {
            case NORTH:
            case SOUTH:
                iRemainingX = Math.max( 0,
                                        BattleField2D.GetInstance().GetWidth() - mCellsDisplayedWidth );
                iRemainingY = Math.max( 0,
                                        BattleField2D.GetInstance().GetHeight() - mCellsDisplayedHeight );
                mMinCenterCellX = ( int ) Math.floor(
                        ( BattleField2D.GetInstance().GetWidth() - iRemainingX ) / ( double ) 2 );
                mMinCenterCellY = ( int ) Math.floor(
                        ( BattleField2D.GetInstance().GetHeight() - iRemainingY ) / ( double ) 2 );
                break;
            case EAST:
            case WEST:
                iRemainingX = Math.max( 0,
                                        BattleField2D.GetInstance().GetHeight() - mCellsDisplayedWidth );
                iRemainingY = Math.max( 0,
                                        BattleField2D.GetInstance().GetWidth() - mCellsDisplayedHeight );
                mMinCenterCellX = ( int ) Math.floor(
                        ( BattleField2D.GetInstance().GetHeight() - iRemainingX ) / ( double ) 2 );
                mMinCenterCellY = ( int ) Math.floor(
                        ( BattleField2D.GetInstance().GetWidth() - iRemainingY ) / ( double ) 2 );
                break;
            case NONE:
                // Cannot happen
                break;
        }

        mMaxCenterCellX = mMinCenterCellX + iRemainingX;
        mMaxCenterCellY = mMinCenterCellY + iRemainingY;

        // Update centered cell if not within min/max range
        CenterOn( mCenterCellPos );
    }

    /**
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated( SurfaceHolder oHolder )
    {
        Logger.i( "surfaceCreated()" );
        if ( mThread != null )
        {
            mOrientationListener.enable();
            mThread.doStart();
        }
    }

    /**
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed( SurfaceHolder oHolder )
    {
        Logger.i( "surfaceDestroyed()" );
        if ( mOrientationListener != null )
        {
            mOrientationListener.disable();
            mOrientationListener = null;
        }

        // free resources
        if ( mThread != null )
        {
            mThread.doStop();
            mThread = null;
        }

        if ( mObserver != null )
        {
            mObserver.detach();
            mObserver = null;
        }

        // Do not forget to remove the callback
        getHolder().removeCallback( this );
    }

    /**
     * Called when users touches the screen: handling is performed by BattleThread2D object
     * (non-Javadoc)
     *
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent( MotionEvent oEvent )
    {
        if ( mThread != null )
        {
            return mThread.onTouchEvent( oEvent );
        }

        return false;
    }

    /**
     * Centers the display on the Given position
     */
    public void CenterOn( Position oPos )
    {
        switch ( mThread.GetDrawingOrientation() )
        {
            case SOUTH:
            case NORTH:
                mCenterCellPos.mPosX = Math.max( mMinCenterCellX,
                                                 Math.min( mMaxCenterCellX, oPos.mPosX ) );
                mCenterCellPos.mPosY = Math.max( mMinCenterCellY,
                                                 Math.min( mMaxCenterCellY, oPos.mPosY ) );
                break;
            case EAST:
            case WEST:
                mCenterCellPos.mPosX = Math.max( mMinCenterCellY,
                                                 Math.min( mMaxCenterCellY, oPos.mPosX ) );
                mCenterCellPos.mPosY = Math.max( mMinCenterCellX,
                                                 Math.min( mMaxCenterCellX, oPos.mPosY ) );
                break;
            case NONE:
                // Should not happen
                break;
        }
    }

    public Position GetCenterCellCoords()
    {
        return mCenterCellPos;
    }

    public float GetZoomFactor()
    {
        return mZoomFactor;
    }

    public void SetZoomFactor( float fNewZoomFactor )
    {
        float fStep = DisplayConstants2D.GetZoomStep();
        int iZoomStep = ( int ) ( fNewZoomFactor / fStep );
        mZoomFactor = Math.max( DisplayConstants2D.GetZoomMin(),
                                Math.min( DisplayConstants2D.GetZoomMax(), iZoomStep * fStep ) );

        UpdateCenterCellConstraints();
        CenterOn( BattleField2D.GetInstance().GetSelectedCell().GetPos() );
    }

    public void ZoomIn()
    {
        synchronized ( BattleThread2D.mDrawingLock )
        {
            SetZoomFactor( mZoomFactor + DisplayConstants2D.GetZoomStep() );
        }
    }

    public void ZoomOut()
    {
        synchronized ( BattleThread2D.mDrawingLock )
        {
            SetZoomFactor( mZoomFactor - DisplayConstants2D.GetZoomStep() );
        }
    }

    public void Update( Observable oObs, Object oObj )
    {
        // Center view on selected cell
        if ( oObs instanceof BattleField )
        {
            BattleField oBattleField = ( BattleField ) oObs;
            CenterOn( oBattleField.GetSelectedCell().GetPos() );
        }
    }

    public Rect GetCenterZoneRect()
    {
        int iHeightThird = mThread.GetSurfaceHeight() / 3;
        int iWidthThird = mThread.GetSurfaceWidth() / 3;
        mCenterZoneRect.set( iWidthThird, iHeightThird, 2 * iWidthThird, 2 * iHeightThird );
        return mCenterZoneRect;
    }
}
