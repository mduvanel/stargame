package stargame.android.view;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import stargame.android.R;
import stargame.android.util.FieldType;
import stargame.android.util.Logger;
import stargame.android.util.Orientation;
import stargame.android.util.Position;
import stargame.android.view.TileSet2D.TilePos;
import stargame.android.view.TileSet2D.TileType;
import stargame.android.model.BattleCell;
import stargame.android.model.BattleField;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;

/**
 * This is the BattleField2D class, handling drawing of the BattleField in 2D
 * It is a singleton for convenience, there should be only one existing at a 
 * time.
 */
public class BattleField2D implements Observer
{
	/** The corresponding model's BattleField object */
	private BattleField mBattleField;

	public BattleField GetBattleField()
	{
		return mBattleField;
	}

	/** The global drawable list */
	private BattleDrawableList mDrawableList;

	public BattleDrawableList GetDrawableList()
	{
		return mDrawableList;
	}

	/** The array of BattleCell2D */
	private BattleCell2D[][] mArrayCells;

	public BattleCell2D GetCell2D( Position oPos )
	{
		return mArrayCells[ oPos.mPosX ][ oPos.mPosY ];
	}

	/** The Tilesets needed to draw this BattleField */
	private HashMap< FieldType, TileSet2D > mTileSets;
	private HashMap< FieldType, TileSet2D > mVerticalTileSets;

	/** The animation for the selected cell */
	private Animation2D mSelectedCellAnimation;

	/** The associated BattleDrawable */
	private BattleDrawable mSelectedCellDrawable;

	/** Position used for drawing */
	private Position mDrawingPosition;

	/** Current orientation of the view, affects drawing */
	private Orientation mDrawingOrientation;

	public void SetDrawingOrientation( Orientation oOrientation )
	{
		mDrawingOrientation = oOrientation;
		// We need to rebuild the drawables...
		ComputeCellsBitmaps();
	}

	public Orientation GetDrawingOrientation()
	{
		return mDrawingOrientation;
	}

	/** The singleton instance */
	private static BattleField2D msTheInstance = null;

	public static BattleField2D GetInstance()
	{
		if ( msTheInstance == null )
		{
			Logger.w( "BattleField2D singleton created" );
			msTheInstance = new BattleField2D();
		}
		return msTheInstance;
	}

	public static void Reset()
	{
		// Cleanup Observer reference
		if ( msTheInstance != null )
		{
			msTheInstance.mBattleField.deleteObserver( msTheInstance );
			msTheInstance.mDrawableList.Clear();
			msTheInstance = null;
			Logger.w( "BattleField2D singleton deleted" );
		}
	}

	private BattleField2D()
	{
		mBattleField = null;
    	mDrawingOrientation = null;
		mArrayCells = null;
		mSelectedCellDrawable = null;
		mDrawableList = new BattleDrawableList();
	}

	public void Init( BattleField oBattleField, Resources oRes )
	{
		mBattleField = oBattleField;
		mBattleField.addObserver( this );
    	mDrawingOrientation = Orientation.NORTH;
    	mDrawingPosition = new Position();

		int iWidth = oBattleField.GetWidth();
		int iHeight = oBattleField.GetHeight();
		mArrayCells = new BattleCell2D[ iWidth ][ iHeight ];
		for ( int i = 0; i < iWidth; ++i )
		{
			for ( int j = 0; j < iHeight; ++j )
			{
				mArrayCells[ i ][ j ] = new BattleCell2D( oBattleField.GetCell( new Position( i, j ) ), mDrawableList, -1 );
			}	
		}

		LoadBitmaps( oRes );

		// Create the selected cell BattleDrawable
		mSelectedCellDrawable = new BattleDrawable( mSelectedCellAnimation, null, new RectF( 0, 0, DisplayConstants2D.GetCellWidth(), DisplayConstants2D.GetCellHeight() ), 0 );
		mDrawableList.AddDrawable( mSelectedCellDrawable );
		UpdateSelectedCellDrawable();

		ComputeCellsBitmaps();
	}

