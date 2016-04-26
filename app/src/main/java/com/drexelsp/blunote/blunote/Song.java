package com.drexelsp.blunote.blunote;


import android.net.Uri;
import android.util.Log;

import com.google.protobuf.ByteString;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;

/**
 * Created by stephencantwell on 4/19/16.
 */
public class Song extends Observable {
    private String TAG = "SONG";
    private FileOutputStream fos;
    private long id;
    private Uri uri;
    private long target;
    private HashMap<Long, BlunoteMessages.SongFragment> cache;

    public Song(long id, File file) throws FileNotFoundException {
        this.id = id;
        this.target = 1;
        this.cache = new HashMap<>();
        this.fos = new FileOutputStream(file);
        this.uri = Uri.fromFile(file);
    }

    public synchronized boolean addFragment(BlunoteMessages.SongFragment frag) {
        if (frag.getSongId() != this.id) {
            return false;
        } else {
            writeFragment(frag);
            if (target > frag.getTotalFragments()) {
                setChanged();
                notifyObservers();
            }
        }
        return true;
    }

    public long getId() {
        return id;
    }

    public Uri getUri() {
        return uri;
    }

    private void writeFragment(BlunoteMessages.SongFragment frag) {
        if (frag.getFragmentId() >= target) {
            cache.put(frag.getFragmentId(), frag);
        }
        while (cache.containsKey(target)) {
            BlunoteMessages.SongFragment f = cache.get(target);
            write(f.getFragment());
            target++;
            Log.v(TAG, String.format("Wrote Fragment(%d/%d) Size(%d) to %s",
                    frag.getFragmentId(),
                    frag.getTotalFragments(),
                    frag.getFragment().size(),
                    this.uri));
        }
    }

    private void write(ByteString frag) {
        try {
            byte[] data = new byte[frag.size()];
            frag.copyTo(data, 0);
            fos.write(data);
        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }
}
