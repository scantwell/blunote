package com.drexelsp.blunote.blunote;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.drexelsp.blunote.events.NextSongEvent;
import com.drexelsp.blunote.events.PauseSongEvent;
import com.drexelsp.blunote.events.PreviousSongEvent;
import com.drexelsp.blunote.events.SeekEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by scantwell on 3/14/2016.
 */
public class Player implements Runnable {

    private static final String TAG = "BlunoteMediaPlayer";
    private Deque<Uri> queue;
    private MediaPlayer player;
    private Context context;
    private Uri lastSong;
    private Uri currentSong;

    public Player(Context context)
    {
        this.currentSong = null;
        this.lastSong = null;
        this.context = context;
        this.queue = new LinkedList<>();
        this.player = new MediaPlayer();
        this.player.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public synchronized void addSongUri(Uri uri)
    {
        Log.v(TAG, String.format("Adding song to queue. Queue size %d", queue.size()));
        queue.add(uri);
        this.notify();
    }

    @Override
    public void run() {
        while (true)
        {
            waitOnQueueOrPlayer();
            playSong();
        }
    }

    private synchronized void waitOnQueueOrPlayer()
    {
        while (queue.size() < 1 || player.isPlaying())
        {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lastSong = currentSong;
    }

    public void playSong()
    {
        try {
            Uri uri = queue.remove();
            currentSong = uri;
            player.reset();
            player.setDataSource(context, uri);
            player.prepare();
            player.start();
            Log.v(TAG, String.format("Playing song. Queue size %d", queue.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onNextSongEvent(NextSongEvent event)
    {
        player.stop();
        notify();
    }

    @Subscribe
    public void onPreviousSongEvent(PreviousSongEvent event)
    {
        if (lastSong != null || getCurrentTimePercentage() > 0.05)
        {
            queue.addFirst(lastSong);
            lastSong = null;
            player.stop();
        } else {
            player.start();
        }
    }

    private float getCurrentTimePercentage()
    {
        return player.getCurrentPosition()/player.getDuration();
    }

    @Subscribe
    public void onPauseSong(PauseSongEvent event)
    {
        if (player.isPlaying())
        {
            player.pause();
        } else {
            player.start();
        }
    }

    @Subscribe
    public void onSeek(SeekEvent event)
    {
        player.seekTo((int)event.position * 1000);
    }
}