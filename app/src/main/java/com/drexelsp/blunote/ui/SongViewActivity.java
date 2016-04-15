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
import com.drexelsp.blunote.events.SongRecommendationEvent;

import org.greenrobot.eventbus.EventBus;

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

    String id;
    String username;

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
        if (v == song_view_add_to_queue) {
            SongRecommendationEvent event = new SongRecommendationEvent(id, username);
            EventBus.getDefault().post(event);
        }

    }

    public void populateSongDetails() {
        Intent intent = getIntent();
        id = intent.getStringExtra("_id");
        String[] selection = { "song_id", "title", "artist", "album" };
        String where = "song_id = ?";
        String[] args = { id };
        Cursor cursor = getContentResolver().query(MetaStoreContract.Track.CONTENT_URI, selection, where, args, null);

        if (cursor != null && cursor.moveToNext()) {
            Log.v(TAG, "Results found");
            String title = cursor.getString(cursor.getColumnIndex("title"));
            songViewTitle.setText(title);
            String artist = cursor.getString(cursor.getColumnIndex("artist"));
            songViewArtist.setText(artist);
            String album = cursor.getString(cursor.getColumnIndex("album"));
            songViewAlbum.setText(album);
            cursor.close();

            username = getUsername(title,artist,album);
            songViewOwner.setText(username);

            songViewAlbumArt.setImageBitmap(getAlbumArt(album));
        }

    }

    private Bitmap getAlbumArt(String album) {
        Uri uri = MetaStoreContract.Album.CONTENT_URI;
        String selection[] = {"album_art"};
        String where = "album = ?";
        String[] args = { album };
        Cursor cursor = getContentResolver().query(uri, selection, where, args, null);

        if (cursor != null && cursor.moveToNext()) {
            byte[] albumArt = cursor.getBlob(cursor.getColumnIndex("album_art"));
            cursor.close();

            return BitmapFactory.decodeByteArray(albumArt, 0, albumArt.length);
        } else {
            return null;
        }
    }

    private String getUsername(String title, String artist, String album) {
        String username;
        String[] selection = {};
        String where = "title = ? AND artist = ? AND album = ?";
        String[] args = {title, artist, album};
        Cursor cursor = getContentResolver().query(MetaStoreContract.UserTracks.CONTENT_URI, selection, where, args, null);
        if (cursor != null && cursor.moveToFirst()) {
            String userId = cursor.getString(cursor.getColumnIndex("user_id"));
            cursor.close();

            where = "user_id = ?";
            args = new String[]{userId};
            cursor = getContentResolver().query(MetaStoreContract.User.CONTENT_URI, selection, where, args, null);
            if (cursor != null && cursor.moveToFirst()) {
                username = cursor.getString(cursor.getColumnIndex("username"));
                cursor.close();
            } else {
                username = "";
            }

        } else {
            username = "";
        }
        return username;
    }
}
