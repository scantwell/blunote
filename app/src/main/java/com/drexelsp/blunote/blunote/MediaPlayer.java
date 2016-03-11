package com.drexelsp.blunote.blunote;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.DeliveryInfo;
import com.drexelsp.blunote.blunote.BlunoteMessages.SongFragment;
import com.drexelsp.blunote.blunote.BlunoteMessages.SongRequest;
import com.drexelsp.blunote.blunote.BlunoteMessages.WrapperMessage;
import com.drexelsp.blunote.blunote.BlunoteMessages.MetadataUpdate;
import com.drexelsp.blunote.blunote.BlunoteMessages.Artist;
import com.drexelsp.blunote.blunote.BlunoteMessages.Album;
import com.drexelsp.blunote.blunote.BlunoteMessages.Song;

import java.util.ArrayList;


/**
 * Created by scantwell on 2/16/2016.
 * Implements all media functionality of the Blunote application and handles all Network messages
 * from which SongRequests and SongFragments are acknowledged and generated respectively.
 */
public class MediaPlayer implements MessageHandler {
    private final String TAG = "MediaPlayer";
    private ContentResolver mContentResolver;
    public MediaPlayer(ContentResolver cResolver)
    {
        this.mContentResolver = cResolver;
        /* Debugging Code
        MetadataUpdate metadataUpdate = this.getMetadata();
        Log.v(TAG, metadataUpdate.toString());
        Log.v(TAG, String.format("Metadata Bytes %d", metadataUpdate.toByteArray().length));
        Log.v(TAG, String.format("Number of songs %d", metadataUpdate.getSongsCount()));
        */
    }

    private Cursor getAlbumcursor()
    {
        final Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        final String album = MediaStore.Audio.Albums.ALBUM;
        final String album_art = MediaStore.Audio.Albums.ALBUM_ART;
        final String album_id = MediaStore.Audio.Albums._ID;
        final String artist = MediaStore.Audio.Albums.ARTIST;
        final String first_year = MediaStore.Audio.Albums.FIRST_YEAR;
        final String last_year = MediaStore.Audio.Albums.LAST_YEAR;
        final String num_of_songs = MediaStore.Audio.Albums.NUMBER_OF_SONGS;
        final String[]columns={ album, album_art, album_id, artist, first_year, last_year, num_of_songs};
        return mContentResolver.query(uri, columns, null, null, null);
    }

    private ArrayList<Album> getAlbumMeta() {
        ArrayList<Album> albums = new ArrayList<Album>();
        Cursor cur = getAlbumcursor();
        Album.Builder albumsBuilder = Album.newBuilder();

        String album, album_art, album_id, artist, first_year, last_year, num_of_songs;

        while(cur.moveToNext())
        {
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


    private Cursor getArtistcursor()
    {
        final Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        final String artist = MediaStore.Audio.Artists.ARTIST;
        final String artist_id = MediaStore.Audio.Artists._ID;
        final String num_of_albums = MediaStore.Audio.Artists.NUMBER_OF_ALBUMS;
        final String num_of_tracks = MediaStore.Audio.Artists.NUMBER_OF_TRACKS;
        final String[]columns={artist_id, artist, num_of_albums, num_of_tracks};
        return mContentResolver.query(uri, columns, null, null, null);
    }

    private ArrayList<Artist> getArtistMeta() {
        ArrayList<Artist> artists = new ArrayList<Artist>();
        Cursor cur = getArtistcursor();
        Artist.Builder artistsBuilder = Artist.newBuilder();

        String artist, number_of_albums, number_of_track, artist_id;
        while(cur.moveToNext())
        {
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

    private Cursor getTrackcursor()
    {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String where = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        final String album_id = MediaStore.Audio.Media.ALBUM_ID;
        final String artist_id = MediaStore.Audio.Media.ARTIST_ID;
        final String duration = MediaStore.Audio.Media.DURATION;
        final String song_id = MediaStore.Audio.Media._ID;
        final String title = MediaStore.Audio.Media.TITLE;
        final String track = MediaStore.Audio.Media.TRACK;
        final String year = MediaStore.Audio.Media.YEAR;
        final String[]columns={album_id, artist_id, duration, song_id, title, track, year};
        return mContentResolver.query(uri, columns, where, null, null);
    }

    private ArrayList<Song> getTrackMeta() {
        ArrayList<Song> songs = new ArrayList<Song>();
        Cursor cur = getTrackcursor();
        Song.Builder songBuilder = Song.newBuilder();

        String album_id, artist_id, duration, song_id, title, track, year;

        while(cur.moveToNext())
        {
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

    public MetadataUpdate getMetadata()
    {
        MetadataUpdate.Builder mdBuilder = MetadataUpdate.newBuilder();
        mdBuilder.setAction(MetadataUpdate.Action.ADD);
        mdBuilder.addAllAlbums(getAlbumMeta());
        mdBuilder.addAllArtists(getArtistMeta());
        mdBuilder.addAllSongs(getTrackMeta());
        mdBuilder.setOwner("SomeClient");
        return mdBuilder.build();
    }

    @Override
    public boolean processMessage(DeliveryInfo dinfo, WrapperMessage message) {
        if (WrapperMessage.Type.SONG_FRAGMENT.equals(message.getType())) {
        } else if (WrapperMessage.Type.SONG_REQUEST.equals(message.getType())) {
        } else {
            Log.v(TAG, "Undefined message.");
        }
        return false;
    }


    private void processMessage(DeliveryInfo dinfo, SongRequest request) {
    }

    private void processMessage(DeliveryInfo dinfo, SongFragment frag) {
    }
}
