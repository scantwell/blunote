package com.drexelsp.blunote.blunote;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.util.Log;

import com.drexelsp.blunote.provider.MetaStoreContract;

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
        mResolver.bulkInsert(MetaStoreContract.Track.CONTENT_URI, (ContentValues[])songs.toArray());
        mResolver.bulkInsert(MetaStoreContract.Artist.CONTENT_URI, (ContentValues[])artists.toArray());
        mResolver.bulkInsert(MetaStoreContract.Album.CONTENT_URI, (ContentValues[]) albums.toArray());
    }

    private void deleteMetadata(BlunoteMessages.MetadataUpdate message) {
        //mResolver.
    }

    private ArrayList<ContentValues> getSongValues(ArrayList<BlunoteMessages.Song> songs) {
        ArrayList<ContentValues> valuesList = new ArrayList<>();
        ContentValues values;
        for (BlunoteMessages.Song song : songs) {
            values = new ContentValues();
            values.put(MetaStoreContract.Track.ALBUM_ID, song.getAlbumId());
            values.put(MetaStoreContract.Track.ARTIST_ID, song.getArtistId());
            values.put(MetaStoreContract.Track.DURATION, song.getDuration());
            values.put(MetaStoreContract.Track.SONG_ID, song.getSongId());
            values.put(MetaStoreContract.Track.TITLE, song.getTitle());
            values.put(MetaStoreContract.Track.TRACK_NO, song.getTrack());
            values.put(MetaStoreContract.Track.YEAR, song.getYear());
            valuesList.add(values);
        }
        return valuesList;
    }

    private ArrayList<ContentValues> getArtistValues(ArrayList<BlunoteMessages.Artist> artists) {
        ArrayList<ContentValues> valuesList = new ArrayList<>();
        ContentValues values;
        for (BlunoteMessages.Artist artist : artists) {
            values = new ContentValues();
            values.put(MetaStoreContract.Artist.ARTIST, artist.getArtist());
            values.put(MetaStoreContract.Artist.NUMBER_OF_ALBUMS, artist.getNumberOfAlbums());
            values.put(MetaStoreContract.Artist.NUMBER_OF_TRACKS, artist.getNumberOfTracks());
            values.put(MetaStoreContract.Artist.ARTIST_ID, artist.getArtistId());
            valuesList.add(values);
        }
        return valuesList;
    }

    private ArrayList<ContentValues> getAlbumValues(ArrayList<BlunoteMessages.Album> albums) {
        ArrayList<ContentValues> valuesList = new ArrayList<>();
        ContentValues values;
        for (BlunoteMessages.Album album : albums) {
            values = new ContentValues();
            values.put(MetaStoreContract.Album.ALBUM, album.getAlbum());
            values.put(MetaStoreContract.Album.ALBUM_ART, album.getAlbumArt());
            values.put(MetaStoreContract.Album.ALBUM_ID, album.getAlbumId());
            values.put(MetaStoreContract.Album.ARTIST, album.getArtist());
            values.put(MetaStoreContract.Album.FIRST_YEAR, album.getFirstYear());
            values.put(MetaStoreContract.Album.LAST_YEAR, album.getLastYear());
            values.put(MetaStoreContract.Album.NUMBER_OF_SONGS, album.getNumberOfSongs());
            valuesList.add(values);
        }
        return valuesList;
    }
}
