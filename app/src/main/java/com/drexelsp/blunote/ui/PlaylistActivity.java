package com.drexelsp.blunote.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.Menu;
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
import java.util.Iterator;
import java.util.List;

/**
 * Need to handle a long press for options menu
 */
public class PlaylistActivity extends BaseBluNoteActivity implements ListView.OnItemClickListener {
    protected List<String> playlist;
    ArrayAdapter playlistAdapter;
    ListView playlistView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playlistView = (ListView) findViewById(R.id.playlist_list);
        this.playlist = new ArrayList<>();
        setSimpleList(playlistView, playlist);
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

    public List<String> getCurrentPlaylist() {
        return this.playlist;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onAddSongEvent(AddSongEvent event) {
        this.playlist.add(String.format("Song: %s Artist: %s Album:%", event.title, event.artist, event.album));
        this.playlistAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onPlaySong(PlaySongEvent event) {
        removeSong(event.title);
        this.playlistAdapter.notifyDataSetChanged();
    }

    private void removeSong(String title) {
        for (String info: this.playlist) {
            if (info.contains(title))
            {
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
