package com.drexelsp.blunote.ui;

import com.drexelsp.blunote.blunote.Constants;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by U6020377 on 1/25/2016.
 */
public class SongViewActivity extends BaseBluNoteActivity
{
    TextView songViewTitle;
    TextView songViewArtist;
    TextView songViewAlbum;
    TextView songViewOwner;
    Button song_view_add_to_queue;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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

    public void populateSongDetails()
    {

    }
}
