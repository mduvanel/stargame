package stargame.android.view;

import java.util.StringTokenizer;
import java.util.Vector;

import stargame.android.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.NinePatchDrawable;

public class DialogDrawer
{
	private final static float FONT_SIZE_FLOAT = 15f;

	private final static int LINE_COUNT = 3;

	/** The string containing the full text to be displayed */
	private String mDialogString;

	/** 
	 * A vector of pre-processed strings that can be directly
	 * printed in the text zone. Recomputed every time the Canvas
	 * dimensions are changed
	 */
	private Vector<String> mVecLines;

	/** When true, text display is waiting for user input to resume */
	private boolean mWaitForInput;

	/** The first line that is printed */
	private int mFirstLine;

	/** The currently printed line */
	private int mCurrentLine;

	/** The last printed character index in the current line */
	private int mCurrentChar;

	/** The number of lines that can be drawn on one screen */
	//private int mDrawableLines;

	/** Pre-computed height of the 'l' character for height calculations */
	private int mVerticalTextSize;

	/** The paint for the text */
	private Paint mPaint;

	/** Member Rect for drawing */
	private Rect mDrawRect;

	private NinePatchDrawable mTextDrawable;

	/** Portrait of the unit that is talking */
	private Bitmap mPortraitBitmap;

	/** Bitmap for end-of-text marker */
	private Bitmap mEndOfTextBitmap;

	/** Surface dimensions */
	private int mWidth;
	private int mHeight;

	/** Text size ratio due to DIP behavior... */
	private float mTextSizeRatio;

	public DialogDrawer( Resources oRes )
	{
		mDialogString = "";
		mWidth = -1;
		mHeight = -1;
		mFirstLine = -1;
		mCurrentLine = -1;
		mCurrentChar = -1;
		//mDrawableLines = -1;
		mWaitForInput = false;
		mVecLines = new Vector<String>();

		mPaint = new Paint();
		mTextSizeRatio = oRes.getDisplayMetrics().density;

		// Paint settings
		mPaint.setTextAlign( Align.LEFT );
		mPaint.setColor( Color.BLACK );
		mPaint.setAlpha( 192 );
		mPaint.setTypeface( Typeface.DEFAULT_BOLD );
		mPaint.setAntiAlias( true );
		mPaint.setTextSize( mTextSizeRatio * FONT_SIZE_FLOAT );

		mDrawRect = new Rect();
		mPaint.getTextBounds( "l", 0, 1, mDrawRect );
		mVerticalTextSize = mDrawRect.height();

		// load bitmap
		Bitmap oButtonBitmap = BitmapRepository.GetInstance().GetBitmap( R.drawable.button_raw );
		byte [] aChunk = oButtonBitmap.getNinePatchChunk();
		mTextDrawable = new NinePatchDrawable( oRes, oButtonBitmap, aChunk, new Rect( 7, 7, 17, 17 ), null );

		mEndOfTextBitmap = BitmapRepository.GetInstance().GetBitmap( R.drawable.end_of_text );
	}

	public void SetDialog( Bitmap oBitmap, String strDialogString )
	{
		synchronized ( BattleThread2D.mDrawingLock )
		{
			mDialogString = strDialogString;
			mPortraitBitmap = oBitmap;

			// Recompute the text and reset the text counters
			RecomputeSplits();
			mCurrentChar = -1;
			mCurrentLine = 0;
			mFirstLine = 0;
			mWaitForInput = false;
		}
	}

	public void DrawPortrait( Canvas oCanvas )
	{
		if ( mPortraitBitmap != null )
		{
			FillTextZoneRect( mDrawRect );
			mDrawRect.offset( mTextDrawable.getMinimumWidth() / 2, mTextDrawable.getMinimumHeight() / 2 );
			mDrawRect.right = mDrawRect.left + mPortraitBitmap.getWidth();
			mDrawRect.bottom = mDrawRect.top + mPortraitBitmap.getHeight();
			oCanvas.drawBitmap( mPortraitBitmap, null, mDrawRect, null );
		}
	}

