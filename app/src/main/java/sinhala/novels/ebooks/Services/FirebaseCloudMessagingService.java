package sinhala.novels.ebooks.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;

import sinhala.novels.ebooks.MainActivity;
import sinhala.novels.ebooks.R;
import sinhala.novels.ebooks.SplashActivity;

public class FirebaseCloudMessagingService extends FirebaseMessagingService {

    private int notification_id=123;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        if (!remoteMessage.getData().isEmpty()){

            Map<String, String> map =remoteMessage.getData();
            String title=map.get("title");
            String message=map.get("message");

            Intent resultIntent = new Intent(this, SplashActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationManagerCompat.from(this).notify(notification_id,
                    createNotification(this,
                            R.mipmap.ic_launcher_round,
                            title,
                            message,
                            "your_channel_id",
                            "your_channel_name",
                            "your_channel_description",
                            pendingIntent,
                            null
                            ));

            notification_id++;

        }

    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        sendTokenToService(token);
    }

    private void sendTokenToService(String token) {
        Log.d("??",token);
    }

    private Notification createNotification(Context context, int smallIcon, String title, String message, String channelID, String channelNameForAndroidO,
                                            String channelDescriptionForAndroidO, PendingIntent pendingIntent, String category){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannel=new NotificationChannel(channelID,channelNameForAndroidO, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(channelDescriptionForAndroidO);

            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel);

        }

        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,channelID);
        builder.setSmallIcon(smallIcon).setContentTitle(title).setContentText(message).setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (category!=null && !category.isEmpty()){
            builder.setCategory(category);
        }

        builder.setAutoCancel(true);

        if (pendingIntent!=null){
            builder.setContentIntent(pendingIntent);
        }

        return builder.build();

    }

}
