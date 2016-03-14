package com.drexelsp.blunote.ui;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.drexelsp.blunote.provider.MetaStoreContract;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Brisbin on 2/10/2016.
 */
public abstract class BaseBluNoteActivity extends AppCompatActivity {
    ViewFlipper vf;
    SearchView searchView;
    static ContentResolver metaStore;
    static MetaStoreObserver metaStoreObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getCurrentContext().getContentResolver().registerContentObserver(
                MetaStoreContract.CONTENT_URI, true, getMetaStoreObserver());

        if (!Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            handleIntent(getIntent());
        }

        vf = ((ViewFlipper) findViewById(R.id.view_flipper));
        vf.setDisplayedChild(getViewConstant());
    }

    @Override
    protected void onPause() {
        super.onPause();
        getCurrentContext().getContentResolver().unregisterContentObserver(getMetaStoreObserver());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCurrentContext().getContentResolver().registerContentObserver(
                MetaStoreContract.CONTENT_URI, true, getMetaStoreObserver());
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

    /**
     * Method to be overridden to if the MetaStore is changed while viewing an activity
     */
    public void handleOnMetaStoreChange() {
    }

    public abstract Context getCurrentContext();

    public abstract int getViewConstant();

    public abstract boolean showMusicMenuItems();

    public abstract boolean showSearchMenuItem();

    protected Map<String, String> getAlbumList() {
        Map<String, String> albumMap = new LinkedHashMap<>();
        Cursor cur = getAlbumListCursor();
        String album, albumID;

        while (cur.moveToNext()) {
            album = cur.getString(cur.getColumnIndex(MetaStoreContract.Album.ALBUM));
            albumID = Integer.toString(cur.getInt(cur.getColumnIndex(MetaStoreContract.Album._ID)));
            if (album != null && albumID != null) {
                albumMap.put(album, albumID);
            }
        }

        return albumMap;
    }

    protected Map<String, String> getArtistList() {
        Map<String, String> artistMap = new LinkedHashMap<>();
        Cursor cur = getArtistListCursor();
        String artist, artistID;

        while (cur.moveToNext()) {
            artist = cur.getString(cur.getColumnIndex(MetaStoreContract.Artist.ARTIST));
            artistID = Integer.toString(cur.getInt(cur.getColumnIndex(MetaStoreContract.Artist._ID)));
            if (artist != null && artistID != null) {
                artistMap.put(artist, artistID);
            }
        }

        return artistMap;
    }

    protected Map<String, String> getSongList() {
        Map<String, String> songList = new LinkedHashMap<>();
        Cursor cur = getTrackListCursor();
        String song, songID;

        while (cur.moveToNext()) {
            song = cur.getString(cur.getColumnIndex(MetaStoreContract.Track.TITLE));
            songID = Integer.toString(cur.getInt(cur.getColumnIndex(MetaStoreContract.Track.SONG_ID)));
            if (song != null && songID != null) {
                songList.put(song, songID);
            }
        }

        return songList;
    }

    private Cursor getAlbumListCursor() {
        final String[] columns = {MetaStoreContract.Album.ALBUM, MetaStoreContract.Album._ID};
        return getMetaStore().query(MetaStoreContract.Album.CONTENT_URI, columns, null, null, MetaStoreContract.Album.SORT_ORDER_DEFAULT);
    }

    private Cursor getArtistListCursor() {
        final String[] columns = {MetaStoreContract.Artist.ARTIST, MetaStoreContract.Artist._ID};
        return getMetaStore().query(MetaStoreContract.Artist.CONTENT_URI, columns, null, null, MetaStoreContract.Artist.SORT_ORDER_DEFAULT);
    }

    private Cursor getTrackListCursor() {
        final String[] columns = {MetaStoreContract.Track.TITLE, MetaStoreContract.Track.SONG_ID};
        return getMetaStore().query(MetaStoreContract.Track.CONTENT_URI, columns, null, null, MetaStoreContract.Track.SORT_ORDER_DEFAULT);
    }

    public ContentResolver getMetaStore() {
        if (metaStore == null) {
            metaStore = getCurrentContext().getContentResolver();
        }

        return metaStore;
    }

    public MetaStoreObserver getMetaStoreObserver() {
        if (metaStoreObserver == null) {
            metaStoreObserver = new MetaStoreObserver(new Handler());
        }

        return metaStoreObserver;
    }

    public class MetaStoreObserver extends ContentObserver {
        public MetaStoreObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            handleOnMetaStoreChange();
        }
    }
}
