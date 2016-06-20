package stargame.android.model;

import stargame.android.model.BattleCell;

import java.io.IOException;
import java.util.Observable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.content.res.XmlResourceParser;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;
import stargame.android.util.FieldType;
import stargame.android.util.Position;

/**
 * This is the BattleField class, owning the BattleCells together
 */
public class BattleField extends Observable implements ISavable
{
	/** Width of the field */
	private int mWidth;

	private final static String M_WIDTH = "Width";

	/** Height of the Field */
	private int mHeight;

	private final static String M_HEIGHT = "Height";

	/** The array of BattleCells */
	private BattleCell[][] mArrayCells;

	private final static String M_CELLS = "Cells";

    /** The currently selected BattleCell */
    private BattleCell mSelectedCell;

	private final static String M_SELECTED_CELL = "Selected";

	/** private for loadState() method */
	private BattleField()
	{
		mWidth = -1;
		mHeight = -1;
		mArrayCells = null;
		mSelectedCell = null;
	}

	/** Ctor */
	public BattleField( int iWidth, int iHeight )
	{
		mWidth = iWidth;
		mHeight = iHeight;

		mArrayCells = new BattleCell[ iWidth ][ iHeight ];
		for ( int i = 0; i < iWidth; ++i )
		{
			for ( int j = 0; j < iHeight; ++j )
			{
				mArrayCells[ i ][ j ] = new BattleCell( i, j );
			}	
		}

		mSelectedCell = mArrayCells[ 0 ][ 0 ];
	}

	public BattleField( XmlResourceParser oParser )
	{
		try 
		{
		    int eventType = oParser.getEventType();
		    boolean bLoadField = false;

		    while ( eventType != XmlPullParser.END_DOCUMENT )
		    {
		    	if ( bLoadField )
	        	{
				    for ( int iIndex = 0; iIndex < mWidth * mHeight; ++iIndex )
		    		{
				    	int iX = iIndex % mWidth;
				    	int iY = iIndex / mWidth;
				    	mArrayCells[ iX ][ iY ] = new BattleCell( iX, iY );

				    	eventType = oParser.nextTag(); // Skip to the "Case" tag
				    	eventType = oParser.nextTag(); // Skip the "Case" tag

				    	assert oParser.getName().equals( "Type" );
				    	eventType = oParser.next();

				    	mArrayCells[ iX ][ iY ].SetType( oParser.getText() );

				    	eventType = oParser.nextTag(); // Skip to the "Height" tag

				    	assert oParser.getName().equals( "Height" );
				    	eventType = oParser.next();

				    	mArrayCells[ iX ][ iY ].SetElevation( Integer.parseInt( oParser.nextText() ) );

				    	eventType = oParser.nextTag(); // Skip the end tag
		    		}

				    // We finished loading everything, exit loop
				    break;
	        	}
		    	else if ( eventType == XmlPullParser.START_TAG )
		        {
		    		if ( oParser.getName().equals( "TotalWidth" ) )
		            {
		                mWidth = Integer.parseInt( oParser.nextText() );
		            }
		            else if ( oParser.getName().equals( "TotalHeight" ) ) 
		            {
		                mHeight = Integer.parseInt( oParser.nextText() ); 
		            }
		            else if ( oParser.getName().equals( "FieldDesc" ) ) 
		            {
		        		mArrayCells = new BattleCell[ mWidth ][ mHeight ];
		                bLoadField = true;
		                continue;
		            }
		        }
		        eventType = oParser.next();
		    }
		}
		catch ( XmlPullParserException e )
		{
		    e.printStackTrace();
		}
		catch ( IOException e )
		{
		    e.printStackTrace();
		}

		mSelectedCell = mArrayCells[ 0 ][ 0 ];
	}

	public boolean handleTouchEvent( MotionEvent event )
	{
		return false;
	}

	public boolean IsPositionFree( int iPosX, int iPosY, BattleUnit oUnit )
	{
		BattleUnit oUnitPresent = mArrayCells[ iPosX ][ iPosY ].GetUnit();

		if ( null == oUnitPresent || oUnitPresent.GetUnit().GetFaction() == oUnit.GetUnit().GetFaction() )
		{
			return true;
		}

		return false;
	}

