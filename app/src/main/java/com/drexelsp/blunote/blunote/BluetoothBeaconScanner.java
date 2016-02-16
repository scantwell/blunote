package com.drexelsp.blunote.blunote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by omnia on 2/15/16.
 *
 * Scanner for Bluetooth Beacons
 * TODO: Return Data to network service
 */
public class BluetoothBeaconScanner {
    private static final String TAG = "Bluetooth Beacon Scanner";
    private static final UUID MY_UUID = UUID.fromString("d0153a8f-b137-4fb2-a5be-6788ece4834a");
    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanFilter mScanFilter;
    private ScanSettings mScanSettings;

    public BluetoothBeaconScanner() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        setScanSettings();
        setScanFilter();
    }

    public void detectBeacons(final Intent intent, final NetworkService ns) {
        final ArrayList<BluetoothDevice> devices = new ArrayList<>();
        ScanCallback mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                devices.add(result.getDevice());
                // Wrap up device in message?
                intent.putExtra("data", devices);
                ns.sendBroadcast(intent);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                for(ScanResult result : results) {
                    devices.add(result.getDevice());
                }
                // Wrap up list of devices in message?
                intent.putExtra("data", devices);
                ns.sendBroadcast(intent);
            }
        };

        mBluetoothLeScanner.startScan(Arrays.asList(mScanFilter), mScanSettings, mScanCallback);
    }

    private void setScanSettings() {
        ScanSettings.Builder mBuilder = new ScanSettings.Builder();
        mBuilder.setReportDelay(0);
        mBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        mScanSettings = mBuilder.build();
    }

    private void setScanFilter() {
        ScanFilter.Builder mBuilder = new ScanFilter.Builder();
        ByteBuffer mManufacturerData = ByteBuffer.allocate(23);
        ByteBuffer mManufacturerDataMask = ByteBuffer.allocate(24);
        byte[] uuid = getIdAsByte(MY_UUID);
        mManufacturerData.put(0, (byte)0xBE);
        mManufacturerData.put(1, (byte)0xAC);
        for (int i = 2; i <= 17; i++ ) {
            mManufacturerData.put(i, uuid[i-2]);
        }
        for (int i = 0; i <= 17; i++ ) {
            mManufacturerDataMask.put(i, (byte)0x01);
        }
        mBuilder.setManufacturerData(224, mManufacturerData.array(), mManufacturerDataMask.array());
        mScanFilter = mBuilder.build();
    }

    private byte[] getIdAsByte(UUID uuid)
    {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}
