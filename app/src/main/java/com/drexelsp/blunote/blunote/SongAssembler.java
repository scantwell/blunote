package com.drexelsp.blunote.blunote;


import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.SongFragment;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by scantwell on 3/12/2016.
 */
public class SongAssembler extends Observable implements Observer {
    private static String TAG = "SongAssembler";
    private File cacheDir;
    private ConcurrentHashMap<Long, Song> songHash;
    private long fileNameSeed;

    public SongAssembler(File cacheDir) {
        this.cacheDir = cacheDir;
        this.fileNameSeed = 0;
        this.songHash = new ConcurrentHashMap<>();
    }

    public void registerSong(long songId) throws IOException {
        if (songHash.containsKey(songId)) {
            throw new RuntimeException("Song has already been registered.");
        } else {
            File file = createTempFile();
            Song s = new Song(songId, file);
            s.addObserver(this);
            songHash.put(songId, s);
        }
    }

    public void onFragment(SongFragment frag) {
        if (songHash.containsKey(frag.getSongId())) {
            this.songHash.get(frag.getSongId()).addFragment(frag);
        } else {
            Log.w(TAG, String.format("Cannot process unrecognized song fragment. id(%d) fragment(%d/%d)",
                    frag.getSongId(),
                    frag.getFragmentId(),
                    frag.getTotalFragments()));
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        Song song = (Song) data;
    }

    private File createTempFile() throws IOException {
        return File.createTempFile(java.util.UUID.randomUUID().toString(), ".mp3", this.cacheDir);
    }
}
