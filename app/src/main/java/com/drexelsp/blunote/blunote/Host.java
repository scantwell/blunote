package com.drexelsp.blunote.blunote;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.preference.PreferenceManager;

import com.drexelsp.blunote.blunote.BlunoteMessages.DeliveryInfo;
import com.drexelsp.blunote.blunote.BlunoteMessages.MetadataUpdate;
import com.drexelsp.blunote.blunote.BlunoteMessages.MultiAnswer;
import com.drexelsp.blunote.blunote.BlunoteMessages.Recommendation;
import com.drexelsp.blunote.blunote.BlunoteMessages.SingleAnswer;
import com.drexelsp.blunote.blunote.BlunoteMessages.SongFragment;
import com.drexelsp.blunote.blunote.BlunoteMessages.Vote;
import com.drexelsp.blunote.blunote.BlunoteMessages.WelcomePacket;
import com.drexelsp.blunote.events.SongRecommendationEvent;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * Created by scantwell on 3/31/2016.
 */
public class Host extends User {

    private Player player;
    private ArrayList<SongAssembler> songAssemblers;
    private String serverName;
    private int numUsers;

    public Host(Service service, Context context) {
        super(service, context);
        this.numUsers = 1;
        this.name = PreferenceManager.getDefaultSharedPreferences(context).getString("pref_key_user_name", BluetoothAdapter.getDefaultAdapter().getName());
        this.serverName = PreferenceManager.getDefaultSharedPreferences(context).getString("pref_key_server_name", "Party Jamz");
        this.songAssemblers = new ArrayList<>();
        this.player = new Player(context);
        new Thread(this.player).start();
    }

    public void addUser()
    {
        this.numUsers++;
    }

    public void removeUser()
    {
        if (this.numUsers < 2)
        {
            return;
        }
        this.numUsers--;
    }

    public void onReceive(DeliveryInfo dinfo, MetadataUpdate message) {
        if (message.getAction() == BlunoteMessages.MetadataUpdate.Action.ADD) {
            addUser();
            this.metadata.addMetadata(message);
        } else {
            removeUser();
            this.metadata.deleteMetadata(message);
        }
        updateWelcomePacket();
    }

    @Override
    public void onReceive(DeliveryInfo dinfo, MultiAnswer message) {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public void onReceive(DeliveryInfo dinfo, Recommendation message) {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public void onReceive(DeliveryInfo dinfo, SingleAnswer message) {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public void onReceive(DeliveryInfo dinfo, SongFragment message) {
        this.media.addSongFragment(message);
    }

    @Override
    public void onReceive(DeliveryInfo dinfo, Vote message) {
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

    private void updateWelcomePacket() {
        WelcomePacket wp = getWelcomePacket();
        this.service.updateHandshake(wp.toByteArray());
        this.service.send(wp);
    }

    public WelcomePacket getWelcomePacket() {
        WelcomePacket.Builder wp = WelcomePacket.newBuilder();
        wp.setNetworkName(this.serverName);
        wp.setNumSongs(this.metadata.getSongCount());
        wp.setNumUsers(Integer.toString(this.numUsers));
        return wp.build();
    }
}
