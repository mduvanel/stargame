package stargame.android.model;

import java.util.Vector;

import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.storage.SavableHelper;

/**
 * This class encapsulates the logic of determining when a battle ends
 *
 * @author Duduche
 */
public class EndCondition implements ISavable
{
    private enum EndConditionType
    {
        TYPE_FACTION_DEATH,
        // A whole faction dies
        TYPE_UNIT_DEATH,     // A specific unit dies
    }

    /**
     * If true, it is a victory condition, otherwise a defeat
     */
    private boolean mVictory;

    private final static String M_VICTORY = "Victory";

    /**
     * The type of EndCondition
     */
    private EndConditionType mType;

    private final static String M_TYPE = "Type";

    /**
     * The faction death triggers the condition
     */
    private Faction mFaction;

    private final static String M_FACTION = "Faction";

    /**
     * The unit whose death triggers the condition
     */
    private BattleUnit mUnit;

    private final static String M_UNIT = "Unit";

    private EndCondition()
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
                    if ( oUnit.GetUnit().GetFaction() == mFaction &&
                            oUnit.GetCurrentHitPoints() > 0 )
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

    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        oObjectStore.putBoolean( M_VICTORY, mVictory );
        oObjectStore.putInt( M_TYPE, mType.ordinal() );
        oObjectStore.putInt( M_FACTION, mFaction.ordinal() );

        String strObjKey = SavableHelper.saveInStore( mUnit, oGlobalStore );
        oObjectStore.putString( M_UNIT, strObjKey );
    }

    public static EndCondition loadState( IStorage oGlobalStore,
                                          String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey, EndCondition.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        EndCondition oCondition = new EndCondition();

        oCondition.mVictory = oObjectStore.getBoolean( M_VICTORY );
        oCondition.mType =
                EndConditionType.values()[ oObjectStore.getInt( M_TYPE ) ];
        oCondition.mFaction =
                Faction.values()[ oObjectStore.getInt( M_FACTION ) ];

        String strKey = oObjectStore.getString( M_UNIT );
        oCondition.mUnit = BattleUnit.loadState( oObjectStore, strKey );

        return oCondition;
    }

    public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
    {
        return loadState( oGlobalStore, strObjKey );
    }
}
