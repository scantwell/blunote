package com.drexelsp.blunote.blunote;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.preference.PreferenceManager;

import com.drexelsp.blunote.events.SongRecommendationEvent;
import com.drexelsp.blunote.blunote.BlunoteMessages.*;
import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * Created by scantwell on 3/31/2016.
 */
public class Host extends User {

    private Player player;
    private ArrayList<SongAssembler> songAssemblers;
    private String serverName;

    public Host(Service service, Context context) {
        super(service, context);
        this.name = PreferenceManager.getDefaultSharedPreferences(context).getString("pref_key_user_name", BluetoothAdapter.getDefaultAdapter().getName());
        this.serverName = this.name = PreferenceManager.getDefaultSharedPreferences(context).getString("pref_key_server_name", "Party Jamz");
        this.songAssemblers = new ArrayList<>();
        this.player = new Player(context);
        new Thread(this.player).run();
    }

   public void onReceive(DeliveryInfo dinfo, MetadataUpdate message)
    {
        super.onReceive(dinfo, message);

        if (message.getAction() == BlunoteMessages.MetadataUpdate.Action.ADD) {
            this.metadata.addMetadata(message);
        } else {
            this.metadata.deleteMetadata(message);
        }
        // Contains the removal of metadata
        //this.service.send(BlunoteMessages.MetadataUpdate);
        updateWelcomePacket();
    }

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

    private void updateWelcomePacket()
    {
        this.service.updateHandshake(getWelcomePacket());
    }

    public byte[] getWelcomePacket()
    {
        WelcomePacket.Builder wp = WelcomePacket.newBuilder();
        wp.setNetworkName(this.serverName);
        wp.setNumSongs("0");
        wp.setNumUsers("1");
        return wp.build().toByteArray();
    }
}
