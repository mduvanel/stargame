package stargame.android.model;

import java.util.HashSet;
import java.util.Set;

import stargame.android.model.jobs.JobType;
import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.storage.SavableHelper;


enum EquipmentType
{
    TYPE_ARMOR_HEAD,
    TYPE_ARMOR_NECK,
    TYPE_ARMOR_BODY,
    TYPE_ARMOR_LEGS,
    TYPE_ARMOR_ARM,
    TYPE_WEAPON_1HAND,
    TYPE_WEAPON_2HAND,
    TYPE_WEAPON_SHIELD
}

/**
 * Class for equipment
 */
class Equipment implements ISavable
{
    /**
     * List of Jobs that can wear the equipment piece
     */
    private Set< JobType > mSetCompatibleJobs;

    private static final String M_JOBS = "Jobs";

    /**
     * Name of the equipment piece
     */
    private String mEquipmentName;

    private static final String M_NAME = "Name";

    /**
     * Type for this equipment
     */
    EquipmentType mType;

    private static final String M_TYPE = "Type";

    /**
     * The passive increase in stats (just for wearing the armor)
     */
    private Attributes mAttributes;

    private static final String M_ATTRIBS = "Attribs";

    private Equipment()
    {
        mSetCompatibleJobs = new HashSet< JobType >();
        mAttributes = new Attributes();
    }

    public void Load()
    {
        // TODO: load an armor from XML
    }

    public EquipmentType GetArmorType()
    {
        return mType;
    }

    public String GetEquipmentName()
    {
        return mEquipmentName;
    }

    Attributes GetAttributes()
    {
        return mAttributes;
    }

    public void saveState( IStorage oObjectStore, IStorage oGlobalStore )
    {
        int[] aJobs = new int[ mSetCompatibleJobs.size() ];
        int iCount = 0;
        for ( JobType eType : mSetCompatibleJobs )
        {
            aJobs[ iCount++ ] = eType.ordinal();
        }
        oObjectStore.putIntArray( M_JOBS, aJobs );
        oObjectStore.putString( M_NAME, mEquipmentName );
        oObjectStore.putInt( M_TYPE, mType.ordinal() );

        String strObjKey = SavableHelper.saveInStore( mAttributes,
                                                      oGlobalStore );
        oObjectStore.putString( M_ATTRIBS, strObjKey );
    }

    public static Equipment loadState( IStorage oGlobalStore, String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey, Equipment.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        Equipment oEquip = new Equipment();

        oEquip.mEquipmentName = oObjectStore.getString( M_NAME );
        oEquip.mType = EquipmentType.values()[ oObjectStore.getInt( M_TYPE ) ];

        String strKey = oObjectStore.getString( M_ATTRIBS );
        oEquip.mAttributes = Attributes.loadState( oGlobalStore, strKey );

        int[] aJobs = oObjectStore.getIntArray( M_JOBS );
        oEquip.mSetCompatibleJobs.clear();
        for ( int job : aJobs )
        {
            oEquip.mSetCompatibleJobs.add( JobType.values()[ job ] );
        }

        return oEquip;
    }

    public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
    {
        return loadState( oGlobalStore, strObjKey );
    }
}