package com.drexelsp.blunote.ui;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;

import com.drexelsp.blunote.blunote.R;

/**
 * Settings Fragment
 * Does Fragment Things...
 *
 * Created by omnia on 4/7/16.
 */
public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

    public EditTextPreference userName;
    public CheckBoxPreference notifications;
    public CheckBoxPreference ghostMode;
    public CheckBoxPreference shareSongs;

    public EditTextPreference networkName;
    public EditTextPreference networkPassword;
    public EditTextPreference queueSize;
    public CheckBoxPreference clientVoting;
    public EditTextPreference userVotes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        userName = (EditTextPreference) findPreference("pref_key_user_name");
        notifications = (CheckBoxPreference) findPreference("pref_key_notifications");
        ghostMode = (CheckBoxPreference) findPreference("pref_key_ghost_client");
        shareSongs = (CheckBoxPreference) findPreference("pref_key_share_songs");

        networkName = (EditTextPreference) findPreference("pref_key_network_name");
        networkPassword = (EditTextPreference) findPreference("pref_key_network_password");
        queueSize = (EditTextPreference) findPreference("pref_key_queue_size");
        clientVoting = (CheckBoxPreference) findPreference("pref_key_client_voting");
        userVotes = (EditTextPreference) findPreference("pref_key_votes_per_user");

        updatePreferenceSummary();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreferenceSummary();
    }

    private void updatePreferenceSummary() {
        userName.setSummary(userName.getText());
        networkName.setSummary(networkName.getText());
        queueSize.setSummary(String.format("Song queue size: %d", Integer.parseInt(queueSize.getText())));
        userVotes.setSummary(String.format("Votes per user: %d", Integer.parseInt(userVotes.getText())));
    }
}
