package com.drexelsp.blunote.blunote;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

import org.greenrobot.eventbus.Subscribe;

import com.drexelsp.blunote.blunote.BlunoteMessages.DeliveryInfo;
import com.drexelsp.blunote.blunote.BlunoteMessages.MultiAnswer;
import com.drexelsp.blunote.blunote.BlunoteMessages.Recommendation;
import com.drexelsp.blunote.blunote.BlunoteMessages.SingleAnswer;
import com.drexelsp.blunote.blunote.BlunoteMessages.SongFragment;
import com.drexelsp.blunote.blunote.BlunoteMessages.SongRequest;
import com.drexelsp.blunote.blunote.BlunoteMessages.Vote;
import com.drexelsp.blunote.events.SongRecommendationEvent;
import com.drexelsp.blunote.network.NetworkNode;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by scantwell on 3/31/2016.
 */
public class Host extends User implements Observer {

    protected static String TAG = "HOST";
    private Player player;
    private ConcurrentHashMap<Long, Song> songHash;
    private String serverName;
    private int numUsers;
    private NetworkNode networkTree;

    public Host(Service service, Context context) {
        super(service, context);
        this.songHash = new ConcurrentHashMap<>();
        this.numUsers = 1;
        this.networkTree = new NetworkNode(BluetoothAdapter.getDefaultAdapter().getAddress());
        this.name = PreferenceManager.getDefaultSharedPreferences(context).getString("pref_key_user_name", BluetoothAdapter
                .getDefaultAdapter().getName());
        this.serverName = PreferenceManager.getDefaultSharedPreferences(context).getString("pref_key_server_name", "Party Jamz");
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

    public void onReceive(DeliveryInfo dinfo, BlunoteMessages.NetworkConnection message) {
        if (message.getType() == BlunoteMessages.NetworkConnection.Type.CONNECTION) {
            String macAddress = message.getMacAddress();
            String hostMacAddress = message.getHostMacAddress();
            this.networkTree.addToNode(hostMacAddress, macAddress);
            Log.v(TAG, String.format("Connection, Mac: %s, Host: %s", macAddress, hostMacAddress));
        } else if (message.getType() == BlunoteMessages.NetworkConnection.Type.DISCONNECTION) {
            String macAddress = message.getMacAddress();
            ArrayList<String> droppedConnections = this.networkTree.removeNodeSubTree(macAddress);
            Log.v(TAG, String.format("Disconnection, Macs to remove: %s", droppedConnections.toString()));
            for (String user_id : droppedConnections) {
                BlunoteMessages.MetadataUpdate update = metadata.deleteHostMetadata(user_id);
                service.send(update);
            }
        }
    }

    @Override
    public void onReceive(DeliveryInfo dinfo, BlunoteMessages.MetadataUpdate message)
    {
        BlunoteMessages.MetadataUpdate update;
        addUser();
        update = this.metadata.addHostMetadata(message);

        updateWelcomePacket();
        this.service.send(update);
    }

    @Override
    public void onReceive(DeliveryInfo dinfo, MultiAnswer message) {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public void onReceive(DeliveryInfo dinfo, Recommendation message) {
        Log.v(TAG, "Entered on received");
        int id = media.findSongId(message.getSong(),
                message.getArtist(), message.getAlbum());
        String username = message.getUsername().isEmpty() ?
                media.findSongUsername(message.getSong(), message.getArtist(), message.getAlbum()) : message.getUsername();
        if (username.equals(this.getName())) {
            playerSongById(id);
        } else {
            addSongRequest(username, id);
        }
        //throw new RuntimeException("Not implemented.");
    }

    @Override
    public void onReceive(DeliveryInfo dinfo, SingleAnswer message) {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public void onReceive(DeliveryInfo dinfo, SongFragment frag) {
        if (songHash.containsKey(frag.getSongId())) {
            this.songHash.get(frag.getSongId()).addFragment(frag);
        } else {
            Log.w(TAG, String.format("Cannot process unrecognized song fragment. id(%d) fragment(%d/%d)",
                    frag.getSongId(),
                    frag.getFragmentId(),
                    frag.getTotalFragments()));
        }
    }

    @Override
    public void onReceive(DeliveryInfo dinfo, Vote message) {
        throw new RuntimeException("Not implemented.");
    }

    /*
    Could potentially cause problems with onRecommendation because of the User class also implementing the same functionality
     */
    @Override
    @Subscribe
    public void onSongRecommendation(SongRecommendationEvent event) {
        long id = Long.parseLong(event.songId);
        String owner = event.owner;

        if (owner.equals(this.name)) {
            playerSongById(id);
        } else {
            addSongRequest(event.owner, id);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        Song song = (Song) observable;
        player.addSongUri(song.getUri());
        songHash.remove(song.getId());
    }

    private void addSongRequest(String username, long id) {
        sendSongRequest(username, id);
        addNewSong(id);
    }

    private void sendSongRequest(String username, long id) {
        SongRequest.Builder builder = SongRequest.newBuilder();
        builder.setSongId(id);
        builder.setUsername(username);
        service.send(builder.build());
    }

    private void addNewSong(long songId) {
        if (songHash.containsKey(songId)) {
            throw new RuntimeException("Song has already been registered.");
        } else {
            try {
                File file = createTempFile();
                Song s = new Song(songId, file);
                s.addObserver(this);
                songHash.put(songId, s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File createTempFile() throws IOException {
        return File.createTempFile(java.util.UUID.randomUUID().toString(), ".mp3", this.context.getCacheDir());
    }

    private void playerSongById(long id) {
        String uri = this.media.getSongUri(id);
        player.addSongUri(Uri.parse(uri));
    }

    private void updateWelcomePacket() {
        BlunoteMessages.WelcomePacket wp = getWelcomePacket();
        this.service.updateHandshake(wp.toByteArray());
        this.service.send(wp);
    }

    public BlunoteMessages.WelcomePacket getWelcomePacket() {
        BlunoteMessages.WelcomePacket.Builder wp = BlunoteMessages.WelcomePacket.newBuilder();
        wp.setNetworkName(this.serverName);
        wp.setNumSongs(this.metadata.getSongCount());
        wp.setNumUsers(Integer.toString(this.numUsers));
        return wp.build();
    }
}
