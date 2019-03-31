package com.example.ridesafedatacollection;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class notificationConfig extends Application {
    public static final String CHANNEL_ID = "RideSafeDataCollectionServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel SensorServiceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "RideSafeDataCollection Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(SensorServiceChannel);
        }
    }
}
