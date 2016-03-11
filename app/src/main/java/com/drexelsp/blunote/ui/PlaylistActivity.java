package com.drexelsp.blunote.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.drexelsp.blunote.blunote.Constants;
import com.drexelsp.blunote.blunote.R;

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

/**
 * Need to handle a long press for options menu
 */
public class PlaylistActivity extends BaseBluNoteActivity implements ListView.OnItemClickListener
{
    protected List<String> playlist;
    ArrayAdapter playlistAdapter;
    ListView playlistView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(!Intent.ACTION_SEARCH.equals(getIntent().getAction()))
        {
            getCurrentPlaylist();
            playlistView = (ListView) findViewById(R.id.playlist_list);
            setSimpleList(playlistView, playlist);
            registerForContextMenu(playlistView);
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo)
    {
        if (v.getId()==R.id.playlist_list)
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(playlist.get(info.position));

            String[] menuItems = getResources().getStringArray(R.array.playlist_context_array);
            for (int i = 0; i < menuItems.length; i++)
            {
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
    public void handleIntent(Intent intent)
    {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            playlistView = (ListView) findViewById(R.id.playlist_list);
            //use the query to search your data somehow
            List<String> connectionList = getCurrentPlaylist();
            Iterator<String> i = connectionList.iterator();
            while(i.hasNext())
            {
                String item = i.next();
                if(!item.contains(query))
                {
                    i.remove();
                }
            }

            playlistAdapter = new ArrayAdapter(this,
                    android.R.layout.simple_list_item_1, playlist);
            playlistView.setAdapter(playlistAdapter);
            SearchView.OnCloseListener closeListener = new SearchView.OnCloseListener(){
                @Override
                public boolean onClose() {
                    playlistAdapter = new ArrayAdapter(playlistAdapter.getContext(),
                            android.R.layout.simple_list_item_1, playlist);
                    playlistView.setAdapter(playlistAdapter);
                    return false;
                }
            };
            searchView.setOnCloseListener(closeListener);
        }
    }

    public List<String> getCurrentPlaylist()
    {
        playlist = new ArrayList<>();
        for(int i = 0; i < 20; ++i)
            playlist.add("Song " + (i + 1));

        return playlist;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

    }
}
