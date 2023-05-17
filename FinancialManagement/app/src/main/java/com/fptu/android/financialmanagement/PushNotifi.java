package com.fptu.android.financialmanagement;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotifi extends FirebaseMessagingService {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        String title = message.getNotification().getTitle();
        String text = message.getNotification().getBody();
        final String CHANNEL_D = "HEAD";
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_D, "head",
                NotificationManager.IMPORTANCE_HIGH
        );
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_D)
                .setContentTitle(title)
                        .setContentText(text)
                                .setSmallIcon(R.drawable.download);
        NotificationManagerCompat.from(this).notify(1, notification.build());
        super.onMessageReceived(message);
    }
}