	public FieldType GetFieldType( Position oPos )
	{
		return GetFieldType( oPos.mPosX, oPos.mPosY );
	}

	public FieldType GetFieldType( int iPosX, int iPosY )
	{
		return mArrayCells[ iPosX ][ iPosY ].GetType();
	}

	public int GetElevation( Position oPos )
	{
		return GetElevation( oPos.mPosX, oPos.mPosY );
	}

	public int GetElevation( int iPosX, int iPosY )
	{
		return mArrayCells[ iPosX ][ iPosY ].GetElevation();
	}

	public int GetElevationDiff( Position oPos1, Position oPos2 )
	{
		return GetElevationDiff( oPos1.mPosX, oPos1.mPosY, oPos2.mPosX, oPos2.mPosY );
	}

	public int GetElevationDiff( int iPos1X, int iPos1Y, int iPos2X, int iPos2Y )
	{
		return GetElevation( iPos1X, iPos1Y ) - GetElevation( iPos2X, iPos2Y );
	}

	public int GetWidth()
	{
		return mWidth;
	}

	public int GetHeight()
	{
		return mHeight;
	}

	public BattleCell GetCell( Position oPos )
	{
		return GetCell( oPos.mPosX, oPos.mPosY );
	}

	public BattleCell GetCell( int iPosX, int iPosY )
	{
		if ( iPosX < 0 || iPosX >= mWidth ||
			 iPosY < 0 || iPosY >= mHeight )
		{
			return null;
		}

		return mArrayCells[ iPosX ][ iPosY ];
	}

	public BattleCell GetSelectedCell()
	{
		return mSelectedCell;
	}

	public void SetSelectedCell( Position oPos )
	{
		if ( oPos.IsInside( new Rect( 0, 0, mWidth - 1, mHeight - 1 ) ) && 
				mArrayCells[ oPos.mPosX ][ oPos.mPosY ] != null )
		{
			mSelectedCell = mArrayCells[ oPos.mPosX ][ oPos.mPosY ];
	    	setChanged();
	    	notifyObservers();
		}
	}

	public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
	{
		oObjectMap.putInt( M_WIDTH, mWidth );
		oObjectMap.putInt( M_HEIGHT, mHeight );

		Bundle oCellsBundle = SavableHelper.saveBidimensionalArrayInMap( mArrayCells, oGlobalMap );
		oObjectMap.putBundle( M_CELLS, oCellsBundle );

		if ( mSelectedCell != null )
		{
			String strObjKey = SavableHelper.saveInMap( mSelectedCell, oGlobalMap );
			oObjectMap.putString( M_SELECTED_CELL, strObjKey );
		}
	}

	public static BattleField loadState( Bundle oGlobalMap, String strObjKey )
	{
		Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey, BattleField.class.getName() );

		if ( oObjectBundle == null )
		{
			return null;
		}

		BattleField oBattlefield = new BattleField();

		oBattlefield.mWidth = oObjectBundle.getInt( M_WIDTH );
		oBattlefield.mHeight = oObjectBundle.getInt( M_HEIGHT );

		Bundle oCellsBundle = oObjectBundle.getBundle( M_CELLS );
		oBattlefield.mArrayCells = new BattleCell[ oBattlefield.mWidth ][ oBattlefield.mHeight ];
		SavableHelper.loadBidimensionalArrayFromMap( 
				oBattlefield.mArrayCells, 
				oCellsBundle, 
				oGlobalMap, 
				new BattleCell() );

		if ( oObjectBundle.containsKey( M_SELECTED_CELL ) )
		{
			String strKey = oObjectBundle.getString( M_SELECTED_CELL );
			oBattlefield.mSelectedCell = BattleCell.loadState( oGlobalMap, strKey );
		}

		return oBattlefield;
	}

	public ISavable createInstance(Bundle oGlobalMap, String strObjKey)
	{
		return loadState( oGlobalMap, strObjKey );
	}
}
