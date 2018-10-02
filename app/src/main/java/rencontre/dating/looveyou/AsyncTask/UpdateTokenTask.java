package rencontre.dating.looveyou.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import rencontre.dating.looveyou.Utils.SharedPreferencesUtils;

public class UpdateTokenTask extends AsyncTask<String,Void,Boolean> {

    private String token;
    private Context ctx;

    public UpdateTokenTask(String token, Context ctx) {
        this.token = token;
        this.ctx = ctx;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        RequestWeb peticionWeb = new RequestWeb("notifications/push/register-device","POST",true);

        JSONObject datos = new JSONObject();
        try {
            String android_id = Settings.Secure.getString(ctx.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            String userId = (String) SharedPreferencesUtils.getParam(ctx, SharedPreferencesUtils.USER_ID, "");
            if (userId.equals(""))
                return null;

            JSONObject user = new JSONObject();
            user.put("id", userId);
            datos.put("real_auth_user", user);
            datos.put("device_id", android_id);
            datos.put("device_token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        peticionWeb.enviar(datos);

        JSONObject d = peticionWeb.getJsonObject();

        return null;
    }
}
