package com.drexelsp.blunote.blunote;

import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.drexelsp.blunote.BlunoteMessages;
import com.drexelsp.blunote.network.ClientService;
import com.drexelsp.blunote.BlunoteMessages.*;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;

/**
 * Created by scantwell on 2/15/2016.
 */
public class Service extends ClientService {
    public class LocalBinder extends Binder {
        public Service getService() {
            return Service.this;
        }
    }

    private String TAG = "Service";
    private ArrayList<MessageHandler> handlers = new ArrayList<MessageHandler>();

    public Service()
    {
        IBinder mBinder = new LocalBinder();
        super.setBinder(mBinder);
        this.handlers.add(new MediaPlayer());
        this.handlers.add(new VoteEngine());
    }

    @Override
    public void onReceived(byte[] data)
    {
        Log.v(TAG, "Received a message.");
        try {
            Pdu pdu = Pdu.parseFrom(data);
            DeliveryInfo dinfo = pdu.getDeliveryInfo();
            WrapperMessage message = pdu.getMessage();

            for (MessageHandler handler : handlers)
            {
                if (handler.processMessage(dinfo, message))
                {
                    break;
                }
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    public void sendRequest(String mediaId, Recommendation.Type type)
    {
        Pdu pdu = createMessage()
                .setMessage(WrapperMessage.newBuilder()
                        .setType(WrapperMessage.Type.RECOMMEND)
                        .setRecommendation(
                                Recommendation.newBuilder()
                                        .setType(type)
                                        .setUsername(getUsername())
                                        .setMediaId(mediaId)
                                        .build())
                        .build())
                        .build();
        super.send(pdu.toByteArray());
    }

    public Pdu.Builder createMessage()
    {
        Pdu.Builder pduBuilder = Pdu.newBuilder();
        pduBuilder.setDeliveryInfo(createDeliveryInfo());
        return pduBuilder;
    }

    public BlunoteMessages.DeliveryInfo createDeliveryInfo()
    {
        BlunoteMessages.DeliveryInfo.Builder dinfoBuilder = BlunoteMessages.DeliveryInfo.newBuilder();
        dinfoBuilder.setTimestamp(getTimestamp());
        dinfoBuilder.setUsername(getUsername());
        return dinfoBuilder.build();
    }

    private long getTimestamp()
    {
        return System.currentTimeMillis()/1000;
    }

    public String getUsername()
    {
        return "FakeUser";
    }
}