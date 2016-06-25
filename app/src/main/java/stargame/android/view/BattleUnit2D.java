package stargame.android.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.RectF;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import stargame.android.model.BattleUnit;
import stargame.android.util.Logger;
import stargame.android.util.Orientation;
import stargame.android.util.Position;

public class BattleUnit2D implements Observer
{
    private BattleUnit mBattleUnit;

    private BattleDrawableList mDrawableList;

    private BattleDrawable mDrawable;
    private BattleDrawable mStatusDrawable;

    private int mCurrentZOrderOffset;
    private Position mCurrentPositionOffset;

    private Position mDrawingPosition;

    private Bitmap mPortraitBitmap;

    private HashMap< String, Bitmap > mStepBitmaps;

    private AnimatedItem2D mAnimations;

    /**
     * Animations for the Unit's status
     */
    private UnitStatus2D mStatus;

    /**
     * The current drawing orientation
     */
    private Orientation mDrawingOrientation;

    public BattleUnit2D( BattleUnit oUnit, Context oContext, BattleDrawableList oDrawableList )
    {
        mDrawableList = oDrawableList;
        mDrawingOrientation = Orientation.NORTH;
        mBattleUnit = oUnit;
        mCurrentZOrderOffset = 0;
        mCurrentPositionOffset = new Position( 0, 0 );
        mDrawingPosition = new Position();

        // Load portrait bitmap
        String strName = String.format( "%s_face", mBattleUnit.GetUnit().GetJobType().toString() );
        mPortraitBitmap = BitmapRepository.GetInstance().GetBitmap( strName );

        mStepBitmaps = new HashMap< String, Bitmap >();
        oUnit.addObserver( this );

        mStatus = new UnitStatus2D( oUnit.GetStatusVector(), oContext );
        LoadAnimations( oContext.getResources() );

        mDrawable = new BattleDrawable(
                mAnimations,
                mStatus.GetStatusPaint(),
                new RectF( 0, 0, DisplayConstants2D.GetCellWidth(),
                           DisplayConstants2D.GetUnitHeight() ),
                0 );

        mStatusDrawable = new BattleDrawable(
                mStatus,
                null,
                new RectF( 0, 0, DisplayConstants2D.GetCellWidth(),
                           DisplayConstants2D.GetUnitHeight() ),
                0 );

        UpdateFromBattleCell();

        // Add this Unit to the drawables list
        mDrawableList.AddDrawable( mDrawable );
        mDrawableList.AddDrawable( mStatusDrawable );
    }

    public Bitmap GetPortraitBitmap()
    {
        return mPortraitBitmap;
    }

    private Bitmap GetStepBitmap( Orientation eOrientation, String strType )
    {
        String strName = String.format( "%s_%s_%s", mBattleUnit.GetUnit().GetJobType().toString(),
                                        eOrientation.toString().toLowerCase(), strType );
        Bitmap oBitmap = mStepBitmaps.get( strName );
        if ( oBitmap == null )
        {
            try
            {
                oBitmap = BitmapRepository.GetInstance().GetBitmap( strName );
                mStepBitmaps.put( strName, oBitmap );
            }
            catch ( Exception e )
            {
                Logger.e( String.format( "Failed to load Bitmap %s!", strName ) );
            }
        }
        return oBitmap;
    }

    public Bitmap GetLeftStepBitmap( Orientation eOrientation )
    {
        return GetStepBitmap( eOrientation, "left" );
    }

    public Bitmap GetCenterStepBitmap( Orientation eOrientation )
    {
        return GetStepBitmap( eOrientation, "center" );
    }

    public Bitmap GetRightStepBitmap( Orientation eOrientation )
    {
        return GetStepBitmap( eOrientation, "right" );
    }

    public Bitmap GetFallBitmap( Orientation eOrientation )
    {
        return GetStepBitmap( eOrientation, "fall" );
    }

    public boolean IsUnit( BattleUnit oUnit )
    {
        return mBattleUnit.equals( oUnit );
    }

