package com.drexelsp.blunote.network;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.drexelsp.blunote.blunote.R;

import java.util.UUID;

/**
 * Created by scantwell on 1/12/2016.
 * <p/>
 * NetworkService exposes the meshnetwork
 */
public class NetworkService extends Service {

    private Messenger messenger = new Messenger(new ClientHandler(this));
    private String TAG = "NetworkService";
    private int NOTIFICATION_ID = 1234;
    private UUID uuid = UUID.fromString("d0153a8f-b137-4fb2-a5be-6788ece4834a");

    public void onReceived(String data) {
        Log.v(TAG, "Received a message.");
        Intent intent = new Intent();
        intent.setAction("networkservice.onrecieved");
        intent.putExtra("data", data);
        sendBroadcast(intent);
    }

    // Sends to another application via bluetooth/etc
    public void send(Message msg) {
        Log.v(TAG, "Sending message.");
        this.onReceived("Hello World!");
    }

    public void connectToNetwork(String device) {
        BlunoteRouter router = new BlunoteRouter();

        BluetoothConnector bluetoothConnector = new BluetoothConnector(router);
        bluetoothConnector.connectToDevice(device);

        // Add Server Listener
        BluetoothServerListener bluetoothServerListener = new BluetoothServerListener(router, uuid);

        makeDiscoverable();
    }

    public void startNetwork() {
        BlunoteRouter router = new BlunoteRouter();

        BluetoothServerListener bluetoothServerListener = new BluetoothServerListener(router, uuid);

        makeDiscoverable();
    }

    public void getAvailableNetworks() {

    }

    private void makeDiscoverable() {
        Log.v(TAG, "Make Discoverable");
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        discoverableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(discoverableIntent);
    }

    private void cancelDiscoverable() {
        Log.v(TAG, "Cancel Discoverable");
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1);
        discoverableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(discoverableIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "Binding user.");
        this.createNotification();
        return messenger.getBinder();
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "Created service.");
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "Destroyed service.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Increases the priority of this running process
        // Must be created in onStartCommand else NotificationManager will be null.

        // If the service is stopped it will be started again with original intent
        // Needed for determining the type of mesh network
        return START_REDELIVER_INTENT;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "Unbinding user.");
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // notificationId allows you to update the notification later on.
        mNotificationManager.cancel(NOTIFICATION_ID);
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(TAG, "Rebind user.");
    }

    // Private

    private void createNotification() {
        Notification note =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("BluNote Network Service")
                        .setContentText("Hello.").build();
        note.flags |= Notification.FLAG_NO_CLEAR;
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, note);
    }
}