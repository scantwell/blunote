package com.drexelsp.blunote.blunote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by U6020377 on 1/25/2016.
 */
public class MediaListActivity extends AppCompatActivity {
    ListView mediaList;
    ToggleButton songsToggle;
    ToggleButton albumsToggle;
    ToggleButton artistsToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewFlipper vf = ((ViewFlipper) findViewById(R.id.view_flipper));
        vf.setDisplayedChild(Constants.ACTIVITY_MEDIA_LIST);

        mediaList = (ListView) findViewById(R.id.media_list);
        setSongList();

        songsToggle = (ToggleButton) findViewById(R.id.songs_toggle);
        albumsToggle = (ToggleButton) findViewById(R.id.albums_toggle);
        artistsToggle = (ToggleButton) findViewById(R.id.artists_toggle);

        songsToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    albumsToggle.setChecked(false);
                    artistsToggle.setChecked(false);
                    songsToggle.setChecked(true);
                    setSongList();
                }
            }
        });

        albumsToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    albumsToggle.setChecked(true);
                    artistsToggle.setChecked(false);
                    songsToggle.setChecked(false);
                    setAlbumList();
                }
            }
        });

        artistsToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    albumsToggle.setChecked(false);
                    artistsToggle.setChecked(true);
                    songsToggle.setChecked(false);
                    setArtistList();
                }
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
            intent = new Intent(MediaListActivity.this, NetworkSettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_preferences) {
            intent = new Intent(MediaListActivity.this, PreferencesActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_songList) {
            intent = new Intent(MediaListActivity.this, MediaListActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_mediaControl) {
            intent = new Intent(MediaListActivity.this, MediaPlayerActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setSongList() {
        List<String> list = new ArrayList<>();
        for (int i = 1; i < 20; ++i)
            list.add("Song " + i);

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        mediaList.setAdapter(adapter);
    }

    private void setAlbumList() {
        List<String> list = new ArrayList<>();
        for (int i = 1; i < 20; ++i)
            list.add("Album " + i);

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        mediaList.setAdapter(adapter);
    }

    private void setArtistList() {
        List<String> list = new ArrayList<>();
        for (int i = 1; i < 20; ++i)
            list.add("Artist " + i);

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        mediaList.setAdapter(adapter);
    }
}
