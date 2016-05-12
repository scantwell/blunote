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
        if (event != null)
        {
            EventBus.getDefault().removeStickyEvent(event);
            this.playlist.add(String.format("Song: %s Artist: %s Album: %s", event.title, event.artist, event.album));
            Log.v(TAG, String.format("Added song to playlist %s", event.title));
            printPlaylist();
            this.playlistAdapter.notifyDataSetChanged();
        }
    }

    private void printPlaylist()
    {
        Log.v(TAG, String.format("Printing Playlist"));
        int count = 0;
        for (String s : playlist)
        {
            Log.v(TAG, String.format("%d: %s", count, s));
            count++;
        }
        this.playlistAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onPlaySong(PlaySongEvent event) {
        if (event != null)
        {
            EventBus.getDefault().removeStickyEvent(event);
            Log.v(TAG, String.format("Received PlaySongEvent for song %s", event.title));
            if (removeSong(event.title)) {
                Log.v(TAG, String.format("Removed song from playlist %s", event.title));
            }
            printPlaylist();
            this.playlistAdapter.notifyDataSetChanged();
        }
    }

    private boolean removeSong(String title) {
        for (String info : this.playlist) {
            if (info.contains(title)) {
                this.playlist.remove(info);
                return true;
            }
        }
        return false;
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
