package stargame.android.controller;

import android.content.Context;
import android.os.Bundle;

import java.util.Iterator;

import stargame.android.R;
import stargame.android.model.Battle;
import stargame.android.model.BattleAction;
import stargame.android.util.Logger;
import stargame.android.util.Orientation;
import stargame.android.util.Position;
import stargame.android.util.Tree;
import stargame.android.view.BattleThread2D;
import stargame.android.view.InterfaceDrawer;

/**
 * Class DPadController encapsulates Battle Control based on 4 directions + Center
 * interface. The interface has its own state machine depending on the possible
 * actions for the unit
 *
 * @author Duduche
 */
public class DPadController
{
    /**
     * Context for fetching resources
     */
    private Context mContext;

    /**
     * The battle that is controlled
     */
    private Battle mBattle;

    /**
     * The Interface Drawer that should be notified of changes
     */
    private InterfaceDrawer mInterfaceDrawer;

    /**
     * Enum for the DPadController state machine
     */
    private enum ControlState
    {
        MENU_HIGH_LEVEL,
        MENU_ACTIONS,
        STATE_SELECT_DIR,
        STATE_ACTION,
        STATE_VIEW,
        STATE_NONE
    }

    /**
     * Enum for describing possible DPad actions
     */
    public enum DPadAction
    {
        DPAD_UP,
        DPAD_DOWN,
        DPAD_LEFT,
        DPAD_RIGHT,
        DPAD_CENTER,
        DPAD_BACK
    }

    private ControlState mControlState;

    private final static String M_CONTROL_STATE = "State";

    /**
     * Action menu position
     */
    private int mActionMenuPos;

    private final static String M_MENU_POS = "MenuPos";

    public DPadController( Battle oBattle, InterfaceDrawer oInterfaceDrawer, Context oContext )
    {
        mBattle = oBattle;
        mInterfaceDrawer = oInterfaceDrawer;
        mContext = oContext;
        Reset();
    }

    /**
     * Resets the state of the controller when a new Unit takes its turn
     */
    public void Reset()
    {
        mControlState = ControlState.MENU_HIGH_LEVEL;
        mActionMenuPos = 0;
    }

    /**
     * End of turn procedure
     */
    public void EndTurn()
    {
        mBattle.NextUnit();
        Reset();
        mBattle.SetSelectedPosition( mBattle.GetActiveUnit().GetCell().GetPos() );
    }

    public void NotifyInterfaceDrawer()
    {
        String strTop = "", strBottom = "", strLeft = "", strRight = "", strCenter = "";

        switch ( mControlState )
        {
            case MENU_ACTIONS:
                strTop = GetTopActionString();
                strBottom = GetBottomActionString();
                strLeft = GetLeftActionString();
                strRight = GetRightActionString();
                break;
            case MENU_HIGH_LEVEL:
                if ( !mBattle.GetActiveUnit().IsMovePerformed() )
                {
                    strTop = mContext.getString( R.string.move_action );
                }
                strBottom = mContext.getString( R.string.end_turn );
                strLeft = mContext.getString( R.string.free_view );
                if ( !mBattle.GetActiveUnit().IsActionPerformed() )
                {
                    strRight = mContext.getString( R.string.actions );
                }
                break;
            case STATE_NONE:
                // Nothing to do (empty strings)
                break;
            case STATE_ACTION:
            case STATE_VIEW:
                strCenter = "toto";
            case STATE_SELECT_DIR:
                strTop = mContext.getString( R.string.up );
                strBottom = mContext.getString( R.string.down );
                strLeft = mContext.getString( R.string.left );
                strRight = mContext.getString( R.string.right );
                break;
        }
        mInterfaceDrawer.SetStrings( strTop, strBottom, strLeft, strRight, strCenter );
    }

    private int GetNbMenuScreens( Tree< BattleAction > oActions )
    {
        int iNbElements = GetNbElements( oActions, R.string.move_action );

        if ( iNbElements < 5 )
        {
            return 1;
        }
        else
        {
            return ( iNbElements - 2 ) / 2;
        }
    }

