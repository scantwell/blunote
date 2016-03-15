package com.drexelsp.blunote.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.drexelsp.blunote.adapters.ArtistViewAdapter;
import com.drexelsp.blunote.beans.ArtistViewAlbum;
import com.drexelsp.blunote.beans.ArtistViewTrack;
import com.drexelsp.blunote.blunote.Constants;
import com.drexelsp.blunote.blunote.R;
import com.drexelsp.blunote.provider.MetaStoreContract;

/**
 * Created by U6020377 on 1/25/2016.
 */
public class ArtistViewActivity extends BaseBluNoteActivity implements ListView.OnItemClickListener {
    ListView artistListView;
    ArtistViewAdapter artistViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        artistListView = (ListView) findViewById(R.id.artist_list);
        artistListView.setOnItemClickListener(this);

        artistViewAdapter = new ArtistViewAdapter(getCurrentContext());

        populateArtistAlbums();
    }

    @Override
    public Context getCurrentContext() {
        return ArtistViewActivity.this;
    }

    @Override
    public int getViewConstant() {
        return Constants.ACTIVITY_ARTIST_VIEW;
    }

    @Override
    public boolean showMusicMenuItems() {
        return true;
    }

    @Override
    public boolean showSearchMenuItem() {
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object itemClicked = artistViewAdapter.getItem(position);
        if (itemClicked instanceof ArtistViewTrack){
            Intent intent = new Intent(ArtistViewActivity.this, SongViewActivity.class);
            intent.putExtra("_id", ((ArtistViewTrack) itemClicked).getSong_id());
            startActivity(intent);
        }
    }

    public void populateArtistAlbums() {
        Intent intent = getIntent();
        String artist = intent.getStringExtra("artist");
        String albumSelection[] = {};
        String albumWhere = "artist = ?";
        String[] albumArgs = {artist};

        Cursor albumCursor = getContentResolver().query(MetaStoreContract.Album.CONTENT_URI,
                albumSelection, albumWhere, albumArgs, null);

        if (albumCursor != null && albumCursor.moveToFirst()) {
            do {
                ArtistViewAlbum album = new ArtistViewAlbum();
                String albumName = albumCursor.getString(albumCursor.getColumnIndex("album"));
                album.setAlbumName(albumName);
                album.setNumberOfTracks(albumCursor.getString(
                        albumCursor.getColumnIndex("number_of_tracks")));
                album.setAlbumYear(albumCursor.getString(albumCursor.getColumnIndex("first_year")));

                byte[] albumArt = albumCursor.getBlob(albumCursor.getColumnIndex("album_art"));
                Bitmap bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.length);
                album.setAlbumArtwork(bitmap);

                artistViewAdapter.addAlbum(album);

                String[] trackSelection = {"title", "track", "song_id"};
                String trackWhere = "album = ?";
                String[] trackArgs = {albumName};
                String trackSort = MetaStoreContract.Track.TRACK_NO + " ASC";
                Cursor trackCursor = getContentResolver().query(MetaStoreContract.Track.CONTENT_URI,
                        trackSelection, trackWhere, trackArgs, trackSort);

                if (trackCursor != null && trackCursor.moveToFirst()) {
                    do {
                        ArtistViewTrack track = new ArtistViewTrack();
                        String song = trackCursor.getString(trackCursor.getColumnIndex(
                                MetaStoreContract.Track.TITLE));
                        String songID = Integer.toString(trackCursor.getInt(
                                trackCursor.getColumnIndex(MetaStoreContract.Track.SONG_ID)));
                        if(song != null){
                            track.setTrackName(song);
                            track.setSong_id(songID);
                        }

                        artistViewAdapter.addTrack(track);
                    } while (trackCursor.moveToNext());

                    trackCursor.close();
                }
            } while (albumCursor.moveToNext());

            albumCursor.close();
        }
    }
}
