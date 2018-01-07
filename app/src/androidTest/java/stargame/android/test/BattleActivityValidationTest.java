package stargame.android.test;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.widget.Button;

import org.junit.Rule;
import org.junit.Test;

import stargame.android.controller.BattleActivity;

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
        InstrumentationRegistry.getInstrumentation().callActivityOnRestart(activity);
    }
}
