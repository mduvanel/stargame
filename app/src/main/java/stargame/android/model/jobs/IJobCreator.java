package stargame.android.model.jobs;

import android.content.res.Resources;

import stargame.android.model.Unit;
import stargame.android.model.UnitJob;

public interface IJobCreator
{
    UnitJob JobCreate( Unit oUnit, JobType eType, Resources oResources );
}
