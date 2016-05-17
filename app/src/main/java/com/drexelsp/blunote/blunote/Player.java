package com.drexelsp.blunote.blunote;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
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
 * Manages the android.MediaPlayer to include queueing properties of Blunote.Song's and post/receive
 * necessary events to/from the UI layer.
 */
public class Player extends Observable implements Runnable, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private static final String TAG = "BlunoteMediaPlayer";
    private Deque<Song> queue;
    private MediaPlayer player;
    private Context context;
    private Song lastSong;
    private Song currentSong;
    private AtomicBoolean isPaused;
    private AtomicBoolean isPlaying;
    private AtomicBoolean isPreparing;

    public Player(Context context) {
        this.isPaused = new AtomicBoolean(false);
        this.isPlaying = new AtomicBoolean(false);
        this.isPreparing = new AtomicBoolean(false);
        this.currentSong = null;
        this.lastSong = null;
        this.context = context;
        this.queue = new LinkedList<>();
        this.player = new MediaPlayer();
        this.player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        this.player.setOnCompletionListener(this);
        EventBus.getDefault().register(this);
    }

    public AtomicBoolean getIsPaused() {
        return isPaused;
    }

    /**
     * Synchronized method to add a Blunote.Song to the player's queue to be played.
     *
     * @param song
     */
    public synchronized void addSong(Song song) {
        Log.v(TAG, String.format("Adding song to queue. Queue size %d", queue.size()));
        queue.add(song);
        EventBus.getDefault().postSticky(new PlaylistUpdateEvent(getPlaylist()));
        this.notify();
    }

    /**
     * Returns the current position of the song in milliseconds.
     *
     * @return millis
     */
    public int getCurrentMillisecond() {
        return (int) player.getCurrentPosition();
    }

    public void onPrepared(MediaPlayer player) {
        this.isPreparing.set(false);
        this.isPlaying.set(true);
        this.player.start();
        EventBus.getDefault().postSticky(new PlaylistUpdateEvent(getPlaylist()));
        EventBus.getDefault().postSticky(new PlaySongEvent(currentSong.getTitle(), currentSong.getArtist(),
                currentSong.getAlbum(), currentSong.getOwner(), Integer.toString(player.getDuration()), this));
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.v(TAG, "OnCompletion Called");
        this.isPlaying.set(false);
        wakeUp();
    }

    /**
     * An EventBus event that notifies the player that the user has clicked the next button on the UI.
     *
     * @param event
     */
    @Subscribe
    public void onNextSongEvent(NextSongEvent event) {
        player.stop();
        isPlaying.set(false);
        isPaused.set(false);
        wakeUp();
    }

    /**
     * An EventBus event that notifies the player that the user has clicked the play/pause button on the UI.
     *
     * @param event
     */
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

    /**
     * An EventBus event that notifies the player that the user has clicked the previous button on the UI.
     *
     * @param event
     */
    @Subscribe
    public void onPreviousSongEvent(PreviousSongEvent event) {
        if (lastSong == null || getCurrentTimePercentage() > 5) {
            player.seekTo(0);
        } else {
            queue.addFirst(currentSong);
            queue.addFirst(lastSong);
            lastSong = null;
            currentSong = null;
            player.stop();
            isPlaying.set(false);
            isPaused.set(false);
            wakeUp();
        }
    }

    /**
     * An EventBus event that notifies the player that the user moved the seek bar on the UI.
     *
     * @param event
     */
    @Subscribe
    public void onSeek(SeekEvent event) {
        player.seekTo((int) event.position);
    }

    @Override
    public void run() {
        int size;
        while (true) {
            synchronized (queue) {
                size = queue.size();
                Log.v(TAG, "Setting size = to queue.size");
            }
            boolean autoplay = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean("pref_autoplay", true);
            if (size < 1 && autoplay) {
                Log.v(TAG, "Notifying Observers");
                setChanged();
                super.notifyObservers();
                Log.v(TAG, "Notified Observers");
            }
            synchronized (queue) {
                size = queue.size();
            }
            if (size > 0) {
                waitOnQueueOrPlayer();
                playSong();
            }
        }
    }

    /**
     * Returns the percentage of the song that has currently been played.
     *
     * @return percent
     */
    private int getCurrentTimePercentage() {
        double current = player.getCurrentPosition() / 1000.00;
        double duration = player.getDuration() / 1000.00;
        return (int) (current / duration * 100);
    }

    private ArrayList<Song> getPlaylist() {
        ArrayList<Song> plist = new ArrayList<>();
        for (Song s : queue) {
            plist.add(s);
        }
        return plist;
    }

    private void playSong() {
        try {
            //this.isPlaying.set(true);
            this.isPreparing.set(true);
            Song song = queue.remove();
            currentSong = song;
            player.reset();
            player.setDataSource(context, song.getUri());
            player.setOnPreparedListener(this);
            player.prepareAsync();
            //player.start();
            Log.v(TAG, String.format("Playing song. Queue size %d", queue.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void waitOnQueueOrPlayer() {
        while (queue.size() < 1 || isPlaying.get() || isPaused.get() || isPreparing.get()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.v(TAG, "Done waiting on queue or player");
        lastSong = currentSong;
    }

    private synchronized void wakeUp() {
        notify();
    }
}