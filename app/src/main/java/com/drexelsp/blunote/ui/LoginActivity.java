package com.drexelsp.blunote.ui;

import java.util.ArrayList;

import com.drexelsp.blunote.adapters.NetworkArrayAdapter;
import com.drexelsp.blunote.beans.ConnectionListItem;
import com.drexelsp.blunote.blunote.Constants;
import com.drexelsp.blunote.blunote.R;
import com.drexelsp.blunote.blunote.Service;
import com.drexelsp.blunote.network.BluetoothScanner;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class LoginActivity extends BaseBluNoteActivity implements View.OnClickListener, ServiceConnection
{

    Button joinNetworkButton;
    Button createNetworkButton;
    Button refreshButton;
    ListView networkListView;

    final String TAG = "LoginActivity";
    private Service mService = null;
    boolean mBound;
    private BluetoothScanner mScanner;
    private NetworkArrayAdapter mAdapter;

    public void onServiceConnected(ComponentName className, IBinder service) {
        // This is called when the connection with the service has been
        // established, giving us the object we can use to
        // interact with the service.  We are communicating with the
        // service using a Messenger, so here we get a client-side
        // representation of that from the raw IBinder object.
        mService = ((Service.LocalBinder) service).getService();
        mBound = true;
    }

    public void onServiceDisconnected(ComponentName className) {
        // This is called when the connection with the service has been
        // unexpectedly disconnected -- that is, its process crashed.
        mService = null;
        mBound = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        joinNetworkButton = (Button) findViewById(R.id.join_network_button);
        createNetworkButton = (Button) findViewById(R.id.create_network_button);
        refreshButton = (Button) findViewById(R.id.refresh_button);

        joinNetworkButton.setOnClickListener(this);
        createNetworkButton.setOnClickListener(this);
        refreshButton.setOnClickListener(this);

        Intent intent = new Intent(this, Service.class);
        startService(intent);

        /*ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Available Networks");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();*/

        //Make Call to load networks
        networkListView = (ListView) findViewById(R.id.connection_list);
        ArrayList<ConnectionListItem> mNetworks = new ArrayList<>();
        mAdapter = new NetworkArrayAdapter(this, mNetworks);
        networkListView.setAdapter(mAdapter);

        // Launch Scanner
        mScanner = new BluetoothScanner(getCurrentContext(), mAdapter);
        mScanner.startDiscovery();

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
    @Override
    public boolean showSearchMenuItem() {
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to the service
        bindService(new Intent(this, Service.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(this);
    }

    @Override
    public void onClick(View v) {
        if(v == joinNetworkButton) {
            if (mBound && mService != null) {
                // Get Selected Network
                ConnectionListItem network = mAdapter.getItem(networkListView.getCheckedItemPosition());
                // Extract Mac Address
                String macAddress = network.getMacAddress();
                // Call Connect To Network
                mService.connectToNetwork(macAddress);
            }
            Intent intent = new Intent(LoginActivity.this, MediaPlayerActivity.class);
            startActivity(intent);
        }
        else if (v == createNetworkButton){
            if (mBound && mService != null) {
                // Call Start Network
                mService.startNetwork();
                // Wait for success callback?
                // Start Media Player Activity
                Intent intent = new Intent(LoginActivity.this, MediaPlayerActivity.class);
                startActivity(intent);
            } else {
                Log.v(TAG, "Failed to start network, service not bound.");
            }
            // Temp commented out
            //Intent intent = new Intent(LoginActivity.this, NetworkSettingsActivity.class);
            //startActivity(intent);
        }
        else if (v == refreshButton) {
            mAdapter.clear();
            mScanner.startDiscovery();
        }
    }

    /**
     * This method is a stub that should be switched out for the real way that we gather the active networks in range.
     *
     * @return a list of list items representing the current active networks - currently just some static garbage data
     */
    private ArrayList<ConnectionListItem> getCurrentAvailableNetworks() {
        ConnectionListItem item1 = new ConnectionListItem();
        item1.setConnectionName("Network 1");
        item1.setTotalConnections(22);
        item1.setTotalSongs(12415);

        ConnectionListItem item2 = new ConnectionListItem();
        item2.setConnectionName("Network 2");
        item2.setTotalConnections(15);
        item2.setTotalSongs(121435);

        ConnectionListItem item3 = new ConnectionListItem();
        item3.setConnectionName("Network 3");
        item3.setTotalConnections(123);
        item3.setTotalSongs(92);

        ArrayList<ConnectionListItem> itemList = new ArrayList<>();
        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);

        return itemList;
    }
}
