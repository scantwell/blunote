package com.drexelsp.blunote.blunote;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.drexelsp.blunote.provider.MetaStoreContract;

import java.util.List;

/**
 * Created by scantwell on 3/10/2016.
 */
public class Metadata implements MessageHandler {

    private String TAG = "Metadata";
    private ContentResolver contentResolver;

    public Metadata(Context context) {
        contentResolver = context.getContentResolver();
    }

    private void addMetadata(BlunoteMessages.MetadataUpdate message) {
        ContentValues[] songs = getSongValues(message.getSongsList());
        ContentValues[] artists = getArtistValues(message.getArtistsList());
        ContentValues[] albums = getAlbumValues(message.getAlbumsList());
        contentResolver.bulkInsert(MetaStoreContract.Track.CONTENT_URI, songs);
        contentResolver.bulkInsert(MetaStoreContract.Artist.CONTENT_URI, artists);
        contentResolver.bulkInsert(MetaStoreContract.Album.CONTENT_URI, albums);
    }

    private void deleteAlbums(List<BlunoteMessages.Album> albums) {
        String[] selectionArgs = new String[albums.size()];
        for (int i = 0; i < albums.size(); ++i) {
            selectionArgs[i] = albums.get(i).getAlbum();
        }
        contentResolver.delete(MetaStoreContract.Album.CONTENT_URI, "album=?", selectionArgs);
    }

    private void deleteArtists(List<BlunoteMessages.Artist> artists) {
        String[] selectionArgs = new String[artists.size()];
        for (int i = 0; i < artists.size(); ++i) {
            selectionArgs[i] = artists.get(i).getArtist();
        }
        contentResolver.delete(MetaStoreContract.Album.CONTENT_URI, "artist=?", selectionArgs);
    }

    private void deleteSongs(List<BlunoteMessages.Song> songs) {
        String[] selectionArgs = new String[songs.size()];
        for (int i = 0; i < songs.size(); ++i) {
            selectionArgs[i] = songs.get(i).getTitle();
        }
        contentResolver.delete(MetaStoreContract.Album.CONTENT_URI, "title=?", selectionArgs);
    }

    private void deleteMetadata(BlunoteMessages.MetadataUpdate message) {
        deleteAlbums(message.getAlbumsList());
        deleteArtists(message.getArtistsList());
        deleteSongs(message.getSongsList());
    }

    private ContentValues[] getAlbumValues(List<BlunoteMessages.Album> albums) {
        ContentValues[] valuesList = new ContentValues[albums.size()];
        ContentValues values;
        BlunoteMessages.Album album;
        for (int i = 0; i < albums.size(); ++i) {
            album = albums.get(i);
            values = new ContentValues();
            values.put(MetaStoreContract.Album.ALBUM, album.getAlbum());
            values.put(MetaStoreContract.Album.ALBUM_ART, album.getAlbumArt());
            values.put(MetaStoreContract.Album.ALBUM_ID, album.getAlbumId());
            values.put(MetaStoreContract.Album.ARTIST, album.getArtist());
            values.put(MetaStoreContract.Album.FIRST_YEAR, album.getFirstYear());
            values.put(MetaStoreContract.Album.LAST_YEAR, album.getLastYear());
            values.put(MetaStoreContract.Album.NUMBER_OF_SONGS, album.getNumberOfSongs());
            valuesList[i] = values;
        }
        return valuesList;
    }

    private ContentValues[] getArtistValues(List<BlunoteMessages.Artist> artists) {
        ContentValues[] valuesList = new ContentValues[artists.size()];
        ContentValues values;
        BlunoteMessages.Artist artist;
        for (int i = 0; i < artists.size(); ++i) {
            values = new ContentValues();
            artist = artists.get(i);
            values.put(MetaStoreContract.Artist.ARTIST, artist.getArtist());
            values.put(MetaStoreContract.Artist.NUMBER_OF_ALBUMS, artist.getNumberOfAlbums());
            values.put(MetaStoreContract.Artist.NUMBER_OF_TRACKS, artist.getNumberOfTracks());
            values.put(MetaStoreContract.Artist.ARTIST_ID, artist.getArtistId());
            valuesList[i] = values;
        }
        return valuesList;
    }

    private ContentValues[] getSongValues(List<BlunoteMessages.Song> songs) {
        ContentValues[] valuesList = new ContentValues[songs.size()];
        ContentValues values;
        BlunoteMessages.Song song;
        for (int i = 0; i < songs.size(); ++i) {
            song = songs.get(i);
            values = new ContentValues();
            values.put(MetaStoreContract.Track.ALBUM_ID, song.getAlbumId());
            values.put(MetaStoreContract.Track.ARTIST_ID, song.getArtistId());
            values.put(MetaStoreContract.Track.DURATION, song.getDuration());
            values.put(MetaStoreContract.Track.SONG_ID, song.getSongId());
            values.put(MetaStoreContract.Track.TITLE, song.getTitle());
            values.put(MetaStoreContract.Track.TRACK_NO, song.getTrack());
            values.put(MetaStoreContract.Track.YEAR, song.getYear());
            valuesList[i] = values;
        }
        return valuesList;
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
}