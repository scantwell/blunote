package com.drexelsp.blunote.blunote;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Need to handle a long press for options menu
 */
public class PlaylistActivity extends AppCompatActivity
{
    protected String[] list;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.playlist_view_toolbar);
        setSupportActionBar(toolbar);

        list = new String[20];
        for(int i = 0; i < 20; ++i)
            list[i] = ("Song " + (i + 1));

        ListView view = (ListView) findViewById(R.id.playlist_list);
        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        view.setAdapter(adapter);
        registerForContextMenu(view);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
