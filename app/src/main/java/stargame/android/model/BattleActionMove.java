package stargame.android.model;

import java.util.Vector;

import android.os.Bundle;

import stargame.android.R;
import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;
import stargame.android.util.Logger;
import stargame.android.util.Orientation;
import stargame.android.util.Position;

/**
 * Class BattleActionMove implements the default Move action for all jobs
 * 
 * @author Duduche
 *
 */
public class BattleActionMove extends BattleAction implements ISavable
{
	private static class MovementStruct implements ISavable
	{
		// Amount of movement still available
		double mRemainingMvt;

		private static final String M_MOVEMENT = "Movement";

		// Coordinates from which the unit comes from
		private Position mSourcePos;

		private static final String M_SOURCE = "Source";

		private boolean mAvailable;

		private static final String M_AVAILABLE = "Available";

		public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
		{
			oObjectMap.putBoolean( M_AVAILABLE, mAvailable );
			oObjectMap.putDouble( M_MOVEMENT, mRemainingMvt );

			String strObjKey = SavableHelper.saveInMap( mSourcePos, oGlobalMap );
			oObjectMap.putString( M_SOURCE, strObjKey );
		}

		public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
		{
			return loadState( oGlobalMap, strObjKey );
		}

		public static MovementStruct loadState( Bundle oGlobalMap, String strObjKey )
		{
			Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey, MovementStruct.class.getName() );

			if ( oObjectBundle == null )
			{
				return null;
			}

			MovementStruct oStruct = new MovementStruct();

			oStruct.mAvailable = oObjectBundle.getBoolean( M_AVAILABLE );
			oStruct.mRemainingMvt = oObjectBundle.getDouble( M_MOVEMENT );

			String strKey = oObjectBundle.getString( M_SOURCE );
			oStruct.mSourcePos = Position.loadState( oGlobalMap, strKey );

