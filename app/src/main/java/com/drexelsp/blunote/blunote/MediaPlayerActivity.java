package com.drexelsp.blunote.blunote;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ViewFlipper;

/**
 * Created by Brisbin on 1/29/2016.
 */
public class MediaPlayerActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewFlipper vf = ((ViewFlipper) findViewById(R.id.view_flipper));
        vf.setDisplayedChild(Constants.ACTIVITY_MEDIA_PLAYER);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.playlist_FAB);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MediaPlayerActivity.this, PlaylistActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_network) {
            intent = new Intent(MediaPlayerActivity.this, NetworkSettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_preferences) {
            intent = new Intent(MediaPlayerActivity.this, PreferencesActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_songList){
            intent = new Intent(MediaPlayerActivity.this, MediaListActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
