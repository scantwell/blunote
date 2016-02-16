package com.drexelsp.blunote.blunote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created by omnia on 2/12/16.
 *
 * Creates and Advertises a Bluetooth Beacon
 * TODO: Add NetworkName, UserCount, SongCount, Latency, and ListeningServerUUID to AdvertiseData
 */
public class BluetoothBeacon {
    private static final String TAG = "Bluetooth Beacon";
    private static final UUID MY_UUID = UUID.fromString("d0153a8f-b137-4fb2-a5be-6788ece4834a");

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private AdvertiseData mAdvertiseData;
    private AdvertiseSettings mAdvertiseSettings;

    public BluetoothBeacon() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();

        setAdvertiseData();
        setAdvertiseSettings();
    }

    public void advertiseBeacon() {
        mBluetoothLeAdvertiser.startAdvertising(mAdvertiseSettings, mAdvertiseData, mAdvertiseCallback);
    }

    public void updateAdvertiseData(String networkName, int userCount, int songCount, int latency) {
        mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);

        // Update Advertise Data

        mBluetoothLeAdvertiser.startAdvertising(mAdvertiseSettings, mAdvertiseData, mAdvertiseCallback);
    }

    protected AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
        }
    };

    private void setAdvertiseSettings() {
        AdvertiseSettings.Builder mBuilder = new AdvertiseSettings.Builder();
        mBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
        mBuilder.setConnectable(false); // explore true for potential
        mBuilder.setTimeout(0);
        mBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
        mAdvertiseSettings = mBuilder.build();
    }

    private void setAdvertiseData() {
        AdvertiseData.Builder mBuilder = new AdvertiseData.Builder();
        ByteBuffer mManufacturerData = ByteBuffer.allocate(24);
        byte[] uuid = getIdAsByte(MY_UUID);
        mManufacturerData.put(0, (byte)0xBE);
        mManufacturerData.put(1, (byte) 0xAC);
        for (int i = 2; i <= 17; i++ ) {
            mManufacturerData.put(i, uuid[i-2]);
        }
        mManufacturerData.put(18, (byte)0x00);
        mManufacturerData.put(19, (byte)0x09);
        mManufacturerData.put(20, (byte)0x00);
        mManufacturerData.put(21, (byte)0x06);
        mManufacturerData.put(22, (byte)0xB5);
        mBuilder.addManufacturerData(224, mManufacturerData.array()); // Using Google ID 224
        mAdvertiseData = mBuilder.build();
    }

    private byte[] getIdAsByte(UUID uuid)
    {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}