	public void FillTextZoneRect( Rect oRect )
	{
		int iHeight = mTextDrawable.getMinimumHeight() + LINE_COUNT * ( int )mPaint.getFontSpacing();
		int iMargin = DisplayConstants2D.GetInterfaceMargin();
		oRect.set(
				iMargin,
				mHeight - iMargin - iHeight,
				mWidth - iMargin,
				mHeight - iMargin );
	}

	public void DrawDialog( Canvas oCanvas )
	{
		int iMargin = DisplayConstants2D.GetInterfaceMargin();
		FillTextZoneRect( mDrawRect );
		float fVerticalOffset = mDrawRect.top + mVerticalTextSize + mTextDrawable.getMinimumHeight() / 2;
		float fHorizontalOffset = mDrawRect.left + mTextDrawable.getMinimumWidth() / 2;
		if ( mPortraitBitmap != null )
		{
			fHorizontalOffset += mPortraitBitmap.getWidth() + iMargin;
		}

		// Print already displayed full lines
		for ( int i = mFirstLine; i < mCurrentLine; ++i )
		{
			oCanvas.drawText( 
					mVecLines.get( i ), 
					fHorizontalOffset,
					fVerticalOffset,
					mPaint );
			fVerticalOffset += mPaint.getFontSpacing(); 
		}

		// Print current line
		String strCurLine = mVecLines.get( mCurrentLine );
		if ( mCurrentChar == strCurLine.length() - 1 )
		{
			oCanvas.drawText( 
					strCurLine, 
					fHorizontalOffset,
					fVerticalOffset,
					mPaint );

			// Are all the lines drawn?
			if ( mCurrentLine - mFirstLine == LINE_COUNT - 1 || mCurrentLine == mVecLines.size() - 1 )
			{
				// draw end of text bitmap
				int iMinimumWidth = mTextDrawable.getMinimumWidth() / 2;
				int iMinimumHeight = mTextDrawable.getMinimumHeight() / 2;
				mDrawRect.set( 
						mWidth - mEndOfTextBitmap.getWidth() - iMargin - iMinimumWidth, 
						mHeight - mEndOfTextBitmap.getHeight() - iMargin - iMinimumHeight,
						mWidth - iMargin - iMinimumWidth,
						mHeight - iMargin - iMinimumHeight );
				oCanvas.drawBitmap( mEndOfTextBitmap, null, mDrawRect, null );
				mWaitForInput = true;
			}
			else
			{
				// Draw first char of next line.
				fVerticalOffset += mPaint.getFontSpacing();
				mCurrentChar = 0;
				++mCurrentLine;

				oCanvas.drawText( 
						mVecLines.get( mCurrentLine ).substring( 0, 1 ), 
						fHorizontalOffset,
						fVerticalOffset,
						mPaint );
			}
		}
		else
		{
			oCanvas.drawText( 
					strCurLine.substring( 0, ++mCurrentChar ), 
					fHorizontalOffset,
					fVerticalOffset,
					mPaint );
		}
	}

	public void doDraw( Canvas oCanvas )
	{
		if ( IsDialogRunning() )
		{
			// Draw the text zone
			FillTextZoneRect( mDrawRect );
			mTextDrawable.setBounds( mDrawRect );
			mTextDrawable.draw( oCanvas );
	
			// Draw the portrait
			DrawPortrait( oCanvas );
	
			// Draw the text
			DrawDialog( oCanvas );
		}
	}

