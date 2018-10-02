package rencontre.dating.looveyou.Services;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import rencontre.dating.looveyou.AsyncTask.UpdateTokenTask;
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();

        // Guardar registro en API Web
        registrarAPIWeb(token, getApplicationContext());

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        /*Intent registrationComplete = new Intent(Constantes.FIREBASE_REGISTRO_COMPLETE_RECEIVER);
        registrationComplete.putExtra("token", token);
        sendBroadcast(registrationComplete);*/
    }

    private void registrarAPIWeb(final String token, Context ctx) {
        new UpdateTokenTask(token, ctx).execute();
    }
}