package stargame.android.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import stargame.android.R;
import stargame.android.util.Logger;
import stargame.android.view.StarView;

public class StarGame extends Activity
{
    StarView mStarView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        Log.i( this.getClass().getName(), "onCreate() called!" );
        if ( null != savedInstanceState )
        {
            Logger.w( "With saved Bundle from previous Activity" );
        }

        super.onCreate( savedInstanceState );

        // turn off the window's title bar
        requestWindowFeature( Window.FEATURE_NO_TITLE );

        // tell system to use the layout defined in our XML file
        setContentView( R.layout.main );

        // get handles to the StarView from XML
        mStarView = ( StarView ) findViewById( R.id.stargame );
    }

    /**
     * Notification that something is about to happen, to give the Activity a
     * chance to save state.
     *
     * @param outState a Bundle into which this Activity should save its state
     */
    @Override
    protected void onSaveInstanceState( Bundle outState )
    {
        super.onSaveInstanceState( outState );

        Log.w( this.getClass().getName(), "SIS called" );
    }

    protected void onDestroy()
    {
        Log.i( this.getClass().getName(), "StarGame.onDestroy() called!" );
        mStarView = null;
        super.onDestroy();
    }

    public void onStartClicked( View view )
    {
        try
        {
            Intent oIntent = new Intent( StarGame.this, BattleActivity.class );

            //Next create the bundle and initialize it
            Bundle oBundle = new Bundle();

            //Add the parameters to bundle as
            oBundle.putString( "BattleName", "stargame.android:xml/battletest" );

            //Add this bundle to the intent
            oIntent.putExtras( oBundle );

            // Start activity
            this.startActivityIfNeeded( oIntent, 0 );
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( ex );
        }
    }

    public void onExitClicked( View view )
    {
        try
        {
            this.finish();
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( ex );
        }
    }
}