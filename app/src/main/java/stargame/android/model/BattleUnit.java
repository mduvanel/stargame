package stargame.android.model;

import android.content.res.Resources;

import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.Vector;

import stargame.android.R;
import stargame.android.model.jobs.JobType;
import stargame.android.model.status.UnitStatus;
import stargame.android.model.status.UnitStatusBarrier;
import stargame.android.model.status.UnitStatusFactory;
import stargame.android.model.status.UnitStatusSlow;
import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.storage.SavableHelper;
import stargame.android.util.Orientation;
import stargame.android.util.Position;
import stargame.android.util.Tree;
import stargame.android.util.TreeNode;

enum UnitControl
{
    TYPE_PLAYER_1,
    TYPE_PLAYER_2,
    TYPE_CPU
}

public class BattleUnit extends Observable implements ISavable
{
    /**
     * When TimeStep goes above this value, a unit can play
     */
    private static final int TIMESTEP_TURN = 100;

    /**
     * Unit object
     */
    private Unit mUnit;

    private final static String M_UNIT = "Unit";

    /**
     * Internal counter determining when a unit can play
     */
    private int mTimeStep;

    private final static String M_TIMESTEP = "TimeStep";

    /**
     * BattleCell the unit is standing onto
     */
    private BattleCell mCell;

    private final static String M_CELL = "Cell";

    /**
     * Battle the unit belongs to
     */
    private Battle mBattle;

    private final static String M_BATTLE = "Battle";

    /**
     * Orientation of the unit
     */
    private Orientation mOrientation;

    private final static String M_ORIENTATION = "Orientation";

    /**
     * Current Health of the BattleUnit
     */
    private int mCurrentHealth;

    private final static String M_CUR_HEALTH = "CurrentHealth";

    /**
     * Current Resource points of the BattleUnit
     */
    private int mCurrentResource;

    private final static String M_CUR_RESOURCE = "CurrentResource";

    /**
     * Type of control for the unit: player or CPU
     */
    private UnitControl mControl;

    private final static String M_CONTROL = "Control";

    /**
     * Status alterations of the unit
     */
    private Vector< UnitStatus > mVecStatus;

    private final static String M_VEC_STATUS = "Status";

    /**
     * BattleActions that can be performed by the unit
     */
    private Tree< BattleAction > mTreeActions;

    private final static String M_TREE_ACTIONS = "Actions";

    /**
     * Tells whether Move has already been performed for this turn
     */
    private boolean mMovePerformed;

    private final static String M_MOVE_DONE = "MoveDone";

    /**
     * Tells whether Action has already been performed for this turn
     */
    private boolean mActionPerformed;

    private final static String M_ACTION_DONE = "ActionDone";

    private BattleUnit()
    {
        // Empty ctor for loadState() method : only create containers
        mVecStatus = new Vector< UnitStatus >();
        mTreeActions = new Tree< BattleAction >();
    }

    protected BattleUnit( BattleCell oCell, Orientation oOrientation,
                          JobType oJobType, Battle oBattle,
                          Resources oResources )
    {
        mControl = UnitControl.TYPE_PLAYER_1;
        mBattle = oBattle;
        mUnit = new Unit( oJobType, oResources );

        init( oCell, oOrientation );
    }

    private void AddJobActions()
    {
        Vector< BattleAction > vecActions = mUnit.GetJob().GetJobBattleActions(
                mBattle, this );

        if ( !vecActions.isEmpty() )
        {
            TreeNode< BattleAction > oJobActions = new TreeNode< BattleAction >();
            for ( BattleAction oAction : vecActions )
            {
                oJobActions.AddChild( new TreeNode< BattleAction >( oAction ) );
            }
            mTreeActions.GetRoot().AddChild( oJobActions );
        }
    }

    private void init( BattleCell oCell, Orientation eOrientation )
    {
        SetCell( oCell );
        SetOrientation( eOrientation );

        // Init health/resource
        mCurrentHealth = mUnit.GetResultingAttributes().GetHitPoints();
        mCurrentResource = mUnit.GetResultingAttributes().GetResourcePoints();
        mTimeStep = 0;
        mActionPerformed = false;
        mMovePerformed = false;

        mVecStatus = new Vector< UnitStatus >( 0 );
        mTreeActions = new Tree< BattleAction >();
        mTreeActions.GetRoot().AddChild( new TreeNode< BattleAction >(
                new BattleActionMove( mBattle, this ) ) );
        mTreeActions.GetRoot().AddChild( new TreeNode< BattleAction >(
                new BattleActionAttack( mBattle, this ) ) );

        AddJobActions();
    }

