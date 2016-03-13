package com.drexelsp.blunote.blunote;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.Artist;
import com.drexelsp.blunote.provider.MetaStoreContract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by scantwell on 3/10/2016.
 */
public class Metadata implements MessageHandler {

    private String TAG = "Metadata";
    private ContentResolver mContentResolver;

    public Metadata(Context context) {
        mContentResolver = context.getContentResolver();
    }

    private void addMetadata(BlunoteMessages.MetadataUpdate message) {
        ContentValues[] songs = getSongValues(message.getSongsList());
        ContentValues[] artists = getArtistValues(message.getArtistsList());
        ContentValues[] albums = getAlbumValues(message.getAlbumsList());
        mContentResolver.bulkInsert(MetaStoreContract.Track.CONTENT_URI, songs);
        mContentResolver.bulkInsert(MetaStoreContract.Artist.CONTENT_URI, artists);
        mContentResolver.bulkInsert(MetaStoreContract.Album.CONTENT_URI, albums);
    }

    private void deleteAlbums(List<BlunoteMessages.Album> albums) {
        String[] selectionArgs = new String[albums.size()];
        for (int i = 0; i < albums.size(); ++i) {
            selectionArgs[i] = albums.get(i).getAlbum();
        }
        mContentResolver.delete(MetaStoreContract.Album.CONTENT_URI, "album=?", selectionArgs);
    }

    private void deleteArtists(List<Artist> artists) {
        String[] selectionArgs = new String[artists.size()];
        for (int i = 0; i < artists.size(); ++i) {
            selectionArgs[i] = artists.get(i).getArtist();
        }
        mContentResolver.delete(MetaStoreContract.Album.CONTENT_URI, "artist=?", selectionArgs);
    }

    private void deleteSongs(List<BlunoteMessages.Song> songs) {
        String[] selectionArgs = new String[songs.size()];
        for (int i = 0; i < songs.size(); ++i) {
            selectionArgs[i] = songs.get(i).getTitle();
        }
        mContentResolver.delete(MetaStoreContract.Album.CONTENT_URI, "title=?", selectionArgs);
    }

    private void deleteMetadata(BlunoteMessages.MetadataUpdate message) {
        deleteAlbums(message.getAlbumsList());
        deleteArtists(message.getArtistsList());
        deleteSongs(message.getSongsList());
    }

    private Cursor getAlbumcursor() {
        final Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        final String album = MediaStore.Audio.Albums.ALBUM;
        final String album_art = MediaStore.Audio.Albums.ALBUM_ART;
        final String album_id = MediaStore.Audio.Albums._ID;
        final String artist = MediaStore.Audio.Albums.ARTIST;
        final String first_year = MediaStore.Audio.Albums.FIRST_YEAR;
        final String last_year = MediaStore.Audio.Albums.LAST_YEAR;
        final String num_of_songs = MediaStore.Audio.Albums.NUMBER_OF_SONGS;
        final String[] columns = {album, album_art, album_id, artist, first_year, last_year, num_of_songs};
        return mContentResolver.query(uri, columns, null, null, null);
    }

