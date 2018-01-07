package stargame.android.test;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import stargame.android.*;
import stargame.android.controller.BattleActivity;
import stargame.android.view.BattleThread2D;
import stargame.android.view.BattleView2D;

import static org.junit.Assert.assertEquals;

public class BattleActivityValidationTest
{
    @Rule
    public ActivityTestRule<BattleActivity> rule =
            new ActivityTestRule<BattleActivity>(BattleActivity.class)
    {
        @Override
        protected Intent getActivityIntent() {
            InstrumentationRegistry.getTargetContext();
            Intent intent = new Intent( Intent.ACTION_MAIN );
            intent.putExtra("BattleName", "stargame.android:xml/battletest" );
            return intent;
        }
    };

    @Test
    public void testPauseResume()
    {
        BattleActivity activity = rule.getActivity();
        BattleView2D oBattleView = ( BattleView2D ) activity.findViewById( stargame.android.R.id.battle );
        InstrumentationRegistry.getInstrumentation().callActivityOnPause( activity );
        assertEquals( oBattleView.GetBattleThread().getMode(),
                      BattleThread2D.STATE_NO_DISPLAY );
        InstrumentationRegistry.getInstrumentation().callActivityOnResume( activity );
        assertEquals( oBattleView.GetBattleThread().getMode(),
                      BattleThread2D.STATE_RUNNING );
    }

    @Test
    public void testStopRestart()
    {
        BattleActivity activity = rule.getActivity();
        BattleView2D oBattleView = ( BattleView2D ) activity.findViewById( stargame.android.R.id.battle );
        InstrumentationRegistry.getInstrumentation().callActivityOnPause( activity );
        assertEquals( oBattleView.GetBattleThread().getMode(),
                      BattleThread2D.STATE_NO_DISPLAY );
        InstrumentationRegistry.getInstrumentation().callActivityOnStop( activity );
        InstrumentationRegistry.getInstrumentation().callActivityOnRestart( activity );
        InstrumentationRegistry.getInstrumentation().callActivityOnStart( activity );
        InstrumentationRegistry.getInstrumentation().callActivityOnResume( activity );
        assertEquals( oBattleView.GetBattleThread().getMode(),
                      BattleThread2D.STATE_RUNNING );
    }
}
