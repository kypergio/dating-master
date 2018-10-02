package rencontre.dating.looveyou.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.astuetz.PagerSlidingTabStrip;

import org.json.JSONException;
import org.json.JSONObject;

import rencontre.dating.looveyou.Adapters.SuperPowerPagerAdapter;
import rencontre.dating.looveyou.Adapters.SuperPowerPaymentFragmentPagerAdapter;

import rencontre.dating.looveyou.R;
import rencontre.dating.looveyou.Services.NotificationUtils;
import rencontre.dating.looveyou.Utils.Const;
import rencontre.dating.looveyou.Utils.HelperFunctions;
import rencontre.dating.looveyou.Utils.PaymentResolver;

/**
 * Created by Anurag on 4/10/2017.
 */

public class SuperPowerActivity extends AppCompatActivity{

    private ViewPager highlight;
    private TabLayout dots;

    private ViewPager paymentPager;
    private PagerSlidingTabStrip strip;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_activity);

        PaymentResolver.setPaymentMode(PaymentResolver.PAYMENT_MODE.NONE);

        getSupportActionBar().setTitle("Super Power");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        highlight = (ViewPager) findViewById(R.id.dot_pager);
        dots = (TabLayout) findViewById(R.id.tabDots);

        dots.setupWithViewPager(highlight);
        highlight.setAdapter(new SuperPowerPagerAdapter(getSupportFragmentManager()));

        //getSuperPowerPackages();

        paymentPager = (ViewPager) findViewById(R.id.payment_pager);
        strip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        strip.setTextSize(HelperFunctions.ConvertDpToPx(this, 12));
        strip.setIndicatorColor(getResources().getColor(R.color.colorAccent));
        paymentPager.setAdapter(new SuperPowerPaymentFragmentPagerAdapter(getSupportFragmentManager()));
        strip.setViewPager(paymentPager);
        strip.setTextColor(Color.WHITE);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Const.Params.FCM_PUSHING_MESSAGE)) {
                    try {
                        String data_ = intent.getStringExtra("data");
                        JSONObject data = new JSONObject(data_);
                        NotificationUtils.MostrarNotificacion(SuperPowerActivity.this, data);

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
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity.
        }
        return super.onOptionsItemSelected(item);
    }
}
