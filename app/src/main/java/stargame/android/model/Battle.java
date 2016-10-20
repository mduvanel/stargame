package stargame.android.model;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;

import java.util.Observable;
import java.util.Vector;

import stargame.android.model.jobs.JobType;
import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;
import stargame.android.util.Orientation;
import stargame.android.util.Position;

/**
 * This is the Battle class, containing the battle logic.
 */
public class Battle extends Observable implements ISavable
{
    /**
     * All the units taking part in the battle
     */
    private Vector< BattleUnit > mVecUnits;

    public Vector< BattleUnit > GetUnits()
    {
        return mVecUnits;
    }

    private final static String M_VEC_UNITS_NAME = "Unit";

    /**
     * All the end conditions for this battle
     */
    private Vector< EndCondition > mVecEndConditions;

    private final static String M_VEC_END_CONDITIONS_NAME = "Condition";

    /**
     * The currently active unit
     */
    private BattleUnit mActiveUnit;

    public BattleUnit GetActiveUnit()
    {
        return mActiveUnit;
    }

    private final static String M_ACTIVE_UNIT_NAME = "ActiveUnit";

    /**
     * The BattleField
     */
    private BattleField mBattleField;

    public BattleField GetBattleField()
    {
        return mBattleField;
    }

    private final static String M_BATTLEFIELD_NAME = "Battlefield";

    /**
     * The current BattleAction
     */
    private BattleAction mCurrentAction;

    public BattleAction GetCurrentAction()
    {
        return mCurrentAction;
    }

    private final static String M_CURRENT_ACTION_NAME = "Action";

    /**
     * The current BattleDialog
     */
    private BattleDialog mCurrentDialog;

    public BattleDialog GetCurrentDialog()
    {
        return mCurrentDialog;
    }

    private final static String M_CURRENT_DIALOG_NAME = "Dialog";

    private Battle()
    {
        mBattleField = null;
        mVecUnits = new Vector< BattleUnit >();
        Init();
    }

    private Battle( XmlResourceParser oParser, Resources oResources )
    {
        mBattleField = new BattleField( oParser );
        mBattleField.SetSelectedCell( new Position( 2, 3 ) );

        BattleUnit oUnit;
        mVecUnits = new Vector< BattleUnit >();
        oUnit = new BattleUnit( mBattleField.GetCell( new Position( 1, 3 ) ), Orientation.EAST,
                                JobType.TYPE_SOLDIER, this, oResources );
        oUnit.GetUnit().SetFaction( Faction.TYPE_MAGIK );
        mVecUnits.add( oUnit );
        oUnit = new BattleUnit( mBattleField.GetCell( new Position( 3, 3 ) ), Orientation.SOUTH,
                                JobType.TYPE_MAGE, this, oResources );
        oUnit.GetUnit().SetFaction( Faction.TYPE_TECHNIK );
        mVecUnits.add( oUnit );
        oUnit = new BattleUnit( mBattleField.GetCell( new Position( 4, 3 ) ), Orientation.EAST,
                                JobType.TYPE_PRIEST, this, oResources );
        oUnit.GetUnit().SetFaction( Faction.TYPE_MAGIK );
        mVecUnits.add( oUnit );
        oUnit = new BattleUnit( mBattleField.GetCell( new Position( 1, 1 ) ), Orientation.NORTH,
                                JobType.TYPE_ROGUE, this, oResources );
        oUnit.GetUnit().SetFaction( Faction.TYPE_TECHNIK );
        mVecUnits.add( oUnit );
        oUnit = new BattleUnit( mBattleField.GetCell( new Position( 1, 4 ) ), Orientation.WEST,
                                JobType.TYPE_BARBARIAN, this, oResources );
        oUnit.GetUnit().SetFaction( Faction.TYPE_TECHNIK );
        mVecUnits.add( oUnit );
        Init();

        // Start by having an active unit
        NextUnit();
    }

    private void Init()
    {
        mActiveUnit = null;
        mCurrentAction = null;
        mCurrentDialog = null;
        mVecEndConditions = new Vector< EndCondition >();
    }

    /**
     * public factory to create a battle
     */
    public static Battle createBattle( Resources oRes, String strName )
    {
        XmlResourceParser oParser = oRes.getXml( oRes.getIdentifier( strName, null, null ) );
        return new Battle( oParser, oRes );
    }

    public void SetCurrentDialog( BattleDialog oBattleDialog ) throws Exception
    {
        if ( mCurrentDialog == null || mCurrentDialog.IsDialogFinished() )
        {
            mCurrentDialog = oBattleDialog;
            NotifyChange();
        }
        else
        {
            throw new Exception();
        }
    }

    public void NextDialog() throws Exception
    {
        if ( mCurrentDialog != null && !mCurrentDialog.IsDialogFinished() )
        {
            mCurrentDialog.NextDialog();
            NotifyChange();
        }
        else
        {
            throw new Exception();
        }
    }

    public boolean IsDialogFinished()
    {
        return mCurrentDialog == null || mCurrentDialog.IsDialogFinished();
    }

    public void SetCurrentAction( BattleAction oAction )
    {
        if ( mCurrentAction != null )
        {
            // Cleanup observers
            mCurrentAction.deleteObservers();
        }

        mCurrentAction = oAction;
        if ( null != oAction )
        {
            oAction.PrepareAction();
        }
        NotifyChange();
    }

    public int GetNbUnits()
    {
        return mVecUnits.size();
    }

    public Position GetSelectedPosition()
    {
        return mBattleField.GetSelectedCell().GetPos();
    }

    public BattleCell GetSelectedCell()
    {
        return mBattleField.GetSelectedCell();
    }