    public void SetOrientation( Orientation orientation )
    {
        this.mOrientation = orientation;
        setChanged();
        notifyObservers();
    }

    public Orientation GetOrientation()
    {
        return mOrientation;
    }

    /**
     * Returns the current movement capacity (including status modifiers)
     */
    double GetMovement()
    {
        double dResultingMovement =
                GetUnit().GetResultingAttributes().GetMovement();

        // Check for slow condition
        for ( UnitStatus oStatus : mVecStatus )
        {
            if ( oStatus.GetType() == R.string.status_slow )
            {
                UnitStatusSlow oSlow = ( UnitStatusSlow ) oStatus;
                dResultingMovement *= oSlow.GetSlowPercentage();
                dResultingMovement /= 100;
                break;
            }
        }

        return dResultingMovement;
    }

    public Unit GetUnit()
    {
        return mUnit;
    }

    private void SetCell( BattleCell oCell )
    {
        if ( this.mCell != null )
        {
            // Clear position on previous cell
            mCell.SetUnit( null );
        }

        mCell = oCell;
        mCell.SetUnit( this );
    }

    public void Move( Position oPos )
    {
        SetCell( mBattle.GetBattleField().GetCell( oPos ) );
        setChanged();
        notifyObservers();
    }

    public BattleCell GetCell()
    {
        return mCell;
    }

    public int GetCurrentHitPoints()
    {
        return mCurrentHealth;
    }

    public BattleAction GetAction( int iActionCode )
    {
        try
        {
            for ( BattleAction oAction : mTreeActions )
            {
                if ( oAction.mActionType == iActionCode )
                {
                    return oAction;
                }
            }

            return null;
        }
        catch ( NoSuchElementException e )
        {
            return null;
        }
    }

    public Tree< BattleAction > GetActions()
    {
        return mTreeActions;
    }

    public int GetNonMoveActionsCount()
    {
        int iCount = 0;

        try
        {
            for ( BattleAction oAction : mTreeActions )
            {
                if ( oAction.mActionType == R.string.move_action )
                {
                    continue;
                }
                iCount++;
            }
        }
        catch ( NoSuchElementException e )
        {
            return -1;
        }

        return iCount;
    }

    public int GetMagicHitChance( BattleUnit oTargetUnit )
    {
        return mUnit.GetResultingAttributes().GetMagicHitChance() -
                oTargetUnit.mUnit.GetResultingAttributes().GetMagicDodge();
    }

    public int GetHitChance( BattleUnit oTargetUnit )
    {
        int iChance = mUnit.GetResultingAttributes().GetHitChance() +
                GetHitChanceOrientation( oTargetUnit );
        return iChance - oTargetUnit.mUnit.GetResultingAttributes().GetDodge();
    }

    /**
     * Returns the additive chance to hit with respect to frontal attack
     */
    private int GetHitChanceOrientation( BattleUnit oTargetUnit )
    {
        int iDiffX = oTargetUnit.GetCell().GetPos().mPosX -
                this.GetCell().GetPos().mPosX;
        int iDiffY = oTargetUnit.GetCell().GetPos().mPosY -
                this.GetCell().GetPos().mPosY;

        switch ( oTargetUnit.GetOrientation() )
        {
            case EAST: // East = pointing right
                if ( iDiffX > Math.abs( iDiffY ) )
                {
                    // from Behind
                    return Unit.BACK_ATTACK_CHANCE - Unit.FRONT_ATTACK_CHANCE;
                }
                else if ( Math.abs( iDiffY ) > Math.abs( iDiffX ) )
                {
                    // from the side
                    return Unit.SIDE_ATTACK_CHANCE - Unit.FRONT_ATTACK_CHANCE;
                }
                break;
            case WEST: // West = pointing left
                if ( -iDiffX > Math.abs( iDiffY ) )
                {
                    // from Behind
                    return Unit.BACK_ATTACK_CHANCE - Unit.FRONT_ATTACK_CHANCE;
                }
                else if ( Math.abs( iDiffY ) > Math.abs( iDiffX ) )
                {
                    // from the side
                    return Unit.SIDE_ATTACK_CHANCE - Unit.FRONT_ATTACK_CHANCE;
                }
                break;
            case NORTH: // North = pointing up
                if ( iDiffY > Math.abs( iDiffX ) )
                {
                    // from Behind
                    return Unit.BACK_ATTACK_CHANCE - Unit.FRONT_ATTACK_CHANCE;
                }
                else if ( Math.abs( iDiffX ) > Math.abs( iDiffY ) )
                {
                    // from the side
                    return Unit.SIDE_ATTACK_CHANCE - Unit.FRONT_ATTACK_CHANCE;
                }
                break;
            case SOUTH: // South = pointing down
                if ( -iDiffY > Math.abs( iDiffX ) )
                {
                    // from Behind
                    return Unit.BACK_ATTACK_CHANCE - Unit.FRONT_ATTACK_CHANCE;
                }
                else if ( Math.abs( iDiffX ) > Math.abs( iDiffY ) )
                {
                    // from the side
                    return Unit.SIDE_ATTACK_CHANCE - Unit.FRONT_ATTACK_CHANCE;
                }
                break;
            case NONE:
                // Should not happen
        }

        return 0;
    }