	public int GetWidth()
	{
		return mBattleField.GetWidth();
	}

	public int GetHeight()
	{
		return mBattleField.GetHeight();
	}

	private void LoadBitmaps( Resources oRes )
	{
		// For now just load all existing Tilesets
		mTileSets = new HashMap< FieldType, TileSet2D >();
		mVerticalTileSets = new HashMap< FieldType, TileSet2D >();

		TileSet2D oTileSet = new TileSet2D( TileSet2D.msDirtString, oRes );
		mTileSets.put( FieldType.TYPE_PLAIN, oTileSet );
		mTileSets.put( FieldType.TYPE_LAVA, oTileSet );
		mTileSets.put( FieldType.TYPE_WATER, new TileSet2D( oRes.getXml( R.xml.tileset_water ), oRes ) );
		oTileSet = new TileSet2D( TileSet2D.msGrass2String, oRes );
		mTileSets.put( FieldType.TYPE_FOREST, oTileSet );
		mTileSets.put( FieldType.TYPE_GRASS, oTileSet );
		mTileSets.put( FieldType.TYPE_ROCK, new TileSet2D( TileSet2D.msGrass1String, oRes ) );

		oTileSet = new TileSet2D( TileSet2D.msVerticalDirtRock, oRes );
		mVerticalTileSets.put( FieldType.TYPE_PLAIN, oTileSet );
		mVerticalTileSets.put( FieldType.TYPE_LAVA, oTileSet );
		mVerticalTileSets.put( FieldType.TYPE_WATER, oTileSet );
		oTileSet = new TileSet2D( TileSet2D.msVerticalGrassRock, oRes );
		mVerticalTileSets.put( FieldType.TYPE_FOREST, oTileSet );
		mVerticalTileSets.put( FieldType.TYPE_GRASS, oTileSet );
		mVerticalTileSets.put( FieldType.TYPE_ROCK, oTileSet );

		XmlResourceParser oAnimationParser = oRes.getXml( R.xml.selected_case );
		mSelectedCellAnimation = new Animation2D( oAnimationParser );
	}

    private TileType GetTileType( Position oPos, 
    		TilePos ePos, 
    		boolean bTopDiv, 
    		boolean bBottomDiv, 
    		boolean bLeftDiv, 
    		boolean bRightDiv,
    		BattleCell oCell )
    {
    	TileType eType, eVerticalType = TileType.PLAIN, eHorizontalType = TileType.PLAIN;
    	boolean bVerticalDiv = false, bHorizontalDiv = false;
    	BattleCell oCellHorizontal = null, oCellVertical = null, oCellCorner = null;

    	switch ( ePos )
    	{
    	case UPPER_LEFT:
    		bVerticalDiv = bTopDiv;
    		bHorizontalDiv = bLeftDiv;
    		eVerticalType = TileType.TOP;
    		eHorizontalType = TileType.LEFT;
    		oCellHorizontal = GetCell( oPos, -1, 0 );
    		oCellVertical = GetCell( oPos, 0, -1 );
    		oCellCorner = GetCell( oPos, -1, -1 );
    		break;
    	case UPPER_RIGHT:
    		bVerticalDiv = bTopDiv;
    		bHorizontalDiv = bRightDiv;
    		eVerticalType = TileType.TOP;
    		eHorizontalType = TileType.RIGHT;
    		oCellHorizontal = GetCell( oPos, 1, 0 );
    		oCellVertical = GetCell( oPos, 0, -1 );
    		oCellCorner = GetCell( oPos, 1, -1 );
    		break;
    	case LOWER_LEFT:
    		bVerticalDiv = bBottomDiv;
    		bHorizontalDiv = bLeftDiv;
    		eVerticalType = TileType.BOTTOM;
    		eHorizontalType = TileType.LEFT;
    		oCellHorizontal = GetCell( oPos, -1, 0 );
    		oCellVertical = GetCell( oPos, 0, 1 );
    		oCellCorner = GetCell( oPos, -1, 1 );
    		break;
    	case LOWER_RIGHT:
    		bVerticalDiv = bBottomDiv;
    		bHorizontalDiv = bRightDiv;
    		eVerticalType = TileType.BOTTOM;
    		eHorizontalType = TileType.RIGHT;
    		oCellHorizontal = GetCell( oPos, 1, 0 );
    		oCellVertical = GetCell( oPos, 0, 1 );
    		oCellCorner = GetCell( oPos, 1, 1 );
    		break;
    	default:
    		break;
    	}
		
    	if ( bVerticalDiv )
		{
			if ( bHorizontalDiv )
			{
				eType = TileType.CORNER;
			}
			else
			{
				eType = eVerticalType;
			}
		}
		else
		{
			if ( bHorizontalDiv )
			{
				eType = eHorizontalType;
			}
			else
			{
				if ( IsPlainTileAngle( oCell, oCellHorizontal, oCellVertical, oCellCorner ) )
				{
					eType = TileType.ANGLE;
				}
				else
				{
					eType = TileType.PLAIN;
				}
			}
		}

		return eType;
    }

