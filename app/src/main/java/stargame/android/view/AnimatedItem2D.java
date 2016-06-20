package stargame.android.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.xmlpull.v1.XmlPullParserException;

import stargame.android.util.Logger;
import stargame.android.util.Orientation;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.content.res.Resources.NotFoundException;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class AnimatedItem2D implements IDrawable
{
	/** All the animation groups for this Animated2D item */
	private HashMap< String, OrientableAnimation2D > mMapAnimationGroups;

	/** The current (base) animation */
	private String mBaseAnimation;

	private static class AnimationDesc
	{
		private String mName;
		private Orientation mOrientation;
		private OrientableAnimation2D mExternalAnimation;

		public AnimationDesc( String strName, Orientation eOrientation )
		{
			mName = strName;
			mOrientation = eOrientation;
			mExternalAnimation = null;
		}

		public AnimationDesc( OrientableAnimation2D oGroup, Orientation eOrientation )
		{
			mName = null;
			mOrientation = eOrientation;
			mExternalAnimation = oGroup;
		}

		public boolean IsExternal()
		{
			return ( mExternalAnimation != null );
		}
	}

	/** The next (non-base) animations */
	private LinkedList< AnimationDesc > mNextAnimations;

	public AnimatedItem2D( String strAnimationSet, Resources oResources )
	{
		mBaseAnimation = null;
		mNextAnimations = new LinkedList< AnimationDesc >();

		// parse the file
		try 
		{
			// Create the parser for the main Animation file
			int id = oResources.getIdentifier( strAnimationSet, "xml", "stargame.android" );
			XmlResourceParser oParser = oResources.getXml( id );

			Logger.i( String.format( "Loading Animation Set %s...", strAnimationSet ) );

		    int eventType = oParser.getEventType();
		    int iAnimationsNb = 0;
		    boolean bLoadAnimations = false;

		    while ( eventType != XmlResourceParser.END_DOCUMENT )
		    {
		    	if ( bLoadAnimations )
	        	{
				    for ( int iAnimation = 0; iAnimation < iAnimationsNb; ++iAnimation )
		    		{
				    	eventType = oParser.nextTag(); // Skip to the "AnimationType" tag
				    	eventType = oParser.nextTag(); // Skip the "AnimationType" tag

				    	assert oParser.getName().equals( "AnimationType" );
				    	eventType = oParser.next();

				    	String strAnimationType = oParser.getText();

				    	eventType = oParser.nextTag(); // Skip to the "AnimationOrientation" tag

				    	assert oParser.getName().equals( "AnimationOrientation" );
				    	eventType = oParser.next();

				    	String strOrientation = oParser.nextText();
				    	Orientation eOrientation = Orientation.valueOf( strOrientation );

				    	eventType = oParser.nextTag(); // Skip to the "AnimationFilename" tag

				    	assert oParser.getName().equals( "AnimationFilename" );
 
				    	String strFilename = oParser.nextText();
				    	XmlResourceParser oAnimationParser = oResources.getXml( 
				    			oResources.getIdentifier( strFilename, "xml", "stargame.android" ) );

				    	Logger.i( String.format( "Loading Animation from file %s...", strFilename ) );

				    	Animation2D oAnimation = new Animation2D( oAnimationParser );

				    	OrientableAnimation2D oGroup = mMapAnimationGroups.get( strAnimationType );
				    	if ( oGroup == null )
				    	{
				    		oGroup = new OrientableAnimation2D();
				    		mMapAnimationGroups.put( strAnimationType, oGroup );
				    	}

			    		oGroup.SetAnimation( oAnimation, eOrientation );

				    	eventType = oParser.nextTag(); // Skip the end tag
		    		}

				    // We finished loading animations, exit
				    return;
	        	}
		    	else if ( eventType == XmlResourceParser.START_TAG )
		        {
		    		if ( oParser.getName().equals( "AnimationsNb" ) )
		            {
		                iAnimationsNb = Integer.parseInt( oParser.nextText() );
		                mMapAnimationGroups = new HashMap< String, OrientableAnimation2D >( iAnimationsNb );
		            }
		            else if ( oParser.getName().equals( "AnimationList" ) ) 
		            {
		            	bLoadAnimations = true;
		                continue;
		            }
		        }
		        eventType = oParser.next();
		    }

		    Logger.i( "Loading Complete!" );
		}
		catch ( NotFoundException e )
		{
			Logger.e( String.format( "Could not find resource: %s", e.getMessage() ) );
		    e.printStackTrace();
		}
		catch ( XmlPullParserException e )
		{
			Logger.e( String.format( "XML Error while loading: %s", e.getMessage() ) );
		    e.printStackTrace();
		}
		catch ( IOException e )
		{
			Logger.e( String.format( "IO Error while loading: %s", e.getMessage() ) );
		    e.printStackTrace();
		}
	}

	private void CheckIfCurrentAnimationIsFinished()
	{
		if ( !mNextAnimations.isEmpty() )
		{
			boolean bFinished = false;
			if ( mNextAnimations.getFirst().IsExternal() )
			{
				bFinished = mNextAnimations.getFirst().mExternalAnimation.isFinished();
			}
			else
			{
				// bFinished = mMapAnimationGroups.get( mNextAnimations.getFirst().mName ).willLoop();
				bFinished = mMapAnimationGroups.get( mNextAnimations.getFirst().mName ).isFinished();
			}

			if ( bFinished )
			{
				mNextAnimations.remove();
				if ( !mNextAnimations.isEmpty() && !mNextAnimations.getFirst().IsExternal() )
				{
					Logger.d( "Setting Orientation..." );
					// Prepare next animation's Orientation
					OrientableAnimation2D oGroup = mMapAnimationGroups.get( mNextAnimations.getFirst().mName );
					oGroup.SetCurrentOrientation( mNextAnimations.getFirst().mOrientation );
				}
			}
		}
	}

	public void doDraw( Canvas oCanvas, RectF oSourceRect, float fZoomFactor, Paint oPaint )
	{
		CheckIfCurrentAnimationIsFinished();

		GetCurrentGroup().doDraw( oCanvas, oSourceRect, fZoomFactor, oPaint );
	}

	public boolean isFinished()
	{
		return GetCurrentGroup().isFinished();
	}

	public Animation2D GetCurrentAnimation()
	{
		return GetCurrentGroup().GetCurrentAnimation();
	}

	public void ResetCurrentAnimation()
	{
		GetCurrentGroup().ResetCurrentAnimation();
	}

	public boolean IsBaseAnimationRunning()
	{
		return mNextAnimations.isEmpty();
	}

	private OrientableAnimation2D GetCurrentGroup()
	{
		if ( !mNextAnimations.isEmpty() )
		{
			if ( mNextAnimations.getFirst().IsExternal() )
			{
				return mNextAnimations.getFirst().mExternalAnimation;
			}
			else
			{
				return mMapAnimationGroups.get( mNextAnimations.getFirst().mName );
			}
		}
		else
		{
			return mMapAnimationGroups.get( mBaseAnimation );
		}
	}

	public void SetDrawingOrientation( Orientation eOrientation )
	{
		// Set the Orientation of the Base animation only
		//GetCurrentGroup().SetCurrentOrientation( eOrientation );
		mMapAnimationGroups.get( mBaseAnimation ).SetCurrentOrientation( eOrientation );
	}

	public void SetBaseAnimation( String strAnimation, Orientation eOrientation )
	{
		mBaseAnimation = strAnimation;
		OrientableAnimation2D oGroup = mMapAnimationGroups.get( strAnimation );
		if ( null != oGroup )
		{
			oGroup.SetCurrentOrientation( eOrientation );
		}
	}

	public String GetBaseAnimation()
	{
		return mBaseAnimation;
	}

	public void SetNextAnimation( String strAnimation, Orientation eOrientation )
	{
		mNextAnimations.add( new AnimationDesc( strAnimation, eOrientation ) );

		// Set correct orientation directly if it is the only element
		if ( mNextAnimations.size() == 1 )
		{
			OrientableAnimation2D oGroup = mMapAnimationGroups.get( strAnimation );
			oGroup.SetCurrentOrientation( eOrientation );
		}
	}

	public void SetNextAnimation( OrientableAnimation2D oGroup, Orientation eOrientation )
	{
		mNextAnimations.add( new AnimationDesc( oGroup, eOrientation ) );
		oGroup.SetCurrentOrientation( eOrientation );
	}
}
