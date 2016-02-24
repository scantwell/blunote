package com.drexelsp.blunote.network;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.util.Log;

import com.drexelsp.blunote.ui.LoginActivity;

import java.util.ArrayList;

/**
 * Created by omnia on 2/21/16.
 *
 */
public class BluetoothScanner extends BroadcastReceiver {
    private static final String TAG = "Bluetooth Scanner";
    private BluetoothAdapter mBluetoothAdapter;
    private Context context;
    private ArrayList<BluetoothDevice> mDevices;

    public BluetoothScanner(Context context, ArrayList<BluetoothDevice> devices) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mDevices = devices;
        this.context = context;
    }

    public boolean startDiscovery() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(this, filter);
        return mBluetoothAdapter.startDiscovery();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.v(TAG, "Found Device: " + device.getName());

            if (device.getUuids() != null) {
                for (ParcelUuid uuid : device.getUuids()) {
                    if (uuid.toString().equals("d0153a8f-b137-4fb2-a5be-6788ece4834a")) {
                        Log.v(TAG, "Blunote UUID Found");

                        // Initiate Connection for Welcome Packet
                        // DO THIS PART

                        // Add Device, To be replaced with some sort of Network Object class
                        mDevices.add(device);
                        break;
                    }
                }
            }
        }
    }
}
