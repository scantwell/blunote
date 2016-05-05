package com.drexelsp.blunote.blunote;

import java.util.ArrayList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.drexelsp.blunote.events.SongRecommendationEvent;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by scantwell on 3/31/2016.
 */
public class User {

    protected static String TAG = "USER";
    protected String name;
    protected Service service;
    protected Context context;
    protected Metadata metadata;
    protected Media media;
    protected static String PREF_USERNAME = "pref_key_user_name";

    public User(Service service, Context context) {
        this.name = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_USERNAME, BluetoothAdapter.getDefaultAdapter().getName());
        this.service = service;
        this.context = context;
        this.media = new Media(context.getContentResolver());
        this.metadata = new Metadata(context);
        EventBus.getDefault().register(this);
    }

    public String getName() {
        return name;
    }
    
    public BlunoteMessages.WelcomePacket getWelcomePacket() {
        return BlunoteMessages.WelcomePacket.newBuilder().build();
    }

    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.WrapperMessage message) {
        if (BlunoteMessages.WrapperMessage.Type.METADATA_UPDATE.equals(message.getType())) {
            this.onReceive(dinfo, message.getMetadataUpdate());
        } else if (BlunoteMessages.WrapperMessage.Type.MULTI_ANSWER.equals(message.getType())) {
            this.onReceive(dinfo, message.getMultiAnswer());
        } else if (BlunoteMessages.WrapperMessage.Type.RECOMMEND.equals(message.getType())) {
            this.onReceive(dinfo, message.getRecommendation());
        } else if (BlunoteMessages.WrapperMessage.Type.SINGLE_ANSWER.equals(message.getType())) {
            this.onReceive(dinfo, message.getSingleAnswer());
        } else if (BlunoteMessages.WrapperMessage.Type.SONG_FRAGMENT.equals(message.getType())) {
            this.onReceive(dinfo, message.getSongFragment());
        } else if (BlunoteMessages.WrapperMessage.Type.SONG_REQUEST.equals(message.getType())) {
            this.onReceive(dinfo, message.getSongRequest());
        } else if (BlunoteMessages.WrapperMessage.Type.VOTE.equals(message.getType())) {
            this.onReceive(dinfo, message.getVote());
        } else if (BlunoteMessages.WrapperMessage.Type.WELCOME_PACKET.equals(message.getType())) {
            this.onReceive(dinfo, message.getWelcomePacket());
        } else if (BlunoteMessages.WrapperMessage.Type.USERNAME_UPDATE.equals(message.getType())) {
            this.onReceive(dinfo, message.getUsernameUpdate());
        } else {
                throw new RuntimeException(String.format("Unhandled message of type '%s'", message.getType().name()));
        }
    }

    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.MetadataUpdate message) {
        if (!message.getOwner().equals(this.name)) {
            if (message.getAction() == BlunoteMessages.MetadataUpdate.Action.ADD) {
                this.metadata.addMetadata(message);
            } else {
                this.metadata.deleteMetadata(message);
            }
        }
    }

    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.MultiAnswer message) {
        throw new RuntimeException("User cannot handle 'BlunoteMessages.MultiAnswer'. Is not a host.");
    }

    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.Recommendation message) {
        throw new RuntimeException("User cannot handle 'BlunoteMessages.Recommendation'. Is not a host.");
    }

    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.SingleAnswer message) {
        throw new RuntimeException("User cannot handle 'BlunoteMessages.Vote'. Is not a host.");
    }

    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.SongFragment message) {
        throw new RuntimeException("User cannot handle 'BlunoteMessages.SongFragment'. Is not a host.");
    }

    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.SongRequest message) {
        if (message.getUsername().equals(this.name)) {
            ArrayList<BlunoteMessages.SongFragment> frags = this.media.getSongFragments(message.getSongId());
            for (BlunoteMessages.SongFragment frag : frags) {
                service.send(frag);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.WelcomePacket message) {
        this.service.updateHandshake(message.toByteArray());
    }

    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.Vote message) {
        throw new RuntimeException("User cannot handle 'BlunoteMessages.Vote'. Not implemented.");
    }

    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.UsernameUpdate message) {
        if (message.getOldUsername().equals(this.getName()) && BluetoothAdapter.getDefaultAdapter()
                .getAddress().equals(message.getUserId())) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PREF_USERNAME, message.getNewUsername());
            editor.commit();
        }
    }

    @Subscribe
    public void onSongRecommendation(SongRecommendationEvent event) {
        String title = event.song;
        String artist = event.artist;
        String album = event.album;
        String owner = event.owner;

        BlunoteMessages.Recommendation.Builder builder = BlunoteMessages.Recommendation.newBuilder();
        builder.setSong(title);
        builder.setArtist(artist);
        builder.setAlbum(album);
        builder.setUsername(owner);
        builder.setType(BlunoteMessages.Recommendation.Type.SONG);
        service.send(builder.build());
    }
}
