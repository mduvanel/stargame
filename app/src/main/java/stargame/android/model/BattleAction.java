package stargame.android.model;

import java.util.Observable;
import java.util.Vector;

import android.content.Context;
import android.os.Bundle;

import stargame.android.model.BattleCell;
import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;
import stargame.android.util.Position;
import stargame.android.view.BattleThread2D;

/**
 * Class BattleAction is the basic abstract class for Actions that 
 * can be performed during battle by units.
 */
public abstract class BattleAction extends Observable implements ISavable
{
	/**
	 * The Battle for which action targets should be computed
	 */
	protected Battle mBattle;

	private final static String M_BATTLE = "Battle";

	/** Action type for this BattleAction */
	protected int mActionType;

	private final static String M_TYPE = "Type";
	
	/** Local storage of the action's name */
	protected String mName;

	private final static String M_NAME = "Name";

	/** List of targets for the ability (only one most of the time) */
	protected Vector< ActionTarget > mVecTargets;

	private final static String M_VEC_TARGETS = "Targets";

	/** Unit that can perform this action */
	protected BattleUnit mSourceUnit;

	private final static String M_SOURCE_UNIT = "SourceUnit";

	/** List of targetable cells */
	protected Vector< Position > mVecTargetableCells;

	private final static String M_VEC_CELLS = "Cells";

	/** Enum to describe Action status */
	public enum ActionStatus
	{
		STATUS_PENDING, // Not yet achieved
		STATUS_CRITICAL, // Critical success
		STATUS_SUCCESS, // Successful
		STATUS_MISS, // Missed
	}

	/** Enum to describe the state of the BattleAction */
	public enum BattleActionState
	{
		STATE_READY,		// Targets are ready to be displayed
		STATE_EXECUTING,	// Currently executing the action
		STATE_DONE			// Action is performed
	}

	/** The current state of this action */
	protected BattleActionState mState;

	private final static String M_STATE = "State";

	public static class ActionTarget implements ISavable
	{
		/** The status of this action target */
		public ActionStatus mActionStatus;
		private final static String M_STATUS = "Status";

		public BattleCell mCell;
		private final static String M_CELL = "Cell";

		private ActionTarget()
		{
			mActionStatus = ActionStatus.STATUS_PENDING;
			mCell = null;
		}

		public ActionTarget( BattleCell oCell )
		{
			mActionStatus = ActionStatus.STATUS_PENDING;
			mCell = oCell;
		}

		public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
		{
			String strObjKey = SavableHelper.saveInMap( mCell, oGlobalMap );
			oObjectMap.putString( M_CELL, strObjKey );
			oObjectMap.putInt( M_STATUS, mActionStatus.ordinal() );
		}

		public ActionTarget loadState( Bundle oGlobalMap, String strObjKey )
		{
			Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey, ActionTarget.class.getName() );

			if ( oObjectBundle == null )
			{
				return null;
			}

			ActionTarget oTarget = new ActionTarget();

			String strKey = oObjectBundle.getString( M_CELL );
			oTarget.mCell = BattleCell.loadState( oGlobalMap, strKey );
			oTarget.mActionStatus = ActionStatus.values()[ oObjectBundle.getInt( M_STATUS ) ];

			return oTarget;
		}

