package com.drexelsp.blunote.blunote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.drexelsp.blunote.blunote.BlunoteMessages.Artist;
import com.drexelsp.blunote.provider.MetaStoreContract;
import com.google.protobuf.ByteString;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Created by scantwell on 3/10/2016.
 */
public class Metadata {

    private String TAG = "Metadata";
    private ContentResolver mContentResolver;

    private static final ContentValues[] EMPTY_CONTENT_ARRAY = new ContentValues[0];

    public Metadata(Context context) {

        mContentResolver = context.getContentResolver();
        BlunoteMessages.MetadataUpdate metadata = getMetadata(context);
        this.addMetadata(metadata);
    }

    public BlunoteMessages.MetadataUpdate getMetadata(Context context) {
        BlunoteMessages.MetadataUpdate.Builder mdBuilder = BlunoteMessages.MetadataUpdate.newBuilder();
        mdBuilder.setAction(BlunoteMessages.MetadataUpdate.Action.ADD);
        mdBuilder.addAllAlbums(getAlbumMeta());
        mdBuilder.addAllArtists(getArtistMeta());
        mdBuilder.addAllSongs(getTrackMeta());
        mdBuilder.setOwner(PreferenceManager.getDefaultSharedPreferences(context).getString(
                "pref_key_user_name", BluetoothAdapter.getDefaultAdapter().getName()));
        mdBuilder.setUserId(BluetoothAdapter.getDefaultAdapter().getAddress());
        return mdBuilder.build();
    }

    public String getSongCount()
    {
        String[] projection = new String[]{MetaStoreContract.Track.TITLE};
        Cursor mediaCursor = mContentResolver.query(MetaStoreContract.Track.CONTENT_URI, projection, null, null, null);
        return Integer.toString(mediaCursor.getCount());
    }

    public BlunoteMessages.MetadataUpdate addHostMetadata(BlunoteMessages.MetadataUpdate message)
    {
        BlunoteMessages.MetadataUpdate.Builder builder = BlunoteMessages.MetadataUpdate.newBuilder();
        builder.setAction(message.getAction());
        builder.setUserId(message.getUserId());
        builder.setOwner(message.getOwner());

        ContentValues[] songs = removeSongAddDuplicates(getSongValues(message.getSongsList()));
        ContentValues[] artists = removeArtistAddDuplicates(getArtistValues(message.getArtistsList()));
        ContentValues[] albums = removeAlbumAddDuplicates(getAlbumValues(message.getAlbumsList()));
        ContentValues[] user_tracks = getUserTracks(message.getSongsList(), message.getUserId());
        insertNewUser(message.getOwner(), message.getUserId()/* Should add latency here when implemented */);
        mContentResolver.bulkInsert(MetaStoreContract.Track.CONTENT_URI, songs);
        mContentResolver.bulkInsert(MetaStoreContract.Artist.CONTENT_URI, artists);
        mContentResolver.bulkInsert(MetaStoreContract.Album.CONTENT_URI, albums);
        mContentResolver.bulkInsert(MetaStoreContract.UserTracks.CONTENT_URI, user_tracks);

        builder.addAllSongs(rebuildSongs(songs));
        builder.addAllArtists(rebuildArtists(artists));
        builder.addAllAlbums(rebuildAlbums(albums));

        return builder.build();
    }

    private ContentValues[] removeSongAddDuplicates(ContentValues[] songs){
        List<ContentValues> songList = new ArrayList<>();
        Collections.addAll(songList, songs);

        Iterator<ContentValues> iterator = songList.iterator();
        while (iterator.hasNext()){
            ContentValues song = iterator.next();
            Cursor c = checkAddSong(song.get(MetaStoreContract.Track.TITLE), song.get(MetaStoreContract.Track.ALBUM),
                    song.get(MetaStoreContract.Track.ARTIST));
            if (c.moveToFirst()){
                iterator.remove();
            }
            c.close();
        }

        return songList.toArray(EMPTY_CONTENT_ARRAY);
    }