    public void FillCellAbsDrawingPosition( Position oCellPos, Position oDrawingPos )
    {
    	oDrawingPos.SetPos( oCellPos );
    	int iWidth = mBattleField.GetWidth();
    	int iHeight = mBattleField.GetHeight();

    	switch ( mDrawingOrientation )
		{
		case NORTH:
			// Nothing to do
			break;
		case SOUTH:
			oDrawingPos.mPosX = iWidth - 1 - oCellPos.mPosX;
			oDrawingPos.mPosY = iHeight - 1 - oCellPos.mPosY;
			break;
		case WEST:
			oDrawingPos.mPosX = iHeight - 1 - oCellPos.mPosY;
			oDrawingPos.mPosY = oCellPos.mPosX;
			break;
		case EAST:
			oDrawingPos.mPosX = oCellPos.mPosY;
			oDrawingPos.mPosY = iWidth - 1 - oCellPos.mPosX;
			break;
		case NONE:
			// Should not happen
			break;
		}

    	oDrawingPos.mPosX *= DisplayConstants2D.GetCellWidth();
    	oDrawingPos.mPosY *= DisplayConstants2D.GetCellHeight();

    	// Move to top of the Cell
    	oDrawingPos.mPosY -= DisplayConstants2D.GetCellStackHeight() * mBattleField.GetCell( oCellPos ).GetElevation();
    }

    private int ComputeCellZOrder( BattleCell oCell )
    {
    	Position oPos = oCell.GetPos();
    	int iZOrder = 0;

    	switch ( mDrawingOrientation )
    	{
    	case NORTH:
    		iZOrder = oPos.mPosY;
    		break;
    	case SOUTH:
    		iZOrder = mBattleField.GetHeight() - 1 - oPos.mPosY;
    		break;
    	case WEST:
    		iZOrder = oPos.mPosX;
    		break;
    	case EAST:
    		iZOrder = mBattleField.GetWidth() - 1 - oPos.mPosX;
    		break;
		case NONE:
			// Should not happen
			break;
    	}

    	return iZOrder * ZOrderPosition.TOTAL.Value();
    }

