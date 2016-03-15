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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
public class AlbumViewActivity extends BaseBluNoteActivity implements ListView.OnItemClickListener {
    private static final String TAG = "Album View Activity";
    ImageView albumArtwork;
    TextView albumNameView;
    TextView artistName;
    TextView numberOfTracks;
    TextView albumYear;
    ListView trackListView;

    List<String> trackList;
    List<String> idList;
    ArrayAdapter trackListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        trackList = new ArrayList<>();
        idList = new ArrayList<>();

        albumArtwork = (ImageView) findViewById(R.id.album_artwork);
        albumNameView = (TextView) findViewById(R.id.album_name);
        artistName = (TextView) findViewById(R.id.album_artist_name);
        numberOfTracks = (TextView) findViewById(R.id.album_number_tracks);
        albumYear = (TextView) findViewById(R.id.album_year_released);
        trackListView = (ListView) findViewById(R.id.album_track_list);

        trackListView.setOnItemClickListener(this);

        populateAlbumTrackList();
    }

    @Override
    public Context getCurrentContext() {
        return AlbumViewActivity.this;
    }

    @Override
    public int getViewConstant() {
        return Constants.ACTIVITY_ALBUM_VIEW;
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
        Intent intent = new Intent(AlbumViewActivity.this, SongViewActivity.class);
        intent.putExtra("_id", idList.get(position));
        startActivity(intent);
    }

    public void populateAlbumTrackList() {
        Intent intent = getIntent();
        String album = intent.getStringExtra("album");
        String[] selection = {"album", "artist", "number_of_songs", "first_year", "album_art"};
        String where = "album = ?";
        String[] args = {album};
        Cursor cursor = getContentResolver().query(MetaStoreContract.Album.CONTENT_URI,
                selection, where, args, null);

        if (cursor != null && cursor.moveToFirst()) {
            Log.v(TAG, "Result Found...");
            String albumName = cursor.getString(cursor.getColumnIndex("album"));
            albumNameView.setText(albumName);
            artistName.setText(cursor.getString(cursor.getColumnIndex("artist")));
            numberOfTracks.setText(cursor.getString(cursor.getColumnIndex("number_of_songs")));
            albumYear.setText(cursor.getString(cursor.getColumnIndex("first_year")));

            byte[] albumArt = cursor.getBlob(cursor.getColumnIndex("album_art"));
            Bitmap bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.length);
            albumArtwork.setImageBitmap(bitmap);

            String[] trackSelection = {"title", "track", "song_id"};
            String trackWhere = "album = ?";
            String[] trackArgs = {albumName};
            String trackSort = MetaStoreContract.Track.TITLE + " ASC";
            Cursor trackCursor = getContentResolver().query(MetaStoreContract.Track.CONTENT_URI,
                    trackSelection, trackWhere, trackArgs, trackSort);

            if (trackCursor != null) {
                String song, songID;
                Map<String, String> trackListMap = new LinkedHashMap<>();
                while (trackCursor.moveToNext())
                {
                    song = trackCursor.getString(trackCursor.getColumnIndex(MetaStoreContract.Track.TITLE));
                    songID = Integer.toString(trackCursor.getInt(
                            trackCursor.getColumnIndex(MetaStoreContract.Track.SONG_ID)));
                    if (song != null) {
                        trackListMap.put(song, songID);
                    }
                }
                trackCursor.close();
                setTrackList(trackListMap);
            }

            cursor.close();
        }

        trackListAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, trackList);
        trackListView.setAdapter(trackListAdapter);
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
