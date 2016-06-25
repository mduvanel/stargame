package stargame.android.model;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;
import stargame.android.util.Logger;

/**
 * This class describes all the attributes that a Unit has. Jobs and equipments also
 * have attributes.
 *
 * @author Duduche
 */
public class Attributes implements ISavable
{
    /**
     * HP
     */
    private int mHitPoints;

    private static final String M_HP = "HP";

    public int GetHitPoints()
    {
        return mHitPoints;
    }

    /**
     * RP (mana or energy)
     */
    private int mResourcePoints;

    private static final String M_RP = "RP";

    public int GetResourcePoints()
    {
        return mResourcePoints;
    }

    /**
     * Movement ability
     */
    private int mMovement;

    private static final String M_MOVEMENT = "Movement";

    public int GetMovement()
    {
        return mMovement;
    }

    /**
     * Vertical jump ability
     */
    private int mVerticalJump;

    private static final String M_VJUMP = "VJump";

    public int GetVerticalJump()
    {
        return mVerticalJump;
    }

    /**
     * Horizontal jump ability
     */
    private int mHorizontalJump;

    private static final String M_HJUMP = "HJump";

    public int GetHorizontalJump()
    {
        return mHorizontalJump;
    }

    /**
     * Physical defense
     */
    private int mPhysicalDef;

    private static final String M_PDEF = "PDef";

    public int GetPhysicalDef()
    {
        return mPhysicalDef;
    }

    /**
     * Magical defense
     */
    private int mMagicDef;

    private static final String M_MDEF = "MDef";

    public int GetMagicDef()
    {
        return mMagicDef;
    }

    /**
     * Physical strength
     */
    private int mStrength;

    private static final String M_STRENGTH = "Strength";

    public int GetStrength()
    {
        return mStrength;
    }

    /**
     * Dexterity
     */
    private int mDexterity;

    private static final String M_DEXTERITY = "Dexterity";

    public int GetDexterity()
    {
        return mDexterity;
    }

    /**
     * Magic attack power
     */
    private int mMagicPower;

    private static final String M_MPOWER = "MPower";

    public int GetMagicPower()
    {
        return mMagicPower;
    }

    /**
     * Dodge capacity (percentage)
     */
    private int mDodge;

    private static final String M_DODGE = "Dodge";

    public int GetDodge()
    {
        return mDodge;
    }

    /**
     * Magic dodge capacity (percentage)
     */
    private int mMagicDodge;

    private static final String M_MDODGE = "MDodge";

    public int GetMagicDodge()
    {
        return mMagicDodge;
    }

    /**
     * Speed
     */
    private int mSpeed;

    private static final String M_SPEED = "Speed";

    public int GetSpeed()
    {
        return mSpeed;
    }

    /**
     * Attack range (straight)
     */
    private int mAttackRange;

    private static final String M_RANGE = "Range";

    public int GetAttackRange()
    {
        return mAttackRange;
    }

    /**
     * Physical attack hit chance
     */
    private int mHitChance;

    private static final String M_HIT = "Hit";

    public int GetHitChance()
    {
        return mHitChance;
    }

    /**
     * Physical attack hit chance
     */
    private int mMagicHitChance;

    private static final String M_MHIT = "MHit";

    public int GetMagicHitChance()
    {
        return mMagicHitChance;
    }

    public Attributes()
    {
        mHitPoints = 0;
        mResourcePoints = 0;
        mMovement = 0;
        mVerticalJump = 0;
        mHorizontalJump = 0;
        mPhysicalDef = 0;
        mMagicDef = 0;
        mStrength = 0;
        mDexterity = 0;
        mMagicPower = 0;
        mDodge = 0;
        mMagicDodge = 0;
        mSpeed = 0;
        mAttackRange = 0;
        mHitChance = 0;
        mMagicHitChance = 0;
    }

    /**
     * "Brute-force" constructor
     */
    public Attributes( int iHitPoints, int iResourcePoints, int iMovement, int iVerticalJump,
                       int iHorizontalJump, int iPhysicalDef, int iMagicDef, int iStrength,
                       int iDexterity, int iMagicPower, int iDodge, int iMagicDodge, int iSpeed,
                       int iAttackRange, int iHitChance, int iMagicHitChance )
    {
        mHitPoints = iHitPoints;
        mResourcePoints = iResourcePoints;
        mMovement = iMovement;
        mVerticalJump = iVerticalJump;
        mHorizontalJump = iHorizontalJump;
        mPhysicalDef = iPhysicalDef;
        mMagicDef = iMagicDef;
        mStrength = iStrength;
        mDexterity = iDexterity;
        mMagicPower = iMagicPower;
        mDodge = iDodge;
        mMagicDodge = iMagicDodge;
        mSpeed = iSpeed;
        mAttackRange = iAttackRange;
        mHitChance = iHitChance;
        mMagicHitChance = iMagicHitChance;
    }

