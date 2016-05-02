package com.drexelsp.blunote.network;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;
import android.widget.ArrayAdapter;

import com.drexelsp.blunote.beans.ConnectionListItem;
import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkMap;
import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkPacket;
import com.drexelsp.blunote.blunote.BlunoteMessages.WelcomePacket;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Scans for bluetooth devices within range that have the Blunote UUID registered
 * Makes a brief connection to each Blunote Device to retrieve the welcome packet via handshake
 *
 * Created by omnia on 2/21/16.
 */
public class BluetoothScanner extends BroadcastReceiver {
    private static final String TAG = "Bluetooth Scanner";
    private final UUID uuid = UUID.fromString("d0153a8f-b137-4fb2-a5be-6788ece4834a");
    private BluetoothAdapter bluetoothAdapter;
    private Context context;
    private ArrayAdapter<ConnectionListItem> arrayAdapter;
    private ArrayList<BluetoothDevice> discoveredDevices;
    private ArrayList<String> blunoteDevices;
    private Set<Integer> whiteList;
    private ProgressDialog dialog;

    public BluetoothScanner(Context context, ArrayAdapter<ConnectionListItem> adapter) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.arrayAdapter = adapter;
        this.context = context;
        this.discoveredDevices = new ArrayList<>();
        this.blunoteDevices = new ArrayList<>();
        this.whiteList = new HashSet<>();
        this.whiteList.add(BluetoothClass.Device.PHONE_SMART);
        this.whiteList.add(BluetoothClass.Device.COMPUTER_HANDHELD_PC_PDA);
        this.dialog = new ProgressDialog(this.context);

    }

    public boolean startDiscovery() {
        if (this.bluetoothAdapter.isDiscovering()) {
            Log.v(TAG, "Discovery Already Running, ignoring request");
            return false;
        } else {
            Log.v(TAG, "Discovery Initiated");
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothDevice.ACTION_UUID);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            this.context.registerReceiver(this, filter);

            this.dialog.setMessage("Loading Available Networks");
            this.dialog.setCancelable(false);
            this.dialog.setInverseBackgroundForced(false);
            this.dialog.show();

            return this.bluetoothAdapter.startDiscovery();
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            handleActionFound((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));

        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            handleActionDiscoveryFinished();

        } else if (BluetoothDevice.ACTION_UUID.equals(action)) {
            handleActionUUID(intent);

            if (!this.discoveredDevices.isEmpty()) {
                BluetoothDevice nextDevice = this.discoveredDevices.remove(0);
                nextDevice.fetchUuidsWithSdp();
            } else {
                Log.v(TAG, "Fetching UUIDs Completed, Deregister Receiver");
                context.unregisterReceiver(this);
                handshakeDevices(this.blunoteDevices);
            }
        }
    }

    private void handleActionFound(BluetoothDevice device) {
        String name = device.getName();
        String address = device.getAddress();
        int bluetoothClass = device.getBluetoothClass().getDeviceClass();
        Log.v(TAG, String.format("Discovered: %s %s %d", name, address, bluetoothClass));
        if (name != null && address != null) {
            if (this.whiteList.contains(bluetoothClass)) {
                if (!this.discoveredDevices.contains(device)) {
                    this.discoveredDevices.add(device);
                }
            }
        }
    }

    private void handleActionDiscoveryFinished() {
        Log.v(TAG, "Discovery Completed, Fetching UUIDs for " + this.discoveredDevices.size() + " device(s)");
        if (!this.discoveredDevices.isEmpty()) {
            BluetoothDevice device = discoveredDevices.remove(0);
            device.fetchUuidsWithSdp();
        } else
            this.dialog.hide();
    }

    private void handleActionUUID(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        Parcelable[] uuids = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
        if (uuids != null) {
            Log.v(TAG, String.format("UUIDs found for Device %s", device.getName()));
            for (Parcelable uuid : uuids) {
                Log.v(TAG, uuid.toString());
                if (this.uuid.toString().equals(uuid.toString())) {
                    Log.v(TAG, String.format("Blunote Device Found: %s", device.getName()));
                    this.blunoteDevices.add(device.getAddress());
                    break;
                }
            }
        }
    }

    private void handshakeDevices(ArrayList<String> macAddresses) {
        Log.v(TAG, String.format("Requesting handshake from %d device(s)", macAddresses.size()));
        ArrayList<Pair<Thread, ClientHandshake>> threads = new ArrayList<>();
        for (String macAddress : macAddresses) {
            BluetoothConnector bluetoothConnector = new BluetoothConnector(this.bluetoothAdapter.getRemoteDevice(macAddress),
                    false, this.bluetoothAdapter, Collections.singletonList(this.uuid));
            try {
                BluetoothConnector.BluetoothSocketWrapper socket = bluetoothConnector.connect();
                BlunoteSocket blunoteSocket = new BlunoteBluetoothSocket(socket.getUnderlyingSocket());
                ClientHandshake clientHandshake = new ClientHandshake(blunoteSocket, false);
                Thread thread = new Thread(clientHandshake);
                thread.start();
                threads.add(new Pair<>(thread, clientHandshake));
            } catch (IOException e) {
                Log.v(TAG, String.format("Failure to setup Handshake: %s", e.getMessage()));
            }
        }

        ArrayList<NetworkPacket> networkPackets = new ArrayList<>();
        for (Pair<Thread, ClientHandshake> pair : threads) {
            try {
                Thread thread = pair.first;
                thread.join();
                ClientHandshake clientHandshake = pair.second;
                if (clientHandshake.getSuccess()){
                    networkPackets.add(clientHandshake.getNetworkPacket());
                }
            } catch (InterruptedException e) {
                Log.v(TAG, String.format("ClientHandshake thread interrupted while waiting for join: %s", e.getMessage()));
            }
        }
        Log.v(TAG, String.format("Handshaking completed, gathered %d packet(s)", networkPackets.size()));

        // TODO: remove duplicate packets

        for (NetworkPacket networkPacket : networkPackets) {
            if (networkPacket.hasNetworkMap()) {
                NetworkMap networkMap = networkPacket.getNetworkMap();
                try {
                    WelcomePacket welcomePacket = WelcomePacket.parseFrom(networkPacket.getPdu().getData());
                    ConnectionListItem item = new ConnectionListItem(networkMap, welcomePacket);
                    this.arrayAdapter.add(item);
                } catch (InvalidProtocolBufferException e) {
                    Log.v(TAG, String.format("Error parsing Welcome Packet data: %s", e.getMessage()));
                }
            }
        }

        this.dialog.hide();
    }
}
