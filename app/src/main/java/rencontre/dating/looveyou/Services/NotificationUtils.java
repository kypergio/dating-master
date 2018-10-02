package rencontre.dating.looveyou.Services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import rencontre.dating.looveyou.Activities.ChatActivity;
import rencontre.dating.looveyou.Activities.DisplayActivity;
import rencontre.dating.looveyou.Applications.ApplicationSingleTon;
import rencontre.dating.looveyou.MainActivity;
import rencontre.dating.looveyou.R;
import rencontre.dating.looveyou.Utils.Const;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationUtils {

    private static String TAG = NotificationUtils.class.getSimpleName();

    private Context mContext;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void showNotificationMessage(String title, String message, Intent intent) {
        showNotificationMessage(title, message, intent, null);
    }

    public void showNotificationMessage(final String title, final String message, Intent intent, String imageUrl) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;


        // notification icon
        /*final int icon = R.drawable.notification;*/

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext);

        final Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        /*if (!TextUtils.isEmpty(imageUrl)) {

            if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

                Bitmap bitmap = getBitmapFromURL(imageUrl);

                if (bitmap != null) {
                    showBigNotification(bitmap, mBuilder, icon, title, message, resultPendingIntent, alarmSound);
                } else {
                    showSmallNotification(mBuilder, icon, title, message,resultPendingIntent, alarmSound);
                }
            }*/
        //} else {
        /*showSmallNotification(mBuilder, icon, title, message, resultPendingIntent, alarmSound);*/
        //playNotificationSound();
        //}
    }


    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, PendingIntent resultPendingIntent, Uri alarmSound) {

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(message);

        Notification notification;
        /*notification = mBuilder.setSmallIcon(R.drawable.notification).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(inboxStyle)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentText(message)
                .build();*/

        /*NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(Constantes.NOTIFICATION_ID, notification);
        Constantes.NOTIFICATION_ID++;*/
    }


    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void MostrarNotificacion(final Context context, final Vibrator vibrator, final String titulo, final String message, String icon, final JSONObject extraData, final Class fragmento) {

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(titulo)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mBuilder.setSmallIcon(R.drawable.icon);
        Intent resultIntent = new Intent(context, fragmento);
        try {
            if (extraData.getString("notification_type").equals("new_message")) {
                resultIntent.putExtra("receiverId", extraData.getString("user_id"));
                resultIntent.putExtra("receiverImageUrl", icon);
                resultIntent.putExtra("receiverName", extraData.getString("user_name"));
                resultIntent.putExtra("contact_id", extraData.getString("contact_id"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        Notification notification = new NotificationCompat.BigTextStyle(mBuilder)
                .bigText(message)
                .setBigContentTitle(titulo)
                .build();

        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationmanager.notify(Const.Params.notification_id, notification);

        vibrator.vibrate(500);
        Const.Params.notification_id++;

    }

    public static void MostrarNotificacion(final Context context, final JSONObject data) {
        try {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            String title = data.getString("title");
            String message = data.getString("body");
            String icon = data.getString("icon");
            JSONObject custom_data = data.getJSONObject("custom_data");
            Class fragment;
            String notification_type = custom_data.getString("notification_type");
            String username = custom_data.getString("user_name");
            switch (notification_type) {
                case "new_message":
                    fragment = ChatActivity.class;
                    break;
                case "visitor":
                    if (username.equals(""))
                        username = "Soemone";
                    message = username + " has visited your profile.";
                    fragment = DisplayActivity.class;
                    break;
                case "liked":
                    if (username.equals(""))
                        username = "Soemone";
                    message = username + " has liked your profile.";
                    fragment = DisplayActivity.class;
                    break;
                default:
                    fragment = DisplayActivity.class;
                    break;
            }
            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mBuilder.setSmallIcon(R.drawable.icon);
            Intent resultIntent = new Intent(context, fragment);

            if (custom_data.getString("notification_type").equals("new_message")) {
                resultIntent.putExtra("receiverId", custom_data.getString("user_id"));
                resultIntent.putExtra("receiverImageUrl", icon);
                resultIntent.putExtra("receiverName", custom_data.getString("user_name"));
                /*resultIntent.putExtra("contact_id", custom_data.getString("contact_id"));*/
            }

            resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            Notification notification = new NotificationCompat.BigTextStyle(mBuilder)
                    .bigText(message)
                    .setBigContentTitle(title)
                    .build();

            NotificationManager notificationmanager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationmanager.notify(Const.Params.notification_id, notification);

            vibrator.vibrate(500);
            Const.Params.notification_id++;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
