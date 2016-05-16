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
import com.drexelsp.blunote.events.PlaylistUpdateEvent;

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

    /**
     * An EventBus event that notifies the user a song has been added to the queue/playlist.
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onPlaylistUpdate(PlaylistUpdateEvent event) {
        if (event != null) {
            Log.v(TAG, "Received PlaylistUpdateEvent. Copying playlist");
            this.playlist.clear();
            for (String s : event.playlist) {
                this.playlist.add(s);
            }
            this.playlistAdapter.notifyDataSetChanged();
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
