package com.drexelsp.blunote.ui;

import com.drexelsp.blunote.blunote.Constants;
import com.drexelsp.blunote.blunote.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by Brisbin on 1/29/2016.
 */
public class MediaPlayerActivity extends BaseBluNoteActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener
{
    FloatingActionButton playlistFAB;
    TextView songName;
    TextView artistName;
    TextView albumName;
    TextView ownerName;
    TextView currentMusticLocation;
    TextView musicDuration;
    ImageButton downVote;
    ImageButton previous;
    ToggleButton playPause;
    ImageButton next;
    ImageButton upVote;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        playlistFAB = (FloatingActionButton) findViewById(R.id.playlist_FAB);
        playlistFAB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MediaPlayerActivity.this, PlaylistActivity.class);
                startActivity(intent);
            }
        });

        songName = (TextView) findViewById(R.id.song_name);
        artistName = (TextView) findViewById(R.id.artist_name);
        albumName = (TextView) findViewById(R.id.album_name);
        ownerName = (TextView) findViewById(R.id.song_owner_name);
        currentMusticLocation = (TextView) findViewById(R.id.musicCurrentLoc);
        musicDuration = (TextView) findViewById(R.id.musicDuration);
        downVote = (ImageButton) findViewById(R.id.downvote);
        previous = (ImageButton) findViewById(R.id.previous);
        playPause = (ToggleButton) findViewById(R.id.playPauseButton);
        next = (ImageButton) findViewById(R.id.nextButton);
        upVote = (ImageButton) findViewById(R.id.upvote);

        downVote.setOnClickListener(this);
        previous.setOnClickListener(this);
        playPause.setOnCheckedChangeListener(this);
        next.setOnClickListener(this);
        upVote.setOnClickListener(this);
    }

    @Override
    public Context getCurrentContext() {
        return MediaPlayerActivity.this;
    }

    @Override
    public int getViewConstant() {
        return Constants.ACTIVITY_MEDIA_PLAYER;
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //Only needs to handle the play/pause button since it's the only toggle button
    }

    @Override
    public void onClick(View v) {
        //Handles all of the other onclicks for the media player screen
        if(v == downVote)
        {
            //handle downvote click
        }
        else if(v == previous)
        {
            //handle previous click
        }
        else if(v == upVote)
        {
            //handle upvote click
        }
        else if(v == next)
        {
            //handle next click
        }
    }

    public void updateTrackInformation()
    {

    }
}