    public double GetCritChance()
    {
        double dChance = Unit.CRITICAL_PHYS_CHANCE;

        // Add dexterity-based crit chance
        dChance += mUnit.GetResultingAttributes().GetDexterity() / 1000.0;

        return dChance;
    }

    public void SetStatus( UnitStatus eStatus )
    {
        // Check if a same status already exists
        boolean found = false;

        for ( UnitStatus oStatus : mVecStatus )
        {
            if ( oStatus.GetType() == eStatus.GetType() )
            {
                // If yes, merge them
                oStatus.MergeStatus( eStatus );
                found = true;
                break;
            }
        }

        // If not, insert the new status
        if ( !found )
        {
            mVecStatus.addElement( eStatus );
        }

        setChanged();
        notifyObservers();
    }

    /**
     * Method called after damage resolution. Handles attribution of damage to
     * the unit.
     * Returns actual damage done.
     */
    public int ApplyDamage( double dDamage )
    {
        int iFinalDamage = ( int ) dDamage;

        // Check for Barrier
        int iBarrierIndex = 0;
        for ( UnitStatus oStatus : mVecStatus )
        {
            if ( oStatus instanceof UnitStatusBarrier )
            {
                UnitStatusBarrier oBarrier = ( UnitStatusBarrier ) oStatus;
                iFinalDamage = oBarrier.Absorb( iFinalDamage );
                if ( iFinalDamage > 0 )
                {
                    mVecStatus.remove( iBarrierIndex );
                }
                break;
            }
            iBarrierIndex++;
        }

        if ( iFinalDamage > GetCurrentHitPoints() )
        {
            iFinalDamage = GetCurrentHitPoints();
            mCurrentHealth = 0;
        }
        else
        {
            mCurrentHealth -= iFinalDamage;
        }

        return iFinalDamage;
    }

    /**
     * Method called after a heal is performed. Handles attribution of
     * HP to the unit. Returns actual healing done.
     */
    public int HealResult( double dHeal )
    {
        int iFinalHeal = ( int ) dHeal;
        if ( iFinalHeal + GetCurrentHitPoints() > mUnit.GetResultingAttributes().GetHitPoints() )
        {
            iFinalHeal = mUnit.GetResultingAttributes().GetHitPoints() - GetCurrentHitPoints();
            mCurrentHealth = mUnit.GetResultingAttributes().GetHitPoints();
        }
        else
        {
            mCurrentHealth += iFinalHeal;
        }

        return iFinalHeal;
    }

    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        oObjectStore.putInt( M_TIMESTEP, mTimeStep );
        oObjectStore.putInt( M_CUR_HEALTH, mCurrentHealth );
        oObjectStore.putInt( M_CUR_RESOURCE, mCurrentResource );
        oObjectStore.putInt( M_ORIENTATION, mOrientation.ordinal() );
        oObjectStore.putInt( M_CONTROL, mControl.ordinal() );
        oObjectStore.putBoolean( M_MOVE_DONE, mMovePerformed );
        oObjectStore.putBoolean( M_ACTION_DONE, mActionPerformed );

        String strObjKey = SavableHelper.saveInStore( mCell, oGlobalStore );
        oObjectStore.putString( M_CELL, strObjKey );

