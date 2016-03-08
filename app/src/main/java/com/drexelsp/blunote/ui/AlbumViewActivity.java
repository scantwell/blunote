package com.drexelsp.blunote.ui;

import java.util.ArrayList;
import java.util.List;

import com.drexelsp.blunote.blunote.Constants;
import com.drexelsp.blunote.blunote.R;

import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

/**
 * Created by U6020377 on 1/25/2016.
 */
public class AlbumViewActivity extends BaseBluNoteActivity
{
    ListView trackList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        List<String> list = new ArrayList<>();
        for(int i = 1; i < 20; ++i)
            list.add("Track " + i);

        trackList = (ListView) findViewById(R.id.album_track_list);
        setSimpleList(trackList, list);
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

    public void populateAlbumTrackList()
    {

    }
}
