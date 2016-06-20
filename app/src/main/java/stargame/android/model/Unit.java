package stargame.android.model;

import android.content.res.Resources;
import android.os.Bundle;

import stargame.android.model.jobs.JobFactory;
import stargame.android.model.jobs.JobType;
import stargame.android.storage.ISavable;
import stargame.android.storage.SavableHelper;
import stargame.android.util.FieldType;

enum Faction
{
	TYPE_TECHNIK,
	TYPE_MAGIK
}

public class Unit implements ISavable
{
	/** Constants */
	final static protected int FRONT_ATTACK_CHANCE = 55;
	final static protected int SIDE_ATTACK_CHANCE = 70;
	final static protected int BACK_ATTACK_CHANCE = 85;
	final static protected int CRITICAL_PHYS_CHANCE = 5;

	/** Level of the unit */
	protected int mLevel;

	private static final String M_LEVEL = "Level";

	/** XP of the unit */
	protected int mExperience;

	private static final String M_EXPERIENCE = "Experience";

	/** The unit base attributes */
	protected Attributes mAttributes;

	private static final String M_ATTRIBUTES = "Attributes";

	/** The sum of base, job and equipment attributes */
	protected Attributes mResultingAttributes;

	private static final String M_RESULTING_ATTRIBUTES = "ResultingAttributes";

	/** Array containing mvt penalty for each type of field */
	protected int mArrayFieldPenalty[];

	private static final String M_ARRAY_PENALTIES = "Penalties";

	/** Contains all the equipment pieces */
	protected UnitEquipment mEquipment;

	private static final String M_EQUIPMENT = "Equipment";

	/** Job the unit has */
	protected UnitJob mJob;

	private static final String M_JOB = "Job";

	/** Faction of the unit */
	protected Faction mFaction;

	private static final String M_FACTION = "Faction";

	private Unit() {}

	public Unit( JobType eType, Resources oResources )
	{
		mFaction = null;
		mLevel = 1;
		mExperience = 0;
		
		mAttributes = new Attributes( 30, 30, 3, 2, 1, 30, 30, 30, 30, 30, 5, 5, 10, 1, FRONT_ATTACK_CHANCE, BACK_ATTACK_CHANCE );
		mResultingAttributes = new Attributes();
		mEquipment = new UnitEquipment();
		mJob = null;
		mArrayFieldPenalty = new int[ FieldType.values().length ];

		for ( int i = 0; i < FieldType.values().length; ++i )
		{
			switch( FieldType.values()[ i ] )
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

	void SetJob( UnitJob oJob )
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

	public void ComputeResultingAttributes()
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

	/** Return movement penalty for the given type of field */
	public int GetFieldPenalty( FieldType field )
	{
		return mArrayFieldPenalty[ field.ordinal() ];
	}

	public void SetFaction( Faction eFaction )
	{
		mFaction = eFaction;
	}

	public Faction GetFaction()
	{
		return mFaction;
	}

	public void saveState( Bundle oObjectMap, Bundle oGlobalMap )
	{
		oObjectMap.putInt( M_LEVEL, mLevel );
		oObjectMap.putInt( M_EXPERIENCE, mExperience );
		oObjectMap.putInt( M_FACTION, mFaction.ordinal() );

		String strObjKey = SavableHelper.saveInMap( mAttributes, oGlobalMap );
		oObjectMap.putString( M_ATTRIBUTES, strObjKey );

		strObjKey = SavableHelper.saveInMap( mResultingAttributes, oGlobalMap );
		oObjectMap.putString( M_RESULTING_ATTRIBUTES, strObjKey );

		oObjectMap.putIntArray( M_ARRAY_PENALTIES, mArrayFieldPenalty );

		strObjKey = SavableHelper.saveInMap( mEquipment, oGlobalMap );
		oObjectMap.putString( M_EQUIPMENT, strObjKey );

		strObjKey = SavableHelper.saveInMap( mJob, oGlobalMap );
		oObjectMap.putString( M_JOB, strObjKey );
	}

	public ISavable createInstance( Bundle oGlobalMap, String strObjKey )
	{
		return loadState( oGlobalMap, strObjKey );
	}

	public static Unit loadState( Bundle oGlobalMap, String strObjKey )
	{
		Bundle oObjectBundle = SavableHelper.retrieveBundle( oGlobalMap, strObjKey, Unit.class.getName() );

		if ( oObjectBundle == null )
		{
			return null;
		}

		Unit oUnit = new Unit();

		oUnit.mLevel = oObjectBundle.getInt( M_LEVEL );
		oUnit.mExperience = oObjectBundle.getInt( M_EXPERIENCE );
		oUnit.mFaction = Faction.values()[ oObjectBundle.getInt( M_FACTION ) ];

		String strKey = oObjectBundle.getString( M_ATTRIBUTES );
		oUnit.mAttributes = Attributes.loadState( oGlobalMap, strKey );

		strKey = oObjectBundle.getString( M_RESULTING_ATTRIBUTES );
		oUnit.mResultingAttributes = Attributes.loadState( oGlobalMap, strKey );

		oUnit.mArrayFieldPenalty = oObjectBundle.getIntArray( M_ARRAY_PENALTIES );

		strKey = oObjectBundle.getString( M_EQUIPMENT );
		oUnit.mEquipment = UnitEquipment.loadState( oGlobalMap, strKey );

		strKey = oObjectBundle.getString( M_JOB );
		UnitJobFactory oFactory = new UnitJobFactory();
		oUnit.mJob = oFactory.loadState( oGlobalMap, strKey );

		return oUnit;
	}
}
