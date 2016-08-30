package com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import model.PopularMovie;

public class DetailActivity extends AppCompatActivity {

    public static final String DETAIL_MOVIE_INFO = "DETAIL_MOVIE";
    public static final String EXTRA_BUNDLE = "EXTRA_BUNDLE";

    public static void actionStart(Context context, PopularMovie.ResultsBean bean) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(DETAIL_MOVIE_INFO, bean);

        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_BUNDLE, bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
