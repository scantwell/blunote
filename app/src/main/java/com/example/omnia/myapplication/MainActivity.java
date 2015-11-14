package com.example.omnia.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    private BluetoothService mBluetoothService;
    private Set<BluetoothDevice> pairedDevices;

    private final ArrayList<BluetoothDevice> devices = new ArrayList<>();
    private BluetoothSocket connectedSocket;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Setup Media Player
        mediaPlayer = new MediaPlayer();

        // Launch Bluetooth Service
        mBluetoothService = new BluetoothService(mHandler);

        // Setup List Array Adapter
        final ArrayAdapter<String> deviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        ListView deviceListView = (ListView) findViewById(R.id.deviceList);
        deviceListView.setAdapter(deviceAdapter);
        deviceListView.setOnItemClickListener(mDeviceClickListener);

        // Set Floating Action Button 1
        FloatingActionButton leftFab = (FloatingActionButton) findViewById(R.id.lfab);
        leftFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pairedDevices = mBluetoothService.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        devices.add(device);
                        deviceAdapter.add(device.getName() + "\n" + device.getAddress());
                    }
                }

            }
        });

        // Set Floating Action Button 2
        FloatingActionButton rightFab = (FloatingActionButton) findViewById(R.id.rfab);
        rightFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Todo: Fix mBluetoothService.isConnected()
                //if (mBluetoothService.isConnected()) {
                //    showToast("Not connected to a device");
                //    return;
                //}

                showToast("Sending. . .");

                // Get Music
                ContentResolver cr = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                Uri newUri;
                String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
                String sortOrder = MediaStore.Audio.Media.SIZE + " ASC";
                Cursor cur = cr.query(uri, null, selection, null, sortOrder);
                int count;
                String data;


                if (cur != null) {
                    count = cur.getCount();
                    if (count > 0 && cur.moveToFirst()) {
                        data = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                        newUri = Uri.parse("file:///" + data);
                        Log.w("Song: ", newUri.toString());

                        try {
                            InputStream inputStream = getContentResolver().openInputStream(newUri);
                            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
                            int bufferSize = 1024;
                            int len;
                            byte[] buffer = new byte[bufferSize];

                            while((len = inputStream.read(buffer)) != -1) {
                                byteBuffer.write(buffer, 0 , len);
                            }

                            if (byteBuffer.size() > 0 ){
                                mBluetoothService.write(byteBuffer.toByteArray());
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                cur.close();



                // Send Data
            }
        });


    }

    @Override
    public void onDestroy() {
        if (mBluetoothService != null) {
            mBluetoothService.cancel();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            byte[] writeBuf = (byte[]) msg.obj;
            int bytes = (int)msg.arg2;
            Log.w("Handler Called, size: ", Integer.toString(bytes));
            Log.w("Message Size:", Integer.toString(writeBuf.length));
            Log.w("Hash Code: ", Integer.toString(writeBuf.hashCode()));

            try {
                File tempMp3 = File.createTempFile("TempSong", "mp3", getCacheDir());
                tempMp3.setReadable(true, false);
                FileOutputStream fos = new FileOutputStream(tempMp3);
                fos.write(writeBuf, 0, bytes);
                fos.close();

                FileInputStream fis = new FileInputStream(tempMp3);
                mediaPlayer.reset();
                mediaPlayer.setDataSource(fis.getFD());
                mediaPlayer.prepare();
                mediaPlayer.start();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    // Item Click Listener for choosing Bluetooth device and initiating connection
    private AdapterView.OnItemClickListener mDeviceClickListener =
            new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
                    // Get Device MAC Address
                    BluetoothDevice targetDevice = devices.get(arg2);
                    mBluetoothService.connectToDevice(targetDevice);
                    // Do stuff with it
                }
            };

    // Toasty - Allows Threads to make a Toast Message
    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_LONG).show();
            }
        });
    }
}

