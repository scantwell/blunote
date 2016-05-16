package com.drexelsp.blunote.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.drexelsp.blunote.beans.ArtistViewAlbum;
import com.drexelsp.blunote.beans.ArtistViewTrack;
import com.drexelsp.blunote.blunote.Constants;
import com.drexelsp.blunote.blunote.R;
import com.drexelsp.blunote.provider.MetaStoreContract;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by U6020377 on 1/25/2016.
 */
public class ArtistViewActivity extends BaseBluNoteActivity implements ListView.OnItemClickListener {
    private static final String TAG = "ArtistViewActivity";

    ListView artistListView;
    TextView artistNameView;

    List<String> trackList;
    List<String> idList;
    ArrayAdapter trackListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        trackList = new ArrayList<>();
        idList = new ArrayList<>();

        artistNameView = (TextView) findViewById(R.id.artist_title);
        artistListView = (ListView) findViewById(R.id.artist_track_list);
        artistListView.setOnItemClickListener(this);

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
            Intent intent = new Intent(ArtistViewActivity.this, SongViewActivity.class);
            intent.putExtra("_id", idList.get(position));
            startActivity(intent);
    }

    public void populateArtistAlbums() {
        Intent intent = getIntent();
        Map<String, String> trackListMap = new LinkedHashMap<>();
        String artist_id = intent.getStringExtra("_id");
        String artistWhere = "_id = ?";
        String[] artistArgs = {artist_id};

        Cursor albumCursor = getContentResolver().query(MetaStoreContract.Album.CONTENT_URI,
                null, artistWhere, artistArgs, null);

        if (albumCursor != null && albumCursor.moveToFirst()) {
            String artist = albumCursor.getString(albumCursor.getColumnIndex("artist"));
            artistNameView.setText(artist);
            do {
                String albumName = albumCursor.getString(albumCursor.getColumnIndex("album"));

                String[] trackSelection = {"title", "track", "song_id"};
                String trackWhere = "album = ?";
                String[] trackArgs = {albumName};
                String trackSort = MetaStoreContract.Track.TRACK_NO + " ASC";
                Cursor trackCursor = getContentResolver().query(MetaStoreContract.Track.CONTENT_URI,
                        trackSelection, trackWhere, trackArgs, trackSort);

                if (trackCursor != null && trackCursor.moveToFirst()) {
                    String song, songID;
                    do {
                        song = trackCursor.getString(trackCursor.getColumnIndex(MetaStoreContract.Track.TITLE));
                        songID = Integer.toString(trackCursor.getInt(
                                trackCursor.getColumnIndex(MetaStoreContract.Track.SONG_ID)));
                        if (song != null) {
                            trackListMap.put(song, songID);
                        }
                    } while (trackCursor.moveToNext());
                    trackCursor.close();
                }
            } while (albumCursor.moveToNext());
            albumCursor.close();
        }
        setTrackList(trackListMap);
        trackListAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, trackList);
        artistListView.setAdapter(trackListAdapter);
    }

    public void setTrackList(Map<String, String> trackMap) {
        trackList.clear();
        idList.clear();

        for (Map.Entry<String, String> entry : trackMap.entrySet()) {
            trackList.add(entry.getKey());
            idList.add(entry.getValue());
        }
    }
}