    private ArrayList<BlunoteMessages.Album> getAlbumMeta() {
        ArrayList<BlunoteMessages.Album> albums = new ArrayList<BlunoteMessages.Album>();
        Cursor cur = getAlbumcursor();
        BlunoteMessages.Album.Builder albumsBuilder = BlunoteMessages.Album.newBuilder();

        String album, album_art, album_id, artist, first_year, last_year, num_of_songs;

        while (cur.moveToNext()) {
            album = cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
            album_art = cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            album_id = cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums._ID));
            artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
            first_year = cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.FIRST_YEAR));
            last_year = cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.LAST_YEAR));
            num_of_songs = cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS));

            album = album == null ? "" : album;
            album_art = album_art == null ? "" : album_art;
            album_id = album_id == null ? "-1" : album_id;
            artist = artist == null ? "" : artist;
            first_year = first_year == null ? "" : first_year;
            last_year = last_year == null ? "" : last_year;
            num_of_songs = num_of_songs == null ? "" : num_of_songs;

            album_art = getAlbumArt(album_art);

            albumsBuilder.setAlbum(album);
            albumsBuilder.setAlbumArt(album_art);
            albumsBuilder.setAlbumId(Integer.parseInt(album_id));
            albumsBuilder.setArtist(artist);
            albumsBuilder.setFirstYear(first_year);
            albumsBuilder.setLastYear(last_year);
            albumsBuilder.setNumberOfSongs(num_of_songs);
            albums.add(albumsBuilder.build());
        }
        return albums;
    }

    private String getAlbumArt(String uri) {
        try {
            FileInputStream fis = new FileInputStream(new File(uri));
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
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

    private Cursor getArtistcursor() {
        final Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        final String artist = MediaStore.Audio.Artists.ARTIST;
        final String artist_id = MediaStore.Audio.Artists._ID;
        final String num_of_albums = MediaStore.Audio.Artists.NUMBER_OF_ALBUMS;
        final String num_of_tracks = MediaStore.Audio.Artists.NUMBER_OF_TRACKS;
        final String[] columns = {artist_id, artist, num_of_albums, num_of_tracks};
        return mContentResolver.query(uri, columns, null, null, null);
    }

    private ArrayList<Artist> getArtistMeta() {
        ArrayList<Artist> artists = new ArrayList<Artist>();
        Cursor cur = getArtistcursor();
        Artist.Builder artistsBuilder = Artist.newBuilder();

        String artist, number_of_albums, number_of_track, artist_id;
        while (cur.moveToNext()) {
            artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            artist_id = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media._ID));
            number_of_albums = cur.getString(cur.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS));
            number_of_track = cur.getString(cur.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));

            artist = artist == null ? "" : artist;
            artist_id = artist_id == null ? "-1" : artist_id;
            number_of_albums = number_of_albums == null ? "" : number_of_albums;
            number_of_track = number_of_track == null ? "" : number_of_track;

            artistsBuilder.setArtist(artist);
            artistsBuilder.setArtistId(Integer.parseInt(artist_id));
            artistsBuilder.setNumberOfAlbums(number_of_albums);
            artistsBuilder.setNumberOfTracks(number_of_track);
            artists.add(artistsBuilder.build());
        }
        return artists;
    }

    private ContentValues[] getArtistValues(List<Artist> artists) {
        ContentValues[] valuesList = new ContentValues[artists.size()];
        ContentValues values;
        Artist artist;
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

    public BlunoteMessages.MetadataUpdate getMetadata() {
        BlunoteMessages.MetadataUpdate.Builder mdBuilder = BlunoteMessages.MetadataUpdate.newBuilder();
        mdBuilder.setAction(BlunoteMessages.MetadataUpdate.Action.ADD);
        mdBuilder.addAllAlbums(getAlbumMeta());
        mdBuilder.addAllArtists(getArtistMeta());
        mdBuilder.addAllSongs(getTrackMeta());
        mdBuilder.setOwner("SomeClient");
        return mdBuilder.build();
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

    private Cursor getTrackcursor() {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String where = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        final String album_id = MediaStore.Audio.Media.ALBUM_ID;
        final String artist_id = MediaStore.Audio.Media.ARTIST_ID;
        final String duration = MediaStore.Audio.Media.DURATION;
        final String song_id = MediaStore.Audio.Media._ID;
        final String title = MediaStore.Audio.Media.TITLE;
        final String track = MediaStore.Audio.Media.TRACK;
        final String year = MediaStore.Audio.Media.YEAR;
        final String[] columns = {album_id, artist_id, duration, song_id, title, track, year};
        return mContentResolver.query(uri, columns, where, null, null);
    }

    private ArrayList<BlunoteMessages.Song> getTrackMeta() {
        ArrayList<BlunoteMessages.Song> songs = new ArrayList<BlunoteMessages.Song>();
        Cursor cur = getTrackcursor();
        BlunoteMessages.Song.Builder songBuilder = BlunoteMessages.Song.newBuilder();

        String album_id, artist_id, duration, song_id, title, track, year;

        while (cur.moveToNext()) {
            album_id = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            artist_id = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
            duration = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DURATION));
            song_id = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media._ID));
            title = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
            track = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TRACK));
            year = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.YEAR));

            album_id = album_id == null ? "-1" : album_id;
            artist_id = artist_id == null ? "-1" : artist_id;
            duration = duration == null ? "" : duration;
            song_id = song_id == null ? "-1" : song_id;
            title = title == null ? "" : title;
            track = track == null ? "" : track;
            year = year == null ? "" : year;

            songBuilder.setAlbumId(Integer.parseInt(album_id));
            songBuilder.setArtistId(Integer.parseInt(artist_id));
            songBuilder.setDuration(duration);
            songBuilder.setSongId(Integer.parseInt(song_id));
            songBuilder.setTitle(title);
            songBuilder.setTrack(track);
            songBuilder.setYear(year);
            songs.add(songBuilder.build());
        }
        return songs;
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