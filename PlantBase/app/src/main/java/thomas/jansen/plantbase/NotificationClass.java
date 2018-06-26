package thomas.jansen.plantbase;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;

public class NotificationClass {

    Context context;
    StringBuilder names;
    NotificationManager notificationManager;
    String CHANNEL_ID = "WaterNotification";
    int NOTIFICATION_ID = 3;




    public NotificationClass(Context context, ArrayList<String> plantNames) {
        this.context = context;

        names = new StringBuilder();
        for (int x = 0; x < plantNames.size(); x++) {
            names.append(plantNames.get(x));
        }
    }

    public void createNotificationChannel () {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Plantbase";
            String description = "Waternotify";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        System.out.println("notificatie??");

        Intent intent = new Intent(context, MyPlantsList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_local_florist_24)
                .setContentTitle("PlantBase")
                .setContentText("These plants need water: " + names)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        //Show the notification
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}


