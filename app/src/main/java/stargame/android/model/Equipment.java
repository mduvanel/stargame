package stargame.android.model;

import java.util.HashSet;
import java.util.Set;

import android.os.Bundle;

import stargame.android.model.jobs.JobType;
import stargame.android.storage.ISavable;
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
public class Equipment implements ISavable
{
	/** List of Jobs that can wear the equipment piece */
	protected Set< JobType > mSetCompatibleJobs;

	private static final String M_JOBS = "Jobs";

	/** Name of the equipment piece */
	protected String mEquipmentName;

	private static final String M_NAME = "Name";

	/** Type for this equipment */
	protected EquipmentType mType;

	private static final String M_TYPE = "Type";

	/** The passive increase in stats (just for wearing the armor) */
	protected Attributes mAttributes;

	private static final String M_ATTRIBS = "Attribs";

	public Equipment()
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

	public Attributes GetAttributes()
	{
		return mAttributes;
	}

	public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
	{
		int[] aJobs = new int[ mSetCompatibleJobs.size() ];
		int iCount = 0;
		for ( JobType eType : mSetCompatibleJobs )
		{
			aJobs[ iCount++ ] = eType.ordinal();
		}
		oObjectMap.putIntArray( M_JOBS, aJobs );
		oObjectMap.putString( M_NAME, mEquipmentName );
		oObjectMap.putInt( M_TYPE, mType.ordinal() );

		String strObjKey = SavableHelper.saveInMap( mAttributes, oGlobalMap );
		oObjectMap.putString( M_ATTRIBS, strObjKey );
	}

	public static Equipment loadState( Bundle oGlobalMap, String strObjKey )
	{
		Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey, Equipment.class.getName() );

		if ( oObjectBundle == null )
		{
			return null;
		}

		Equipment oEquip = new Equipment();

		oEquip.mEquipmentName = oObjectBundle.getString( M_NAME );
		oEquip.mType = EquipmentType.values()[ oObjectBundle.getInt( M_TYPE ) ];

		String strKey = oObjectBundle.getString( M_ATTRIBS );
		oEquip.mAttributes = Attributes.loadState( oGlobalMap, strKey );

		int[] aJobs = oObjectBundle.getIntArray( M_JOBS );
		oEquip.mSetCompatibleJobs.clear();
		for ( int i = 0; i < aJobs.length; ++i )
		{
			oEquip.mSetCompatibleJobs.add( JobType.values()[ aJobs[ i ] ] );
		}

		return oEquip;
	}

	public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
	{
		return loadState( oGlobalMap, strObjKey );
	}
}