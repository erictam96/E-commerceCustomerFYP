package com.ecommerce.customer.fypproject.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ecommerce.customer.fypproject.CheckoutItemActivity;
import com.ecommerce.customer.fypproject.MainActivity;
import com.ecommerce.customer.fypproject.ManageOrderActivity;
import com.ecommerce.customer.fypproject.R;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by erict on 3/21/2018.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{
    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "default";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String TAG = "Message";

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getNotification()!=null) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getNotification().getBody());
            showNotification("Notf from console",remoteMessage.getNotification().getBody(),"");

        }
        else if(remoteMessage.getData().size()>0){
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                JSONObject message = json.getJSONObject("data");
                String msg = message.getString("message");
                String title=message.getString("title");
                JSONObject payload = message.getJSONObject("payload");
                String GOTO = payload.getString("goto");
                showNotification(title,msg,GOTO);
                Log.d("MESSAGE",msg);
            } catch (JSONException e) {
                Crashlytics.logException(e);
                // handle your exception here!
                e.printStackTrace();
            }
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
    }

    private void showNotification(String title ,String message,String GOTO) {
        PendingIntent pendingIntent;
        switch (GOTO){
            case "donepackitem":
                Intent intentCheckOrder = new Intent(this, ManageOrderActivity.class);
                intentCheckOrder.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                pendingIntent = PendingIntent.getActivity(this,
                        0,intentCheckOrder,PendingIntent.FLAG_UPDATE_CURRENT);
                break;
            case "checkoutorder":
                Intent intentPackOrder = new Intent(this, CheckoutItemActivity.class);
                intentPackOrder.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                pendingIntent = PendingIntent.getActivity(this,
                        0,intentPackOrder,PendingIntent.FLAG_UPDATE_CURRENT);
                break;

            default:
                Intent defaultIntent = new Intent(this, MainActivity.class);
                defaultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                pendingIntent = PendingIntent.getActivity(this,
                        0,defaultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                break;
        }


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{500,1000});
            notificationChannel.enableVibration(true);
            Objects.requireNonNull(notificationManager).createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{500,1000})
                .setLights(Color.BLUE,4000,8000)
                .setSmallIcon(R.drawable.cashierbooknotification)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.mipmap.ic_cashierbook_logo))
                .setContentTitle(title)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message);

        Objects.requireNonNull(notificationManager).notify(NOTIFICATION_ID, builder.build());
    }
}