    private ContentValues[] removeArtistAddDuplicates(ContentValues[] artists){
        List<ContentValues> artistList = new ArrayList<>();
        Collections.addAll(artistList, artists);

        Iterator<ContentValues> iterator = artistList.iterator();
        while (iterator.hasNext()) {
            ContentValues artist = iterator.next();
            Cursor c = checkAddArtist(artist.get(MetaStoreContract.Artist.ARTIST));
            if (c.moveToFirst()){
                iterator.remove();
            }
            c.close();
        }

        return artistList.toArray(EMPTY_CONTENT_ARRAY);
    }

    private ContentValues[] removeAlbumAddDuplicates(ContentValues[] albums){
        List<ContentValues> albumList = new ArrayList<>();
        Collections.addAll(albumList, albums);

        Iterator<ContentValues> iterator = albumList.iterator();
        while (iterator.hasNext()) {
            ContentValues album = iterator.next();
            Cursor c = checkAddAlbum(album.get(MetaStoreContract.Album.ALBUM),
                    album.get(MetaStoreContract.Album.ARTIST));
            if (c.moveToFirst()) {
                iterator.remove();
            }
            c.close();
        }

        return albumList.toArray(EMPTY_CONTENT_ARRAY);
    }

    private List<BlunoteMessages.Song> rebuildSongs(ContentValues[] addedSongs){
        List<BlunoteMessages.Song> rebuiltSongs = new ArrayList<>();
        BlunoteMessages.Song.Builder builder;
        for (ContentValues values : addedSongs) {
            builder = BlunoteMessages.Song.newBuilder();
            builder.setAlbum(((String) values.get(MetaStoreContract.Track.ALBUM)));
            builder.setArtist(((String) values.get(MetaStoreContract.Track.ARTIST)));
            builder.setDuration(((String) values.get(MetaStoreContract.Track.DURATION)));
            builder.setSongId(((Long) values.get(MetaStoreContract.Track.SONG_ID)));
            builder.setTitle(((String) values.get(MetaStoreContract.Track.TITLE)));
            builder.setTrack(((String) values.get(MetaStoreContract.Track.TRACK_NO)));
            builder.setYear(((String) values.get(MetaStoreContract.Track.YEAR)));
            rebuiltSongs.add(builder.build());
        }
        return rebuiltSongs;
    }

    private List<BlunoteMessages.Artist> rebuildArtists(ContentValues[] addedArtists){
        List<BlunoteMessages.Artist> rebuiltArtists = new ArrayList<>();
        BlunoteMessages.Artist.Builder builder;
        for (ContentValues values : addedArtists) {
            builder = BlunoteMessages.Artist.newBuilder();
            builder.setArtist(((String) values.get(MetaStoreContract.Artist.ARTIST)));
            builder.setNumberOfAlbums(((String) values.get(MetaStoreContract.Artist.NUMBER_OF_ALBUMS)));
            builder.setNumberOfTracks(((String) values.get(MetaStoreContract.Artist.NUMBER_OF_TRACKS)));
            rebuiltArtists.add(builder.build());
        }
        return rebuiltArtists;
    }

    private List<BlunoteMessages.Album> rebuildAlbums(ContentValues[] addedAlbums){
        List<BlunoteMessages.Album> rebuiltAlbums = new ArrayList<>();
        BlunoteMessages.Album.Builder builder = BlunoteMessages.Album.newBuilder();
        for (ContentValues values : addedAlbums) {
            builder.setAlbum(((String) values.get(MetaStoreContract.Album.ALBUM)));
            builder.setAlbumArt(ByteString.copyFrom(
                    ((byte[]) values.get(MetaStoreContract.Album.ALBUM_ART))));
            builder.setArtist(((String) values.get(MetaStoreContract.Album.ARTIST)));
            builder.setFirstYear(((String) values.get(MetaStoreContract.Album.FIRST_YEAR)));
            builder.setLastYear(values.get(MetaStoreContract.Album.LAST_YEAR).toString());
            builder.setNumberOfSongs(((String) values.get(MetaStoreContract.Album.NUMBER_OF_SONGS)));
            rebuiltAlbums.add(builder.build());
        }
        return rebuiltAlbums;
    }