    private void AddBitmap( Position oPos, 
    		TilePos ePos, 
    		boolean bTopDiv, 
    		boolean bBottomDiv, 
    		boolean bLeftDiv, 
    		boolean bRightDiv,
    		BattleCell oCell,
    		RectF oDestRect,
    		boolean bFront )
    {
    	TileType eType = GetTileType( oPos,
    			ePos, 
    			bTopDiv,
        		bBottomDiv, 
        		bLeftDiv, 
        		bRightDiv,
        		oCell );
		IDrawable oDrawable = mTileSets.get( oCell.GetType() ).GetDrawable( eType, ePos );
		mArrayCells[ oPos.mPosX ][ oPos.mPosY ].AddTile( oDrawable, oDestRect, bFront );
    }
/*
    private void AddVerticalBitmap( Position oPos, 
    		TilePos ePos, 
    		boolean bTopDiv, 
    		boolean bBottomDiv, 
    		boolean bLeftDiv, 
    		boolean bRightDiv,
    		BattleCell oCell )
    {
    	TileType eType = GetVerticalTileType( oPos,
    			ePos, 
    			bTopDiv,
        		bBottomDiv, 
        		bLeftDiv, 
        		bRightDiv,
        		oCell );
		Bitmap oBitmap = mTileSets.get( oCell.GetType() ).GetBitmap( eType, ePos );
		mArrayCells[ oPos.mPosX ][ oPos.mPosY ].AddBitmap( oBitmap );
    }
*/
    private boolean IsVerticalTilePlain( int iVerticalTileHeight, BattleCell oCell, BattleCell oSideCell, BattleCell oLowerSideCell )
    {
    	return ( oSideCell != null && oSideCell.GetType() == oCell.GetType() && oSideCell.GetElevation() >= iVerticalTileHeight );
    }

    private boolean IsPlainTileAngle( BattleCell oCell, BattleCell oSideCell, BattleCell oVerticalCell, BattleCell oCornerCell )
    {
    	return ( oSideCell != null && oVerticalCell != null && oCornerCell != null && 
    			( ( oVerticalCell.GetType() == oCell.GetType() && oVerticalCell.GetElevation() == oCell.GetElevation() && IsTileDivision( oVerticalCell, oCornerCell ) ) || 
    			  ( oSideCell.GetType() == oCell.GetType() && oSideCell.GetElevation() == oCell.GetElevation() && IsTileDivision( oSideCell, oCornerCell ) ) ) );
    }

    private boolean IsVerticalBottomTileDivision( BattleCell oSideCell, BattleCell oLowerCell )
    {
    	return ( oSideCell == null || oSideCell.GetElevation() < oLowerCell.GetElevation() || 
    			 ( oSideCell.GetType() != oLowerCell.GetType() && oSideCell.GetElevation() == oLowerCell.GetElevation() ) );
    }

    private boolean IsTileDivision( BattleCell oCell, BattleCell oOtherCell )
    {
    	if ( oOtherCell == null )
    	{
    		return true;
    	}
    	else
    	{
	    	if ( oCell.GetElevation() == oOtherCell.GetElevation() )
	    	{
	    		return ( oOtherCell.GetType() != oCell.GetType() );
	    	}
	    	else
	    	{
	    		return ( oCell.GetElevation() > oOtherCell.GetElevation() );
	    	}
    	}
    }

