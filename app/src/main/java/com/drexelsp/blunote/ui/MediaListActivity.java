package com.drexelsp.blunote.ui;

import java.util.ArrayList;
import java.util.List;

import com.drexelsp.blunote.blunote.Constants;
import com.drexelsp.blunote.blunote.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

/**
 * Created by U6020377 on 1/25/2016.
 */
public class MediaListActivity extends BaseBluNoteActivity implements CompoundButton.OnCheckedChangeListener {

    ListView mediaList;
    ToggleButton songsToggle;
    ToggleButton albumsToggle;
    ToggleButton artistsToggle;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewFlipper vf = ((ViewFlipper) findViewById(R.id.view_flipper));
        vf.setDisplayedChild(Constants.ACTIVITY_MEDIA_LIST);

        title = (TextView) findViewById(R.id.media_list_title);
        mediaList = (ListView) findViewById(R.id.media_list);
        setSongList();

        songsToggle = (ToggleButton) findViewById(R.id.songs_toggle);
        albumsToggle = (ToggleButton) findViewById(R.id.albums_toggle);
        artistsToggle = (ToggleButton) findViewById(R.id.artists_toggle);

        songsToggle.setOnCheckedChangeListener(this);
        albumsToggle.setOnCheckedChangeListener(this);
        artistsToggle.setOnCheckedChangeListener(this);
    }

    @Override
    public Context getCurrentContext() {
        return MediaListActivity.this;
    }

    @Override
    public int getViewConstant() { return Constants.ACTIVITY_MEDIA_LIST; }

    @Override
    public boolean showMusicMenuItems() {
        return true;
    }

    @Override
    public boolean showSearchMenuItem() {
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == songsToggle && isChecked) {
            albumsToggle.setChecked(false);
            artistsToggle.setChecked(false);
            songsToggle.setChecked(true);
            setSongList();

        } else if (buttonView == albumsToggle && isChecked) {
            albumsToggle.setChecked(true);
            artistsToggle.setChecked(false);
            songsToggle.setChecked(false);
            setAlbumList();

        } else if (buttonView == artistsToggle && isChecked) {
            albumsToggle.setChecked(false);
            artistsToggle.setChecked(true);
            songsToggle.setChecked(false);
            setArtistList();
        }
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
        mediaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
        mediaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
        mediaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(ArtistViewActivity.class);
            }
        });
    }

    private void startActivity(Class activityClass) {
        Intent intent = new Intent(MediaListActivity.this, activityClass);
        startActivity(intent);
    }
}
