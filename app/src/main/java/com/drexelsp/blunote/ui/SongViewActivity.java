package com.drexelsp.blunote.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.drexelsp.blunote.blunote.Constants;
import com.drexelsp.blunote.blunote.R;

/**
 * Created by U6020377 on 1/25/2016.
 */
public class SongViewActivity extends BaseBluNoteActivity implements View.OnClickListener {
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

    }
}
