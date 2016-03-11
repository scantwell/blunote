package com.drexelsp.blunote.blunote;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by scantwell on 3/10/2016.
 */
public class Metadata implements MessageHandler {

    private String TAG = "Metadata";
    private ContentResolver mResolver;

    public Metadata(ContentResolver cResolver) {
        mResolver = cResolver;
    }

    @Override
    public boolean processMessage(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.WrapperMessage message) {
        if (BlunoteMessages.WrapperMessage.Type.METADATA_UPDATE.equals(message.getType())) {
            if (message.getMetadataUpdate().getAction() == BlunoteMessages.MetadataUpdate.Action.ADD) {
                addMetadata(message.getMetadataUpdate());
            } else {
                deleteMetadata(message.getMetadataUpdate());
            }
            return true;
        } else {
            Log.v(TAG, "Undefined message.");
        }
        return false;
    }

    private void addMetadata(BlunoteMessages.MetadataUpdate message) {
        ArrayList<ContentValues> songs = getSongValues((ArrayList) message.getSongsList());
        ArrayList<ContentValues> artists = getArtistValues((ArrayList) message.getArtistsList());
        ArrayList<ContentValues> albums = getAlbumValues((ArrayList) message.getAlbumsList());
        //mResolver.bulkInsert();
    }

    private void deleteMetadata(BlunoteMessages.MetadataUpdate message) {
        //mResolver.
    }

    private ArrayList<ContentValues> getSongValues(ArrayList<BlunoteMessages.Song> songs) {
        ArrayList<ContentValues> valuesList = new ArrayList<>();
        ContentValues values;
        for (BlunoteMessages.Song song : songs) {
            values = new ContentValues();
        }
        return valuesList;
    }

    private ArrayList<ContentValues> getArtistValues(ArrayList<BlunoteMessages.Artist> artists) {
        ArrayList<ContentValues> valuesList = new ArrayList<>();
        ContentValues values;
        for (BlunoteMessages.Artist artist : artists) {
            values = new ContentValues();
        }
        return valuesList;
    }

    private ArrayList<ContentValues> getAlbumValues(ArrayList<BlunoteMessages.Album> albums) {
        ArrayList<ContentValues> valuesList = new ArrayList<>();
        ContentValues values;
        for (BlunoteMessages.Album album : albums) {
            values = new ContentValues();
        }
        return valuesList;
    }
}
