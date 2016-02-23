package com.drexelsp.blunote.ui;

import com.drexelsp.blunote.blunote.Constants;
import com.drexelsp.blunote.blunote.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

/**
 * Activity for the Network Settings page
 */
public class NetworkSettingsActivity extends BaseBluNoteActivity
{
    Button launchNetworkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        launchNetworkButton = (Button) findViewById(R.id.launch_network_button);

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
