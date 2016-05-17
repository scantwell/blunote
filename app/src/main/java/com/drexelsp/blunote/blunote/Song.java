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
    private String title;
    private String album;
    private String artist;
    private String owner;

    public Song(long id, Uri uri, String title, String album, String artist, String owner) {
        this.id = id;
        this.uri = uri;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.owner = owner;
    }

    public Song(long id, File file) throws FileNotFoundException {
        this.id = id;

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

    public void setUri(Uri uri) { this.uri = uri; }

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

    public void createFileURI(File f) throws FileNotFoundException{
        this.target = 1;
        this.cache = new HashMap<>();
        this.fos = new FileOutputStream(f);
        this.uri = Uri.fromFile(f);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