    /** Set the bitmaps for the terrain based on type and cells around */
	private void ComputeCellsBitmaps()
	{
		int iWidth = mBattleField.GetWidth();
		int iHeight = mBattleField.GetHeight();
		int iCellWidth = DisplayConstants2D.GetCellWidth();
		int iCellHeight = DisplayConstants2D.GetCellHeight();
		int iStackHeight = DisplayConstants2D.GetCellStackHeight();
		RectF oDestRect = new RectF( 0, 0, iCellWidth / 2, iCellHeight / 2 );

		Position oPos = new Position( 0, 0 );
		for ( oPos.mPosY = 0; oPos.mPosY < iHeight; ++oPos.mPosY )
		{
			for ( oPos.mPosX = 0; oPos.mPosX < iWidth; ++oPos.mPosX )
			{
				// Move the destination RectF to the correct location
				FillCellAbsDrawingPosition( oPos, mDrawingPosition );
				oDestRect.offsetTo( mDrawingPosition.mPosX, mDrawingPosition.mPosY );

				BattleCell oCell = GetCell( oPos, 0, 0 );
				TileType eType = TileType.PLAIN;
				mArrayCells[ oPos.mPosX ][ oPos.mPosY ].Reset( ComputeCellZOrder( oCell ) );

				BattleCell oCellAbove = GetCell( oPos, 0, -1 );
				BattleCell oCellBelow = GetCell( oPos, 0, 1 );
				BattleCell oCellLeft = GetCell( oPos, -1, 0 );
				BattleCell oCellRight = GetCell( oPos, 1, 0 );
				BattleCell oCellBelowLeft = GetCell( oPos, -1, 1 );
				BattleCell oCellBelowRight = GetCell( oPos, 1, 1 );

				boolean bTopDiv = IsTileDivision( oCell, oCellAbove ); 
				boolean bLeftDiv = IsTileDivision( oCell, oCellLeft );
				boolean bRightDiv = IsTileDivision( oCell, oCellRight );
				boolean bBottomDiv = IsTileDivision( oCell, oCellBelow );

				// Upper-left part
				AddBitmap( oPos, 
						TilePos.UPPER_LEFT, 
						bTopDiv, 
						bBottomDiv, 
						bLeftDiv, 
						bRightDiv, 
						oCell,
						oDestRect,
						true );

				// Upper-right part
				oDestRect.offset( iCellWidth / 2, 0 );
				AddBitmap( oPos, 
						TilePos.UPPER_RIGHT, 
						bTopDiv, 
						bBottomDiv, 
						bLeftDiv, 
						bRightDiv, 
						oCell,
						oDestRect,
						true );

				// Lower-left part
				oDestRect.offset( -iCellWidth / 2, iCellHeight / 2 );
				AddBitmap( oPos, 
						TilePos.LOWER_LEFT, 
						bTopDiv, 
						bBottomDiv, 
						bLeftDiv, 
						bRightDiv, 
						oCell,
						oDestRect,
						true );

				// Lower-right part
				oDestRect.offset( iCellWidth / 2, 0 );
				AddBitmap( oPos, 
						TilePos.LOWER_RIGHT, 
						bTopDiv, 
						bBottomDiv, 
						bLeftDiv, 
						bRightDiv, 
						oCell,
						oDestRect,
						true );

				// Vertical part
				if ( oCellBelow == null || oCellBelow.GetElevation() < oCell.GetElevation() )
				{
					IDrawable oDrawable;
					int iDiffOrig, iDiff = oCell.GetElevation();
					if ( oCellBelow != null )
					{
						iDiff -= oCellBelow.GetElevation();
					}
					iDiffOrig = iDiff;

					boolean bMiddle = false;
					boolean bUpper = false;
					if ( iDiff > 1 )
					{
						// Top part, left
						if ( IsVerticalTilePlain( oCell.GetElevation(), oCell, oCellLeft, oCellBelowLeft ) )
						{
							eType = TileType.TOP;
						}
						else
						{
							eType = TileType.CORNER;
						}

						oDrawable = mVerticalTileSets.get( oCell.GetType() ).GetDrawable( eType, TilePos.UPPER_LEFT );
						oDestRect.offset( -iCellWidth / 2, iCellHeight / 2 );
						mArrayCells[ oPos.mPosX ][ oPos.mPosY ].AddTile( oDrawable, oDestRect, true );

						// Top part, right
						if ( IsVerticalTilePlain( oCell.GetElevation(), oCell, oCellRight, oCellBelowRight ) )
						{
							eType = TileType.TOP;
						}
						else
						{
							eType = TileType.CORNER;
						}

						oDrawable = mVerticalTileSets.get( oCell.GetType() ).GetDrawable( eType, TilePos.UPPER_RIGHT );
						oDestRect.offset( iCellWidth / 2, 0 );
						mArrayCells[ oPos.mPosX ][ oPos.mPosY ].AddTile( oDrawable, oDestRect, true );

						--iDiff;

						while ( iDiff > 1 )
						{
							if ( IsVerticalTilePlain( oCell.GetElevation() - ( iDiffOrig - iDiff ), oCell, oCellLeft, oCellBelowLeft ) )
							{
								eType = TileType.PLAIN;
							}
							else
							{
								eType = TileType.LEFT;
							}

							oDrawable = mVerticalTileSets.get( oCell.GetType() ).GetDrawable( eType, ( bUpper ? TilePos.UPPER_LEFT : TilePos.LOWER_LEFT ) );
							oDestRect.offset( -iCellWidth / 2, iStackHeight );
							mArrayCells[ oPos.mPosX ][ oPos.mPosY ].AddTile( oDrawable, oDestRect, true );

							if ( IsVerticalTilePlain( oCell.GetElevation() - ( iDiffOrig - iDiff ), oCell, oCellRight, oCellBelowRight ) )
							{
								eType = TileType.PLAIN;
							}
							else
							{
								eType = TileType.RIGHT;
							}

							oDrawable = mVerticalTileSets.get( oCell.GetType() ).GetDrawable( eType, ( bUpper ? TilePos.UPPER_RIGHT : TilePos.LOWER_RIGHT ) );
							oDestRect.offset( iCellWidth / 2, 0 );
							mArrayCells[ oPos.mPosX ][ oPos.mPosY ].AddTile( oDrawable, oDestRect, true );

							bUpper = !bUpper;
							--iDiff;
						}
					}
					else
					{
						bMiddle = true;
					}

					// Add the last part: the background tile then the lower tile
					IDrawable oBackgroundDrawable;

					oDestRect.offset( -iCellWidth / 2, iStackHeight );
					if ( oCellBelow != null )
					{
						// We need to set a background bitmap for these
						if ( IsVerticalBottomTileDivision( oCellLeft, oCellBelow ) )
						{
							eType = TileType.LEFT;
						}
						else
						{
							eType = TileType.PLAIN;
						}
						oBackgroundDrawable = mTileSets.get( oCellBelow.GetType() ).GetDrawable( eType, TilePos.LOWER_LEFT );
						mArrayCells[ oPos.mPosX ][ oPos.mPosY ].AddTile( oBackgroundDrawable, oDestRect, false );
					}

					if ( IsVerticalTilePlain( oCell.GetElevation() - ( iDiffOrig - iDiff ), oCell, oCellLeft, oCellBelowLeft ) )
					{
						if ( bMiddle )
						{
							eType = TileType.PLAIN;
						}
						else
						{
							eType = TileType.BOTTOM;
						}
					}
					else
					{
						eType = TileType.CORNER;
					}

					TilePos ePos = bMiddle ? TilePos.MIDDLE_LEFT : TilePos.LOWER_LEFT;
					oDrawable = mVerticalTileSets.get( oCell.GetType() ).GetDrawable( eType, ePos );
					mArrayCells[ oPos.mPosX ][ oPos.mPosY ].AddTile( oDrawable, oDestRect, true );

					oDestRect.offset( iCellWidth / 2, 0 );
					if ( oCellBelow != null )
					{
						// We need to set a background bitmap for these
						if ( IsVerticalBottomTileDivision( oCellRight, oCellBelow ) )
						{
							eType = TileType.RIGHT;
						}
						else
						{
							eType = TileType.PLAIN;
						}
						oBackgroundDrawable = mTileSets.get( oCellBelow.GetType() ).GetDrawable( eType, TilePos.LOWER_RIGHT );
						mArrayCells[ oPos.mPosX ][ oPos.mPosY ].AddTile( oBackgroundDrawable, oDestRect, false );
					}

					if ( IsVerticalTilePlain( oCell.GetElevation() - ( iDiffOrig - iDiff ), oCell, oCellRight, oCellBelowRight ) )
					{
						if ( bMiddle )
						{
							eType = TileType.PLAIN;
						}
						else
						{
							eType = TileType.BOTTOM;
						}
					}
					else
					{
						eType = TileType.CORNER;
					}

					ePos = bMiddle ? TilePos.MIDDLE_RIGHT : TilePos.LOWER_RIGHT;
					oDrawable = mVerticalTileSets.get( oCell.GetType() ).GetDrawable( eType, ePos );
					mArrayCells[ oPos.mPosX ][ oPos.mPosY ].AddTile( oDrawable, oDestRect, true );
				}
			}
		}

		if ( mBattleField.GetSelectedCell() != null )
		{
			UpdateSelectedCellDrawable();
		}
	}

