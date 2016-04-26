package com.drexelsp.blunote.network;

/**
 * Created by stephencantwell on 4/26/16.
 */
public class DownstreamCallback implements Callback{
    private Router router;

    public DownstreamCallback(Router router)
    {
        this.router = router;
    }

    public void onReceivePacket(byte[] data)
    {
        this.router.addUpstreamMessage(data);
    }
}