	public void RecomputeSplits()
	{
		if ( mDialogString.length() > 0 )
		{
			// Compute the available width to write the dialog text
			FillTextZoneRect( mDrawRect );
			int iAvailableWidth = mDrawRect.width() - mTextDrawable.getMinimumWidth() - mEndOfTextBitmap.getWidth();
			if ( mPortraitBitmap != null )
			{
				iAvailableWidth -= mPortraitBitmap.getWidth() + DisplayConstants2D.GetInterfaceMargin();
			}

			// Compute how many lines can be written in the dialog box
			//int iAvailableHeight = oDisplayRect.height() - mTextDrawable.getMinimumHeight();
			//
			//mPaint.getTextBounds( mDialogString, 0, mDialogString.length(), oClipRect );
	
			//iAvailableHeight -= oClipRect.height();
			//mDrawableLines = 1 + iAvailableHeight / ( int )mPaint.getFontSpacing();
	
			// Now group the tokens in lines
			mVecLines.clear();
			String strCurrentString = "";
			StringTokenizer oTokenizer = new StringTokenizer( mDialogString );
	
			while ( oTokenizer.hasMoreTokens() )
			{
				String strWord = oTokenizer.nextToken();
				String strTmpString = strCurrentString;
	
				if ( strCurrentString.length() != 0 )
				{
					strTmpString += " ";
				}
				strTmpString += strWord;
	
				// Check if tmp string still fits on one line
				mPaint.getTextBounds( strTmpString, 0, strTmpString.length(), mDrawRect );
				if ( mDrawRect.width() > iAvailableWidth )
				{
					// Now check if the newly added word can fit alone or should be split
					mPaint.getTextBounds( strWord, 0, strWord.length(), mDrawRect );
					if ( mDrawRect.width() > iAvailableWidth )
					{
						boolean bDone = false;
						int iSplitFactor = 2;
						do
						{
							String strStart = strWord.substring( 0, ( int )Math.ceil( ( double )strWord.length() / ( double )iSplitFactor ) );
							mPaint.getTextBounds( strStart, 0, strStart.length(), mDrawRect );
							if ( mDrawRect.width() > iAvailableWidth )
							{
								// Try again with bigger split factor
								++iSplitFactor;
							}
							else
							{
								int iStartPos = 0, iWordLength = strStart.length();
	
								// Test if first split can fit in current line
								mPaint.getTextBounds( strStart, 0, strStart.length(), mDrawRect );
								if ( mDrawRect.width() > iAvailableWidth )
								{
									// All splits should be inserted in a new line
									mVecLines.add( strCurrentString );
								}
								else
								{
									mVecLines.add( strCurrentString + " " + strStart );
									iStartPos = strStart.length();
								}
	
								// Now add remaining splits
								for ( int i = ( iStartPos > 0 ? 1 : 0 ); i < iSplitFactor - 1; ++i )
								{
									String strSplit = strWord.substring( iStartPos, ( i + 1 ) * iWordLength );
									mVecLines.add( strSplit );
									iStartPos += iWordLength;
								}
	
								// Last split is put in strCurrentString to try to have a longer line
								strCurrentString = strWord.substring( iStartPos );
								bDone = true;
							}
						} while ( !bDone );
					}
					else
					{
						// Add the line and add new word to the next line
						mVecLines.add( strCurrentString );
						strCurrentString = strWord;
					}
				}
				else
				{
					// Expand the string
					strCurrentString = strTmpString;
				}
			}

			if ( strCurrentString.length() > 0 )
			{
				// Do not forget to add current string!
				mVecLines.add( strCurrentString );
			}
		}
	}

	public void SetSurfaceSize( int iWidth, int iHeight )
	{
		mWidth = iWidth;
		mHeight = iHeight;

		// TODO: check that we are still on the same word after orientation changes
		RecomputeSplits();
	}

	/**
	 * Resumes the text display. If all text was not displayed yet, display it all. 
	 * Returns true if all the dialog has been displayed, false otherwise.
	 */
	public boolean ResumeTextDisplay()
	{
		if ( mWaitForInput )
		{
			if ( ( mVecLines.size() - 1 == mCurrentLine ) && mWaitForInput )
			{
				// Cleanup
				mCurrentLine = -1;
				mCurrentChar = -1;
				return true;
			}
			else
			{
				// Prepare next round of lines
				mWaitForInput = false;
				++mCurrentLine;
				mFirstLine = mCurrentLine;
				mCurrentChar = -1;
				return false;
			}
		}
		else
		{
			// Not yet waiting, force display of all the text for this bunch
			mWaitForInput = true;
			mCurrentLine = Math.min( mVecLines.size() - 1, mFirstLine + LINE_COUNT - 1 );
			mCurrentChar = mVecLines.get( mCurrentLine ).length() - 1;
			return false;
		}
	}

	public boolean IsDialogRunning()
	{
		return ( mCurrentLine != -1 );
	}
}
