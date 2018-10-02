package rencontre.dating.looveyou.Services;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import rencontre.dating.looveyou.Activities.ChatActivity;
import rencontre.dating.looveyou.Activities.ChatHistoryActivity;
import rencontre.dating.looveyou.Activities.DisplayActivity;
import rencontre.dating.looveyou.MainActivity;
import rencontre.dating.looveyou.Utils.Const;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private Vibrator vibrator;

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (remoteMessage == null)
            return;

        /*// Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }*/

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("notification");

            if(!NotificationUtils.isAppIsInBackground(getApplicationContext()))
            {
                Intent pushNotification = new Intent(Const.Params.FCM_PUSHING_MESSAGE);
                pushNotification.putExtra("data", data.toString());
                sendBroadcast(pushNotification);
            }else{
                NotificationUtils.MostrarNotificacion(getApplicationContext(), data);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }
}