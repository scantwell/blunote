package com.drexelsp.blunote.network;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.DeliveryInfo;
import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkConfiguration;
import com.drexelsp.blunote.blunote.BlunoteMessages.NetworkPacket;
import com.drexelsp.blunote.blunote.BlunoteMessages.Pdu;
import com.drexelsp.blunote.blunote.R;
import com.drexelsp.blunote.events.OnConnectionEvent;
import com.drexelsp.blunote.events.OnDisconnectionEvent;
import com.drexelsp.blunote.events.OnReceiveDownstream;
import com.drexelsp.blunote.events.OnReceiveUpstream;
import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.UUID;

/**
 * Created by scantwell on 1/12/2016.
 * <p>
 * NetworkService exposes the meshnetwork
 */
public class NetworkService extends Service {

    private Messenger messenger = new Messenger(new ClientHandler(this));
    private String TAG = "NetworkService";
    private int NOTIFICATION_ID = 1234;
    private UUID uuid = UUID.fromString("d0153a8f-b137-4fb2-a5be-6788ece4834a");
    private BluetoothServerListener mBluetoothServerListener;
    private NetworkConfiguration configuration;
    private Router router;

    @Subscribe
    public void onConnectionEvent(OnConnectionEvent event) {
        if (event.direction == OnConnectionEvent.UPSTREAM) {
            if (this.configuration.getNotifyOnConnectUpstream()) {
            }
        } else if (event.direction == OnConnectionEvent.DOWNSTREAM) {
            if (this.configuration.getNotifyOnConnectDownstream()) {
            }
        }
    }

    @Subscribe
    public void onDisconnectEvent(OnDisconnectionEvent event) {
        if (event.direction == OnDisconnectionEvent.UPSTREAM) {
            if (this.configuration.getNotifyOnDisconnectUpstream()) {
            }
        } else if (event.direction == OnDisconnectionEvent.DOWNSTREAM) {
            if (this.configuration.getNotifyOnDisconnectUpstream()) {
            }
        }
    }

    @Subscribe
    public void onReceiveUpstream(OnReceiveUpstream event) {
        NetworkPacket np = event.getNetworkPacket();
        if (NetworkPacket.Type.NETWORK_DATA_UPDATE.equals(np.getType())) {

        }
        if (this.configuration.getReceiveDownstream() && np.hasPdu()) {
            //broadcast np.getPdu()
        }
        // send a broadcast request ?or check the networkpack for other events?
    }

    @Subscribe
    public void onReceiveDownstream(OnReceiveDownstream event) {
        NetworkPacket np = event.getNetworkPacket();
        if (NetworkPacket.Type.NETWORK_DATA_UPDATE.equals(np.getType())) {

        }
        if (this.configuration.getReceiveDownstream() && np.hasPdu()) {
            //broadcast np.getPdu()
        }
    }

    public void onReceived(String data) {
        Log.v(TAG, "Received a message.");
        Intent intent = new Intent();
        intent.setAction("networkservice.onrecieved");
        intent.putExtra("Type", "MessageReceived");
        intent.putExtra("Data", data);
        sendBroadcast(intent);
    }

    // Sends to another application via bluetooth/etc
    public void sendDownstream(byte[] data) {
        Log.v(TAG, "Sending message.");
        if (this.router != null) {
            this.router.addDownstreamMessage(createNetworkPacket(data).build().toByteArray());
        }
    }

    // Sends to another application via bluetooth/etc
    public void sendUpstream(byte[] data) {
        Log.v(TAG, "Sending message.");
        if (this.router != null) {
            this.router.addUpstreamMessage(createNetworkPacket(data).build().toByteArray());
        }
    }

    public void connectToNetwork(NetworkConfiguration config) {
        this.router = createRouter(config);

        /*BlunoteRouter.getInstance().setClientMode(getApplicationContext());
        BluetoothConnector bluetoothConnector = new BluetoothConnector(uuid);
        bluetoothConnector.connectToDevice(device);
        mBluetoothServerListener = new BluetoothServerListener(uuid);
        makeDiscoverable();
        */
    }

    public void disconnect() {
        Log.w(TAG, "Disconnecting from network.");
        this.router.shutdown();
    }

    public void startNetwork(NetworkConfiguration config) {
        this.configuration = config;
        this.router = createRouter(config);
        mBluetoothServerListener = new BluetoothServerListener(this.router, uuid, config.getHandshake().toByteArray());
        makeDiscoverable();
    }

    public void updateHandshake(ByteString handshake) {
        NetworkConfiguration.Builder configBuilder = NetworkConfiguration.newBuilder().mergeFrom(this.configuration);
        configBuilder.setHandshake(handshake);
        this.configuration = configBuilder.build();
    }

    private Router createRouter(NetworkConfiguration config) {
        Router router = new Router(BluetoothAdapter.getDefaultAdapter().getAddress());
        router.setNotifyOnConnectDownstream(true);
        router.setNotifyOnConnectUpstream(true);
        router.setNotifyOnDisconnectDownstream(true);
        router.setNotifyOnDisconnectUpstream(true);
        router.setNotifyOnReceiveDownstream(true);
        router.setNotifyOnReceiveUpstream(true);
        return router;
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
        EventBus.getDefault().register(this);
        this.createNotification();
        return messenger.getBinder();
    }

    @Override
    public void onCreate()  { Log.v(TAG, "Created service."); }

    @Override
    public void onDestroy() {
        if (mBluetoothServerListener != null) {
            mBluetoothServerListener.shutdown();
        }
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
        EventBus.getDefault().unregister(this);
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

    private NetworkPacket.Builder createNetworkPacket(byte[] data) {
        NetworkPacket.Builder networkBuilder = NetworkPacket.newBuilder();
        networkBuilder.setPdu(createPdu(data));
        return networkBuilder;
    }

    private Pdu createPdu(byte[] data) {
        Pdu.Builder builder = Pdu.newBuilder()
                .setDeliveryInfo(createDeliveryInfo())
                .setData(ByteString.copyFrom(data));
        return builder.build();
    }

    private DeliveryInfo createDeliveryInfo() {
        DeliveryInfo.Builder dinfoBuilder = DeliveryInfo.newBuilder();
        dinfoBuilder.setTimestamp(getTimestamp());
        dinfoBuilder.setUsername(BluetoothAdapter.getDefaultAdapter().getAddress());
        return dinfoBuilder.build();
    }

    private long getTimestamp() {
        return System.currentTimeMillis() / 1000;
    }
}
