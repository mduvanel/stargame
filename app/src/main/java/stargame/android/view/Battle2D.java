package stargame.android.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import stargame.android.model.Battle;
import stargame.android.model.BattleAction;
import stargame.android.model.BattleDialog;
import stargame.android.model.BattleUnit;
import stargame.android.util.Logger;
import stargame.android.util.Orientation;
import stargame.android.util.Position;
import stargame.android.view.action.BattleAction2D;
import stargame.android.view.action.BattleAction2DFactory;
import stargame.android.view.action.BattleActionTarget2D;

public class Battle2D implements Observer
{
    /**
     * The context of the app
     */
    private Context mContext;

    public Context GetContext()
    {
        return mContext;
    }

    /**
     * The battle from the model
     */
    private Battle mBattle;

    /**
     * The current target animation to display
     */
    private BattleActionTarget2D mBattleActionTarget;

    /**
     * The current BattleAction2D animations
     */
    private BattleAction2D mCurrentBattleAction;

    public BattleAction2D GetCurrentBattleAction()
    {
        return mCurrentBattleAction;
    }

    /**
     * Member handling dialogs drawing
     */
    private DialogDrawer mDialogDrawer;

    public DialogDrawer GetDialogDrawer()
    {
        return mDialogDrawer;
    }

    /**
     * This stores the current zoom factor used to display the battle
     */
    private float mZoomFactor;

    public void SetZoom( float fNewZoom )
    {
        mZoomFactor = fNewZoom;
    }

    public float GetCurrentZoom()
    {
        return mZoomFactor;
    }

    /**
     * This stores the current offset in X and Y directions used to display the battle
     */
    private int mOffsetX;
    private int mOffsetY;

    public int GetOffsetX()
    {
        return mOffsetX;
    }

    public int GetOffsetY()
    {
        return mOffsetY;
    }

    public void SetOffset( int iNewOffsetX, int iNewOffsetY )
    {
        mOffsetX = iNewOffsetX;
        mOffsetY = iNewOffsetY;
    }

    private Vector< BattleUnit2D > mVecUnits2D;

    public Battle2D( Battle oBattle, Context oContext )
    {
        mBattle = oBattle;
        mBattle.addObserver( this );
        mContext = oContext;
        Resources oRes = oContext.getResources();

        mZoomFactor = 1;
        mOffsetX = 0;
        mOffsetY = 0;

        mBattleActionTarget = new BattleActionTarget2D( null, oRes );
        mVecUnits2D = new Vector< BattleUnit2D >( oBattle.GetNbUnits() );
        mCurrentBattleAction = null;

        // Instantiate the new BattleField2D singleton
        BattleField2D.Reset();
        BattleField2D.GetInstance().Init( oBattle.GetBattleField(), oRes );

        // Create the dependent 2D structures
        Vector< BattleUnit > vecUnits = oBattle.GetUnits();
        for ( BattleUnit oUnit : vecUnits )
        {
            BattleUnit2D oUnit2D = new BattleUnit2D( oUnit, oContext,
                                                     BattleField2D.GetInstance().GetDrawableList() );
            oUnit2D.SetBaseAnimation( "Stand" );
            mVecUnits2D.add( oUnit2D );
        }

        mDialogDrawer = new DialogDrawer( oRes );
    }

    public void SetDrawingOrientation( Orientation oOrientation )
    {
        BattleField2D.GetInstance().SetDrawingOrientation( oOrientation );

        for ( BattleUnit2D oUnit2D : mVecUnits2D )
        {
            oUnit2D.SetDrawingOrientation( oOrientation );
        }

        // this will force an update of the current action
        mBattleActionTarget.UpdateDrawables( null );
    }

    public void doDraw( Canvas oCanvas, float fZoom, Position oPosition )
    {
        BattleField2D.GetInstance().doDraw( oCanvas, fZoom, oPosition );
        mDialogDrawer.doDraw( oCanvas );
    }

    public void update( Observable oObs, Object oObj )
    {
        if ( oObs instanceof BattleAction )
        {
            // BattleAction notifies us of some change
            BattleAction oAction = ( BattleAction ) oObs;

            switch ( oAction.GetState() )
            {
                case STATE_READY:
                    // Do nothing until the action is ready
                    break;
                case STATE_EXECUTING:

                    if ( null == mCurrentBattleAction )
                    {
                        mCurrentBattleAction = BattleAction2DFactory.GetInstance().BattleAction2DCreate(
                                mBattle.GetCurrentAction(), this );

                        // Clear the targets while executing the action
                        mBattleActionTarget.SetBattleAction( null );
                    }
                    else
                    {
                        // Update status
                        mCurrentBattleAction.Update( oAction );
                    }

                    try
                    {
                        while ( !mBattle.IsDialogFinished() )
                        {
                            // release the lock to allow drawing to take place
                            BattleThread2D.mDrawingLock.notifyAll();
                            BattleThread2D.mDrawingLock.wait( 40 );
                        }

                        // Start drawing and wait for animation to be done
                        mCurrentBattleAction.EnableDrawables();
                        while ( !mCurrentBattleAction.isFinished() )
                        {
                            // release the lock to allow drawing to take place
                            BattleThread2D.mDrawingLock.notifyAll();
                            BattleThread2D.mDrawingLock.wait( 40 );
                        }
                    }
                    catch ( Exception e )
                    {
                        e.printStackTrace();
                    }

                    break;
                case STATE_DONE:
                    mCurrentBattleAction = null;
                    mBattleActionTarget.SetBattleAction( null );
                    break;
            }
        }
        else if ( oObs instanceof Battle )
        {
            // Update action from the Battle (can be the same as previous)
            BattleAction oAction = mBattle.GetCurrentAction();
            mBattleActionTarget.SetBattleAction( oAction );

            // Subscribe to this action's notifications
            if ( oAction != null )
            {
                oAction.addObserver( this );
            }

            // Update dialogs
            UpdateDialogs();
        }
    }

    public BattleUnit2D GetBattleUnit2D( BattleUnit oUnit )
    {
        for ( BattleUnit2D oUnit2D : mVecUnits2D )
        {
            if ( oUnit2D.IsUnit( oUnit ) )
            {
                return oUnit2D;
            }
        }

        return null;
    }

    /**
     * Will update the given drawer with current dialog
     */
    public void UpdateDialogs()
    {
        BattleDialog oCurDialog = mBattle.GetCurrentDialog();
        if ( oCurDialog != null && oCurDialog.HasChanged() )
        {
            if ( !oCurDialog.IsDialogFinished() )
            {
                Bitmap oPortrait = GetBattleUnit2D(
                        oCurDialog.GetCurrentDialogUnit() ).GetPortraitBitmap();
                String strText = mContext.getString( oCurDialog.GetCurrentDialogID() );
                mDialogDrawer.SetDialog( oPortrait, strText );
                Logger.i( "UpdateDialogs() called with unfinished dialog: setting next Dialog." );
            }
            else
            {
                // Cleanup ??
                Logger.i( "UpdateDialogs() called with finished dialog" );
            }
            oCurDialog.ClearChanged();
        }
    }
}