    private Cursor checkAddSong(Object song, Object album, Object artist){
        String[] selectionArgs = new String[]{((String) song), ((String) album), ((String) artist)};
        String where = "track=? AND album=? AND artist=?";
        return mContentResolver.query(MetaStoreContract.Track.CONTENT_URI, null, where, selectionArgs, null);
    }

    private Cursor checkAddArtist(Object artist){
        String[] selectionArgs = new String[]{((String) artist)};
        String where = "artist=?";
        return mContentResolver.query(MetaStoreContract.Artist.CONTENT_URI, null, where, selectionArgs, null);
    }

    private Cursor checkAddAlbum(Object album, Object artist){
        String[] selectionArgs = new String[]{((String) album), ((String) artist)};
        String where = "album=? AND artist=?";
        return mContentResolver.query(MetaStoreContract.Album.CONTENT_URI, null, where, selectionArgs, null);
    }

    public BlunoteMessages.MetadataUpdate deleteHostMetadata(BlunoteMessages.MetadataUpdate message)
    {
        BlunoteMessages.MetadataUpdate.Builder builder = BlunoteMessages.MetadataUpdate.newBuilder();
        builder.setAction(message.getAction());
        builder.setUserId(message.getUserId());
        builder.setOwner(message.getOwner());

        deleteUserAndTracks(message.getOwner(), message.getUserId());
        List<BlunoteMessages.Song> songs = findSongDeletions();
        List<BlunoteMessages.Artist> artists = findArtistDeletions();
        List<BlunoteMessages.Album> albums = findAlbumDeletions();

        deleteSongs(songs);
        deleteArtists(artists);
        deleteAlbums(albums);

        builder.addAllSongs(songs);
        builder.addAllArtists(artists);
        builder.addAllAlbums(albums);

        return builder.build();
    }

    private List<BlunoteMessages.Song> findSongDeletions() {
        Cursor c = mContentResolver.query(MetaStoreContract.SONG_DELETION_URI, null, null, null, null);
        List<BlunoteMessages.Song> songList = new ArrayList<>();

        while (c.moveToNext()) {
            BlunoteMessages.Song.Builder builder = BlunoteMessages.Song.newBuilder();
            builder.setAlbum(c.getString(c.getColumnIndex(MetaStoreContract.Track.ALBUM)));
            builder.setArtist(c.getString(c.getColumnIndex(MetaStoreContract.Track.ARTIST)));
            builder.setDuration(c.getString(c.getColumnIndex(MetaStoreContract.Track.DURATION)));
            builder.setSongId(c.getInt(c.getColumnIndex(MetaStoreContract.Track.SONG_ID)));
            builder.setTitle(c.getString(c.getColumnIndex(MetaStoreContract.Track.TITLE)));
            builder.setTrack(c.getString(c.getColumnIndex(MetaStoreContract.Track.TRACK_NO)));
            builder.setYear(c.getString(c.getColumnIndex(MetaStoreContract.Track.YEAR)));
            songList.add(builder.build());
        }

        return songList;
    }

    private List<BlunoteMessages.Artist> findArtistDeletions() {
        Cursor c = mContentResolver.query(MetaStoreContract.ARTIST_DELETION_URI, null, null, null, null);
        List<BlunoteMessages.Artist> artistList = new ArrayList<>();

        while (c.moveToNext()) {
            BlunoteMessages.Artist.Builder builder = BlunoteMessages.Artist.newBuilder();
            builder.setArtist(c.getString(c.getColumnIndex(MetaStoreContract.Artist.ARTIST)));
            builder.setNumberOfAlbums(c.getString(c.getColumnIndex(MetaStoreContract.Artist.NUMBER_OF_ALBUMS)));
            builder.setNumberOfTracks(c.getString(c.getColumnIndex(MetaStoreContract.Artist.NUMBER_OF_TRACKS)));
            artistList.add(builder.build());
        }

        return artistList;
    }

