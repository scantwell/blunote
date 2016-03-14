package com.drexelsp.blunote.blunote;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by scantwell on 3/14/2016.
 */
public class Player implements Runnable {

    private SynchronousQueue<Uri> mQueue;
    private MediaPlayer mPlayer;
    private Context mContext;

    public Player(Context context)
    {
        this.mContext = context;
        this.mQueue = new SynchronousQueue<>();
        this.mPlayer = new MediaPlayer();
        this.mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public synchronized void addSongUri(Uri uri)
    {
        mQueue.add(uri);
        this.notify();
    }

    @Override
    public void run() {
        while (true)
        {
            while(mQueue.size() > 0)
            {
                playSong();
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
        Uri uri = mQueue.remove();
        try {
            mPlayer.setDataSource(mContext, uri);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
