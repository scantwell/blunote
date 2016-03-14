package com.drexelsp.blunote.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.drexelsp.blunote.blunote.Constants;
import com.drexelsp.blunote.blunote.R;
import com.drexelsp.blunote.provider.MetaStoreContract;

/**
 * Created by U6020377 on 1/25/2016.
 */
public class SongViewActivity extends BaseBluNoteActivity implements View.OnClickListener {
    private static final String TAG = "Song View Activity";
    TextView songViewTitle;
    TextView songViewArtist;
    TextView songViewAlbum;
    TextView songViewOwner;
    Button song_view_add_to_queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        song_view_add_to_queue = (Button) findViewById(R.id.song_view_add_to_queue);

        songViewTitle = (TextView) findViewById(R.id.song_view_title);
        songViewArtist = (TextView) findViewById(R.id.song_view_artist);
        songViewAlbum = (TextView) findViewById(R.id.song_view_album);
        songViewOwner = (TextView) findViewById(R.id.song_view_owner);

        song_view_add_to_queue.setOnClickListener(this);

        populateSongDetails();
    }

    @Override
    public Context getCurrentContext() {
        return SongViewActivity.this;
    }

    @Override
    public int getViewConstant() {
        return Constants.ACTIVITY_SONG_VIEW;
    }

    @Override
    public boolean showMusicMenuItems() {
        return true;
    }

    @Override
    public boolean showSearchMenuItem() {
        return false;
    }

    @Override
    public void onClick(View v) {

    }

    public void populateSongDetails() {
        Intent intent = getIntent();
        String id = intent.getStringExtra("_id");
        Uri uri = Uri.withAppendedPath(MetaStoreContract.Track.CONTENT_URI, id);
        Log.v(TAG, uri.toString());
        String[] selection = { "title", "artist", "album" };
        Cursor cursor = getContentResolver().query(uri, selection, null, null, null);

        if (cursor != null && cursor.moveToNext()) {
            Log.v(TAG, "Results found");
            String title = cursor.getString(cursor.getColumnIndex("title"));
            songViewTitle.setText(title);
            String artist = cursor.getString(cursor.getColumnIndex("artist"));
            songViewArtist.setText(artist);
            String album = cursor.getString(cursor.getColumnIndex("album"));
            songViewAlbum.setText(album);
            String owner = "SongOwner";
            songViewOwner.setText(owner);
            Log.v(TAG, String.format("Title: %s, Artist: %s, Album: %s, Owner: %s", title, artist, album, owner));
            cursor.close();
        }

    }
}
