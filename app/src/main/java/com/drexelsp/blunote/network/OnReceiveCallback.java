package com.drexelsp.blunote.network;

/**
 * Created by stephencantwell on 4/26/16.
 */
public class OnReceiveCallback implements Callback{

    private NetworkService service;

    public OnReceiveCallback(NetworkService service)
    {
        this.service = service;
    }


    @Override
    public void onReceivePacket(byte[] data) {
        this.service.onReceiveUpstream(data);
    }
}
