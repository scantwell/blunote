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
    }

    private Cursor getAlbumcursor()
    {
        String where = null;
        final Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        final String album = MediaStore.Audio.Albums.ALBUM;
        final String album_art = MediaStore.Audio.Albums.ALBUM_ART;
        final String album_id = MediaStore.Audio.Albums.ALBUM_ID;
        final String artist = MediaStore.Audio.Albums.ARTIST;
        final String first_year = MediaStore.Audio.Albums.FIRST_YEAR;
        final String last_year = MediaStore.Audio.Albums.LAST_YEAR;
        final String num_of_songs = MediaStore.Audio.Albums.NUMBER_OF_SONGS;
        final String num_of_song_for_artist = MediaStore.Audio.Albums.NUMBER_OF_SONGS_FOR_ARTIST;
        final String[]columns={ album, album_art, album_id, artist, first_year, last_year, num_of_songs, num_of_song_for_artist};
        return mContentResolver.query(uri,columns,where,null, null);
    }

    private ArrayList<Album> getAlbumMeta() {
        ArrayList<Album> albums = new ArrayList<Album>();
        Cursor cur = getAlbumcursor();
        Album.Builder albumsBuilder = Album.newBuilder();
        while(cur.moveToNext())
        {
            albumsBuilder.setAlbum(cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
            albumsBuilder.setAlbumArt(cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));
            albumsBuilder.setAlbumId(Integer.parseInt(cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID))));
            albumsBuilder.setArtist(cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
            albumsBuilder.setFirstYear(cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.FIRST_YEAR)));
            albumsBuilder.setLastYear(cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.LAST_YEAR)));
            albumsBuilder.setNumberOfSongs(cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));
            albumsBuilder.setNumberOfSongsForArtist(cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS_FOR_ARTIST)));
            albums.add(albumsBuilder.build());
        }
        return albums;
    }


    private Cursor getArtistcursor()
    {
        String where = null;
        final Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        final String artist = MediaStore.Audio.Artists.ARTIST;
        final String artist_id = MediaStore.Audio.Artists._ID;
        final String num_of_albums = MediaStore.Audio.Artists.NUMBER_OF_ALBUMS;
        final String num_of_tracks = MediaStore.Audio.Artists.NUMBER_OF_TRACKS;
        final String[]columns={artist_id, artist, num_of_albums, num_of_tracks};
        return mContentResolver.query(uri,columns,where,null, null);
    }

    private ArrayList<Artist> getArtistMeta() {
        ArrayList<Artist> artists = new ArrayList<Artist>();
        Cursor cur = getArtistcursor();
        Artist.Builder artistsBuilder = Artist.newBuilder();
        while(cur.moveToNext())
        {
            artistsBuilder.setArtist(cur.getString(cur.getColumnIndex(MediaStore.Audio.Artists.ARTIST)));
            artistsBuilder.setArtistId(Integer.parseInt(cur.getString(cur.getColumnIndex(MediaStore.Audio.Artists._ID))));
            artistsBuilder.setNumberOfAlbums(Integer.parseInt(cur.getString(cur.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS))));
            artistsBuilder.setNumberOfTracks(Integer.parseInt(cur.getString(cur.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS))));
            artists.add(artistsBuilder.build());
        }
        return artists;
    }

    private Cursor getTrackcursor()
    {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String album_id = MediaStore.Audio.Media.ALBUM_ID;
        final String artist_id = MediaStore.Audio.Media.ARTIST_ID;
        final String duration = MediaStore.Audio.Media.DURATION;
        final String song_id = MediaStore.Audio.Media._ID;
        final String title = MediaStore.Audio.Media.TITLE;
        final String track = MediaStore.Audio.Media.TRACK;
        final String year = MediaStore.Audio.Media.YEAR;
        final String[]columns={album_id, artist_id, duration, song_id, title, track, year};
        return mContentResolver.query(uri,columns,null,null,null);
    }

    private ArrayList<Song> getTrackMeta() {
        ArrayList<Song> songs = new ArrayList<Song>();
        Cursor cur = getTrackcursor();
        Song.Builder songBuilder = Song.newBuilder();
        while(cur.moveToNext())
        {
            songBuilder.setAlbumId(Integer.parseInt(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))));
            songBuilder.setArtistId(Integer.parseInt(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))));
            songBuilder.setDuration(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DURATION)));
            songBuilder.setSongId(Integer.parseInt(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media._ID))));
            songBuilder.setTitle(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            songBuilder.setTrack(Integer.parseInt(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TRACK))));
            songBuilder.setYear(cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.YEAR)));
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
