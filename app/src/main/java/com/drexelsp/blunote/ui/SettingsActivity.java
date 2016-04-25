package com.drexelsp.blunote.ui;

import android.app.Activity;
import android.os.Bundle;

/**
 * Settings Activity
 * Doesn't need to do more than just setup the SettingsFragment
 * <p/>
 * Created by omnia on 4/7/16.
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
