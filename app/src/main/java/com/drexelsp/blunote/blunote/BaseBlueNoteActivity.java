package com.drexelsp.blunote.blunote;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ViewFlipper;

/**
 * Created by Brisbin on 2/10/2016.
 */
public abstract class BaseBlueNoteActivity extends AppCompatActivity
{
    ViewFlipper vf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    public abstract Context getCurrentContext();

    public abstract int getViewConstant();

    public abstract boolean showMusicMenuItems();
}
