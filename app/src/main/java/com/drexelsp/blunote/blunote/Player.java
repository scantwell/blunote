package com.drexelsp.blunote.blunote;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.drexelsp.blunote.events.NextSongEvent;
import com.drexelsp.blunote.events.PauseSongEvent;
import com.drexelsp.blunote.events.PlaySongEvent;
import com.drexelsp.blunote.events.PlaylistUpdateEvent;
import com.drexelsp.blunote.events.PreviousSongEvent;
import com.drexelsp.blunote.events.SeekEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by scantwell on 3/14/2016.
 */
public class Player extends Observable implements Runnable, MediaPlayer.OnCompletionListener {

    private static final String TAG = "BlunoteMediaPlayer";
    private Deque<Song> queue;
    private MediaPlayer player;
    private Context context;
    private Song lastSong;
    private Song currentSong;
    private AtomicBoolean isPaused;
    private AtomicBoolean isPlaying;

    public Player(Context context) {
        this.isPaused = new AtomicBoolean(false);
        this.isPlaying = new AtomicBoolean(false);
        this.currentSong = null;
        this.lastSong = null;
        this.context = context;
        this.queue = new LinkedList<>();
        this.player = new MediaPlayer();
        this.player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        this.player.setOnCompletionListener(this);
        EventBus.getDefault().register(this);

    }

    public synchronized void addSong(Song song) {
        Log.v(TAG, String.format("Adding song to queue. Queue size %d", queue.size()));
        EventBus.getDefault().postSticky(new PlaylistUpdateEvent(getPlaylist()));
        queue.add(song);
        this.notify();
    }

    @Override
    public void run() {
        int size;
        while (true)
        {
            Log.v(TAG, "New Main loop");
            synchronized (queue){
                size = queue.size();
                Log.v(TAG, "Setting size = to queue.size");
            }
            if (size < 1)
            {
                Log.v(TAG, "Notifying Observers");
                notifyObservers();
                Log.v(TAG, "Notified Observers");
            }
            waitOnQueueOrPlayer();
            playSong();
        }
    }

    private synchronized void waitOnQueueOrPlayer() {
        while (queue.size() < 1 || isPlaying.get() || isPaused.get()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.v(TAG, "Done waiting on queue or player");
        lastSong = currentSong;
    }

    public void playSong() {
        try {
            this.isPlaying.set(true);
            Song song = queue.remove();
            currentSong = song;
            player.reset();
            player.setDataSource(context, song.getUri());
            player.prepare();
            player.start();
            EventBus.getDefault().postSticky(new PlaylistUpdateEvent(getPlaylist()));
            EventBus.getDefault().postSticky(new PlaySongEvent(song.getTitle(), song.getArtist(),
                    song.getAlbum(), song.getOwner(), Integer.toString(player.getDuration()), this));
            Log.v(TAG, String.format("Playing song. Queue size %d", queue.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Song> getPlaylist()
    {
        ArrayList<Song> plist = new ArrayList<>();
        for (Song s : queue)
        {
            plist.add(s);
        }
        return plist;
    }

    @Override
    public void notifyObservers() {
        setChanged();
        super.notifyObservers();
    }

    @Subscribe
    public void onNextSongEvent(NextSongEvent event) {
        player.stop();
        wakeUp();
    }

    @Subscribe
    public void onPreviousSongEvent(PreviousSongEvent event) {
        if (lastSong == null || getCurrentTimePercentage() > 0.05) {
            player.seekTo(0);
        } else {
            queue.addFirst(currentSong);
            queue.addFirst(lastSong);
            lastSong = null;
            player.stop();
            wakeUp();
        }
    }

    private synchronized void wakeUp() {
        notify();
    }

    private float getCurrentTimePercentage() {
        return player.getCurrentPosition() / player.getDuration();
    }

    public int getCurrentMillisecond() {
        return (int) player.getCurrentPosition();
    }

    @Subscribe
    public void onPauseSong(PauseSongEvent event) {
        if (this.isPlaying.get()) {
            player.pause();
            isPlaying.set(false);
            isPaused.set(true);
        } else {
            isPlaying.set(true);
            isPaused.set(false);
            player.start();
        }
    }

    @Subscribe
    public void onSeek(SeekEvent event) {
        player.seekTo((int) event.position);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.v(TAG, "OnCompletion Called");
        this.isPlaying.set(false);
        wakeUp();
    }
}