    private List<BlunoteMessages.Album> findAlbumDeletions() {
        Cursor c = mContentResolver.query(MetaStoreContract.ALBUM_DELETION_URI, null, null, null, null);
        List<BlunoteMessages.Album> albumList = new ArrayList<>();

        while (c.moveToNext()) {
            BlunoteMessages.Album.Builder builder = BlunoteMessages.Album.newBuilder();
            builder.setAlbum(c.getString(c.getColumnIndex(MetaStoreContract.Album.ALBUM)));
            builder.setAlbumArt(ByteString.copyFrom(c.getBlob(c.getColumnIndex(MetaStoreContract.Album.ALBUM_ART))));
            builder.setArtist(c.getString(c.getColumnIndex(MetaStoreContract.Album.ARTIST)));
            builder.setFirstYear(c.getString(c.getColumnIndex(MetaStoreContract.Album.FIRST_YEAR)));
            builder.setLastYear(c.getString(c.getColumnIndex(MetaStoreContract.Album.LAST_YEAR)));
            builder.setNumberOfSongs(c.getString(c.getColumnIndex(MetaStoreContract.Album.NUMBER_OF_SONGS)));
            albumList.add(builder.build());
        }

        return albumList;
    }

    public BlunoteMessages.MetadataUpdate getDownstreamMetadata()
    {
        BlunoteMessages.MetadataUpdate.Builder mdBuilder = BlunoteMessages.MetadataUpdate.newBuilder();
        mdBuilder.setAction(BlunoteMessages.MetadataUpdate.Action.ADD);
        mdBuilder.addAllAlbums(getAlbumMetaForServer());
        mdBuilder.addAllArtists(getArtistMetaForServer());
        mdBuilder.addAllSongs(getTrackMetaForServer());
        mdBuilder.setOwner("");
        mdBuilder.setUserId(BluetoothAdapter.getDefaultAdapter().getAddress());
        return mdBuilder.build();
    }

    public List<BlunoteMessages.Album> getAlbumMetaForServer() {
        ArrayList<BlunoteMessages.Album> albums = new ArrayList<BlunoteMessages.Album>();
        Cursor cur = mContentResolver.query(MetaStoreContract.Album.CONTENT_URI,
                MetaStoreContract.Album.PROJECTION_ALL, null, null, null);
        BlunoteMessages.Album.Builder albumsBuilder = BlunoteMessages.Album.newBuilder();

        String album, artist, first_year, last_year, num_of_songs;
        byte[] album_art;
        byte[] album_art_bytes;

        while (cur.moveToNext()) {
            album = cur.getString(cur.getColumnIndex(MetaStoreContract.Album.ALBUM));
            album_art = cur.getBlob(cur.getColumnIndex(MetaStoreContract.Album.ALBUM_ART));
            artist = cur.getString(cur.getColumnIndex(MetaStoreContract.Album.ARTIST));
            first_year = cur.getString(cur.getColumnIndex(MetaStoreContract.Album.FIRST_YEAR));
            last_year = cur.getString(cur.getColumnIndex(MetaStoreContract.Album.LAST_YEAR));
            num_of_songs = cur.getString(cur.getColumnIndex(MetaStoreContract.Album.NUMBER_OF_SONGS));

            album = album == null ? "" : album;
            artist = artist == null ? "" : artist;
            first_year = first_year == null ? "" : first_year;
            last_year = last_year == null ? "" : last_year;
            num_of_songs = num_of_songs == null ? "" : num_of_songs;

            //Temp Disable album art
            //album_art_bytes = getAlbumArt(album_art);
            album_art_bytes = new byte[0];

            albumsBuilder.setAlbum(album);
            albumsBuilder.setAlbumArt(ByteString.copyFrom(album_art_bytes));
            albumsBuilder.setArtist(artist);
            albumsBuilder.setFirstYear(first_year);
            albumsBuilder.setLastYear(last_year);
            albumsBuilder.setNumberOfSongs(num_of_songs);
            albums.add(albumsBuilder.build());
        }
        cur.close();
        return albums;
    }