		public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
		{
			return loadState( oGlobalMap, strObjKey );
		}
	}

	protected BattleAction()
	{
		mVecTargets = new Vector< ActionTarget >();
		mVecTargetableCells = new Vector< Position >();
	}

	protected BattleAction( Battle oBattle, BattleUnit oUnit )
	{
		mBattle = oBattle;
		mSourceUnit = oUnit;
		mName = null;
		mState = BattleActionState.STATE_DONE;

		// By default an action has only one target
		mVecTargets = new Vector< ActionTarget >();
		mVecTargets.setSize( 1 );

		mVecTargetableCells = new Vector< Position >();
	}

	/** 
	 * Return the name of the action. If a valid context is provided,
	 * it can be used to retrieve a string constant from it.
	 */
	public String GetName( Context oContext )
	{
		if ( null != oContext && null == mName )
		{
			mName = oContext.getString( mActionType );
		}

		return mName;
	}

	public int GetActionType()
	{
		return mActionType;
	}

	public BattleUnit GetSourceUnit()
	{
		return mSourceUnit;
	}

	public Vector< ActionTarget > GetTargets()
	{
		return mVecTargets;
	}

	public BattleActionState GetState()
	{
		return mState;
	}

	public Vector< Position > GetTargetableCells()
	{
		return mVecTargetableCells;
	}

	/** Set the target for this action */
	public boolean SetTarget( BattleCell oCell )
	{
		if ( !IsValidTarget( oCell.GetPos() ) )
		{
			return false;
		}

		mVecTargets.set( 0, new ActionTarget( oCell ) );
		return true;
	}

	/** Prepare the Action for use */
	public void PrepareAction()
	{
		ComputeTargets();
		mState = BattleActionState.STATE_READY;
	}

	public boolean PerformAction()
	{
		if ( CanExecuteAction() )
		{
			// Lock to avoid changes during drawing
			synchronized ( BattleThread2D.mDrawingLock )
			{
				mState = BattleActionState.STATE_EXECUTING;
				ExecuteAction();
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	public void FinishAction()
	{
		mState = BattleActionState.STATE_DONE;
		NotifyActionUpdate();
	}

	protected void NotifyActionUpdate()
	{
		setChanged();
		notifyObservers();
	}

	protected void SaveBattleActionState( Bundle oObjectMap, Bundle oGlobalMap )
	{
		oObjectMap.putInt( M_STATE, mState.ordinal() );
		oObjectMap.putInt( M_TYPE, mActionType );
		oObjectMap.putString( M_NAME, mName );

		String[] astrIds = SavableHelper.saveCollectionInMap( mVecTargets, oGlobalMap );
		oObjectMap.putStringArray( M_VEC_TARGETS, astrIds );

		astrIds = SavableHelper.saveCollectionInMap( mVecTargetableCells, oGlobalMap );
		oObjectMap.putStringArray( M_VEC_CELLS, astrIds );

		String strObjKey = SavableHelper.saveInMap( mSourceUnit, oGlobalMap );
		oObjectMap.putString( M_SOURCE_UNIT, strObjKey );

		strObjKey = SavableHelper.saveInMap( mBattle, oGlobalMap );
		oObjectMap.putString( M_BATTLE, strObjKey );
	}

	protected void LoadBattleActionState( Bundle oObjectMap, Bundle oGlobalMap )
	{
		mState = BattleActionState.values()[ oObjectMap.getInt( M_STATE ) ];
		mActionType = oObjectMap.getInt( M_TYPE );
		mName = oObjectMap.getString( M_NAME );

		String[] astrIds = oObjectMap.getStringArray( M_VEC_TARGETS );
		SavableHelper.loadCollectionFromMap( mVecTargets, astrIds, oGlobalMap, new ActionTarget() );

		astrIds = oObjectMap.getStringArray( M_VEC_CELLS );
		SavableHelper.loadCollectionFromMap( mVecTargetableCells, astrIds, oGlobalMap, new Position() );

		String strObjKey = oObjectMap.getString( M_SOURCE_UNIT );
		mSourceUnit = BattleUnit.loadState( oGlobalMap, strObjKey );

		strObjKey = oObjectMap.getString( M_BATTLE );
		mBattle = Battle.loadState( oGlobalMap, strObjKey );
	}

	/** Generic method called to generate the valid targets */
	public abstract void ComputeTargets();

	/** Generic method that returns if a given Cell is a valid target for this action or not */
	public abstract boolean IsValidTarget( Position oPos );

	/** Generic method that executes the action. */
	public abstract void ExecuteAction();

	/** Generic method that tells if an action can be executed. */
	public abstract boolean CanExecuteAction();
}
