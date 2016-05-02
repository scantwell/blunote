package com.drexelsp.blunote.blunote;

import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.*;
import com.drexelsp.blunote.events.BluetoothEvent;
import com.drexelsp.blunote.events.OnDisconnectionEvent;
import com.drexelsp.blunote.events.OnLeaveNetworkEvent;
import com.drexelsp.blunote.network.ClientService;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by scantwell on 2/15/2016.
 * Allows communication to flow from the Application process to another device on the network.
 * Handles incoming messages via the onReceived method and sends messages via the send overloads.
 */
public class Service extends ClientService {

    public class LocalBinder extends Binder {
        public Service getService() {
            return Service.this;
        }
    }

    private String TAG = "Service";
    private User user;

    public Service() {
        IBinder mBinder = new LocalBinder();
        super.setBinder(mBinder);
    }

    public void onReceiveDownstream(byte[] data) {
        this.onReceiveUpstream(data);
    }

    public void onReceiveUpstream(byte[] data) {
        try {
            Pdu pdu = Pdu.parseFrom(data);
            DeliveryInfo dinfo = pdu.getDeliveryInfo();
            WrapperMessage message = WrapperMessage.parseFrom(pdu.getData());
            this.user.onReceive(dinfo, message);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    public void onConnectionUpstream(String address)
    {
        // Should throw a connection event
        BluetoothEvent event = new BluetoothEvent(BluetoothEvent.CONNECTOR, true, address);
        EventBus.getDefault().post(event);

        Metadata metadata = new Metadata(getApplicationContext());
        BlunoteMessages.MetadataUpdate metadataUpdate = metadata.getMetadata(getApplicationContext());
        super.sendUpstream(WrapperMessage.newBuilder()
                .setType(WrapperMessage.Type.METADATA_UPDATE)
                .setMetadataUpdate(metadataUpdate).build().toByteArray());
    }

    public void onConnectionDownstream(String address) { Log.v(TAG, "Client has connected to us."); }

    public void onDisconnectionDownstream(String address) { Log.v(TAG, "Client has disconnected from us."); }

    public void onDisconnectionUpstream(String address) {
        EventBus.getDefault().post(new OnDisconnectionEvent(OnDisconnectionEvent.UPSTREAM, address));
    }

    public void onCreate() {
        EventBus.getDefault().register(this);
        super.onCreate();
    }

    public void startNetwork() {
        this.user = new Host(this, getApplicationContext());
        NetworkConfiguration.Builder configBuilder = NetworkConfiguration.newBuilder();
        configBuilder.setHandshake(ByteString.copyFrom(WelcomePacket.newBuilder().setNetworkName("Party Jamz HardCoded").setNumSongs("0").setNumUsers("0").build().toByteArray()));
        configBuilder.setNotifyOnConnectDownstream(true);
        configBuilder.setNotifyOnConnectUpstream(true);
        configBuilder.setNotifyOnDisconnectDownstream(true);
        configBuilder.setNotifyOnDisconnectUpstream(true);
        configBuilder.setReceiveUpstream(true);
        configBuilder.setHandshake(ByteString.copyFrom(user.getWelcomePacket().toByteArray()));
        super.startNetwork(configBuilder.build());
    }

    public void connectToNetwork(NetworkMap networkMap) {
        this.user = new User(this, getApplicationContext());
        NetworkConfiguration.Builder configBuilder = NetworkConfiguration.newBuilder();
        configBuilder.setNotifyOnConnectDownstream(true);
        configBuilder.setNotifyOnConnectUpstream(true);
        configBuilder.setNotifyOnDisconnectDownstream(true);
        configBuilder.setNotifyOnDisconnectUpstream(true);
        configBuilder.setReceiveDownstream(true);
        configBuilder.setNetworkMap(networkMap);
        super.connectToNetwork(configBuilder.build());
    }

    @Subscribe
    public void onLeaveNetworkEvent(OnLeaveNetworkEvent event)
    {
        Log.v(TAG, "Disconnecting");
        this.disconnect();
    }

    public void disconnect() {
        super.disconnect();
    }

    public void send(SingleAnswer message) {
        super.sendUpstream(WrapperMessage.newBuilder()
                .setType(WrapperMessage.Type.SINGLE_ANSWER)
                .setSingleAnswer(message).build().toByteArray());
    }

    public void send(MultiAnswer message) {
        super.sendUpstream(WrapperMessage.newBuilder()
                .setType(WrapperMessage.Type.MULTI_ANSWER)
                .setMultiAnswer(message).build().toByteArray());
    }

    public void send(SongFragment message) {
        super.sendUpstream(WrapperMessage.newBuilder()
                .setType(WrapperMessage.Type.SONG_FRAGMENT)
                .setSongFragment(message).build().toByteArray());
    }

    public void send(Recommendation message) {
        super.sendUpstream(WrapperMessage.newBuilder()
                .setType(WrapperMessage.Type.RECOMMEND)
                .setRecommendation(message).build().toByteArray());
    }

    public void send(SongRequest message) {
        super.sendDownstream(WrapperMessage.newBuilder()
                .setType(WrapperMessage.Type.SONG_REQUEST)
                .setSongRequest(message).build().toByteArray());
    }

    public void send(WelcomePacket message) {
        super.sendDownstream(WrapperMessage.newBuilder()
                .setType(WrapperMessage.Type.WELCOME_PACKET)
                .setWelcomePacket(message).build().toByteArray());
    public void send(MetadataUpdate message) {
        Pdu pdu = createMessage()
                .setMessage(WrapperMessage.newBuilder()
                        .setType(WrapperMessage.Type.METADATA_UPDATE)
                        .setMetadataUpdate(message)).build();
        super.send(pdu.toByteArray());
    }

    public Pdu.Builder createMessage() {
        Pdu.Builder pduBuilder = Pdu.newBuilder();
        pduBuilder.setDeliveryInfo(createDeliveryInfo());
        return pduBuilder;
    }
}