    public List<BlunoteMessages.Artist> getArtistMetaForServer() {
        ArrayList<Artist> artists = new ArrayList<Artist>();
        Cursor cur = mContentResolver.query(MetaStoreContract.Artist.CONTENT_URI,
                MetaStoreContract.Artist.PROJECTION_ALL, null, null, null);
        Artist.Builder artistsBuilder = Artist.newBuilder();

        String artist, number_of_albums, number_of_track;
        while (cur.moveToNext()) {
            artist = cur.getString(cur.getColumnIndex(MetaStoreContract.Artist.ARTIST));
            number_of_albums = cur.getString(cur.getColumnIndex(MetaStoreContract.Artist.NUMBER_OF_ALBUMS));
            number_of_track = cur.getString(cur.getColumnIndex(MetaStoreContract.Artist.NUMBER_OF_TRACKS));

            artist = artist == null ? "" : artist;
            number_of_albums = number_of_albums == null ? "" : number_of_albums;
            number_of_track = number_of_track == null ? "" : number_of_track;

            artistsBuilder.setArtist(artist);
            artistsBuilder.setNumberOfAlbums(number_of_albums);
            artistsBuilder.setNumberOfTracks(number_of_track);
            artists.add(artistsBuilder.build());
        }
        cur.close();
        return artists;
    }

    public List<BlunoteMessages.Song> getTrackMetaForServer() {
        ArrayList<BlunoteMessages.Song> songs = new ArrayList<BlunoteMessages.Song>();
        Cursor cur = mContentResolver.query(MetaStoreContract.Track.CONTENT_URI,
                MetaStoreContract.Track.PROJECTION_ALL, null, null, null);
        BlunoteMessages.Song.Builder songBuilder = BlunoteMessages.Song.newBuilder();

        String album, artist, duration, song_id, title, track, year;

        while (cur.moveToNext()) {
            album = cur.getString(cur.getColumnIndex(MetaStoreContract.Track.ALBUM));
            artist = cur.getString(cur.getColumnIndex(MetaStoreContract.Track.ARTIST));
            duration = cur.getString(cur.getColumnIndex(MetaStoreContract.Track.DURATION));
            song_id = cur.getString(cur.getColumnIndex(MetaStoreContract.Track._ID));
            title = cur.getString(cur.getColumnIndex(MetaStoreContract.Track.TITLE));
            track = cur.getString(cur.getColumnIndex(MetaStoreContract.Track.TRACK_NO));
            year = cur.getString(cur.getColumnIndex(MetaStoreContract.Track.YEAR));

            album = album == null ? "" : album;
            artist = artist == null ? "" : artist;
            duration = duration == null ? "" : duration;
            song_id = song_id == null ? "-1" : song_id;
            title = title == null ? "" : title;
            track = track == null ? "" : track;
            year = year == null ? "" : year;

            songBuilder.setAlbum(album);
            songBuilder.setArtist(artist);
            songBuilder.setDuration(duration);
            songBuilder.setSongId(Integer.parseInt(song_id));
            songBuilder.setTitle(title);
            songBuilder.setTrack(track);
            songBuilder.setYear(year);
            songs.add(songBuilder.build());
        }
        cur.close();
        return songs;
    }

    public void addMetadata(BlunoteMessages.MetadataUpdate message) {
        ContentValues[] songs = getSongValues(message.getSongsList());
        ContentValues[] artists = getArtistValues(message.getArtistsList());
        ContentValues[] albums = getAlbumValues(message.getAlbumsList());
        ContentValues[] user_tracks = getUserTracks(message.getSongsList(), message.getUserId());
        insertNewUser(message.getOwner(), message.getUserId()/* Should add latency here when implemented */);
        mContentResolver.bulkInsert(MetaStoreContract.Track.CONTENT_URI, songs);
        mContentResolver.bulkInsert(MetaStoreContract.Artist.CONTENT_URI, artists);
        mContentResolver.bulkInsert(MetaStoreContract.Album.CONTENT_URI, albums);
        mContentResolver.bulkInsert(MetaStoreContract.UserTracks.CONTENT_URI, user_tracks);
    }

    private void deleteAlbums(List<BlunoteMessages.Album> albums) {
        for (int i = 0; i < albums.size(); ++i) {
            String[] selectionArgs = new String[]{albums.get(i).getAlbum(), albums.get(i).getArtist()};
            mContentResolver.delete(MetaStoreContract.Album.CONTENT_URI, "album=? AND artist=?", selectionArgs);
        }
    }