	public BattleCell GetSelectedCell()
	{
		return mBattleField.GetSelectedCell();
	}

	private BattleCell GetCell( Position oPos, int iXOffsetWest, int iYOffsetNorth )
	{
		return mBattleField.GetCell( Position.OffsetPositionRelative( oPos, mDrawingOrientation, iXOffsetWest, iYOffsetNorth ) );
	}

	public void doDraw( Canvas oCanvas, float fZoom, Position oPosition )
	{
		// Draw background
		oCanvas.drawColor( Color.BLACK );

		int iOffsetX, iOffsetY;
		float fXStep = DisplayConstants2D.GetCellWidth() * fZoom;
		float fYStep = DisplayConstants2D.GetCellHeight() * fZoom;
		int iCanvasWidth = oCanvas.getWidth();
		int iCanvasHeight = oCanvas.getHeight();

		// Compute offset for the given center cell: move to upperleft corner
		switch ( mDrawingOrientation )
		{
		case NORTH:
			iOffsetX = ( int )( oPosition.mPosX * fXStep - ( iCanvasWidth - fXStep ) / 2 );
			iOffsetY = ( int )( oPosition.mPosY * fYStep - ( iCanvasHeight - fYStep ) / 2 );
			break;
		case SOUTH:
			// probably not up-to-date...
			iOffsetX = ( int )( oPosition.mPosX * fXStep - ( iCanvasWidth - fXStep ) / 2 );
			iOffsetY = ( int )( oPosition.mPosY * fYStep - ( iCanvasHeight - fYStep ) / 2 );
			break;
		case WEST:
			iOffsetX = ( int )( ( mBattleField.GetHeight() - 1 - oPosition.mPosY ) * fXStep - ( iCanvasWidth - fXStep ) / 2 );
			iOffsetY = ( int )( ( oPosition.mPosX ) * fYStep - ( iCanvasHeight - fYStep ) / 2 );
			break;
		case EAST:
			iOffsetX = ( int )( ( oPosition.mPosY ) * fXStep - ( iCanvasWidth - fXStep ) / 2 );
			iOffsetY = ( int )( ( mBattleField.GetWidth() - 1 - oPosition.mPosX ) * fYStep - ( iCanvasHeight - fYStep ) / 2 );
			break;
		default:
			iOffsetX = 0;
			iOffsetY = 0;
			break;
		}

		// Prevent change of drawn Objects during draw method
		synchronized ( BattleThread2D.mDrawingLock )
		{
			mDrawableList.DrawBattle( oCanvas, -iOffsetX, -iOffsetY, fZoom );
		}
	}

	public void UpdateSelectedCellDrawable()
	{
		// Update drawing rectangle
		FillCellAbsDrawingPosition( mBattleField.GetSelectedCell().GetPos(), mDrawingPosition );
		mSelectedCellDrawable.MoveDestRect( mDrawingPosition.mPosX, mDrawingPosition.mPosY );

		// Update ZOrder
		if ( mSelectedCellDrawable.SetZOrder( GetCell2D( mBattleField.GetSelectedCell().GetPos() ).GetZOrder() + ZOrderPosition.SELECTED_ANIM.Value() ) )
		{
			mDrawableList.Changed();
		}
	}

	public void update( Observable oObs, Object oObj )
	{
		// BattleField notifies us of a change: update selected cell drawable
		if ( mBattleField.GetSelectedCell() != null )
		{
			synchronized ( BattleThread2D.mDrawingLock )
			{
				UpdateSelectedCellDrawable();
			}
		}
	}
}
