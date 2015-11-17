package com.example.omnia.myapplication;

import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    private BluetoothService mBluetoothService;
    private Set<BluetoothDevice> pairedDevices;

    private final ArrayList<BluetoothDevice> devices = new ArrayList<>();

    private interface bluetoothCommands {
        int DEBUG_TOAST = 0;
        int PLAY_SONG = 1;
    }

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
        final ArrayAdapter<String> deviceAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1);
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
                // Get Song byte array
                showToast("Loading Song. . .");
                new Thread(new Runnable()  {
                    public void run() {
                        byte[] song = getSongByteArray();
                        showToast("Sending . . .");
                        mBluetoothService.write(mBluetoothService.getConnectedDevices().get(0),
                                bluetoothCommands.PLAY_SONG,
                                song);
                    }
                }).start();
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

    // Handler for Bluetooth Service to call on events
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == bluetoothCommands.DEBUG_TOAST) {
                showToast((String)msg.obj);
                return;
            }

            if (mBluetoothService.numConnectedThreads() > 1) {
                // Multiple connected devices, perform daisy chain
                BluetoothDevice sender = mBluetoothService.getDeviceById(msg.arg1);
                ArrayList<BluetoothDevice> otherDevices = mBluetoothService.getConnectedDevices();
                otherDevices.remove(sender);
                for (BluetoothDevice device : otherDevices) {
                    showToast("Sending . . .");
                    mBluetoothService.write(device, bluetoothCommands.PLAY_SONG,
                            (byte[]) msg.obj);
                }
            } else {
                // Single connected Device, receive and play song
                switch(msg.what) {
                    case bluetoothCommands.PLAY_SONG:
                        convertToFileAndPlay((byte[]) msg.obj);
                        break;
                }
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

    // Get Smallest song on device and convert to byte array
    private byte[] getSongByteArray() {
        ContentResolver cr = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri newUri;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0",
                sortOrder = MediaStore.Audio.Media.SIZE + " ASC",
                data;
        int count, len, bufferSize = 1024;
        Cursor cur = cr.query(uri, null, selection, null, sortOrder);
        byte[] buffer = new byte[0];

        if (cur != null) {
            count = cur.getCount();
            if (count > 0 && cur.moveToFirst()) {
                data = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                newUri = Uri.parse("file:///" + data);
                Log.w("Song: ", newUri.toString());

                try {
                    InputStream inputStream = getContentResolver().openInputStream(newUri);
                    ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
                    buffer = new byte[bufferSize];

                    if (inputStream != null) {
                        while((len = inputStream.read(buffer)) != -1) {
                            byteBuffer.write(buffer, 0 , len);
                        }
                    }

                    if (byteBuffer.size() > 0 ){
                        return byteBuffer.toByteArray();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cur.close();
        }
        return buffer;
    }

    // Convert byte array into file and play with Media Player
    private void convertToFileAndPlay(byte[] bytes) {
        try {
            File tempMp3 = File.createTempFile("TempSong", "mp3", getCacheDir());
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(bytes);
            fos.close();

            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e ) {
            e.printStackTrace();
        }

    }

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

