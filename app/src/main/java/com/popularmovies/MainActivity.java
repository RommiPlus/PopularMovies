package com.popularmovies;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import fragment.DetailActivityFragment;
import fragment.MainActivityFragment;
import sync.MovieSyncAdapter;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.OnItemChangedListener {
    private String mOrderInfo;
    private boolean mTwoPlane;
    private static final String DETAIL_FRAGMENT = "DETAIL_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOrderInfo = PreferenceManager.getDefaultSharedPreferences(this).
                getString(getString(R.string.order), getString(R.string.popular_movie));
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPlane = true;

            if (getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT) == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailActivityFragment(), DETAIL_FRAGMENT)
                        .commit();
            }
        } else {
            mTwoPlane = false;
        }

        MovieSyncAdapter.initializeSyncAdapter(this);
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
            if (mf != null) {
                mf.onOderChanged();
            }

//            DetailActivityFragment df = (DetailActivityFragment) getSupportFragmentManager()
//                    .findFragmentByTag(DETAIL_FRAGMENT);
//            if (null != df) {
//                df.onOderChanged();
//            }
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

    @Override
    public void onItemClicked(int movieId) {
        if (mTwoPlane) {
            DetailActivityFragment fragment = (DetailActivityFragment)
                    getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT);
            fragment.setData(movieId);
            return;
        }

        DetailActivity.actionStart(this, movieId);
    }
}
