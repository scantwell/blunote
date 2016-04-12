package com.drexelsp.blunote.blunote;

import android.content.Context;

import com.drexelsp.blunote.events.SongRecommendationEvent;
import com.drexelsp.blunote.blunote.BlunoteMessages.*;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * Created by scantwell on 3/31/2016.
 */
public class Host extends User {

    private Player player;

    public Host(Service service, Context context) {
        super(service, context);
       // this.player = new Player(context);
    }

   /* public void onReceive(DeliveryInfo dinfo, MetadataUpdate message)
    {
        if (message.getAction() == BlunoteMessages.MetadataUpdate.Action.ADD) {
            this.metadata.addMetadata(message);
        } else {
            this.metadata.deleteMetadata(message);
        }
        // Contains the removal of metadata
        //this.service.send(BlunoteMessages.MetadataUpdate);
    }*/

    @Override
    public void onReceive(DeliveryInfo dinfo, MultiAnswer message)
    {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public void onReceive(DeliveryInfo dinfo, Recommendation message)
    {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public void onReceive(DeliveryInfo dinfo, SingleAnswer message)
    {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public void onReceive(DeliveryInfo dinfo, SongFragment message)
    {
        this.media.addSongFragment(message);
    }

    @Override
    public void onReceive(DeliveryInfo dinfo, Vote message)
    {
        throw new RuntimeException("Not implemented.");
    }

    /*
    Could potentially cause problems with onRecommendation because of the User class also implemeting the same functionality
     */
    @Override
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
