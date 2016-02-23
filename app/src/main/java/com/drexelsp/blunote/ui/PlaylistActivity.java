package com.drexelsp.blunote.ui;

import com.drexelsp.blunote.blunote.Constants;
import com.drexelsp.blunote.blunote.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Need to handle a long press for options menu
 */
public class PlaylistActivity extends BaseBluNoteActivity
{
    protected String[] list;
    ListView playlistList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        list = new String[20];
        for(int i = 0; i < 20; ++i)
            list[i] = ("Song " + (i + 1));

        playlistList = (ListView) findViewById(R.id.playlist_list);
        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        playlistList.setAdapter(adapter);
        registerForContextMenu(playlistList);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo)
    {
        if (v.getId()==R.id.playlist_list)
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(list[info.position]);
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
}
