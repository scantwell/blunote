package com.drexelsp.blunote.ui;

import android.Manifest;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.drexelsp.blunote.adapters.NetworkArrayAdapter;
import com.drexelsp.blunote.beans.ConnectionListItem;
import com.drexelsp.blunote.blunote.Constants;
import com.drexelsp.blunote.blunote.R;
import com.drexelsp.blunote.blunote.Service;
import com.drexelsp.blunote.events.BluetoothEvent;
import com.drexelsp.blunote.network.BluetoothScanner;
import com.drexelsp.blunote.provider.MetaStoreContract;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Iterator;

public class LoginActivity extends BaseBluNoteActivity implements View.OnClickListener, ServiceConnection {

    final String TAG = "LoginActivity";
    Button joinNetworkButton;
    Button createNetworkButton;
    Button refreshButton;
    ListView networkListView;
    NetworkArrayAdapter adapter;
    boolean mBound;
    private Service mService = null;
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

        PreferenceManager.setDefaultValues(this, R.xml.preference, false);

        joinNetworkButton = (Button) findViewById(R.id.join_network_button);
        createNetworkButton = (Button) findViewById(R.id.create_network_button);
        refreshButton = (Button) findViewById(R.id.refresh_button);

        joinNetworkButton.setOnClickListener(this);
        createNetworkButton.setOnClickListener(this);
        refreshButton.setOnClickListener(this);

        Intent intent = new Intent(this, Service.class);
        startService(intent);

        //Make Call to load networks
        if (!Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            networkListView = (ListView) findViewById(R.id.connection_list);
            ArrayList<ConnectionListItem> mNetworks = new ArrayList<>();
            mAdapter = new NetworkArrayAdapter(this, mNetworks);
            networkListView.setAdapter(mAdapter);
        }

        BlunoteCheckPermission();

        //dialog.hide();

    }

    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            networkListView = (ListView) findViewById(R.id.connection_list);
            //use the query to search your data somehow
            ArrayList<ConnectionListItem> connectionList = getCurrentAvailableNetworks();
            Iterator<ConnectionListItem> i = connectionList.iterator();
            while (i.hasNext()) {
                ConnectionListItem item = i.next();
                if (!item.getConnectionName().contains(query)) {
                    i.remove();
                }
            }

            adapter = new NetworkArrayAdapter(this, connectionList);
            networkListView.setAdapter(adapter);
            SearchView.OnCloseListener closeListener = new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    adapter = new NetworkArrayAdapter(adapter.getContext(), getCurrentAvailableNetworks());
                    networkListView.setAdapter(adapter);
                    return false;
                }
            };
            searchView.setOnCloseListener(closeListener);
        }
    }

    @Override
    public int getViewConstant() {
        return Constants.ACTIVITY_LOGIN;
    }

    @Override
    public Context getCurrentContext() {
        return LoginActivity.this;
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
    public boolean showSettingsCog() {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.getItem(Constants.MENU_ITEM_SEARCH).setVisible(false);
        return true;
    }

    @Override
    protected void onStart() {
        getApplicationContext().getContentResolver().delete(MetaStoreContract.Album.CONTENT_URI, null, null);
        getApplicationContext().getContentResolver().delete(MetaStoreContract.Artist.CONTENT_URI, null, null);
        getApplicationContext().getContentResolver().delete(MetaStoreContract.Track.CONTENT_URI, null, null);
        getApplicationContext().getContentResolver().delete(MetaStoreContract.User.CONTENT_URI, null, null);
        getApplicationContext().getContentResolver().delete(MetaStoreContract.UserTracks.CONTENT_URI, null, null);
        super.onStart();
        bindService(new Intent(this, Service.class), this, Context.BIND_AUTO_CREATE);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        unbindService(this);
        super.onStop();
    }

    @Subscribe
    public void onMessageEvent(BluetoothEvent bluetoothEvent) {
        Log.v(TAG, "BluetoothEvent Received");
        if (bluetoothEvent.event == BluetoothEvent.CONNECTOR) {
            if (bluetoothEvent.success) {
                Intent intent = new Intent(LoginActivity.this, MediaPlayerActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getCurrentContext(), "Connection Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == joinNetworkButton) {
            joinNetworkClicked();
        } else if (v == createNetworkButton) {
            createNetworkClicked();
        } else if (v == refreshButton) {
            refreshButtonClickedSuccessfully();
        }
    }

    private void refreshButtonClickedSuccessfully() {
        if (mScanner == null) {
            // Launch Scanner
            mScanner = new BluetoothScanner(getCurrentContext(), mAdapter);
            mAdapter.clear();
            mScanner.startDiscovery();
        }
    }

    private void joinNetworkClicked() {
        int position = networkListView.getCheckedItemPosition();
        if (position == AdapterView.INVALID_POSITION) {
            Toast toast = Toast.makeText(getCurrentContext(), "No Network Selected", Toast.LENGTH_SHORT);
            toast.show();
        } else if (mBound && mService != null) {
            ConnectionListItem network = mAdapter.getItem(position);
            mService.connectToNetwork(network.getNetworkMap());
        }
    }

    private void createNetworkClicked() {
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
    }

    private void BlunoteCheckPermission() {
        Log.v(TAG, "Checking Permissions");
        ArrayList<String> permissionsToGrant = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Need to request permission for Bluetooth");
            permissionsToGrant.add(Manifest.permission.BLUETOOTH);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Need to request permission for Bluetooth Admin");
            permissionsToGrant.add(Manifest.permission.BLUETOOTH_ADMIN);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Need to request permission for Coarse Location");
            permissionsToGrant.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Need to request permission for External Storage");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                permissionsToGrant.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }

        if (permissionsToGrant.size() > 0) {
            Log.v(TAG, "Requesting permissions");
            ActivityCompat.requestPermissions(this, permissionsToGrant.toArray(new String[permissionsToGrant.size()]), 1);
        } else {
            Log.v(TAG, "All permissions have been granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            this.finish();
            System.exit(0);
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
