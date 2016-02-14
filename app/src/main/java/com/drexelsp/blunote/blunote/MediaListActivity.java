package com.drexelsp.blunote.blunote;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by U6020377 on 1/25/2016.
 */
public class MediaListActivity extends BaseBluNoteActivity {

    ListView mediaList;
    ToggleButton songsToggle;
    ToggleButton albumsToggle;
    ToggleButton artistsToggle;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewFlipper vf = ((ViewFlipper) findViewById(R.id.view_flipper));
        vf.setDisplayedChild(Constants.ACTIVITY_MEDIA_LIST);

        title = (TextView) findViewById(R.id.media_list_title);
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
    public Context getCurrentContext() {
        return MediaListActivity.this;
    }

    @Override
    public int getViewConstant() {
        return Constants.ACTIVITY_MEDIA_LIST;
    }

    @Override
    public boolean showMusicMenuItems() {
        return true;
    }

    @Override
    public boolean showSearchMenuItem() {
        return true;
    }

    /**
     * Replace with code to generate song list
     */
    private void setSongList() {
        title.setText(getResources().getString(R.string.all_songs));
        List<String> list = new ArrayList<>();
        for (int i = 1; i < 20; ++i)
            list.add("Song " + i);
        setSimpleList(mediaList, list);
        mediaList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This is where the drilling down occurs, for now it simply shows a default song view
                startActivity(SongViewActivity.class);
            }
        });
    }

    /**
     * Replace with code to generate album list
     */
    private void setAlbumList() {
        title.setText(getResources().getString(R.string.all_albums));
        List<String> list = new ArrayList<>();
        for (int i = 1; i < 20; ++i)
            list.add("Album " + i);
        setSimpleList(mediaList, list);
        mediaList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This is where the drilling down occurs, for now it simply shows a default album view
                startActivity(AlbumViewActivity.class);
            }
        });
    }

    /**
     * Replace with code to generate artist list
     */
    private void setArtistList() {
        title.setText(getResources().getString(R.string.all_artists));
        List<String> list = new ArrayList<>();
        for (int i = 1; i < 20; ++i)
            list.add("Artist " + i);
        setSimpleList(mediaList, list);
        mediaList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This is where the drilling down occurs, for now it simply shows a default artist view
                startActivity(ArtistViewActivity.class);
            }
        });
    }

    private void startActivity(Class activityClass)
    {
        Intent intent = new Intent(MediaListActivity.this, activityClass);
        startActivity(intent);
    }
}