    public void SetSelectedPosition( Position oPosition )
    {
        mBattleField.SetSelectedCell( oPosition );
    }

    /**
     * End the current unit turn and gives the turn to the next unit
     */
    public void NextUnit()
    {
        mActiveUnit = null;
        boolean bDone = false;
        float fBestReadiness = -1;
        //boolean bVictory;

        while ( !bDone )
        {
            // Check if any end condition is met first...
            for ( EndCondition oCond : mVecEndConditions )
            {
                if ( oCond.IsThisTheEnd( this ) )
                {
                    bDone = true;
                    //bVictory = oCond.IsVictory();
                    break;
                }
            }

            // End condition!
            if ( bDone )
            {
                break;
            }

            // Check if any Unit is ready, pick the "most ready"
            for ( BattleUnit oUnit : mVecUnits )
            {
                if ( oUnit.IsReady() && oUnit.ReadySince() > fBestReadiness )
                {
                    bDone = true;
                    fBestReadiness = oUnit.ReadySince();
                    mActiveUnit = oUnit;
                }
            }

            // If not, step all units and loop
            if ( !bDone )
            {
                for ( BattleUnit oUnit : mVecUnits )
                {
                    oUnit.TimeStep();
                }
            }
            else
            {
                // Activate unit's turn
                mActiveUnit.TurnPassed();

                // Check that unit is still alive (poison or bleed?)
                if ( mActiveUnit.GetCurrentHitPoints() == 0 )
                {
                    bDone = false;
                    // TODO: notify early to allow animations?
                }
            }
        }

        // Move selected cell on new active unit
        mBattleField.SetSelectedCell( mActiveUnit.GetCell().GetPos() );
        NotifyChange();
    }

    /**
     * Made public so that Controller can send notifications too...
     */
    public void NotifyChange()
    {
        setChanged();
        notifyObservers();
    }

    public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
    {
        String strObjKey;
        int iIndex = 0;
        oObjectMap.putInt( M_VEC_END_CONDITIONS_NAME, mVecEndConditions.size() );
        for ( EndCondition oCondition : mVecEndConditions )
        {
            strObjKey = SavableHelper.saveInMap( oCondition, oGlobalMap );
            oObjectMap.putString( String.format( "%s_%d", M_VEC_END_CONDITIONS_NAME, iIndex++ ),
                                  strObjKey );
        }

        oObjectMap.putInt( M_VEC_UNITS_NAME, mVecUnits.size() );
        iIndex = 0;
        for ( BattleUnit oUnit : mVecUnits )
        {
            strObjKey = SavableHelper.saveInMap( oUnit, oGlobalMap );
            oObjectMap.putString( String.format( "%s_%d", M_VEC_UNITS_NAME, iIndex++ ), strObjKey );
            if ( mActiveUnit == oUnit )
            {
                oObjectMap.putString( M_ACTIVE_UNIT_NAME, strObjKey );
            }
        }

        strObjKey = SavableHelper.saveInMap( mBattleField, oGlobalMap );
        oObjectMap.putString( M_BATTLEFIELD_NAME, strObjKey );

        strObjKey = SavableHelper.saveInMap( mCurrentAction, oGlobalMap );
        oObjectMap.putString( M_CURRENT_ACTION_NAME, strObjKey );

        strObjKey = SavableHelper.saveInMap( mCurrentDialog, oGlobalMap );
        oObjectMap.putString( M_CURRENT_DIALOG_NAME, strObjKey );
    }

    public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
    {
        return loadState( oGlobalMap, strObjKey );
    }

    public static Battle loadState( Bundle oGlobalMap, String strObjKey )
    {
        Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey,
                                                             Battle.class.getName() );

        if ( oObjectBundle == null )
        {
            return null;
        }

        Battle oBattle = new Battle();

        String strKey;
        int iCount = oObjectBundle.getInt( M_VEC_END_CONDITIONS_NAME );
        oBattle.mVecEndConditions.setSize( iCount );
        for ( int i = 0; i < iCount; ++i )
        {
            strKey = oGlobalMap.getString( String.format( "%s_%d", M_VEC_END_CONDITIONS_NAME, i ) );
            EndCondition oCondition = EndCondition.loadState( oGlobalMap, strKey );
            oBattle.mVecEndConditions.set( i, oCondition );
        }

        iCount = oObjectBundle.getInt( M_VEC_UNITS_NAME );
        oBattle.mVecUnits.setSize( iCount );
        for ( int i = 0; i < iCount; ++i )
        {
            strKey = oGlobalMap.getString( String.format( "%s_%d", M_VEC_UNITS_NAME, i ) );
            BattleUnit oUnit = BattleUnit.loadState( oGlobalMap, strKey );
            oBattle.mVecUnits.set( i, oUnit );
        }

        strKey = oGlobalMap.getString( M_ACTIVE_UNIT_NAME );
        oBattle.mActiveUnit = BattleUnit.loadState( oGlobalMap, strKey );

        strKey = oGlobalMap.getString( M_BATTLEFIELD_NAME );
        oBattle.mBattleField = BattleField.loadState( oGlobalMap, strKey );

        strKey = oGlobalMap.getString( M_CURRENT_ACTION_NAME );
        BattleActionFactory oFactory = new BattleActionFactory();
        oBattle.mCurrentAction = oFactory.loadState( oGlobalMap, strKey );

        strKey = oGlobalMap.getString( M_CURRENT_DIALOG_NAME );
        oBattle.mCurrentDialog = BattleDialog.loadState( oGlobalMap, strKey );

        return oBattle;
    }
}
