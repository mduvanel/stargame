package stargame.android.model;

import java.util.HashMap;
import java.util.Set;

import stargame.android.storage.ISavable;
import stargame.android.storage.IStorage;
import stargame.android.storage.SavableHelper;

/**
 * Centralizes all the unit equipment
 */
class UnitEquipment implements ISavable
{
    private enum EquipmentSlot
    {
        HEAD,
        LEGS,
        BODY,
        NECK,
        LEFT_ARM,
        RIGHT_ARM
    }

    /**
     * Set containing all the armor and weapons
     */
    private HashMap< EquipmentSlot, Equipment > mMapEquipment;

    private static final String M_EQUIPMENTS = "Equipments";

    /**
     * The resulting attributes of all pieces of equipment
     */
    private Attributes mResultingAttributes;

    UnitEquipment()
    {
        mMapEquipment = new HashMap< EquipmentSlot, Equipment >(
                EquipmentSlot.values().length );
        mResultingAttributes = new Attributes();
        ComputeResultingAttributes();
    }

    public boolean EquipArmor( Equipment oEquip, EquipmentSlot oSlot )
    {
        // Verify the Equipment type fits the slot type
        switch ( oSlot )
        {
            case HEAD:
                if ( oEquip.mType != EquipmentType.TYPE_ARMOR_HEAD )
                {
                    return false;
                }
                break;
            case LEGS:
                if ( oEquip.mType != EquipmentType.TYPE_ARMOR_LEGS )
                {
                    return false;
                }
                break;
            case BODY:
                if ( oEquip.mType != EquipmentType.TYPE_ARMOR_BODY )
                {
                    return false;
                }
                break;
            case NECK:
                if ( oEquip.mType != EquipmentType.TYPE_ARMOR_NECK )
                {
                    return false;
                }
                break;
            case LEFT_ARM:
            case RIGHT_ARM:
                if ( ( oEquip.mType != EquipmentType.TYPE_WEAPON_1HAND ) &&
                        ( oEquip.mType != EquipmentType.TYPE_WEAPON_2HAND ) &&
                        ( oEquip.mType != EquipmentType.TYPE_WEAPON_SHIELD ) )
                {
                    return false;
                }
                break;
            default:
                return false;
        }

        // Set the armor in the given slot
        mMapEquipment.put( oSlot, oEquip );

        // Recompute resulting attributes
        ComputeResultingAttributes();

        return true;
    }

    public Attributes GetResultingAttributes()
    {
        return mResultingAttributes;
    }

    private void ComputeResultingAttributes()
    {
        mResultingAttributes.Reset();
        for ( Equipment oEquipment : mMapEquipment.values() )
        {
            mResultingAttributes.AddAttributes( oEquipment.GetAttributes() );
        }
    }

    public void saveState( IStorage oObjectStore,
                           IStorage oGlobalStore )
    {
        String strObjKey;
        IStorage oEquipStore = SavableHelper.buildStore();
        Set< EquipmentSlot > setKeys = mMapEquipment.keySet();
        for ( EquipmentSlot oSlot : setKeys )
        {
            strObjKey = SavableHelper.saveInStore(
                    mMapEquipment.get( oSlot ), oGlobalStore );
            oEquipStore.putString( oSlot.name(), strObjKey );
        }

        oObjectStore.putStore( M_EQUIPMENTS, oEquipStore );
    }

    public static UnitEquipment loadState( IStorage oGlobalStore, String strObjKey )
    {
        IStorage oObjectStore = SavableHelper.retrieveStore(
                oGlobalStore, strObjKey, UnitEquipment.class.getName() );

        if ( oObjectStore == null )
        {
            return null;
        }

        UnitEquipment oEquip = new UnitEquipment();

        IStorage oKeysStore = oObjectStore.getStore( M_EQUIPMENTS );
        for ( String strSlot : oKeysStore.keySet() )
        {
            String strKey = oObjectStore.getString( strSlot );
            oEquip.mMapEquipment.put(
                    EquipmentSlot.valueOf( strSlot ),
                    Equipment.loadState( oGlobalStore, strKey ) );
        }

        oEquip.ComputeResultingAttributes();
        return oEquip;
    }

    public ISavable createInstance( IStorage oGlobalStore, String strObjKey )
    {
        return loadState( oGlobalStore, strObjKey );
    }
}
