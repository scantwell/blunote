package com.drexelsp.blunote.blunote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * A base activity so that the menu bar can call a single method to decrease duplication.
 */
public class BaseBluNoteActivity extends AppCompatActivity
{
    public void networkSettings(View view)
    {
        Intent networkIntent = new Intent(this, NetworkSettingsActivity.class);
        startActivity(networkIntent);
    }

    public void preferences(View view)
    {
        Intent preferencesIntent = new Intent(this, PreferencesActivity.class);
        startActivity(preferencesIntent);
    }
}
