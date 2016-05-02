package com.drexelsp.blunote.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.drexelsp.blunote.blunote.Constants;
import com.drexelsp.blunote.blunote.R;
import com.drexelsp.blunote.events.NextSongEvent;
import com.drexelsp.blunote.events.PauseSongEvent;
import com.drexelsp.blunote.events.PlaySongEvent;
import com.drexelsp.blunote.events.PreviousSongEvent;
import com.drexelsp.blunote.events.SeekEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;

/**
 * Created by Brisbin on 1/29/2016.
 */
public class MediaPlayerActivity extends BaseBluNoteActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {
    FloatingActionButton playlistFAB;
    TextView songName;
    TextView artistName;
    TextView albumName;
    TextView ownerName;
    TextView currentMusicLocation;
    TextView musicDuration;
    ImageButton previous;
    ToggleButton playPause;
    ImageButton next;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playlistFAB = (FloatingActionButton) findViewById(R.id.playlist_FAB);
        playlistFAB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MediaPlayerActivity.this, PlaylistActivity.class);
                startActivity(intent);
            }
        });

        seekBar = (SeekBar) findViewById(R.id.musicSeekBar);
        songName = (TextView) findViewById(R.id.song_name);
        artistName = (TextView) findViewById(R.id.artist_name);
        albumName = (TextView) findViewById(R.id.album_name);
        ownerName = (TextView) findViewById(R.id.song_owner_name);
        currentMusicLocation = (TextView) findViewById(R.id.musicCurrentLoc);
        musicDuration = (TextView) findViewById(R.id.musicDuration);
        previous = (ImageButton) findViewById(R.id.previous);
        playPause = (ToggleButton) findViewById(R.id.playPauseButton);
        next = (ImageButton) findViewById(R.id.nextButton);

        seekBar.setOnSeekBarChangeListener(this);
        previous.setOnClickListener(this);
        playPause.setOnCheckedChangeListener(this);
        next.setOnClickListener(this);
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
        PauseSongEvent pauseSong = new PauseSongEvent();
        EventBus.getDefault().post(pauseSong);
    }

    @Override
    public void onClick(View v) {
        //Handles all of the other onclicks for the media player screen
        if (v == previous) {
            PreviousSongEvent prevSong = new PreviousSongEvent();
            EventBus.getDefault().post(prevSong);
        } else if (v == next) {
            NextSongEvent nextSong = new NextSongEvent();
            EventBus.getDefault().post(nextSong);
        }
    }

    @Subscribe
    public void onPlaySong(PlaySongEvent event)
    {
        songName.setText(event.title);
        artistName.setText(event.artist);
        albumName.setText(event.album);
        currentMusicLocation.setText("0");
        musicDuration.setText(durationToTime(Integer.parseInt(event.duration)));
        seekBar.setMax(Integer.parseInt(event.duration));
    }

    private String durationToTime(int duration)
    {
        return String.format("%02d:%02d",
                TimeUnit.SECONDS.toMinutes(duration), // The change is in this line
                TimeUnit.SECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(duration)));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        EventBus.getDefault().post(new SeekEvent(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
