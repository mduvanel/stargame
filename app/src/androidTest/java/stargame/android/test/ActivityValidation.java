package stargame.android.test;

import stargame.android.controller.BattleActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

public class ActivityValidation extends
		ActivityInstrumentationTestCase2< BattleActivity >
{

	public ActivityValidation()
	{
		super( "stargame.android.controller.BattleActivity",
				BattleActivity.class );
	}

	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testExit()
	{
		BattleActivity activity = getActivity();
		Button exit = ( Button ) activity.findViewById( stargame.android.R.id.exit_button );
	}
}
