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
import com.drexelsp.blunote.events.SongRecommendationEvent;
import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
public class Media {
    private final static int FRAGMENT_SIZE = 1024 * 10;
    private final String TAG = "Media";

    private File cacheDir;
    private ContentResolver mContentResolver;
    private Service mService;
    private HashMap<Long, SongAssembler> songsHash;
    private Player player;

    public Media(Context context, Service service) {
        this.mService = service;
        this.mContentResolver = context.getContentResolver();
        this.cacheDir = context.getCacheDir();
        this.songsHash = new HashMap<>();
        this.player = new Player(context);
        Thread thread = new Thread(this.player);
        thread.start();
        EventBus.getDefault().register(this);
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

    private String getSongUri(long id) {
        Uri mediaContentUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.toString(id));
        String[] projection = new String[]{MediaStore.Audio.Media.DATA};
        Cursor mediaCursor = mContentResolver.query(mediaContentUri, projection, null, null, null);

        if (mediaCursor != null && mediaCursor.moveToNext()) {
            String rv = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            Log.v(TAG, String.format("Id to URI: %s", rv));
            mediaCursor.close();
            return rv;
        } else {
            mediaCursor.close();
            throw new RuntimeException("No URI matching ID");
        }
    }

    public void addSongFragment(SongFragment frag)
    {
        if (songsHash.containsKey(frag.getSongId())) {
            SongAssembler asm = songsHash.get(frag.getSongId());
            asm.addFragment(frag);
            if (asm.isCompleted())
            {
                player.addSongUri(asm.getURI());
                songsHash.remove(frag.getSongId());
            }
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
