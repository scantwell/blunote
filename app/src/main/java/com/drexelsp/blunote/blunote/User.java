package com.drexelsp.blunote.blunote;

import android.content.Context;

import com.drexelsp.blunote.events.SongRecommendationEvent;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * Created by scantwell on 3/31/2016.
 */
public class User {

    protected String name = "FakeUser";
    protected Service service;
    protected Context context;
    protected Metadata metadata;
    protected Media media;

    public User(Service service, Context context)
    {
        this.service = service;
        this.context = context;
        this.media = new Media(context, service);
        this.metadata = new Metadata(context);
    }

    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.WrapperMessage message)
    {
        if (BlunoteMessages.WrapperMessage.Type.METADATA_UPDATE.equals(message.getType()))
        {
            this.onReceive(dinfo, message.getMetadataUpdate());
        }
        else if (BlunoteMessages.WrapperMessage.Type.MULTI_ANSWER.equals(message.getType()))
        {
            this.onReceive(dinfo, message.getMultiAnswer());
        }
        else if (BlunoteMessages.WrapperMessage.Type.RECOMMEND.equals(message.getType()))
        {
            this.onReceive(dinfo, message.getRecommendation());
        }
        else if (BlunoteMessages.WrapperMessage.Type.SINGLE_ANSWER.equals(message.getType()))
        {
            this.onReceive(dinfo, message.getSingleAnswer());
        }
        else if (BlunoteMessages.WrapperMessage.Type.SONG_FRAGMENT.equals(message.getType()))
        {
            this.onReceive(dinfo, message.getSongFragment());
        }
        else if (BlunoteMessages.WrapperMessage.Type.SONG_REQUEST.equals(message.getType()))
        {
            this.onReceive(dinfo, message.getSongRequest());
        }
        else if (BlunoteMessages.WrapperMessage.Type.VOTE.equals(message.getType()))
        {
            this.onReceive(dinfo, message.getVote());
        }
        else
        {
            throw new RuntimeException(String.format("Unhandled message of type '%s'", message.getType().name()));
        }
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
        if (message.getUsername().equals(this.name))
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

    @Subscribe
    public void onSongRecommendation(SongRecommendationEvent event) {
        String id = event.songId;
        String owner = event.owner;

        BlunoteMessages.SongRequest.Builder builder = BlunoteMessages.SongRequest.newBuilder();
        builder.setSongId(Long.parseLong(id));
        builder.setUsername(owner);

        service.send(builder.build());
    }
}