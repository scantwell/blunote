package com.drexelsp.blunote;
import com.drexelsp.blunote.BlunoteMessages.Pdu;

/**
 * Created by stephencantwell on 11/11/15.
 */
public class Communication {

    public void createMessage()
    {
        Pdu.Builder pduBuilder = Pdu.newBuilder();
        pduBuilder.setDeliveryInfo(createDeliveryInfo());
    }

    public BlunoteMessages.DeliveryInfo createDeliveryInfo()
    {
        BlunoteMessages.DeliveryInfo.Builder dinfoBuilder = BlunoteMessages.DeliveryInfo.newBuilder();
        dinfoBuilder.setTimestamp(getTimestamp());
        return dinfoBuilder.build();
    }

    public BlunoteMessages.DeliveryInfo createDeliveryInfo(String username)
    {
        BlunoteMessages.DeliveryInfo.Builder dinfoBuilder = BlunoteMessages.DeliveryInfo.newBuilder();
        dinfoBuilder.setTimestamp(getTimestamp());
        dinfoBuilder.setUsername(username);
        return dinfoBuilder.build();
    }

    public BlunoteMessages.SongFragment createSongFragment(int id,int totalfrags,com.google.protobuf.ByteString fragment)
    {
        BlunoteMessages.SongFragment.Builder fragBuilder = BlunoteMessages.SongFragment.newBuilder();
        fragBuilder.setFragment(fragment);
        fragBuilder.setFragmentId(id);
        fragBuilder.setTotalFragments(totalfrags);
        return fragBuilder.build();
    }

    private long getTimestamp()
    {
        return System.currentTimeMillis()/1000;
    }
}
