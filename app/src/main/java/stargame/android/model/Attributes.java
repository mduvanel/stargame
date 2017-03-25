package stargame.android.model;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.storage.SavableHelper;
import stargame.android.util.Logger;

/**
 * This class describes all the attributes that a Unit has. Jobs and equipments
 * also have attributes.
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

    int GetResourcePoints()
    {
        return mResourcePoints;
    }

    /**
     * Movement ability
     */
    private int mMovement;

    private static final String M_MOVEMENT = "Movement";

    int GetMovement()
    {
        return mMovement;
    }

    /**
     * Vertical jump ability
     */
    private int mVerticalJump;

    private static final String M_VERTICAL_JUMP = "VJump";

    int GetVerticalJump()
    {
        return mVerticalJump;
    }

    /**
     * Horizontal jump ability
     */
    private int mHorizontalJump;

    private static final String M_HORIZONTAL_JUMP = "HJump";

    int GetHorizontalJump()
    {
        return mHorizontalJump;
    }

    /**
     * Physical defense
     */
    private int mPhysicalDef;

    private static final String M_PHY_DEF = "PDef";

    public int GetPhysicalDef()
    {
        return mPhysicalDef;
    }

    /**
     * Magical defense
     */
    private int mMagicDef;

    private static final String M_MAGIC_DEF = "MDef";

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

    int GetDexterity()
    {
        return mDexterity;
    }

    /**
     * Magic attack power
     */
    private int mMagicPower;

    private static final String M_MAGIC_POWER = "MPower";

    public int GetMagicPower()
    {
        return mMagicPower;
    }

    /**
     * Dodge capacity (percentage)
     */
    private int mDodge;

    private static final String M_DODGE = "Dodge";

    int GetDodge()
    {
        return mDodge;
    }

    /**
     * Magic dodge capacity (percentage)
     */
    private int mMagicDodge;

    private static final String M_MAGIC_DODGE = "MDodge";

    int GetMagicDodge()
    {
        return mMagicDodge;
    }

    /**
     * Speed
     */
    private int mSpeed;

    private static final String M_SPEED = "Speed";

    int GetSpeed()
    {
        return mSpeed;
    }

    /**
     * Attack range (straight)
     */
    private int mAttackRange;

    private static final String M_RANGE = "Range";

    int GetAttackRange()
    {
        return mAttackRange;
    }

    /**
     * Physical attack hit chance
     */
    private int mHitChance;

    private static final String M_HIT = "Hit";

    int GetHitChance()
    {
        return mHitChance;
    }

    /**
     * Physical attack hit chance
     */
    private int mMagicHitChance;

    private static final String M_MAGIC_HIT = "MHit";

    int GetMagicHitChance()
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
    public Attributes(
            int iHitPoints, int iResourcePoints, int iMovement,
            int iVerticalJump, int iHorizontalJump, int iPhysicalDef,
            int iMagicDef, int iStrength, int iDexterity, int iMagicPower,
            int iDodge, int iMagicDodge, int iSpeed, int iAttackRange,
            int iHitChance, int iMagicHitChance )
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
    void AddAttributes( Attributes oAttr )
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

    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        oObjectStore.putInt( M_DEXTERITY, mDexterity );
        oObjectStore.putInt( M_DODGE, mDodge );
        oObjectStore.putInt( M_HIT, mHitChance );
        oObjectStore.putInt( M_HORIZONTAL_JUMP, mHorizontalJump );
        oObjectStore.putInt( M_HP, mHitPoints );
        oObjectStore.putInt( M_MAGIC_DEF, mMagicDef );
        oObjectStore.putInt( M_MAGIC_DODGE, mMagicDodge );
        oObjectStore.putInt( M_MAGIC_HIT, mMagicHitChance );
        oObjectStore.putInt( M_MOVEMENT, mMovement );
        oObjectStore.putInt( M_MAGIC_POWER, mMagicPower );
        oObjectStore.putInt( M_PHY_DEF, mPhysicalDef );
        oObjectStore.putInt( M_RANGE, mAttackRange );
        oObjectStore.putInt( M_RP, mResourcePoints );
        oObjectStore.putInt( M_SPEED, mSpeed );
        oObjectStore.putInt( M_STRENGTH, mStrength );
        oObjectStore.putInt( M_VERTICAL_JUMP, mVerticalJump );
    }

    boolean LoadFromResources( Resources oResources, String strResourceName )
    {
        boolean bSuccess = false;

        // Create the parser from the UnitJob type
        int id = oResources.getIdentifier(
                strResourceName, "xml", "stargame.android" );
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
                            mResourcePoints = Integer.parseInt(
                                    oParser.nextText() );
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
                            mMagicHitChance = Integer.parseInt(
                                    oParser.nextText() );
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
                            mHorizontalJump = Integer.parseInt(
                                    oParser.nextText() );
                        }
                        else if ( oParser.getName().equals( "VJump" ) )
                        {
                            mVerticalJump = Integer.parseInt(
                                    oParser.nextText() );
                        }
                    }
                    eventType = oParser.next();
                }

                bSuccess = true;
            }
            catch ( XmlPullParserException e )
            {
                Logger.e( String.format(
                        "XML Error while loading: %s", e.getMessage() ) );
                e.printStackTrace();
            }
            catch ( IOException e )
            {
                Logger.e( String.format(
                        "XML Error while loading: %s", e.getMessage() ) );
                e.printStackTrace();
            }
        }

        return bSuccess;
    }

    public static Attributes loadState( IStorage oGlobalStore, String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey, Attributes.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        Attributes oAttributes = new Attributes();

        oAttributes.mDexterity = oObjectStore.getInt( M_DEXTERITY );
        oAttributes.mDodge = oObjectStore.getInt( M_DODGE );
        oAttributes.mHitChance = oObjectStore.getInt( M_HIT );
        oAttributes.mHorizontalJump = oObjectStore.getInt( M_HORIZONTAL_JUMP );
        oAttributes.mHitPoints = oObjectStore.getInt( M_HP );
        oAttributes.mMagicDef = oObjectStore.getInt( M_MAGIC_DEF );
        oAttributes.mMagicDodge = oObjectStore.getInt( M_MAGIC_DODGE );
        oAttributes.mMagicHitChance = oObjectStore.getInt( M_MAGIC_HIT );
        oAttributes.mMovement = oObjectStore.getInt( M_MOVEMENT );
        oAttributes.mMagicPower = oObjectStore.getInt( M_MAGIC_POWER );
        oAttributes.mPhysicalDef = oObjectStore.getInt( M_PHY_DEF );
        oAttributes.mAttackRange = oObjectStore.getInt( M_RANGE );
        oAttributes.mResourcePoints = oObjectStore.getInt( M_RP );
        oAttributes.mSpeed = oObjectStore.getInt( M_SPEED );
        oAttributes.mStrength = oObjectStore.getInt( M_STRENGTH );
        oAttributes.mVerticalJump = oObjectStore.getInt( M_VERTICAL_JUMP );

        return oAttributes;
    }

    public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
    {
        return loadState( oGlobalStore, strObjKey );
    }
}
