package com.drexelsp.blunote.blunote;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by scantwell on 3/10/2016.
 */
public class Metadata implements MessageHandler {

    private String TAG = "Metadata";
    private ContentResolver mContentResolver;

    public Metadata(Context context) {
        mContentResolver = context.getContentResolver();
        /* Debugging Code
        MetadataUpdate metadataUpdate = this.getMetadata();
        Log.v(TAG, metadataUpdate.toString());
        Log.v(TAG, String.format("Metadata Bytes %d", metadataUpdate.toByteArray().length));
        Log.v(TAG, String.format("Number of songs %d", metadataUpdate.getSongsCount()));
        */
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

    private ArrayList<ContentValues> getAlbumValues(ArrayList<BlunoteMessages.Album> albums) {
        ArrayList<ContentValues> valuesList = new ArrayList<>();
        ContentValues values;
        for (BlunoteMessages.Album album : albums) {
            values = new ContentValues();
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

    private ArrayList<BlunoteMessages.Artist> getArtistMeta() {
        ArrayList<BlunoteMessages.Artist> artists = new ArrayList<BlunoteMessages.Artist>();
        Cursor cur = getArtistcursor();
        BlunoteMessages.Artist.Builder artistsBuilder = BlunoteMessages.Artist.newBuilder();

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

    private ArrayList<ContentValues> getArtistValues(ArrayList<BlunoteMessages.Artist> artists) {
        ArrayList<ContentValues> valuesList = new ArrayList<>();
        ContentValues values;
        for (BlunoteMessages.Artist artist : artists) {
            values = new ContentValues();
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

    private ArrayList<ContentValues> getSongValues(ArrayList<BlunoteMessages.Song> songs) {
        ArrayList<ContentValues> valuesList = new ArrayList<>();
        ContentValues values;
        for (BlunoteMessages.Song song : songs) {
            values = new ContentValues();
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
