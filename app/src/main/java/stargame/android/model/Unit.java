package stargame.android.model;

import android.content.res.Resources;

import stargame.android.model.jobs.JobFactory;
import stargame.android.model.jobs.JobType;
import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.storage.SavableHelper;
import stargame.android.util.FieldType;

enum Faction
{
    TYPE_TECHNIK,
    TYPE_MAGIK
}

public class Unit implements ISavable
{
    /**
     * Constants
     */
    final static int FRONT_ATTACK_CHANCE = 55;
    final static int SIDE_ATTACK_CHANCE = 70;
    final static int BACK_ATTACK_CHANCE = 85;
    final static int CRITICAL_PHYS_CHANCE = 5;

    /**
     * Level of the unit
     */
    int mLevel;

    private static final String M_LEVEL = "Level";

    /**
     * XP of the unit
     */
    int mExperience;

    private static final String M_EXPERIENCE = "Experience";

    /**
     * The unit base attributes
     */
    Attributes mAttributes;

    private static final String M_ATTRIBUTES = "Attributes";

    /**
     * The sum of base, job and equipment attributes
     */
    private Attributes mResultingAttributes;

    private static final String M_RESULTING_ATTRIBUTES = "ResultingAttributes";

    /**
     * Array containing mvt penalty for each type of field
     */
    private int mArrayFieldPenalty[];

    private static final String M_ARRAY_PENALTIES = "Penalties";

    /**
     * Contains all the equipment pieces
     */
    private UnitEquipment mEquipment;

    private static final String M_EQUIPMENT = "Equipment";

    /**
     * Job the unit has
     */
    private UnitJob mJob;

    private static final String M_JOB = "Job";

    /**
     * Faction of the unit
     */
    private Faction mFaction;

    private static final String M_FACTION = "Faction";

    private Unit()
    {
    }

    public Unit( JobType eType, Resources oResources )
    {
        mFaction = null;
        mLevel = 1;
        mExperience = 0;

        mAttributes = new Attributes( 30, 30, 3, 2, 1, 30, 30, 30, 30, 30, 5, 5, 10, 1,
                                      FRONT_ATTACK_CHANCE, BACK_ATTACK_CHANCE );
        mResultingAttributes = new Attributes();
        mEquipment = new UnitEquipment();
        mJob = null;
        mArrayFieldPenalty = new int[ FieldType.values().length ];

        for ( int i = 0; i < FieldType.values().length; ++i )
        {
            switch ( FieldType.values()[ i ] )
            {
                case TYPE_GRASS:
                    mArrayFieldPenalty[ i ] = 110;
                    break;
                case TYPE_WATER:
                    mArrayFieldPenalty[ i ] = 200;
                    break;
                case TYPE_LAVA:
                    mArrayFieldPenalty[ i ] = 200;
                    break;
                case TYPE_ROCK:
                    mArrayFieldPenalty[ i ] = 130;
                    break;
                case TYPE_PLAIN:
                    mArrayFieldPenalty[ i ] = 100;
                    break;
                case TYPE_FOREST:
                    mArrayFieldPenalty[ i ] = 150;
                    break;
                case TYPE_NONE:
                    mArrayFieldPenalty[ i ] = 0;
            }
        }

        SetJob( JobFactory.GetInstance().JobCreate( this, eType, oResources ) );
    }

    private void SetJob( UnitJob oJob )
    {
        mJob = oJob;

        // Update resulting attributes
        ComputeResultingAttributes();
    }

    UnitJob GetJob()
    {
        return mJob;
    }

    public JobType GetJobType()
    {
        return mJob.GetJobType();
    }

    private void ComputeResultingAttributes()
    {
        mResultingAttributes.Reset();
        mResultingAttributes.AddAttributes( mAttributes );
        mResultingAttributes.AddAttributes( mJob.GetAttributes() );
        mResultingAttributes.AddAttributes( mEquipment.GetResultingAttributes() );
    }

    public Attributes GetResultingAttributes()
    {
        return mResultingAttributes;
    }

    /**
     * Return movement penalty for the given type of field
     */
    int GetFieldPenalty( FieldType field )
    {
        return mArrayFieldPenalty[ field.ordinal() ];
    }

    void SetFaction( Faction eFaction )
    {
        mFaction = eFaction;
    }

    Faction GetFaction()
    {
        return mFaction;
    }

    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        oObjectStore.putInt( M_LEVEL, mLevel );
        oObjectStore.putInt( M_EXPERIENCE, mExperience );
        oObjectStore.putInt( M_FACTION, mFaction.ordinal() );

        String strObjKey = SavableHelper.saveInStore( mAttributes,
                                                      oGlobalStore );
        oObjectStore.putString( M_ATTRIBUTES, strObjKey );

        strObjKey = SavableHelper.saveInStore( mResultingAttributes,
                                               oGlobalStore );
        oObjectStore.putString( M_RESULTING_ATTRIBUTES, strObjKey );

        oObjectStore.putIntArray( M_ARRAY_PENALTIES, mArrayFieldPenalty );

        strObjKey = SavableHelper.saveInStore( mEquipment, oGlobalStore );
        oObjectStore.putString( M_EQUIPMENT, strObjKey );

        strObjKey = SavableHelper.saveInStore( mJob, oGlobalStore );
        oObjectStore.putString( M_JOB, strObjKey );
    }

    public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
    {
        return loadState( oGlobalStore, strObjKey );
    }

    public static Unit loadState( IStorage oGlobalStore, String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey,
                Unit.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        Unit oUnit = new Unit();

        oUnit.mLevel = oObjectStore.getInt( M_LEVEL );
        oUnit.mExperience = oObjectStore.getInt( M_EXPERIENCE );
        oUnit.mFaction = Faction.values()[ oObjectStore.getInt( M_FACTION ) ];

        String strKey = oObjectStore.getString( M_ATTRIBUTES );
        oUnit.mAttributes = Attributes.loadState( oGlobalStore, strKey );

        strKey = oObjectStore.getString( M_RESULTING_ATTRIBUTES );
        oUnit.mResultingAttributes = Attributes.loadState( oGlobalStore, strKey );

        oUnit.mArrayFieldPenalty = oObjectStore.getIntArray( M_ARRAY_PENALTIES );

        strKey = oObjectStore.getString( M_EQUIPMENT );
        oUnit.mEquipment = UnitEquipment.loadState( oGlobalStore, strKey );

        strKey = oObjectStore.getString( M_JOB );
        UnitJobFactory oFactory = new UnitJobFactory();
        oUnit.mJob = oFactory.loadState( oGlobalStore, strKey );

        return oUnit;
    }
}
