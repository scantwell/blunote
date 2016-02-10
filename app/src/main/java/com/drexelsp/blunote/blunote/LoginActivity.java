package com.drexelsp.blunote.blunote;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.drexelsp.blunote.adapters.NetworkArrayAdapter;
import com.drexelsp.blunote.beans.ConnectionListItem;

import java.util.ArrayList;

public class LoginActivity extends BaseBlueNoteActivity {

    Button joinNetworkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        joinNetworkButton = (Button) findViewById(R.id.join_network_button);

        joinNetworkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MediaPlayerActivity.class);
                startActivity(intent);
            }
        });

        /*ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Available Networks");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();*/

        //Make Call to load networks
        ListView networkListView = (ListView) findViewById(R.id.connection_list);
        NetworkArrayAdapter adapter = new NetworkArrayAdapter(this, getCurrentAvailableNetworks());
        networkListView.setAdapter(adapter);

        //dialog.hide();

    }

    @Override
    public int getViewConstant() {
        return Constants.ACTIVITY_LOGIN;
    }

    @Override
    public boolean showMusicMenuItems() {
        return false;
    }

    @Override
    public Context getCurrentContext() {
        return LoginActivity.this;
    }

    /**
     * This method is a stub that should be switched out for the real way that we gather the active networks in range.
     *
     * @return a list of list items representing the current active networks - currently just some static garbage data
     */
    private ArrayList<ConnectionListItem> getCurrentAvailableNetworks() {
        ConnectionListItem item1 = new ConnectionListItem();
        item1.setConnectionName("Network 1");
        item1.setTotalConnections("22");
        item1.setTotalSongs("12415");

        ConnectionListItem item2 = new ConnectionListItem();
        item2.setConnectionName("Network 2");
        item2.setTotalConnections("15");
        item2.setTotalSongs("121435");

        ConnectionListItem item3 = new ConnectionListItem();
        item3.setConnectionName("Network 3");
        item3.setTotalConnections("123");
        item3.setTotalSongs("92");

        ArrayList<ConnectionListItem> itemList = new ArrayList<>();
        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);

        return itemList;
    }
}
