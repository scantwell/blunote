package com.drexelsp.blunote.blunote;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

/**
 * Activity for the Network Settings page
 */
public class NetworkSettingsActivity extends BaseBluNoteActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public Context getCurrentContext() {
        return NetworkSettingsActivity.this;
    }

    @Override
    public int getViewConstant() {
        return Constants.ACTIVITY_NETWORK_SETTINGS;
    }

    @Override
    public boolean showMusicMenuItems() {
        return false;
    }

    @Override
    public boolean showSearchMenuItem() {
        return false;
    }
}
