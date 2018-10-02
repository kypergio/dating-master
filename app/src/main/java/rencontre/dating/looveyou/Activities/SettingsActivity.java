package rencontre.dating.looveyou.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import rencontre.dating.looveyou.Adapters.MySettingListAdapter;
import rencontre.dating.looveyou.R;
import rencontre.dating.looveyou.Services.NotificationUtils;
import rencontre.dating.looveyou.Utils.Const;

public class SettingsActivity extends AppCompatActivity {

    private RecyclerView setting_list;
    private MySettingListAdapter adapter;
   // private String[] titles = {"Basic info", "Account", "Account Preferences", "Help Centre", "About"};
   private String[] titles = {"Basic info", "Account"};
   private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setting_list = (RecyclerView) findViewById(R.id.setting_list);
        adapter = new MySettingListAdapter(titles, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        setting_list.setLayoutManager(linearLayoutManager);
        setting_list.setItemAnimator(new DefaultItemAnimator());
        setting_list.setAdapter(adapter);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Const.Params.FCM_PUSHING_MESSAGE)) {
                    try {
                        String data_ = intent.getStringExtra("data");
                        JSONObject data = new JSONObject(data_);
                        NotificationUtils.MostrarNotificacion(SettingsActivity.this, data);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(Const.Params.FCM_PUSHING_MESSAGE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
