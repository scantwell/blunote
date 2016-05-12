package com.drexelsp.blunote.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.drexelsp.blunote.blunote.Constants;
import com.drexelsp.blunote.blunote.R;
import com.drexelsp.blunote.events.AddSongEvent;
import com.drexelsp.blunote.events.PlaySongEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Need to handle a long press for options menu
 */
public class PlaylistActivity extends BaseBluNoteActivity implements ListView.OnItemClickListener {
    private static final String TAG = "PlaylistActivity";
    protected List<String> playlist;
    ArrayAdapter playlistAdapter;
    ListView playlistView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playlistView = (ListView) findViewById(R.id.playlist_list);
        playlist = new ArrayList<>();
        playlistAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, playlist);
        playlistView.setAdapter(playlistAdapter);
        registerForContextMenu(playlistView);
    }

    @Override
    public Context getCurrentContext() {
        return PlaylistActivity.this;
    }

    @Override
    public int getViewConstant() {
        return Constants.ACTIVITY_PLAYLIST_VIEW;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onAddSongEvent(AddSongEvent event) {
        this.playlist.add(String.format("Song: %s Artist: %s Album:%s", event.title, event.artist, event.album));
        Log.v(TAG, String.format("Added song to playlist {%s}", event.title));
        this.playlistAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onPlaySong(PlaySongEvent event) {
        removeSong(event.title);
        Log.v(TAG, String.format("Removed song to playlist {%s}", event.title));
        this.playlistAdapter.notifyDataSetChanged();
    }

    private void removeSong(String title) {
        for (String info : this.playlist) {
            if (info.contains(title)) {
                this.playlist.remove(info);
            }
        }
    }

    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