        strObjKey = SavableHelper.saveInStore( mBattle, oGlobalStore );
        oObjectStore.putString( M_BATTLE, strObjKey );

        String[] astrIds = SavableHelper.saveCollectionInStore( mVecStatus,
                                                                oGlobalStore );
        oObjectStore.putStringArray( M_VEC_STATUS, astrIds );

        IStorage oTreeStore = SavableHelper.saveTreeInStore( mTreeActions,
                                                             oGlobalStore );
        oObjectStore.putStore( M_TREE_ACTIONS, oTreeStore );

        strObjKey = SavableHelper.saveInStore( mUnit, oGlobalStore );
        oObjectStore.putString( M_UNIT, strObjKey );
    }

    public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
    {
        return loadState( oGlobalStore, strObjKey );
    }

    public static BattleUnit loadState( IStorage oGlobalStore, String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey, BattleUnit.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        BattleUnit oUnit = new BattleUnit();

        oUnit.mTimeStep = oObjectStore.getInt( M_TIMESTEP );
        oUnit.mCurrentHealth = oObjectStore.getInt( M_CUR_HEALTH );
        oUnit.mCurrentResource = oObjectStore.getInt( M_CUR_RESOURCE );
        oUnit.mOrientation =
                Orientation.values()[ oObjectStore.getInt( M_ORIENTATION ) ];
        oUnit.mControl =
                UnitControl.values()[ oObjectStore.getInt( M_CONTROL ) ];
        oUnit.mMovePerformed = oObjectStore.getBoolean( M_MOVE_DONE );
        oUnit.mActionPerformed = oObjectStore.getBoolean( M_ACTION_DONE );

        String strKey = oObjectStore.getString( M_CELL );
        oUnit.mCell = BattleCell.loadState( oGlobalStore, strKey );

        strKey = oObjectStore.getString( M_BATTLE );
        oUnit.mBattle = Battle.loadState( oGlobalStore, strKey );

        String[] astrIds = oObjectStore.getStringArray( M_VEC_STATUS );
        UnitStatusFactory oFactory = new UnitStatusFactory();
        for ( String strVal : astrIds )
        {
            oUnit.mVecStatus.add(
                    oFactory.loadState( oGlobalStore, strVal ) );
        }

        IStorage oTreeStore = oObjectStore.getStore( M_TREE_ACTIONS );
        SavableHelper.loadTreeFromStore(
                oTreeStore, oGlobalStore, new BattleActionFactory() );

        strKey = oObjectStore.getString( M_UNIT );
        oUnit.mUnit = Unit.loadState( oGlobalStore, strKey );

        return oUnit;
    }

    public Vector< UnitStatus > GetStatusVector()
    {
        return mVecStatus;
    }

    void TimeStep()
    {
        // Do not continue time stepping if we are dead
        if ( GetCurrentHitPoints() > 0 )
        {
            mTimeStep += mUnit.GetResultingAttributes().GetSpeed();
        }
    }

    /**
     * Called at the beginning of the turn of a BattleUnit,
     * resolves all periodic status ailments
     */
    void TurnPassed()
    {
        // pass turn for all status ailments
        for ( UnitStatus oStatus : mVecStatus )
        {
            if ( oStatus.TurnPassed() )
            {
                // Status ailment ended: remove status
                mVecStatus.remove( oStatus );
            }
        }

        mMovePerformed = false;
        mActionPerformed = false;

        // Reduce current timestep
        mTimeStep -= TIMESTEP_TURN;
    }

    boolean IsReady()
    {
        return ( mTimeStep >= TIMESTEP_TURN );
    }

    /**
     * When ready, returns a float between 0 and 1
     */
    float ReadySince()
    {
        if ( IsReady() )
        {
            float fExtra = mTimeStep - TIMESTEP_TURN;
            return fExtra / mUnit.GetResultingAttributes().GetSpeed();
        }

        return -1;
    }

    public boolean IsMovePerformed()
    {
        return mMovePerformed;
    }

    void SetMovePerformed()
    {
        mMovePerformed = true;
    }

    public boolean IsActionPerformed()
    {
        return mActionPerformed;
    }

    public void SetActionPerformed()
    {
        mActionPerformed = true;
    }

    public boolean IsTargetable()
    {
        for ( UnitStatus oStatus : mVecStatus )
        {
            if ( oStatus.GetType() == R.string.status_invisible )
            {
                return false;
            }
        }

        return true;
    }
}