    private void deleteArtists(List<Artist> artists) {
        String[] selectionArgs = new String[artists.size()];
        for (int i = 0; i < artists.size(); ++i) {
            selectionArgs[i] = artists.get(i).getArtist();
        }
        mContentResolver.delete(MetaStoreContract.Album.CONTENT_URI, "artist=?", selectionArgs);
    }

    private void deleteSongs(List<BlunoteMessages.Song> songs) {
        for (int i = 0; i < songs.size(); ++i) {
            String[] selectionArgs = new String[]{songs.get(i).getTitle(), songs.get(i).getAlbum(), songs.get(i).getArtist()};
            mContentResolver.delete(MetaStoreContract.Album.CONTENT_URI, "title=? AND album=? AND artist=?", selectionArgs);
        }
    }

    public void deleteUserAndTracks(String username, String user_id){
        String[] userTracksSelection = new String[]{user_id};
        mContentResolver.delete(MetaStoreContract.UserTracks.CONTENT_URI, "user_id=?", userTracksSelection);
        String[] userSelection = new String[]{username};
        mContentResolver.delete(MetaStoreContract.User.CONTENT_URI, "username=?", userSelection);
    }

    public void deleteMetadata(BlunoteMessages.MetadataUpdate message) {
        deleteAlbums(message.getAlbumsList());
        deleteArtists(message.getArtistsList());
        deleteSongs(message.getSongsList());
        deleteUserAndTracks(message.getOwner(), message.getUserId());

    }

    private Cursor getAlbumcursor() {
        final Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        final String album = MediaStore.Audio.Albums.ALBUM;
        final String album_art = MediaStore.Audio.Albums.ALBUM_ART;
        final String artist = MediaStore.Audio.Albums.ARTIST;
        final String first_year = MediaStore.Audio.Albums.FIRST_YEAR;
        final String last_year = MediaStore.Audio.Albums.LAST_YEAR;
        final String num_of_songs = MediaStore.Audio.Albums.NUMBER_OF_SONGS;
        final String[] columns = {album, album_art, artist, first_year, last_year, num_of_songs};
        return mContentResolver.query(uri, columns, null, null, null);
    }

