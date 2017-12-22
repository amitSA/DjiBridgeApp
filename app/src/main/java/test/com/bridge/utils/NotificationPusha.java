package test.com.bridge.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import test.com.bridge.R;

/**
 * Created by Amit on 7/26/2017.
 */

/**
 * Utility class to send notifications to the android system's notification drawer
 */
public class NotificationPusha {
    private static NotificationPusha instance = null;

    private Context appContext;


    private NotificationPusha(Context c) {
        appContext = c.getApplicationContext();
    }

    /**
     * Returns the singleton NotificationPusha instance, creating it if it does not currently exist.
     * @return the singleton NotificationPusha instance
     */
    public static NotificationPusha getInstance(){
        if(instance == null){
            instance = new NotificationPusha(HelpMes.getApplicationContext());
        }
        return instance;
    }

    public void sendSimpleNotification(int id, String title, String content, TaskStackBuilder sBuilder){
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(appContext); //TODO: this method was deprecated in API Level 26
        nBuilder.setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(content);
        PendingIntent pendingIntent = sBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, nBuilder.build());
    }

    public void cancelNotification(int id){
        NotificationManager nManager = (NotificationManager)appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.cancel(id);
    }

}
