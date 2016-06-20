package stargame.android.model;

import java.util.HashMap;
import java.util.Set;

import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;
import android.os.Bundle;

/**
 * Centralizes all the unit equipment
 */
public class UnitEquipment implements ISavable
{
	public static enum EquipmentSlot
	{
		HEAD,
		LEGS,
		BODY,
		NECK,
		LEFT_ARM,
		RIGHT_ARM
	}

	/** Set containing all the armor and weapons */
	protected HashMap< EquipmentSlot, Equipment > mMapEquipment;

	private static final String M_EQUIPMENTS = "Equipments";

	/** The resulting attributes of all pieces of equipment */
	protected Attributes mResultingAttributes;

	public UnitEquipment()
	{
		mMapEquipment = new HashMap< EquipmentSlot, Equipment >( EquipmentSlot.values().length );
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

	public void ComputeResultingAttributes()
	{
		mResultingAttributes.Reset();
		for ( Equipment oEquipment : mMapEquipment.values() )
		{
			mResultingAttributes.AddAttributes( oEquipment.GetAttributes() );
		}
	}

	public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
	{
		String strObjKey;
		Bundle oEquipMap = new Bundle();
		Set<EquipmentSlot> setKeys = mMapEquipment.keySet();
		for ( EquipmentSlot oSlot : setKeys )
		{
			strObjKey = SavableHelper.saveInMap( mMapEquipment.get( oSlot ), oGlobalMap );
			oEquipMap.putString( oSlot.name(), strObjKey );
		}

		oObjectMap.putBundle( M_EQUIPMENTS, oEquipMap );
	}

	public static UnitEquipment loadState( Bundle oGlobalMap, String strObjKey )
	{
		Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey, UnitEquipment.class.getName() );

		if ( oObjectBundle == null )
		{
			return null;
		}

		UnitEquipment oEquip = new UnitEquipment();

		Bundle oKeysMap = oObjectBundle.getBundle( M_EQUIPMENTS );
		for ( String strSlot : oKeysMap.keySet() )
		{
			String strKey = oObjectBundle.getString( strSlot );
			oEquip.mMapEquipment.put( 
					EquipmentSlot.valueOf( strSlot ), 
					Equipment.loadState( oGlobalMap, strKey ) );
		}

		oEquip.ComputeResultingAttributes();
		return oEquip;
	}

	public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
	{
		return loadState( oGlobalMap, strObjKey );
	}
}