    /**
     * Copy-constructor
     */
    public Attributes( Attributes oAttr )
    {
        mHitPoints = oAttr.mHitPoints;
        mResourcePoints = oAttr.mResourcePoints;
        mMovement = oAttr.mMovement;
        mVerticalJump = oAttr.mVerticalJump;
        mHorizontalJump = oAttr.mHorizontalJump;
        mPhysicalDef = oAttr.mPhysicalDef;
        mMagicDef = oAttr.mMagicDef;
        mStrength = oAttr.mStrength;
        mDexterity = oAttr.mDexterity;
        mMagicPower = oAttr.mMagicPower;
        mDodge = oAttr.mDodge;
        mMagicDodge = oAttr.mMagicDodge;
        mSpeed = oAttr.mSpeed;
        mAttackRange = oAttr.mAttackRange;
        mHitChance = oAttr.mHitChance;
        mMagicHitChance = oAttr.mMagicHitChance;
    }

    /**
     * Add the given attributes to the current Attributes
     */
    public void AddAttributes( Attributes oAttr )
    {
        mHitPoints += oAttr.mHitPoints;
        mResourcePoints += oAttr.mResourcePoints;
        mMovement += oAttr.mMovement;
        mVerticalJump += oAttr.mVerticalJump;
        mHorizontalJump += oAttr.mHorizontalJump;
        mPhysicalDef += oAttr.mPhysicalDef;
        mMagicDef += oAttr.mMagicDef;
        mStrength += oAttr.mStrength;
        mDexterity += oAttr.mDexterity;
        mMagicPower += oAttr.mMagicPower;
        mDodge += oAttr.mDodge;
        mMagicDodge += oAttr.mMagicDodge;
        mSpeed += oAttr.mSpeed;
        mAttackRange += oAttr.mAttackRange;
        mHitChance += oAttr.mHitChance;
        mMagicHitChance += oAttr.mMagicHitChance;
    }

    public void Reset()
    {
        mHitPoints = 0;
        mResourcePoints = 0;
        mMovement = 0;
        mVerticalJump = 0;
        mHorizontalJump = 0;
        mPhysicalDef = 0;
        mMagicDef = 0;
        mStrength = 0;
        mDexterity = 0;
        mMagicPower = 0;
        mDodge = 0;
        mMagicDodge = 0;
        mSpeed = 0;
        mAttackRange = 0;
        mHitChance = 0;
        mMagicHitChance = 0;
    }

    public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
    {
        oObjectMap.putInt( M_DEXTERITY, mDexterity );
        oObjectMap.putInt( M_DODGE, mDodge );
        oObjectMap.putInt( M_HIT, mHitChance );
        oObjectMap.putInt( M_HJUMP, mHorizontalJump );
        oObjectMap.putInt( M_HP, mHitPoints );
        oObjectMap.putInt( M_MDEF, mMagicDef );
        oObjectMap.putInt( M_MDODGE, mMagicDodge );
        oObjectMap.putInt( M_MHIT, mMagicHitChance );
        oObjectMap.putInt( M_MOVEMENT, mMovement );
        oObjectMap.putInt( M_MPOWER, mMagicPower );
        oObjectMap.putInt( M_PDEF, mPhysicalDef );
        oObjectMap.putInt( M_RANGE, mAttackRange );
        oObjectMap.putInt( M_RP, mResourcePoints );
        oObjectMap.putInt( M_SPEED, mSpeed );
        oObjectMap.putInt( M_STRENGTH, mStrength );
        oObjectMap.putInt( M_VJUMP, mVerticalJump );
    }

