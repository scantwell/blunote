package com.drexelsp.blunote.network;

/**
 * Created by stephencantwell on 4/26/16.
 */
public class UpstreamCallback implements Callback {

    private Router router;

    public UpstreamCallback(Router router) {
        this.router = router;
    }

    public void onReceivePacket(byte[] data) {
        router.addDownstreamMessage(data);
    }
}