    /**
     * Get the n-th BattleAction, excluding the ActionType that is excluded
     */
    private BattleAction GetElement( int iIndex, Tree< BattleAction > treeActions,
                                     int iExceptActionType )
    {
        if ( iIndex < 0 || iIndex > GetNbElements( treeActions, R.string.move_action ) - 1 )
        {
            return null;
        }

        // TODO: fix 0-based
        Iterator< BattleAction > oIterator = treeActions.iterator();
        BattleAction oAction;
        int iCount = -1;

        do
        {
            oAction = oIterator.next();
            if ( oAction.GetActionType() != iExceptActionType )
            {
                iCount++;
            }
        } while ( iCount < iIndex );
        return oAction;
    }

    private int GetNbElements( Tree< BattleAction > treeActions, int iExceptActionType )
    {
        int iCount = treeActions.GetNbElements();
        for ( BattleAction oAction : treeActions )
        {
            if ( oAction.GetActionType() == iExceptActionType )
            {
                iCount--;
            }
        }
        return iCount;
    }

    private String GetTopActionString()
    {
        Tree< BattleAction > treeActions = mBattle.GetActiveUnit().GetActions();
        if ( mActionMenuPos > 0 )
        {
            return mContext.getString( R.string.previous_action );
        }
        else
        {
            return GetElement( 0, treeActions, R.string.move_action ).GetName( mContext );
        }
    }

    private String GetLeftActionString()
    {
        Tree< BattleAction > treeActions = mBattle.GetActiveUnit().GetActions();
        if ( GetNbElements( treeActions, R.string.move_action ) > 1 )
        {
            return GetElement( 1 + mActionMenuPos * 2, treeActions, R.string.move_action ).GetName(
                    mContext );
        }
        else
        {
            return "";
        }
    }

    private String GetRightActionString()
    {
        Tree< BattleAction > treeActions = mBattle.GetActiveUnit().GetActions();
        if ( GetNbElements( treeActions, R.string.move_action ) > 2 )
        {
            return GetElement( 2 + mActionMenuPos * 2, treeActions, R.string.move_action ).GetName(
                    mContext );
        }
        else
        {
            return "";
        }
    }

    private String GetBottomActionString()
    {
        Tree< BattleAction > treeActions = mBattle.GetActiveUnit().GetActions();
        int iNbMenuScreens = GetNbMenuScreens( treeActions );
        if ( mActionMenuPos < iNbMenuScreens - 1 )
        {
            return mContext.getString( R.string.next_action );
        }
        else
        {
            // determine if there should be an empty string or the last action's name
            int iNbActions = GetNbElements( treeActions, R.string.move_action );
            if ( ( iNbMenuScreens == 1 && iNbActions < 4 ) || Orientation.modulo( iNbActions,
                                                                                  2 ) != 0 )
            {
                return "";
            }
            else
            {
                return treeActions.GetElement( 3 + mActionMenuPos * 2 ).GetName( mContext );
            }
        }
    }

    private void DisplayState()
    {
        Logger.i( String.format( "Current Controller state: %s", mControlState.name() ) );
    }

    public void BackAction()
    {
        switch ( mControlState )
        {
            case MENU_HIGH_LEVEL:
            case STATE_NONE:
                break; // Cannot cancel or go back
            case STATE_SELECT_DIR:
            case MENU_ACTIONS:
                mControlState = ControlState.MENU_HIGH_LEVEL;
                break;
            case STATE_ACTION:
                mActionMenuPos = 0;

                // We don't go back to the same menu if we are on MOVE action or other
                if ( mBattle.GetCurrentAction().GetActionType() == R.string.move_action )
                {
                    mControlState = ControlState.MENU_HIGH_LEVEL;
                }
                else
                {
                    mControlState = ControlState.MENU_ACTIONS;
                }

                // Suppress active action
                mBattle.SetCurrentAction( null );

                // Center back on the active unit
                mBattle.SetSelectedPosition( mBattle.GetActiveUnit().GetCell().GetPos() );
                break;
            case STATE_VIEW:
                mControlState = ControlState.MENU_HIGH_LEVEL;

                // Center back on the active unit
                mBattle.SetSelectedPosition( mBattle.GetActiveUnit().GetCell().GetPos() );
                break;
        }
        NotifyInterfaceDrawer();
        DisplayState();
    }

