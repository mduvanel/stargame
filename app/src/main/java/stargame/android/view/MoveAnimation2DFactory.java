package stargame.android.view;

import android.graphics.Bitmap;

import java.util.Observer;
import java.util.Vector;

import stargame.android.model.BattleCell;
import stargame.android.util.Orientation;
import stargame.android.util.Position;

public class MoveAnimation2DFactory
{
    private static final int WALK_STEPS = 3;

    public static OrientableAnimation2D Create(
            BattleUnit2D oUnit,
            Position oSourcePos,
            Position oDestPos,
            Observer oObserver )
    {
        // Create the animation group
        OrientableAnimation2D oAnimations = new OrientableAnimation2D();

        // Check height difference
        BattleCell oSourceCell = BattleField2D.GetInstance().GetBattleField().GetCell( oSourcePos );
        BattleCell oDestCell = BattleField2D.GetInstance().GetBattleField().GetCell( oDestPos );
        int iDiff = oDestCell.GetElevation() - oSourceCell.GetElevation();
        int iCellWidth = DisplayConstants2D.GetCellWidth();
        int iCellHeight = DisplayConstants2D.GetCellHeight();
        int iCellStackHeight = DisplayConstants2D.GetCellStackHeight();

        int iDistance = Math.abs(
                ( oSourcePos.mPosX + oSourcePos.mPosY ) - ( oDestPos.mPosX + oDestPos.mPosY ) );
        boolean bAnimate = true;

        if ( iDistance > 1 && iDiff != 0 )
        {
            // Do not animate yet vertical + horizontal jumps
            bAnimate = false;
        }

        if ( bAnimate )
        {
            // for each Orientation create the animation
            for ( int i = 0; i < Orientation.NONE.ordinal(); ++i )
            {
                Orientation eDrawingOrientation = Orientation.values()[ i ];

                // Build the animation for this Orientation
                Vector< Bitmap > vecBitmaps = new Vector< Bitmap >();
                vecBitmaps.add( 0, oUnit.GetLeftStepBitmap( eDrawingOrientation ) );
                vecBitmaps.add( 1, oUnit.GetCenterStepBitmap( eDrawingOrientation ) );
                vecBitmaps.add( 2, oUnit.GetRightStepBitmap( eDrawingOrientation ) );
                vecBitmaps.add( 3, oUnit.GetFallBitmap( eDrawingOrientation ) );
                Vector< Animation2DStep > vecSteps = new Vector< Animation2DStep >();

                // Compute the direction multiplier
                int iOffset = GetDestZOffset( oSourcePos, oDestPos,
                                              BattleField2D.GetInstance().GetDrawingOrientation() );
                int iPositiveOffset = Math.max( 1, iOffset );
                Position oAbsDirFactor = oDestPos.Sub( oSourcePos );
                oAbsDirFactor = oAbsDirFactor.Div( iDistance );
                Position oDirFactor = Position.OffsetPositionAbsolute(
                        new Position( 0, 0 ),
                        BattleField2D.GetInstance().GetDrawingOrientation(),
                        oAbsDirFactor.mPosX,
                        oAbsDirFactor.mPosY );

                // Compute the default step (1/4 of cell)
                Position oStepMove = oDirFactor.Mul( iCellWidth / 4, iCellHeight / 4 );

                if ( iDistance > 1 )
                {
                    // Horizontal jump
                    int iJumpDistanceWidth = iCellWidth * iDistance - iCellWidth / 4;
                    int iJumpDistanceHeight = iCellHeight * iDistance - iCellHeight / 4;

                    // Create the steps
                    vecSteps.add( new Animation2DStep(
                            0,
                            oStepMove.mPosX,
                            oStepMove.mPosY,
                            WALK_STEPS,
                            iPositiveOffset ) );
                    vecSteps.add( new Animation2DStep(
                            3,
                            oStepMove.mPosX + oDirFactor.mPosX * iJumpDistanceWidth / 4,
                            oStepMove.mPosY + oDirFactor.mPosY * iJumpDistanceHeight / 4 - iCellHeight / 3,
                            WALK_STEPS,
                            iPositiveOffset ) );
                    vecSteps.add( new Animation2DStep(
                            3,
                            oStepMove.mPosX + oDirFactor.mPosX * iJumpDistanceWidth / 2,
                            oStepMove.mPosY + oDirFactor.mPosY * iJumpDistanceHeight / 2 - iCellHeight / 2,
                            WALK_STEPS,
                            iPositiveOffset ) );
                    vecSteps.add( new Animation2DStep(
                            3,
                            oStepMove.mPosX + oDirFactor.mPosX * 3 * iJumpDistanceWidth / 4,
                            oStepMove.mPosY + oDirFactor.mPosY * 3 * iJumpDistanceHeight / 4 - iCellHeight / 3,
                            WALK_STEPS,
                            iPositiveOffset ) );
                    vecSteps.add( new Animation2DStep(
                            1,
                            oStepMove.mPosX * 4 * iDistance,
                            oStepMove.mPosY * 4 * iDistance,
                            WALK_STEPS,
                            iPositiveOffset ) );
                }
                else
                {
                    if ( iDiff == 0 )
                    {
                        // Simple horizontal move

                        // Create the steps
                        vecSteps.add( new Animation2DStep(
                                0,
                                oStepMove.mPosX,
                                oStepMove.mPosY,
                                WALK_STEPS,
                                iPositiveOffset ) );
                        vecSteps.add( new Animation2DStep(
                                1,
                                oStepMove.mPosX * 2,
                                oStepMove.mPosY * 2,
                                WALK_STEPS,
                                iPositiveOffset ) );
                        vecSteps.add( new Animation2DStep(
                                2,
                                oStepMove.mPosX * 3,
                                oStepMove.mPosY * 3,
                                WALK_STEPS,
                                iPositiveOffset ) );
                        vecSteps.add( new Animation2DStep(
                                1,
                                oStepMove.mPosX * 4,
                                oStepMove.mPosY * 4,
                                WALK_STEPS,
                                iPositiveOffset ) );
                    }
                    else
                    {
                        // Vertical jump : first steps are different depending on direction
                        if ( iDiff > 0 )
                        {
                            // Jump up
                            vecSteps.add( new Animation2DStep(
                                    3,
                                    oStepMove.mPosX,
                                    oStepMove.mPosY - iDiff * iCellStackHeight * 2 / 3,
                                    WALK_STEPS,
                                    0 ) );
                            vecSteps.add( new Animation2DStep(
                                    3,
                                    oStepMove.mPosX * 2,
                                    oStepMove.mPosY * 2 - iDiff * iCellStackHeight - 5,
                                    WALK_STEPS,
                                    0 ) );
                            vecSteps.add( new Animation2DStep(
                                    0,
                                    oStepMove.mPosX * 3,
                                    oStepMove.mPosY * 3 - iDiff * iCellStackHeight,
                                    WALK_STEPS,
                                    iOffset ) );
                        }
                        else
                        {
                            // Jump down
                            vecSteps.add( new Animation2DStep(
                                    0,
                                    oStepMove.mPosX,
                                    oStepMove.mPosY,
                                    WALK_STEPS,
                                    0 ) );
                            vecSteps.add( new Animation2DStep(
                                    3,
                                    oStepMove.mPosX * 2,
                                    oStepMove.mPosY * 2 - 5,
                                    WALK_STEPS,
                                    0 ) );
                            vecSteps.add( new Animation2DStep(
                                    3,
                                    oStepMove.mPosX * 3,
                                    oStepMove.mPosY * 3 - iDiff * iCellStackHeight * 2 / 3,
                                    WALK_STEPS,
                                    iOffset ) );
                        }

                        // Arrival step
                        vecSteps.add( new Animation2DStep(
                                1,
                                oStepMove.mPosX * 4,
                                oStepMove.mPosY * 4 - iDiff * iCellStackHeight,
                                WALK_STEPS,
                                iOffset ) );
                    }
                }

                Animation2D oAnimation = new Animation2D( vecBitmaps, vecSteps, false );

                // Debug
                oAnimation.SetDebugLog(
                        true,
                        String.format(
                                "Move from %s to %s, Orientation = %s",
                                oSourcePos.toString(),
                                oDestPos.toString(),
                                eDrawingOrientation.toString() ) );

                oAnimation.addObserver( oObserver );
                oAnimations.SetAnimation( oAnimation, eDrawingOrientation );
            }

            return oAnimations;
        }
        else
        {
            return null;
        }
    }

    public static int GetDestZOffset( Position oSourcePos, Position oDestPos,
                                      Orientation eDrawingOrientation )
    {
        int iDiff = 0;
        switch ( eDrawingOrientation )
        {
            case NORTH:
                iDiff = oDestPos.mPosY - oSourcePos.mPosY;
                break;
            case SOUTH:
                iDiff = oSourcePos.mPosY - oDestPos.mPosY;
                break;
            case WEST:
                iDiff = oDestPos.mPosX - oSourcePos.mPosX;
                break;
            case EAST:
                iDiff = oSourcePos.mPosX - oDestPos.mPosX;
                break;
            case NONE:
            default:
                break;
        }

        // Add 1 to be drawn in front of other units
        return ( iDiff * ZOrderPosition.TOTAL.ordinal() ) + 1;
    }
}
