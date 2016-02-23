package com.drexelsp.blunote.blunote;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by U6020377 on 1/25/2016.
 */
public class AlbumViewActivity extends BaseBluNoteActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        List<String> list = new ArrayList<>();
        for(int i = 1; i < 20; ++i)
            list.add("Track " + i);

        ListView view = (ListView) findViewById(R.id.album_track_list);
        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        view.setAdapter(adapter);
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
}
