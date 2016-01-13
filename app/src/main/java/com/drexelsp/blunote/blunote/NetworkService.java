package com.drexelsp.blunote.blunote;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.os.Messenger;
import android.util.Log;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;
import android.content.Context;

/**
 * Created by scantwell on 1/12/2016.
 */
public class NetworkService extends Service {

    private Messenger messenger = new Messenger(new ClientHandler());
    private String TAG = "NetworkService";
    private int NOTIFICATION_ID = 1234;

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        Log.v(TAG, "Binding user.");
        return messenger.getBinder();
    }

    @Override
    public void onCreate() { Log.v(TAG, "Created service."); }

    @Override
    public void onDestroy()
    {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // notificationId allows you to update the notification later on.
        mNotificationManager.cancel(NOTIFICATION_ID);
        Log.v(TAG, "Destroyed service.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // Must be created in onStartCommand else NotificationManager will be null.
        this.createNotification();
        // If the service is stopped it will be started again with original intent
        // Needed for determining the type of mesh network
        return START_REDELIVER_INTENT;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        Log.v(TAG, "Unbinding user.");
        return true;
    }

    @Override
    public void onRebind(Intent intent) { Log.v(TAG, "Rebind user."); }

    // Private

    private void createNotification()
    {
        Notification note =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("BluNote Network Service")
                        .setContentText("Hello.").build();
        note.flags |= Notification.FLAG_NO_CLEAR;
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, note);
    }
}