    private ArrayList<BlunoteMessages.Album> getAlbumMeta() {
        ArrayList<BlunoteMessages.Album> albums = new ArrayList<BlunoteMessages.Album>();
        Cursor cur = getAlbumcursor();
        BlunoteMessages.Album.Builder albumsBuilder = BlunoteMessages.Album.newBuilder();

        String album, album_art, artist, first_year, last_year, num_of_songs;
        byte[] album_art_bytes;

        while (cur != null && cur.moveToNext()) {
            album = cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
            album_art = cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
            first_year = cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.FIRST_YEAR));
            last_year = cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.LAST_YEAR));
            num_of_songs = cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS));

            album = album == null ? "" : album;
            album_art = album_art == null ? "" : album_art;
            artist = artist == null ? "" : artist;
            first_year = first_year == null ? "" : first_year;
            last_year = last_year == null ? "" : last_year;
            num_of_songs = num_of_songs == null ? "" : num_of_songs;

            //Temp Disable album art
            //album_art_bytes = getAlbumArt(album_art);
            album_art_bytes = new byte[0];

            albumsBuilder.setAlbum(album);
            albumsBuilder.setAlbumArt(ByteString.copyFrom(album_art_bytes));
            albumsBuilder.setArtist(artist);
            albumsBuilder.setFirstYear(first_year);
            albumsBuilder.setLastYear(last_year);
            albumsBuilder.setNumberOfSongs(num_of_songs);
            albums.add(albumsBuilder.build());
        }
        if (cur != null) {
            cur.close();
        }
        return albums;
    }

    private byte[] getAlbumArt(String uri) {
        if (!uri.isEmpty()) {
            try {
                File file = new File(uri);
                Log.v(TAG, String.format("Byte array size %d", file.length()));
                FileInputStream fis = new FileInputStream(file);
                byte[] ba = new byte[(int) file.length()];
                fis.read(ba);
                fis.close();

                return ba;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return new byte[0];
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }

    private ContentValues[] getAlbumValues(List<BlunoteMessages.Album> albums) {
        ContentValues[] valuesList = new ContentValues[albums.size()];
        ContentValues values;
        BlunoteMessages.Album album;
        for (int i = 0; i < albums.size(); ++i) {
            album = albums.get(i);
            values = new ContentValues();
            values.put(MetaStoreContract.Album.ALBUM, album.getAlbum());
            values.put(MetaStoreContract.Album.ALBUM_ART, album.getAlbumArt().toByteArray());
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
        while (cur != null && cur.moveToNext()) {
            artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            artist_id = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media._ID));
            number_of_albums = cur.getString(cur.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS));
            number_of_track = cur.getString(cur.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));

            artist = artist == null ? "" : artist;
            artist_id = artist_id == null ? "-1" : artist_id;
            number_of_albums = number_of_albums == null ? "" : number_of_albums;
            number_of_track = number_of_track == null ? "" : number_of_track;

            artistsBuilder.setArtist(artist);
            artistsBuilder.setNumberOfAlbums(number_of_albums);
            artistsBuilder.setNumberOfTracks(number_of_track);
            artists.add(artistsBuilder.build());
        }
        if (cur != null) {
            cur.close();
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
            values.put(MetaStoreContract.Track.ALBUM, song.getAlbum());
            values.put(MetaStoreContract.Track.ARTIST, song.getArtist());
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
        final String album = MediaStore.Audio.Media.ALBUM;
        final String artist = MediaStore.Audio.Media.ARTIST;
        final String duration = MediaStore.Audio.Media.DURATION;
        final String song_id = MediaStore.Audio.Media._ID;
        final String title = MediaStore.Audio.Media.TITLE;
        final String track = MediaStore.Audio.Media.TRACK;
        final String year = MediaStore.Audio.Media.YEAR;
        final String[] columns = {album, artist, duration, song_id, title, track, year};
        return mContentResolver.query(uri, columns, where, null, null);
    }

    private ArrayList<BlunoteMessages.Song> getTrackMeta() {
        ArrayList<BlunoteMessages.Song> songs = new ArrayList<BlunoteMessages.Song>();
        Cursor cur = getTrackcursor();
        BlunoteMessages.Song.Builder songBuilder = BlunoteMessages.Song.newBuilder();

        String album, artist, duration, song_id, title, track, year;

        while (cur != null && cur.moveToNext()) {
            album = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            duration = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DURATION));
            song_id = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media._ID));
            title = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
            track = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TRACK));
            year = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.YEAR));

            album = album == null ? "" : album;
            artist = artist == null ? "" : artist;
            duration = duration == null ? "" : duration;
            song_id = song_id == null ? "-1" : song_id;
            title = title == null ? "" : title;
            track = track == null ? "" : track;
            year = year == null ? "" : year;

            songBuilder.setAlbum(album);
            songBuilder.setArtist(artist);
            songBuilder.setDuration(duration);
            songBuilder.setSongId(Integer.parseInt(song_id));
            songBuilder.setTitle(title);
            songBuilder.setTrack(track);
            songBuilder.setYear(year);
            songs.add(songBuilder.build());
        }
        if (cur != null) {
            cur.close();
        }
        return songs;
    }

    private void insertNewUser(String user, String user_id) {
        ContentValues values = new ContentValues();
        values.put(MetaStoreContract.User.USERNAME, user);
        values.put(MetaStoreContract.User.USER_ID, user_id);
        values.put(MetaStoreContract.User.LATENCY, 0);
        mContentResolver.insert(MetaStoreContract.User.CONTENT_URI, values);
    }

    private ContentValues[] getUserTracks(List<BlunoteMessages.Song> songs, String user_id) {
        ContentValues[] valuesList = new ContentValues[songs.size()];
        ContentValues values;
        BlunoteMessages.Song song;
        for (int i = 0; i < songs.size(); ++i) {
            song = songs.get(i);
            values = new ContentValues();
            values.put(MetaStoreContract.UserTracks.USER_ID, user_id);
            values.put(MetaStoreContract.UserTracks.ALBUM, song.getAlbum());
            values.put(MetaStoreContract.UserTracks.ARTIST, song.getArtist());
            values.put(MetaStoreContract.UserTracks.TITLE, song.getTitle());
            valuesList[i] = values;
        }
        return valuesList;
    }
}
