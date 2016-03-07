package com.drexelsp.blunote.network;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Parcelable;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.drexelsp.blunote.adapters.NetworkArrayAdapter;
import com.drexelsp.blunote.beans.ConnectionListItem;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by omnia on 2/21/16.
 *
 */
public class BluetoothScanner extends BroadcastReceiver {
    private static final String TAG = "Bluetooth Scanner";
    private BluetoothAdapter mBluetoothAdapter;
    private Context mContext;
    private ArrayAdapter<ConnectionListItem> mAdapter;
    private ArrayList<BluetoothDevice> mDevices;
    private Set<Integer> whiteList;

    public BluetoothScanner(Context context, ArrayAdapter<ConnectionListItem> adapter) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mAdapter = adapter;
        mContext = context;

        mDevices = new ArrayList<>();
        whiteList = new HashSet<>();
        // Add Other Devices?
        whiteList.add(BluetoothClass.Device.PHONE_SMART);

    }

    public boolean startDiscovery() {
        if (mBluetoothAdapter.isDiscovering()) {
            Log.v(TAG, "Discovery Already Running, ignoring request");
            return false;
        } else {
            Log.v(TAG, "Discovery Initiated");
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothDevice.ACTION_UUID);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            mContext.registerReceiver(this, filter);
            return mBluetoothAdapter.startDiscovery();
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String name = device.getName();
            String address = device.getAddress();
            int bluetoothClass = device.getBluetoothClass().getDeviceClass();
            Log.v(TAG, String.format("Discovered: %s %s %d", name, address, bluetoothClass));

            // Valid Device
            if (name != null && address != null) {
                // Device Class White List
                if (whiteList.contains(bluetoothClass)) {
                    if (!mDevices.contains(device)) {
                        mDevices.add(device);
                    }
                }
            }

        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            Log.v(TAG, "Discovery Completed, Fetching UUIDs for " + mDevices.size() + " device(s)");
            if (!mDevices.isEmpty()) {
                BluetoothDevice device = mDevices.remove(0);
                device.fetchUuidsWithSdp();
            }

        } else if (BluetoothDevice.ACTION_UUID.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Parcelable[] uuids = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
            if (uuids != null) {
                for (Parcelable uuid : uuids) {
                    if (uuid.toString().equals("d0153a8f-b137-4fb2-a5be-6788ece4834a")) {
                        Log.v(TAG, String.format("Blunote Device Found: %s", device.getName()));

                        // Get Welcome Packet
                        // Still needs to be implemented

                        // Temporary Solution
                        ConnectionListItem item = new ConnectionListItem();
                        item.setConnectionName(device.getName());
                        item.setMacAddress(device.getAddress());
                        item.setTotalConnections(1);
                        item.setTotalSongs(1);
                        mAdapter.add(item);
                        break;
                    }
                }
            }

            if (mDevices.isEmpty()) {
                Log.v(TAG, "Fetching UUIDs Completed, Deregister Receiver");
                context.unregisterReceiver(this);
            } else {
                BluetoothDevice nextDevice = mDevices.remove(0);
                nextDevice.fetchUuidsWithSdp();
            }
        }
    }
}