    public void CenterAction()
    {
        switch ( mControlState )
        {
            case MENU_HIGH_LEVEL:
            case MENU_ACTIONS:
            case STATE_NONE:
            case STATE_SELECT_DIR:
                break; // no action mapped
            case STATE_ACTION:
                mBattle.GetCurrentAction().SetTarget( mBattle.GetSelectedCell() );

                if ( mBattle.GetCurrentAction().PerformAction() )
                {
                    mControlState = ControlState.MENU_HIGH_LEVEL;
                    DisplayState();

                    synchronized ( BattleThread2D.mDrawingLock )
                    {
                        // Finish action only with lock taken: avoid showing Interface during one frame
                        mBattle.GetCurrentAction().FinishAction();

                        // Center back on the active unit
                        mBattle.SetSelectedPosition( mBattle.GetActiveUnit().GetCell().GetPos() );
                        NotifyInterfaceDrawer();
                        mBattle.SetCurrentAction( null );
                    }
                }
                break;
            case STATE_VIEW:
                // TODO: display unit status
                break;
        }
    }

    public void UpAction( Orientation oOrientation )
    {
        switch ( mControlState )
        {
            case MENU_HIGH_LEVEL:
                // Up is Move
                if ( !mBattle.GetActiveUnit().IsMovePerformed() )
                {
                    mBattle.SetCurrentAction(
                            mBattle.GetActiveUnit().GetAction( R.string.move_action ) );
                    mControlState = ControlState.STATE_ACTION;
                }
                break;
            case MENU_ACTIONS:
                if ( mActionMenuPos == 0 )
                {
                    // Action 0 selected: find the first non-move action
                    Iterator< BattleAction > it = mBattle.GetActiveUnit().GetActions().iterator();
                    BattleAction oAction = it.next();
                    if ( oAction.GetActionType() == R.string.move_action )
                    {
                        mBattle.SetCurrentAction( it.next() );
                    }
                    else
                    {
                        mBattle.SetCurrentAction( oAction );
                    }
                    mControlState = ControlState.STATE_ACTION;
                }
                else
                {
                    // Go up one menu screen
                    mActionMenuPos--;
                }
                break;
            case STATE_NONE:
                break;
            case STATE_SELECT_DIR:
                mBattle.GetActiveUnit().SetOrientation(
                        Orientation.NORTH.TransformOrientation( oOrientation ) );
                EndTurn();
                break;
            case STATE_ACTION:
            case STATE_VIEW:
                mBattle.SetSelectedPosition(
                        Position.OffsetPositionRelative( mBattle.GetSelectedPosition(),
                                                         oOrientation, 0, -1 ) );
                break;
        }
        NotifyInterfaceDrawer();
        DisplayState();
    }

