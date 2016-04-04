package com.drexelsp.blunote.blunote;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by scantwell on 3/31/2016.
 */
public class User {

    private String name;
    private Service service;
    private Context context;
    private Metadata metadata;
    private Media media;

    public User(Service service, Context context)
    {
        this.service = service;
        this.context = context;
        this.media = new Media(context, service);
        this.metadata = new Metadata(context);
    }

    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.MetadataUpdate message)
    {
        if (message.getAction() == BlunoteMessages.MetadataUpdate.Action.ADD) {
            this.metadata.addMetadata(message);
        } else {
            this.metadata.deleteMetadata(message);
        }
    }

    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.MultiAnswer message)
    {
        throw new RuntimeException("User cannot handle 'BlunoteMessages.MultiAnswer'. Is not a host.");
    }

    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.Recommendation message)
    {
        throw new RuntimeException("User cannot handle 'BlunoteMessages.Recommendation'. Is not a host.");
    }

    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.SingleAnswer message)
    {
        throw new RuntimeException("User cannot handle 'BlunoteMessages.Vote'. Is not a host.");
    }

    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.SongFragment message)
    {
        throw new RuntimeException("User cannot handle 'BlunoteMessages.SongFragment'. Is not a host.");
    }

    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.SongRequest message)
    {
        if (message.getUsername().equals("FakeUser"))
        {
            ArrayList<BlunoteMessages.SongFragment> frags = this.media.getSongFragments(message.getSongId());
            for (BlunoteMessages.SongFragment frag : frags) {
                service.send(frag);
            }
        }
    }

    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.Vote message)
    {
        throw new RuntimeException("User cannot handle 'BlunoteMessages.Vote'. Not implemented.");
    }
}