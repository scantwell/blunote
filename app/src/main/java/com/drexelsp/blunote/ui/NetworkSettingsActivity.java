package com.drexelsp.blunote.ui;

import com.drexelsp.blunote.blunote.Constants;
import com.drexelsp.blunote.blunote.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

/**
 * Activity for the Network Settings page
 */
public class NetworkSettingsActivity extends BaseBluNoteActivity implements View.OnClickListener
{
    Button launchNetworkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        launchNetworkButton = (Button) findViewById(R.id.launch_network_button);
        launchNetworkButton.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(NetworkSettingsActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
