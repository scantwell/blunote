package com.drexelsp.blunote.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.drexelsp.blunote.blunote.Constants;
import com.drexelsp.blunote.blunote.R;
import com.drexelsp.blunote.provider.MetaStoreContract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by U6020377 on 1/25/2016.
 */
public class SongViewActivity extends BaseBluNoteActivity implements View.OnClickListener {
    private static final String TAG = "Song View Activity";
    ImageView songViewAlbumArt;
    TextView songViewTitle;
    TextView songViewArtist;
    TextView songViewAlbum;
    TextView songViewOwner;
    Button song_view_add_to_queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        song_view_add_to_queue = (Button) findViewById(R.id.song_view_add_to_queue);

        songViewAlbumArt = (ImageView) findViewById(R.id.song_view_album_art);

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


            Uri uri1 = MetaStoreContract.Album.CONTENT_URI;
            String selection1[] = {"album_art"};
            String where1 = "album = ?";
            String[] args1 = { album };
            Cursor cursor1 = getContentResolver().query(uri1, selection1, where1, args1, null);

            if (cursor1 != null && cursor1.moveToNext()) {
                Log.v(TAG, "Album Found");
                byte[] albumArt = cursor1.getBlob(cursor1.getColumnIndex("album_art"));
                Bitmap bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.length);
                songViewAlbumArt.setImageBitmap(bitmap);
            }
        }

    }
}
