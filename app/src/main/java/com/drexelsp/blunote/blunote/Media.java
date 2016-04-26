package com.drexelsp.blunote.blunote;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.SongFragment;
import com.drexelsp.blunote.provider.MetaStoreContract;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by scantwell on 2/16/2016.
 * Implements all media functionality of the Blunote application and handles all Network messages
 * from which SongRequests and SongFragments are acknowledged and generated respectively.
 */
public class Media {
    private final static int FRAGMENT_SIZE = 1024 * 10;
    private final String TAG = "Media";

    private ContentResolver contentResolver;

    public Media(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    private byte[] getSongData(String uri) {
        File songFile = new File(uri);
        int filelength = (int) songFile.length();
        byte[] songByteArray = new byte[filelength];
        try {
            FileInputStream fis = new FileInputStream(songFile);
            fis.read(songByteArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return songByteArray;
    }

    public ArrayList<SongFragment> getSongFragments(long id) {
        ArrayList<SongFragment> frags = new ArrayList<>();
        String songUri = getSongUri(id);
        byte[] songData = getSongData(songUri);
        int total_frags = songData.length / FRAGMENT_SIZE;
        if (songData.length % FRAGMENT_SIZE != 0) {
            total_frags += 1;
        }
        int start = 0;
        int size = FRAGMENT_SIZE;
        SongFragment.Builder fragBuilder;
        for (int frag_no = 1; frag_no <= total_frags; ++frag_no) {
            fragBuilder = SongFragment.newBuilder();
            if (start + size > songData.length) {
                size = songData.length - start;
            }
            Log.v(TAG, String.format("Start: %d, End: %d, Frag#: %d", start, size, frag_no));
            fragBuilder.setFragmentId(frag_no);
            fragBuilder.setTotalFragments(total_frags);
            fragBuilder.setSongId(id);
            fragBuilder.setFragment(ByteString.copyFrom(songData, start, size));
            frags.add(fragBuilder.build());
            start += size;
        }
        return frags;
    }

    public String getSongUri(long id) {
        Uri mediaContentUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.toString(id));
        String[] projection = new String[]{MediaStore.Audio.Media.DATA};
        Cursor mediaCursor = this.contentResolver.query(mediaContentUri, projection, null, null, null);

        if (mediaCursor == null) {
            throw new RuntimeException(String.format("No URI matching ID '%d'", id));
        }
        mediaCursor.moveToFirst();
        String rv = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        Log.v(TAG, String.format("Id to URI: %s", rv));
        mediaCursor.close();
        return rv;
    }

    public int findSongId(String title, String artist, String album){
        Uri uri = MetaStoreContract.Track.CONTENT_URI;
        String[] proj = new String[]{MetaStoreContract.Track.SONG_ID};
        String where = "WHERE title = ? AND artist = ? AND album = ?";
        String[] params = new String[]{title, artist, album};
        Cursor c = contentResolver.query(uri, proj, where, params, null);

        if(c == null) {
            throw new RuntimeException(String.format(
                    "No valid song with params: Title - %s, Artist - %s, Album - %s", title, artist, album));
        }
        c.moveToFirst();
        return c.getInt(c.getColumnIndex(MetaStoreContract.Track.SONG_ID));
    }

    public String findSongUsername(String title, String artist, String album) {
        Uri uri = MetaStoreContract.FIND_USERNAME_URI;
        String[] params = new String[]{title, artist, album};
        Cursor c = contentResolver.query(uri, null, null, params, null);

        if(c == null) {
            throw new RuntimeException(String.format(
                    "No valid username for params: Title - %s, Artist - %s, Album - %s", title, artist, album));
        }
        c.moveToFirst();
        return c.getString(c.getColumnIndex(MetaStoreContract.User.USERNAME));
    }
}