    public boolean LoadFromResources( Resources oResources, String strResourceName )
    {
        boolean bSuccess = false;

        // Create the parser from the UnitJob type
        int id = oResources.getIdentifier( strResourceName, "xml", "stargame.android" );
        XmlResourceParser oParser = oResources.getXml( id );
        if ( null != oParser )
        {
            try
            {
                int eventType = oParser.getEventType();

                while ( eventType != XmlResourceParser.END_DOCUMENT )
                {
                    if ( eventType == XmlResourceParser.START_TAG )
                    {
                        if ( oParser.getName().equals( "HitPoints" ) )
                        {
                            mHitPoints = Integer.parseInt( oParser.nextText() );
                        }
                        else if ( oParser.getName().equals( "ResourcePoints" ) )
                        {
                            mResourcePoints = Integer.parseInt( oParser.nextText() );
                        }
                        else if ( oParser.getName().equals( "Power" ) )
                        {
                            mStrength = Integer.parseInt( oParser.nextText() );
                        }
                        else if ( oParser.getName().equals( "MPower" ) )
                        {
                            mMagicPower = Integer.parseInt( oParser.nextText() );
                        }
                        else if ( oParser.getName().equals( "Defense" ) )
                        {
                            mPhysicalDef = Integer.parseInt( oParser.nextText() );
                        }
                        else if ( oParser.getName().equals( "MDefense" ) )
                        {
                            mMagicDef = Integer.parseInt( oParser.nextText() );
                        }
                        else if ( oParser.getName().equals( "Dodge" ) )
                        {
                            mDodge = Integer.parseInt( oParser.nextText() );
                        }
                        else if ( oParser.getName().equals( "MDodge" ) )
                        {
                            mMagicDodge = Integer.parseInt( oParser.nextText() );
                        }
                        else if ( oParser.getName().equals( "Hit" ) )
                        {
                            mHitChance = Integer.parseInt( oParser.nextText() );
                        }
                        else if ( oParser.getName().equals( "MHit" ) )
                        {
                            mMagicHitChance = Integer.parseInt( oParser.nextText() );
                        }
                        else if ( oParser.getName().equals( "Speed" ) )
                        {
                            mSpeed = Integer.parseInt( oParser.nextText() );
                        }
                        else if ( oParser.getName().equals( "Movement" ) )
                        {
                            mMovement = Integer.parseInt( oParser.nextText() );
                        }
                        else if ( oParser.getName().equals( "HJump" ) )
                        {
                            mHorizontalJump = Integer.parseInt( oParser.nextText() );
                        }
                        else if ( oParser.getName().equals( "VJump" ) )
                        {
                            mVerticalJump = Integer.parseInt( oParser.nextText() );
                        }
                    }
                    eventType = oParser.next();
                }
            }
            catch ( XmlPullParserException e )
            {
                Logger.e( String.format( "XML Error while loading: %s", e.getMessage() ) );
                e.printStackTrace();
            }
            catch ( IOException e )
            {
                Logger.e( String.format( "XML Error while loading: %s", e.getMessage() ) );
                e.printStackTrace();
            }
        }

        return bSuccess;
    }

    public static Attributes loadState( Bundle oGlobalMap, String strObjKey )
    {
        Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey,
                                                             Attributes.class.getName() );

        if ( oObjectBundle == null )
        {
            return null;
        }

        Attributes oAttribs = new Attributes();

        oAttribs.mDexterity = oObjectBundle.getInt( M_DEXTERITY );
        oAttribs.mDodge = oObjectBundle.getInt( M_DODGE );
        oAttribs.mHitChance = oObjectBundle.getInt( M_HIT );
        oAttribs.mHorizontalJump = oObjectBundle.getInt( M_HJUMP );
        oAttribs.mHitPoints = oObjectBundle.getInt( M_HP );
        oAttribs.mMagicDef = oObjectBundle.getInt( M_MDEF );
        oAttribs.mMagicDodge = oObjectBundle.getInt( M_MDODGE );
        oAttribs.mMagicHitChance = oObjectBundle.getInt( M_MHIT );
        oAttribs.mMovement = oObjectBundle.getInt( M_MOVEMENT );
        oAttribs.mMagicPower = oObjectBundle.getInt( M_MPOWER );
        oAttribs.mPhysicalDef = oObjectBundle.getInt( M_PDEF );
        oAttribs.mAttackRange = oObjectBundle.getInt( M_RANGE );
        oAttribs.mResourcePoints = oObjectBundle.getInt( M_RP );
        oAttribs.mSpeed = oObjectBundle.getInt( M_SPEED );
        oAttribs.mStrength = oObjectBundle.getInt( M_STRENGTH );
        oAttribs.mVerticalJump = oObjectBundle.getInt( M_VJUMP );

        return oAttribs;
    }

    public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
    {
        return loadState( oGlobalMap, strObjKey );
    }
}
