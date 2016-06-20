package stargame.android.model.jobs;

import android.content.res.Resources;
import stargame.android.model.Unit;
import stargame.android.model.UnitJob;

public interface IJobCreator
{
	public UnitJob JobCreate( Unit oUnit, JobType eType, Resources oResources );
}
