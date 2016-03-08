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
 * Activity for the Settings/Preferences page
 */
public class PreferencesActivity extends BaseBluNoteActivity implements View.OnClickListener
{
    Button leaveNetworkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        leaveNetworkButton = (Button) findViewById(R.id.leave_network_button);
        leaveNetworkButton.setOnClickListener(this);
    }

    @Override
    public Context getCurrentContext() {
        return PreferencesActivity.this;
    }

    @Override
    public int getViewConstant() {
        return Constants.ACTIVITY_PREFERENCES;
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
        Intent intent = new Intent(PreferencesActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
