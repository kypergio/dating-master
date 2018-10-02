package rencontre.dating.looveyou.Activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import rencontre.dating.looveyou.Networking.Endpoints;
import rencontre.dating.looveyou.R;
import rencontre.dating.looveyou.Utils.Const;
import rencontre.dating.looveyou.Utils.HelperMethods;
import rencontre.dating.looveyou.Utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Anurag on 4/11/2017.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText email_et, password_et;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");

        email_et = (EditText) findViewById(R.id.email_et);
        password_et = (EditText) findViewById(R.id.password_et);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkForEmpty()) {
                    login();
                }
            }
        });
    }

    private void login() {
        final ProgressDialog progressDialog = HelperMethods.getLoadingDialog(this, "Logging in...");
        progressDialog.show();

        JsonObject json = new JsonObject();
        json.addProperty("username", email_et.getText().toString());
        json.addProperty("password", password_et.getText().toString());

        Ion.with(this)
                .load(Endpoints.loginUrl)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if (result == null)
                            return;
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());
                            if (jsonObject.optString(Const.Params.STATUS).equals("success")) {
                                JSONObject successObject = jsonObject.getJSONObject("success_data");
                                SharedPreferencesUtils.setParam(LoginActivity.this, SharedPreferencesUtils.SESSION_TOKEN, successObject.optString("access_token"));
                                SharedPreferencesUtils.setParam(LoginActivity.this, SharedPreferencesUtils.USER_ID, successObject.optString("user_id"));
                                final Intent intent = new Intent(LoginActivity.this, DisplayActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                SharedPreferencesUtils.setParam(LoginActivity.this, SharedPreferencesUtils.SESSION_TOKEN, successObject.optString("access_token"));
                                SharedPreferencesUtils.setParam(LoginActivity.this, SharedPreferencesUtils.USER_ID, successObject.optString("user_id"));
                                String android_id = Settings.Secure.getString(getContentResolver(),
                                        Settings.Secure.ANDROID_ID);
                                JsonObject datos = new JsonObject();
                                JsonObject user = new JsonObject();
                                user.addProperty("id", successObject.optString("user_id"));
                                datos.add("real_auth_user", user);
                                datos.addProperty("device_id", android_id);
                                datos.addProperty("device_token", FirebaseInstanceId.getInstance().getToken());
                                Ion.with(getApplicationContext())
                                        .load(Endpoints.token_firebase)
                                        .setJsonObjectBody(datos)
                                        .asJsonObject()
                                        .setCallback(new FutureCallback<JsonObject>() {
                                            @Override
                                            public void onCompleted(Exception e, JsonObject result) {
                                                progressDialog.hide();
                                                startActivity(intent);
                                                finish();
                                            }
                                        });

                            } else {
                                JSONObject erorObject = jsonObject.getJSONObject("error_data");
                                Toast.makeText(LoginActivity.this, erorObject.optString("error_text"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });

    }

    private boolean checkForEmpty() {

        if (email_et.getText().toString().isEmpty()) {
            email_et.setError("Enter a email address");
            return false;
        } else {
            if (!isValidEmail(email_et.getText().toString())) {
                email_et.setError("Enter a valid email address");
                return false;
            }
        }
        if (password_et.getText().toString().isEmpty() || password_et.getText().toString().length() < 8) {
            password_et.setError("Enter a password with minimum 8 letters");
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }
}