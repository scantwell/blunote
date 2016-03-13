package com.drexelsp.blunote.blunote;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.DeliveryInfo;
import com.drexelsp.blunote.blunote.BlunoteMessages.SongFragment;
import com.drexelsp.blunote.blunote.BlunoteMessages.SongRequest;
import com.drexelsp.blunote.blunote.BlunoteMessages.WrapperMessage;
import com.google.protobuf.ByteString;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by scantwell on 2/16/2016.
 * Implements all media functionality of the Blunote application and handles all Network messages
 * from which SongRequests and SongFragments are acknowledged and generated respectively.
 */
public class Media implements MessageHandler {
    private final static int FRAGMENT_SIZE = 1024;
    private final String TAG = "Media";

    private File cacheDir;
    private ContentResolver mContentResolver;
    private MediaPlayer mediaPlayer;
    private Service mService;
    private HashMap<Long, SongAssembler> songsHash;

    public Media(Context context, Service service) {
        this.mService = service;
        this.mContentResolver = context.getContentResolver();
        this.cacheDir = context.getCacheDir();
        this.mediaPlayer = new MediaPlayer();
        this.songsHash = new HashMap<>();
    }

    private byte[] getSongData(String uri) {
        File songFile = new File(uri);
        int filelength = (int) songFile.length();
        byte[] songByteArray = new byte[filelength];
        try {
            BufferedInputStream bis1 = new BufferedInputStream(new FileInputStream(songFile));
            bis1.read(songByteArray, 0, songByteArray.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return songByteArray;
    }

    private SongFragment[] getSongFragments(long id) {
        ArrayList<SongFragment> frags = new ArrayList<>();
        String songUri = getSongUri(id);
        byte[] songData = getSongData(songUri);
        int total_frags = songData.length / FRAGMENT_SIZE;
        if (songData.length % FRAGMENT_SIZE != 0) {
            total_frags += 1;
        }
        int start = 0;
        int end = 0;
        SongFragment.Builder fragBuilder;
        for (int frag_no = 1; frag_no <= total_frags; ++frag_no) {
            fragBuilder = SongFragment.newBuilder();
            end += FRAGMENT_SIZE;
            if (end > songData.length) {
                end = songData.length;
            }
            fragBuilder.setFragmentId(frag_no);
            fragBuilder.setTotalFragments(total_frags);
            fragBuilder.setSongId(id);
            fragBuilder.setFragment(ByteString.copyFrom(songData, start, end));
            frags.add(fragBuilder.build());
        }
        return (SongFragment[]) frags.toArray();
    }

    private String getSongUri(long id) {
        Uri mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA};
        String selection = MediaStore.Audio.Media._ID + "=?";
        String[] selectionArgs = new String[]{"" + id}; //This is the id you are looking for

        Cursor mediaCursor = mContentResolver.query(mediaContentUri, projection, selection, selectionArgs, null);

        return mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
    }

    @Override
    public boolean processMessage(DeliveryInfo dinfo, WrapperMessage message) {
        if (WrapperMessage.Type.SONG_FRAGMENT.equals(message.getType())) {
            processMessage(dinfo, message.getSongFragment());
            return true;
        } else if (WrapperMessage.Type.SONG_REQUEST.equals(message.getType())) {
            processMessage(dinfo, message.getSongRequest());
            return true;
        } else {
            Log.v(TAG, "Undefined message.");
        }
        return false;
    }

    private void processMessage(DeliveryInfo dinfo, SongRequest request) {
        if (request.getUsername() == "myusername") {
            SongFragment frags[] = getSongFragments(request.getSongId());
            for (int i = 0; i < frags.length; ++i) {
                mService.send(frags[i]);
            }
        }
    }

    private void processMessage(DeliveryInfo dinfo, SongFragment frag) {
        if (songsHash.containsKey(frag.getSongId())) {
            songsHash.get(frag.getSongId()).addFragment(frag);
        } else {
            try {
                File file = File.createTempFile(Long.toString(frag.getSongId()), "mp3", this.cacheDir);
                SongAssembler asm = new SongAssembler(file);
                asm.addFragment(frag);
                songsHash.put(frag.getSongId(), asm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
