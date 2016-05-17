package com.drexelsp.blunote.network;

import android.util.Log;

import java.io.IOException;

/**
 * Created by stephencantwell on 4/25/16.
 */
public class Reader implements Runnable {

    private BlunoteSocket socket;
    private BlunoteInputStream inputStream;
    private Callback callback;

    public Reader(Callback callback, BlunoteSocket socket) throws IOException {
        this.callback = callback;
        this.socket = socket;
        this.inputStream = socket.getInputStream();
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] buf = this.inputStream.rawRead();
                callback.onReceivePacket(buf);
            } catch (IOException e) {
                try {
                    this.inputStream = socket.getInputStream();
                } catch (IOException e1) {
                    Log.w("Reader", String.format("Failed to read from connection %s. %s", socket.getAddress(), e1.getMessage()));
                    callback.onReadFailure(socket);
                    return;
                }
            }
        }
    }
}