			return oStruct;
		}
	}

	/** Start position for the movement */
	private Position mStartPos;

	private static final String M_POSITION = "StartPos";

	/** Last movement's trajectory */
	private Vector< Position > mTrajectory;

	private static final String M_VEC_TRAJECTORY = "Trajectory";

	private MovementStruct mMovementArray [][];

	private static final String M_MOVE_ARRAY = "Movement";

	private BattleActionMove()
	{
		super();
		mTrajectory = new Vector< Position >();
	}

	public BattleActionMove( Battle oBattle, BattleUnit oUnit )
	{
		super( oBattle, oUnit );
		mTrajectory = new Vector< Position >();
		mActionType = R.string.move_action;
		mStartPos = new Position( 0, 0 );

		InitMovementArray();
	}

	protected void InitMovementArray()
	{
		mMovementArray = new MovementStruct[ mBattle.GetBattleField().GetWidth() ][ mBattle.GetBattleField().GetHeight() ];
		for ( int i = 0; i < mBattle.GetBattleField().GetWidth(); ++i )
		{
			for ( int j = 0; j < mBattle.GetBattleField().GetHeight(); ++j )
			{
				mMovementArray[ i ][ j ] = new MovementStruct();
			}
		}		
	}

	private void ResetMovementArray()
	{
		for ( int i = 0; i < mBattle.GetBattleField().GetWidth(); ++i )
		{
			for ( int j = 0; j < mBattle.GetBattleField().GetHeight(); ++j )
			{
				mMovementArray[ i ][ j ].mSourcePos = new Position( 0, 0 );
				mMovementArray[ i ][ j ].mRemainingMvt = 0;
				mMovementArray[ i ][ j ].mAvailable = false;
			}
		}
		mVecTargetableCells.clear();
	}

	/**
	 * Déplacement d'une case à sa voisine. La vérification si l'unité a encore assez de mouvement
     * pour se déplacer doit avoir été faite auparavant.
	 * 
	 * @param oUnit         L'unité qui se déplace
	 * @param iTargetPosX   La coordonnée X de la case destination
	 * @param iTargetPosY   La coordonnée Y de la case destination
	 * @param iSourcePosX   La coordonnée X de la case source
	 * @param iSourcePosY   La coordonnée Y de la case source
	 * @param dCost         Le coût du déplacement
	 * @param dRemainingMvt Le mouvement restant après ce déplacement
	 */
	private void Move( BattleUnit oUnit, int iTargetPosX, int iTargetPosY, int iSourcePosX, int iSourcePosY, double dCost, double dRemainingMvt )
	{
	    // Si l'unité peut sauter la hauteur entre les deux cases
		if ( oUnit.GetUnit().GetResultingAttributes().GetVerticalJump() - mBattle.GetBattleField().GetElevationDiff( iSourcePosX, iSourcePosY, iTargetPosX, iTargetPosY ) >= 0 )
	    {
			// S'il n'y a pas d'obstacle/ennemis sur la case de destination
			if ( mBattle.GetBattleField().IsPositionFree( iTargetPosX, iTargetPosY, oUnit ) )
			{
				// Si on n'est pas encore passé par cette case pour calculer le déplacement
		        // ou si on est déjà passé dessus avec un mouvement restant moindre
				MovementStruct oStruct = mMovementArray[ iTargetPosX ][ iTargetPosY ];
				if ( ( dRemainingMvt - dCost ) > oStruct.mRemainingMvt )
				{
					oStruct.mRemainingMvt = dRemainingMvt - dCost;
					oStruct.mSourcePos.SetPos( iSourcePosX, iSourcePosY );
					if ( !oStruct.mAvailable )
					{
						if ( mBattle.GetBattleField().GetCell( iTargetPosX, iTargetPosY ).GetUnit() == null )
						{
							mVecTargetableCells.add( new Position( iTargetPosX, iTargetPosY ) );
						}
						oStruct.mAvailable = true;
					}
					ComputePossibleMvt( oUnit, dRemainingMvt - dCost, iTargetPosX, iTargetPosY );
				}
			}
	    }
	}

	/**
	 * Vérifie s'il est possible de sauter d'une case à l'autre (saut horizontal).
	 * La condition est que toutes les case par-dessus lesquelles l'unité saute soient
	 * plus basses que la plus basses des deux cases de départ et d'arrivée.
	 * 
	 * @param iSourcePosX la coordonnée X de départ du saut
	 * @param iSourcePosY la coordonnée Y de départ du saut
	 * @param iTargetPosX la coordonnée X de destination du saut
	 * @param iTargetPosY la coordonnée Y de destination du saut
	 * @return            true si le saut est possible, false sinon
	 */
	private boolean CheckJumpable( int iSourcePosX, int iSourcePosY, int iTargetPosX,
								   int iTargetPosY )
	{
		int iMaxHeight = Math.min( mBattle.GetBattleField().GetElevation( iSourcePosX, iSourcePosY ), 
				mBattle.GetBattleField().GetElevation( iTargetPosX, iTargetPosY ) );

		if ( iSourcePosX != iTargetPosX )
		{
			for ( int iNewPosX = Math.min( iSourcePosX, iTargetPosX ) + 1;
				  iNewPosX < Math.max( iSourcePosX, iTargetPosX );
				  ++iNewPosX )
			{
				if ( mBattle.GetBattleField().GetElevation( iNewPosX, iSourcePosY ) >= iMaxHeight )
				{
					return false;
				}
			}
			return true;
		}
		else
		{
			for ( int iNewPosY = Math.min( iSourcePosY, iTargetPosY ) + 1;
				  iNewPosY < Math.max( iSourcePosY, iTargetPosY );
				  ++iNewPosY )
			{
				if ( mBattle.GetBattleField().GetElevation( iSourcePosX, iNewPosY ) >= iMaxHeight )
				{
					return false;
				}
			}
			return true;
		}
	}
	
	/**
	 * Calcule les déplacements possible pour une unité, depuis une case donnée vers les cases
     * alentour. Appel récursif de cette méthode, le damier de calcul est utilisé pour éviter de
     * recalculer plusieurs fois chaque case.
	 * 
	 * @param oUnit         l'unité à déplacer
	 * @param dRemainingMvt la quantité de mouvement restant à l'unité
	 * @param iPosX         la coordonnée X de l'unité
	 * @param iPosY         la coordonnée Y de l'unité
	 */
	private void ComputePossibleMvt( BattleUnit oUnit, double dRemainingMvt, int iPosX, int iPosY )
	{
		double dCost = oUnit.GetUnit().GetFieldPenalty( mBattle.GetBattleField().GetFieldType( iPosX, iPosY ) ) / 100;

		if ( dRemainingMvt < dCost )
		{
			return;
		}

		// On s'occupe d'abord des d�placements normaux (cases voisines)
		if ( iPosX > 0 )
		{
			Move( oUnit, iPosX - 1, iPosY, iPosX, iPosY, dCost, dRemainingMvt );
		}
		if ( iPosY > 0 )
		{
			Move( oUnit, iPosX, iPosY - 1, iPosX, iPosY, dCost, dRemainingMvt );
		}
		if ( iPosX < mBattle.GetBattleField().GetWidth() - 1 )
		{
			Move( oUnit, iPosX + 1, iPosY, iPosX, iPosY, dCost, dRemainingMvt );
		}
		if ( iPosY < mBattle.GetBattleField().GetHeight() - 1 )
		{
			Move( oUnit, iPosX, iPosY + 1, iPosX, iPosY, dCost, dRemainingMvt );
		}

		// On regarde les sauts
		for ( int i = 1; i < oUnit.GetUnit().GetResultingAttributes().GetHorizontalJump(); ++i )
		{
			dCost = dCost + 1; // Saut par-dessus des cases, pas de p�nalit� pour le saut

			if ( dRemainingMvt < dCost )
				return;
			
			if ( iPosX > i && CheckJumpable( iPosX, iPosY, iPosX - ( i + 1 ), iPosY ) )
			{
				Move( oUnit, iPosX - ( i + 1 ), iPosY, iPosX, iPosY, dCost, dRemainingMvt );
			}
			if ( iPosY > i && CheckJumpable( iPosX, iPosY, iPosX, iPosY - ( i + 1 ) ) )
			{
				Move( oUnit, iPosX, iPosY - ( i + 1 ), iPosX, iPosY, dCost, dRemainingMvt );
			}
			if ( iPosX < mBattle.GetBattleField().GetWidth() - ( i + 1 ) && CheckJumpable( iPosX, iPosY, iPosX + i + 1, iPosY ) )
			{
				Move( oUnit, iPosX + i + 1, iPosY, iPosX, iPosY, dCost, dRemainingMvt );
			}
			if ( iPosY < mBattle.GetBattleField().GetHeight() - ( i + 1 ) && CheckJumpable( iPosX, iPosY, iPosX, iPosY + i + 1 ) )
			{
				Move( oUnit, iPosX, iPosY + i + 1, iPosX, iPosY, dCost, dRemainingMvt );
			}
		}
	}

	public void ComputeTargets()
	{
		ResetMovementArray();
		mVecTargets.set( 0, null );
		mStartPos.SetPos( mSourceUnit.GetCell().GetPos() );

		ComputePossibleMvt( 
				mSourceUnit, 
				mSourceUnit.GetMovement(), 
				mSourceUnit.GetCell().GetPos().mPosX,
				mSourceUnit.GetCell().GetPos().mPosY );

		// Remove cases where a friendly unit is present
		for ( int i = 0; i < mBattle.GetBattleField().GetWidth(); ++i )
		{
			for ( int j = 0; j < mBattle.GetBattleField().GetHeight(); ++j )
			{
				if ( mBattle.GetBattleField().GetCell( i, j ).GetUnit() != null )
				{
					mMovementArray[ i ][ j ].mAvailable = false;
				}
			}
		}
	}

	public Vector< Position > GetTrajectory()
	{
		return mTrajectory;
	}

	private void StoreTrajectory()
	{
		mTrajectory.clear();

		// Build the trajectory from the end
		Position oPosition = mVecTargets.get( 0 ).mCell.GetPos();
		MovementStruct oCell = mMovementArray[ oPosition.mPosX ][ oPosition.mPosY ];
		mTrajectory.add( 0, oPosition );

		while ( !oCell.mSourcePos.Equals( mStartPos ) )
		{
			mTrajectory.add( 0, oCell.mSourcePos );
			oCell = mMovementArray[ oCell.mSourcePos.mPosX ][ oCell.mSourcePos.mPosY ];
		}
		mTrajectory.add( 0, mStartPos );

		// Debug: log trajectory
		int iCount = 0;
		Logger.d( String.format( "Source position: %d, %d", mStartPos.mPosX, mStartPos.mPosY ) );
		Logger.d( String.format( "Destination position: %d, %d", oPosition.mPosX, oPosition.mPosY ) );
		for ( Position oPos : mTrajectory )
		{
			Logger.d( String.format( "%dth position: %d, %d", iCount, oPos.mPosX, oPos.mPosY ) );
			iCount++;
		}
	}

	public boolean CanExecuteAction()
	{
		return ( mVecTargets.get( 0 ) != null );
	}

	public void ExecuteAction()
	{
		StoreTrajectory();
		ResetMovementArray();
		Position oStartPos = null;
		Orientation eNewOrientation = Orientation.NONE;

		// Move incrementally
		for ( Position oPos : mTrajectory )
		{
			if ( oStartPos != null )
			{
				// Set the new position
				mSourceUnit.Move( oPos );
				eNewOrientation = oStartPos.GetOrientation( oPos );
			}
			NotifyActionUpdate();
			oStartPos = oPos;
		}

		if ( Orientation.NONE != eNewOrientation )
		{
			// Set the orientation of the unit as the last move's orientation
			mSourceUnit.SetOrientation( eNewOrientation );
		}

		mSourceUnit.SetMovePerformed();
	}

	public boolean IsValidTarget( Position oPos )
	{
		return mMovementArray[ oPos.mPosX ][ oPos.mPosY ].mAvailable;
	}

	public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
	{
		// Save parent info
		super.SaveBattleActionState( oObjectMap, oGlobalMap );

		String[] astrIds = SavableHelper.saveCollectionInMap( mTrajectory, oGlobalMap );
		oObjectMap.putStringArray( M_VEC_TRAJECTORY, astrIds );

		String strObjKey = SavableHelper.saveInMap( mStartPos, oGlobalMap );
		oObjectMap.putString( M_POSITION, strObjKey );

		Bundle oArrayBundle = SavableHelper.saveBidimensionalArrayInMap( mMovementArray, oGlobalMap );
		oObjectMap.putBundle( M_MOVE_ARRAY, oArrayBundle );
	}

	public static BattleActionMove loadState( Bundle oGlobalMap, String strObjKey )
	{
		Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey, BattleActionMove.class.getName() );

		if ( oObjectBundle == null )
		{
			return null;
		}

		BattleActionMove oAction = new BattleActionMove();

		// Load parent info
		oAction.LoadBattleActionState( oObjectBundle, oGlobalMap );

		String strKey = oObjectBundle.getString( M_POSITION );
		oAction.mStartPos = Position.loadState( oGlobalMap, strKey );

		String[] astrIds = oObjectBundle.getStringArray( M_VEC_TRAJECTORY );
		SavableHelper.loadCollectionFromMap( oAction.mTrajectory, astrIds, oGlobalMap, new Position() );

		oAction.InitMovementArray();
		Bundle oArrayBundle = oObjectBundle.getBundle( M_MOVE_ARRAY );
		SavableHelper.loadBidimensionalArrayFromMap( oAction.mMovementArray, oArrayBundle, oGlobalMap, new MovementStruct() );

		return oAction;
	}

	public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
	{
		return loadState( oGlobalMap, strObjKey );
	}
}