    private void LoadAnimations( Resources oRes )
    {
        switch ( mBattleUnit.GetUnit().GetJobType() )
        {
            case TYPE_ARCHER:
                mAnimations = new AnimatedItem2D( "archer_animations", oRes );
                break;
            case TYPE_SOLDIER:
                mAnimations = new AnimatedItem2D( "soldier_animations", oRes );
                break;
            case TYPE_MAGE:
                mAnimations = new AnimatedItem2D( "mage_animations", oRes );
                break;
            case TYPE_PRIEST:
                mAnimations = new AnimatedItem2D( "priest_animations", oRes );
                break;
            case TYPE_ROGUE:
                mAnimations = new AnimatedItem2D( "rogue_animations", oRes );
                break;
            case TYPE_BARBARIAN:
                mAnimations = new AnimatedItem2D( "barbarian_animations", oRes );
                break;
            default:
                break;
        }
    }

    public void SetDrawingOrientation( Orientation eDrawingOrientation )
    {
        synchronized ( BattleThread2D.mDrawingLock )
        {
            mDrawingOrientation = eDrawingOrientation;

            // Be sure to update position from BattleCell
            UpdateFromBattleCell();
            mAnimations.SetDrawingOrientation(
                    mBattleUnit.GetOrientation().GetResultingOrientation( mDrawingOrientation ) );
        }
    }

    public void SetBaseAnimation( String strAnimation )
    {
        synchronized ( BattleThread2D.mDrawingLock )
        {
            mAnimations.SetBaseAnimation( strAnimation,
                                          mBattleUnit.GetOrientation().GetResultingOrientation(
                                                  mDrawingOrientation ) );
        }
    }

    public void SetNextAnimation( String strAnimation, Orientation eAnimationOrientation )
    {
        synchronized ( BattleThread2D.mDrawingLock )
        {
            mAnimations.SetNextAnimation( strAnimation,
                                          eAnimationOrientation.GetResultingOrientation(
                                                  mDrawingOrientation ) );
        }
    }

    public void SetNextAnimation( OrientableAnimation2D oAnimations,
                                  Orientation eAnimationOrientation )
    {
        synchronized ( BattleThread2D.mDrawingLock )
        {
            mAnimations.SetNextAnimation( oAnimations,
                                          eAnimationOrientation.GetResultingOrientation(
                                                  mDrawingOrientation ) );
        }
    }

    /**
     * Compute the drawing ZOrder for this unit
     */
    private int ComputeUnitZOrder()
    {
        int iCellZOrder = BattleField2D.GetInstance().GetCell2D(
                mBattleUnit.GetCell().GetPos() ).GetZOrder();
        return iCellZOrder + ZOrderPosition.UNIT.Value() + mCurrentZOrderOffset;
    }

    public void UpdateDrawing( int iZOrderOffset, int iXOffset, int iYOffset )
    {
        mCurrentZOrderOffset = iZOrderOffset;
        mCurrentPositionOffset.SetPos( iXOffset, iYOffset );
        Logger.d( String.format( "New Unit ZOffset = %d", mCurrentZOrderOffset ) );
    }

    public void UpdateFromBattleCell()
    {
        // Check if BattleCell changed and update BattleDrawables
        int iZOrder = ComputeUnitZOrder();
        boolean changed = mDrawable.SetZOrder( iZOrder );
        if ( !mAnimations.IsBaseAnimationRunning() )
        {
            // do not display status while attacking / moving
            iZOrder = -1;
        }
        else
        {
            // status should be displayed in front of the unit
            ++iZOrder;
        }

        if ( changed || mStatusDrawable.SetZOrder( iZOrder ) )
        {
            mDrawableList.Changed();
        }

        // Update status vector
        mStatus.SetStatusVector( mBattleUnit.GetStatusVector() );

        // Update status paint
        mDrawable.SetPaint( mStatus.GetStatusPaint() );

        // Update drawing rects
        BattleField2D.GetInstance().FillCellAbsDrawingPosition( mBattleUnit.GetCell().GetPos(),
                                                                mDrawingPosition );
        mDrawingPosition.Add( mCurrentPositionOffset );
        mDrawingPosition.mPosY += ( DisplayConstants2D.GetCellHeight() - DisplayConstants2D.GetUnitHeight() );
        mDrawable.MoveDestRect( mDrawingPosition.mPosX, mDrawingPosition.mPosY );
        mStatusDrawable.MoveDestRect( mDrawingPosition.mPosX, mDrawingPosition.mPosY );
    }

    public void update( Observable oObs, Object oObj )
    {
        // BattleUnit changed: update Orientation and BattleCell info
        Logger.i( String.format(
                "BattleUnit2D receives change notification from BattleUnit. New pos is %s",
                mBattleUnit.GetCell().GetPos().toString() ) );
        SetDrawingOrientation( mDrawingOrientation );
    }
}
