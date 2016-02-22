package com.drexelsp.blunote.blunote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by omnia on 2/21/16.
 *
 */
public class BluetoothScanner {
    private BluetoothAdapter mBluetoothAdapter;
    private NetworkService mNetworkService;

    public BluetoothScanner(NetworkService networkService) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mNetworkService = networkService;
    }

    public boolean startDiscovery() {
        return mBluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver mReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Do something with device?
            }
        }
    };
}
