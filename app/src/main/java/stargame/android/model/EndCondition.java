package stargame.android.model;

import android.os.Bundle;

import java.util.Vector;

import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;

/**
 * This class encapsulates the logic of determining when a battle ends
 *
 * @author Duduche
 */
public class EndCondition implements ISavable
{
    public enum EndConditionType
    {
        TYPE_FACTION_DEATH,
        // A whole faction dies
        TYPE_UNIT_DEATH,     // A specific unit dies
    }

    /**
     * If true, it is a victory condition, otherwise a defeat
     */
    boolean mVictory;

    private final static String M_VICTORY = "Victory";

    /**
     * The type of EndCondition
     */
    EndConditionType mType;

    private final static String M_TYPE = "Type";

    /**
     * The faction death triggers the condition
     */
    Faction mFaction;

    private final static String M_FACTION = "Faction";

    /**
     * The unit whose death triggers the condition
     */
    BattleUnit mUnit;

    private final static String M_UNIT = "Unit";

    public EndCondition()
    {
        mFaction = null;
        mUnit = null;
        mType = null;
    }

    public void SetUnitCondition( BattleUnit oUnit )
    {
        mUnit = oUnit;
        mType = EndConditionType.TYPE_UNIT_DEATH;
    }

    public void SetFactionCondition( Faction eFaction )
    {
        mFaction = eFaction;
        mType = EndConditionType.TYPE_FACTION_DEATH;
    }

    public boolean IsThisTheEnd( Battle oBattle )
    {
        switch ( mType )
        {
            case TYPE_UNIT_DEATH:
                return ( mUnit.GetCurrentHitPoints() == 0 );
            case TYPE_FACTION_DEATH:
                Vector< BattleUnit > vecUnits = oBattle.GetUnits();

                for ( BattleUnit oUnit : vecUnits )
                {
                    if ( oUnit.GetUnit().GetFaction() == mFaction && oUnit.GetCurrentHitPoints() > 0 )
                    {
                        return false;
                    }
                }

                return true;
            default:
                return false;
        }
    }

    public boolean IsVictory()
    {
        return mVictory;
    }

    public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
    {
        oObjectMap.putBoolean( M_VICTORY, mVictory );
        oObjectMap.putInt( M_TYPE, mType.ordinal() );
        oObjectMap.putInt( M_FACTION, mFaction.ordinal() );

        String strObjKey = SavableHelper.saveInMap( mUnit, oGlobalMap );
        oObjectMap.putString( M_UNIT, strObjKey );
    }

    public static EndCondition loadState( Bundle oGlobalMap, String strObjKey )
    {
        Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey,
                                                             EndCondition.class.getName() );

        if ( oObjectBundle == null )
        {
            return null;
        }

        EndCondition oCondition = new EndCondition();

        oCondition.mVictory = oObjectBundle.getBoolean( M_VICTORY );
        oCondition.mType = EndConditionType.values()[ oObjectBundle.getInt( M_TYPE ) ];
        oCondition.mFaction = Faction.values()[ oObjectBundle.getInt( M_FACTION ) ];

        String strKey = oObjectBundle.getString( M_UNIT );
        oCondition.mUnit = BattleUnit.loadState( oObjectBundle, strKey );

        return oCondition;
    }

    public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
    {
        return loadState( oGlobalMap, strObjKey );
    }
}
