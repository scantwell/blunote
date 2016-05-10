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

        if (!Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            getCurrentPlaylist();
            playlistView = (ListView) findViewById(R.id.playlist_list);
            setSimpleList(playlistView, playlist);
            registerForContextMenu(playlistView);
        }
        this.playlist = new ArrayList<>();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.playlist_list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(playlist.get(info.position));

            String[] menuItems = getResources().getStringArray(R.array.playlist_context_array);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
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
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            playlistView = (ListView) findViewById(R.id.playlist_list);
            //use the query to search your data somehow
            List<String> connectionList = getCurrentPlaylist();
            Iterator<String> i = connectionList.iterator();
            while (i.hasNext()) {
                String item = i.next();
                if (!item.contains(query)) {
                    i.remove();
                }
            }

            registerForContextMenu(playlistView);
            playlistAdapter = new ArrayAdapter(this,
                    android.R.layout.simple_list_item_1, playlist);
            playlistView.setAdapter(playlistAdapter);
            SearchView.OnCloseListener closeListener = new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    playlistAdapter = new ArrayAdapter(playlistAdapter.getContext(),
                            android.R.layout.simple_list_item_1, getCurrentPlaylist());
                    playlistView.setAdapter(playlistAdapter);
                    return false;
                }
            };
            searchView.setOnCloseListener(closeListener);
        }
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
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onPlaySong(PlaySongEvent event) {
        removeSong(event.title);
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
