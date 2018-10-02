package rencontre.dating.looveyou.Activities;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import rencontre.dating.looveyou.Fragments.FavouritesFragment;
import rencontre.dating.looveyou.Fragments.LikedYouFragment;
import rencontre.dating.looveyou.Fragments.VisitorsFragment;
import rencontre.dating.looveyou.R;
import rencontre.dating.looveyou.Services.NotificationUtils;
import rencontre.dating.looveyou.Utils.Const;

public class ActivityPageActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(),
                this));
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Const.Params.FCM_PUSHING_MESSAGE)) {
                    try {
                        String data_ = intent.getStringExtra("data");
                        JSONObject data = new JSONObject(data_);
                        NotificationUtils.MostrarNotificacion(ActivityPageActivity.this, data);

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

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 3;
        private String tabTitles[] = new String[]{"Liked You", "Visitors", "Favourites"};
        private Context context;

        public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new LikedYouFragment();
                case 1:
                    return new VisitorsFragment();
                case 2:
                    return new FavouritesFragment();

                default:
                    return new FavouritesFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }
}
