package com.drexelsp.blunote.blunote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by U6020377 on 1/25/2016.
 */
public class ArtistViewActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewFlipper vf = ((ViewFlipper) findViewById(R.id.view_flipper));
        vf.setDisplayedChild(Constants.ACTIVITY_ARTIST_VIEW);

        List<String> list = new ArrayList<>();
        for(int i = 1; i < 20; ++i)
            list.add("Track " + i);

        ListView view = (ListView) findViewById(R.id.album_track_list);
        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        view.setAdapter(adapter);
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
            intent = new Intent(ArtistViewActivity.this, NetworkSettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_preferences) {
            intent = new Intent(ArtistViewActivity.this, PreferencesActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_songList){
            intent = new Intent(ArtistViewActivity.this, MediaListActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_mediaControl){
            intent = new Intent(ArtistViewActivity.this, MediaPlayerActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
