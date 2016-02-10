package com.drexelsp.blunote.blunote;

import android.content.Intent;
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
import android.widget.ViewFlipper;

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
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewFlipper vf = ((ViewFlipper) findViewById(R.id.view_flipper));
        vf.setDisplayedChild(Constants.ACTIVITY_PLAYLIST_VIEW);

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
        Intent intent;
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_network) {
            intent = new Intent(PlaylistActivity.this, NetworkSettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_preferences) {
            intent = new Intent(PlaylistActivity.this, PreferencesActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_songList){
            intent = new Intent(PlaylistActivity.this, MediaListActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_mediaControl){
            intent = new Intent(PlaylistActivity.this, MediaPlayerActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
