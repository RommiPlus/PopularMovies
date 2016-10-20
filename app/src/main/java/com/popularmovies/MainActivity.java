package com.popularmovies;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import fragment.MainActivityFragment;

public class MainActivity extends AppCompatActivity {
    private String mOrderInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOrderInfo = PreferenceManager.getDefaultSharedPreferences(this).
                getString(getString(R.string.order), getString(R.string.popular_movie));
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String orderInfo = PreferenceManager.getDefaultSharedPreferences(this).
                getString(getString(R.string.order), getString(R.string.popular_movie));
        if (mOrderInfo != null && !mOrderInfo.equals(orderInfo)) {
            MainActivityFragment mf = (MainActivityFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment);
            if(mf != null) {
                mf.onOderChanged();
            }

            mOrderInfo = orderInfo;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.movie_order_setting) {
            SettingActivity.actionStart(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
