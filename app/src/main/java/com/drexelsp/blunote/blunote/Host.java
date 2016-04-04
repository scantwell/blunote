package com.drexelsp.blunote.blunote;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by scantwell on 3/31/2016.
 */
public class Host extends User {

    private Player player;

    public Host(Service service, Context context) {
        super(service, context);
        this.player = new Player(context);
    }

    @Override
    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.MultiAnswer message)
    {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.Recommendation message)
    {
        throw new RuntimeException("User cannot handle 'BlunoteMessages.Recommendation'. Is not a host.");
    }

    @Override
    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.SingleAnswer message)
    {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.SongFragment message)
    {

    }

    @Override
    public void onReceive(BlunoteMessages.DeliveryInfo dinfo, BlunoteMessages.Vote message)
    {
        throw new RuntimeException("Not implemented.");
    }
}
