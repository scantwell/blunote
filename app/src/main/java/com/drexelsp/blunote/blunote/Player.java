package com.drexelsp.blunote.blunote;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by scantwell on 3/14/2016.
 */
public class Player implements Runnable {

    private BlockingQueue<Uri> mQueue;
    private MediaPlayer mPlayer;
    private Context mContext;

    public Player(Context context)
    {
        this.mContext = context;
        this.mQueue = new ArrayBlockingQueue<>(10);
        this.mPlayer = new MediaPlayer();
        this.mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public synchronized void addSongUri(Uri uri)
    {
        Log.v("PLAYER", "ADDING A SONG TO QUEUE");
        mQueue.add(uri);
        this.notify();
    }

    @Override
    public void run() {
        while (true)
        {
            sleep();
            Log.v("PLAYER", String.format("QUEUE SIZE %d", mQueue.size()));
            while(mQueue.size() > 0)
            {
                playSong();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private synchronized void sleep()
    {
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void playSong()
    {
        try {
            if (!mPlayer.isPlaying())
            {
                Uri uri = mQueue.remove();
                mPlayer.reset();
                mPlayer.setDataSource(mContext, uri);
                mPlayer.prepare();
                mPlayer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
