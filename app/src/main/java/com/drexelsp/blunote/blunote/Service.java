package com.drexelsp.blunote.blunote;

import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.drexelsp.blunote.blunote.BlunoteMessages.DeliveryInfo;
import com.drexelsp.blunote.blunote.BlunoteMessages.MultiAnswer;
import com.drexelsp.blunote.blunote.BlunoteMessages.Pdu;
import com.drexelsp.blunote.blunote.BlunoteMessages.Recommendation;
import com.drexelsp.blunote.blunote.BlunoteMessages.SingleAnswer;
import com.drexelsp.blunote.blunote.BlunoteMessages.SongFragment;
import com.drexelsp.blunote.blunote.BlunoteMessages.WrapperMessage;
import com.drexelsp.blunote.blunote.BlunoteMessages.SongRequest;
import com.drexelsp.blunote.events.BluetoothEvent;
import com.drexelsp.blunote.network.ClientService;
import com.google.protobuf.InvalidProtocolBufferException;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

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
    private ArrayList<MessageHandler> handlers = new ArrayList<MessageHandler>();
    private User user;

    public Service() {
        IBinder mBinder = new LocalBinder();
        super.setBinder(mBinder);
    }

    @Override
    public void onReceive(byte[] data) {
        //Log.v(TAG, "Received a message.");
        try {
            Pdu pdu = Pdu.parseFrom(data);
            DeliveryInfo dinfo = pdu.getDeliveryInfo();
            WrapperMessage message = pdu.getMessage();

            if (WrapperMessage.Type.METADATA_UPDATE.equals(message.getType()))
            {
                user.onRecieve(dinfo, message.getMetadataUpdate());
            }
            else if (WrapperMessage.Type.MULTI_ANSWER.equals(message.getType()))
            {
                user.onRecieve(dinfo, message.getMultiAnswer());
            }
            else if (WrapperMessage.Type.RECOMMEND.equals(message.getType()))
            {
                user.onRecieve(dinfo, message.getRecommendation());
            }
            else if (WrapperMessage.Type.SINGLE_ANSWER.equals(message.getType()))
            {
                user.onRecieve(dinfo, message.getSingleAnswer());
            }
            else if (WrapperMessage.Type.SONG_FRAGMENT.equals(message.getType()))
            {
                user.onRecieve(dinfo, message.getSongFragment());
            }
            else if (WrapperMessage.Type.SONG_REQUEST.equals(message.getType()))
            {
                user.onRecieve(dinfo, message.getSongRequest());
            }
            else if (WrapperMessage.Type.VOTE.equals(message.getType()))
            {
                user.onRecieve(dinfo, message.getVote());
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkEvent(BluetoothEvent bluetoothEvent) {
        EventBus.getDefault().post(bluetoothEvent);
        if ((bluetoothEvent.event == BluetoothEvent.CONNECTOR || bluetoothEvent.event == BluetoothEvent.SERVER_LISTENER) && bluetoothEvent.success) {
            // Gather Metadata and Send it
            Metadata metadata = new Metadata(getApplicationContext());
            BlunoteMessages.MetadataUpdate metadataUpdate = metadata.getMetadata();
            Pdu pdu = createMessage()
                    .setMessage(WrapperMessage.newBuilder()
                            .setType(WrapperMessage.Type.METADATA_UPDATE)
                            .setMetadataUpdate(metadataUpdate)).build();
            super.send(pdu.toByteArray());
        }
    }

    public void onCreate() {
        super.onCreate();
        this.handlers.add(new Metadata(getApplicationContext()));
        this.handlers.add(new Media(getApplicationContext(), this));
        this.handlers.add(new VoteEngine());
        this.user = new User(this, getApplicationContext());
    }

    public void startNetwork() {
        super.startNetwork();
    }

    public void connectToNetwork(String macAddress) {
        super.connectToNetwork(macAddress);
    }

    public void send(SingleAnswer message) {
        Pdu pdu = createMessage()
                .setMessage(WrapperMessage.newBuilder()
                        .setType(WrapperMessage.Type.SINGLE_ANSWER)
                        .setSingleAnswer(message)).build();
        super.send(pdu.toByteArray());
    }

    public void send(MultiAnswer message) {
        Pdu pdu = createMessage()
                .setMessage(WrapperMessage.newBuilder()
                        .setType(WrapperMessage.Type.MULTI_ANSWER)
                        .setMultiAnswer(message)).build();
        super.send(pdu.toByteArray());
    }

    public void send(SongFragment message) {
        Pdu pdu = createMessage()
                .setMessage(WrapperMessage.newBuilder()
                        .setType(WrapperMessage.Type.SONG_FRAGMENT)
                        .setSongFragment(message)).build();
        super.send(pdu.toByteArray());
    }

    public void send(Recommendation message) {
        Pdu pdu = createMessage()
                .setMessage(WrapperMessage.newBuilder()
                        .setType(WrapperMessage.Type.RECOMMEND)
                        .setRecommendation(message)).build();
        super.send(pdu.toByteArray());
    }

    public void send(SongRequest message) {
        Pdu pdu = createMessage()
                .setMessage(WrapperMessage.newBuilder()
                        .setType(WrapperMessage.Type.SONG_REQUEST)
                        .setSongRequest(message)).build();
        super.send(pdu.toByteArray());
    }

    public Pdu.Builder createMessage() {
        Pdu.Builder pduBuilder = Pdu.newBuilder();
        pduBuilder.setDeliveryInfo(createDeliveryInfo());
        return pduBuilder;
    }

    public DeliveryInfo createDeliveryInfo() {
        DeliveryInfo.Builder dinfoBuilder = DeliveryInfo.newBuilder();
        dinfoBuilder.setTimestamp(getTimestamp());
        dinfoBuilder.setUsername(getUsername());
        return dinfoBuilder.build();
    }

    private long getTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    public String getUsername() {
        return "FakeUser";
    }
}