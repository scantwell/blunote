package com.drexelsp.blunote.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.drexelsp.blunote.blunote.Constants;
import com.drexelsp.blunote.blunote.Player;
import com.drexelsp.blunote.blunote.R;
import com.drexelsp.blunote.events.NextSongEvent;
import com.drexelsp.blunote.events.PauseSongEvent;
import com.drexelsp.blunote.events.PlaySongEvent;
import com.drexelsp.blunote.events.PreviousSongEvent;
import com.drexelsp.blunote.events.SeekEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    int progress;
    Player player = null;

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

        final Handler mHandler = new Handler();
        MediaPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (player != null) {
                    int mCurrentPosition = player.getCurrentMillisecond();
                    seekBar.setProgress(mCurrentPosition);
                    currentMusicLocation.setText(durationToTime(mCurrentPosition));
                }
                mHandler.postDelayed(this, 1000);
            }
        });
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onPlaySong(PlaySongEvent event) {
        songName.setText(event.title);
        artistName.setText(event.artist);
        albumName.setText(event.album);
        currentMusicLocation.setText("0");
        musicDuration.setText(durationToTime(Integer.parseInt(event.duration)));
        seekBar.setMax(Integer.parseInt(event.duration));
        this.player = event.player;
    }

    private String durationToTime(int duration) {
        String dur = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        Log.v("MediaPlayerActivity", String.format("Created time duration string %s from %d.", dur, duration));
        return dur;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        this.progress = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.v("MediaPlayerActivity", String.format("Our progress is %d", progress));
        EventBus.getDefault().post(new SeekEvent(progress));
    }

    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
