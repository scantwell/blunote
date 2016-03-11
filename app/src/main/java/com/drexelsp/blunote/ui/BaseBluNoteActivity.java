package com.drexelsp.blunote.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.drexelsp.blunote.blunote.Constants;
import com.drexelsp.blunote.blunote.R;

import java.util.List;

/**
 * Created by Brisbin on 2/10/2016.
 */
public abstract class BaseBluNoteActivity extends AppCompatActivity {
    ViewFlipper vf;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            handleIntent(getIntent());
        }

        vf = ((ViewFlipper) findViewById(R.id.view_flipper));
        vf.setDisplayedChild(getViewConstant());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);

        menu.getItem(Constants.MENU_ITEM_SONG_LIST)
                .setVisible(showMusicMenuItems());

        menu.getItem(Constants.MENU_ITEM_MEDIA_PLAYER)
                .setVisible(showMusicMenuItems());

        if (showSearchMenuItem()) {
            menu.getItem(Constants.MENU_ITEM_SEARCH).setVisible(true);

            // Associate login_activity_seachable configuration with the SearchView
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        menu.getItem(Constants.MENU_ITEM_SETTINGS)
                .setVisible(showSettingsCog());

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
            intent = new Intent(getCurrentContext(), NetworkSettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_preferences) {
            intent = new Intent(getCurrentContext(), PreferencesActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_songList) {
            intent = new Intent(getCurrentContext(), MediaListActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_mediaControl) {
            intent = new Intent(getCurrentContext(), MediaPlayerActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    protected void setSimpleList(ListView listView, List<String> list) {
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
    }

    public boolean showSettingsCog() {
        return true;
    }

    /**
     * Method to override for subclasses that use search functionality, can't be abstract since not all classes
     * use it.
     *
     * @param intent - the search intent passed in on search
     */
    public void handleIntent(Intent intent) {
    }

    public abstract Context getCurrentContext();

    public abstract int getViewConstant();

    public abstract boolean showMusicMenuItems();

    public abstract boolean showSearchMenuItem();
}
