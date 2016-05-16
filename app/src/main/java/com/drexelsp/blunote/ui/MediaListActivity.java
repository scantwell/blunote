package com.drexelsp.blunote.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import com.drexelsp.blunote.blunote.Constants;
import com.drexelsp.blunote.blunote.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by U6020377 on 1/25/2016.
 */
public class MediaListActivity extends BaseBluNoteActivity implements CompoundButton.OnCheckedChangeListener {

    ListView mediaListView;
    List<String> mediaList;
    List<String> idList;
    ToggleButton songsToggle;
    ToggleButton albumsToggle;
    ToggleButton artistsToggle;
    TextView title;
    ArrayAdapter mediaListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewFlipper vf = ((ViewFlipper) findViewById(R.id.view_flipper));
        vf.setDisplayedChild(Constants.ACTIVITY_MEDIA_LIST);

        title = (TextView) findViewById(R.id.media_list_title);
        mediaListView = (ListView) findViewById(R.id.media_list);

        if (!Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            setSongList();
        }

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

    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY).toLowerCase();
            mediaListView = (ListView) findViewById(R.id.media_list);
            //use the query to search your data somehow

            setMediaList();

            Iterator<String> i = mediaList.iterator();
            while (i.hasNext()) {
                String item = i.next().toLowerCase();
                if (!item.contains(query)) {
                    i.remove();
                }
            }

            mediaListAdapter = new ArrayAdapter(this,
                    android.R.layout.simple_list_item_1, mediaList);
            mediaListView.setAdapter(mediaListAdapter);
            SearchView.OnCloseListener closeListener = new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    setMediaList();
                    mediaListAdapter = new ArrayAdapter(mediaListAdapter.getContext(),
                            android.R.layout.simple_list_item_1, mediaList);
                    mediaListView.setAdapter(mediaListAdapter);
                    return false;
                }
            };
            searchView.setOnCloseListener(closeListener);
        }
    }

    @Override
    public void handleOnMetaStoreChange() {
        String method;
        if (albumsToggle.isChecked()) {
            method = "album";
        } else if (artistsToggle.isChecked()) {
            method = "artist";
        } else {
            method = "song";
        }

        UpdateMediaListTask task = new UpdateMediaListTask();
        task.execute(method);
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

    private void setMediaList() {
        if (albumsToggle.isChecked()) {
            setAlbumList();
        } else if (artistsToggle.isChecked()) {
            setArtistList();
        } else {
            setSongList();
        }
    }

    /**
     * Replace with code to generate song list
     */
    private void setSongList() {
        title.setText(getResources().getString(R.string.all_songs));
        setMediaLists(getSongList());
        mediaListAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, mediaList);
        mediaListView.setAdapter(mediaListAdapter);
        mediaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(SongViewActivity.class, position);
            }
        });
    }

    /**
     * Replace with code to generate album list
     */
    private void setAlbumList() {
        title.setText(getResources().getString(R.string.all_albums));
        setMediaLists(getAlbumList());
        mediaListAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, mediaList);
        mediaListView.setAdapter(mediaListAdapter);
        mediaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(AlbumViewActivity.class, position);
            }
        });
    }

    /**
     * Replace with code to generate artist list
     */
    private void setArtistList() {
        title.setText(getResources().getString(R.string.all_artists));
        setMediaLists(getArtistList());
        mediaListAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, mediaList);
        mediaListView.setAdapter(mediaListAdapter);
        mediaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(ArtistViewActivity.class, position);
            }
        });
    }

    private void startActivity(Class activityClass, int position) {
        Intent intent = new Intent(MediaListActivity.this, activityClass);
        intent.putExtra("_id", idList.get(position));
        startActivity(intent);
    }

    private void setMediaLists(Map<String, String> mediaMap) {
        mediaList = new ArrayList<>();
        idList = new ArrayList<>();

        for (Map.Entry<String, String> entry : mediaMap.entrySet()) {
            mediaList.add(entry.getKey());
            idList.add(entry.getValue());
        }
    }

    private class UpdateMediaListTask extends AsyncTask<String, Void, Map<String, String>> {
        @Override
        protected Map<String, String> doInBackground(String... param) {
            Map<String, String> newMediaMap;

            if (param.equals("album")) {
                newMediaMap = getAlbumList();
            } else if (param.equals("artist")) {
                newMediaMap = getArtistList();
            } else {
                newMediaMap = getSongList();
            }

            return newMediaMap;
        }

        @Override
        protected void onPostExecute(Map<String, String> map) {
            setMediaLists(map);
            mediaListAdapter.notifyDataSetChanged();
        }
    }
}