    public void DownAction( Orientation oOrientation )
    {
        switch ( mControlState )
        {
            case MENU_HIGH_LEVEL:
                mControlState = ControlState.STATE_SELECT_DIR;
                break;
            case MENU_ACTIONS:
                Tree< BattleAction > treeActions = mBattle.GetActiveUnit().GetActions();
                int iNbMenuScreens = GetNbMenuScreens( treeActions );
                int iActionIndex = 3 + 2 * mActionMenuPos;
                int iNbElements = GetNbElements( treeActions, R.string.move_action );

                if ( iNbMenuScreens < mActionMenuPos )
                {
                    // We go down one screen
                    mActionMenuPos++;
                }
                else if ( iNbElements - 1 == iActionIndex )
                {
                    mBattle.SetCurrentAction(
                            GetElement( iNbElements - 1, treeActions, R.string.move_action ) );
                    mControlState = ControlState.STATE_ACTION;
                }
                break;
            case STATE_NONE:
                break;
            case STATE_SELECT_DIR:
                mBattle.GetActiveUnit().SetOrientation(
                        Orientation.SOUTH.TransformOrientation( oOrientation ) );
                EndTurn();
                break;
            case STATE_ACTION:
            case STATE_VIEW:
                mBattle.SetSelectedPosition(
                        Position.OffsetPositionRelative( mBattle.GetSelectedPosition(),
                                                         oOrientation, 0, 1 ) );
                break;
        }
        NotifyInterfaceDrawer();
        DisplayState();
    }

    public void LeftAction( Orientation oOrientation )
    {
        switch ( mControlState )
        {
            case MENU_HIGH_LEVEL:
                // Left is free view
                mControlState = ControlState.STATE_VIEW;
                break;
            case MENU_ACTIONS:
                // We set the current action of the Tree (if there is one)
                Tree< BattleAction > treeActions = mBattle.GetActiveUnit().GetActions();
                BattleAction oAction = GetElement( 1 + mActionMenuPos * 2, treeActions,
                                                   R.string.move_action );
                if ( null != oAction )
                {
                    mBattle.SetCurrentAction( oAction );
                    mControlState = ControlState.STATE_ACTION;
                }
                break;
            case STATE_NONE:
                break;
            case STATE_SELECT_DIR:
                mBattle.GetActiveUnit().SetOrientation(
                        Orientation.WEST.TransformOrientation( oOrientation ) );
                EndTurn();
                break;
            case STATE_ACTION:
            case STATE_VIEW:
                mBattle.SetSelectedPosition(
                        Position.OffsetPositionRelative( mBattle.GetSelectedPosition(),
                                                         oOrientation, -1, 0 ) );
                break;
        }
        NotifyInterfaceDrawer();
        DisplayState();
    }

    public void RightAction( Orientation oOrientation )
    {
        switch ( mControlState )
        {
            case MENU_HIGH_LEVEL:
                // Right is Actions menu
                if ( !mBattle.GetActiveUnit().IsActionPerformed() )
                {
                    mControlState = ControlState.MENU_ACTIONS;
                }
                break;
            case MENU_ACTIONS:
                Tree< BattleAction > treeActions = mBattle.GetActiveUnit().GetActions();
                BattleAction oAction = GetElement( 2 + mActionMenuPos * 2, treeActions,
                                                   R.string.move_action );
                if ( null != oAction )
                {
                    mBattle.SetCurrentAction( oAction );
                    mControlState = ControlState.STATE_ACTION;
                }
                break;
            case STATE_NONE:
                break;
            case STATE_SELECT_DIR:
                mBattle.GetActiveUnit().SetOrientation(
                        Orientation.EAST.TransformOrientation( oOrientation ) );
                EndTurn();
                break;
            case STATE_ACTION:
            case STATE_VIEW:
                mBattle.SetSelectedPosition(
                        Position.OffsetPositionRelative( mBattle.GetSelectedPosition(),
                                                         oOrientation, 1, 0 ) );
                break;
        }
        NotifyInterfaceDrawer();
        DisplayState();
    }

    public void SaveState( Bundle oObjectMap )
    {
        oObjectMap.putInt( M_CONTROL_STATE, mControlState.ordinal() );
        oObjectMap.putInt( M_MENU_POS, mActionMenuPos );
    }

    public void LoadState( Bundle oObjectMap )
    {
        mControlState = ControlState.values()[ oObjectMap.getInt( M_CONTROL_STATE ) ];
        mActionMenuPos = oObjectMap.getInt( M_MENU_POS );
    }
}
