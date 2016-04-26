package com.drexelsp.blunote.network;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by stephencantwell on 4/25/16.
 */
public class Writer implements Runnable {

    private Router router;

    public Writer(Router router)
    {
        this.router = router;
    }

    @Override
    public void run() {
        while(true)
        {
            this.router.processMessage();
        }
    }
}
