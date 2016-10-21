package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.popularmovies.DetailActivity;
import com.popularmovies.R;

import java.util.ArrayList;
import java.util.List;

import adapter.DetailAdapter;
import service.DataSyncIntentService;
import util.DetailMovieDataLoader;
import util.NetworkUtil;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Object>> {
    private RecyclerView mRecyclerView;
    private DetailAdapter mDetailAdapter;

    List<Object> mAdapterData;

    private static final int ERROR_MOVIE_ID = -1;

    private final int DETAIL_ID = 0;

    private int mMovieId;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        mAdapterData = new ArrayList<>();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mDetailAdapter = new DetailAdapter(mAdapterData, getContext());
        mRecyclerView.setAdapter(mDetailAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Intent intent = getActivity().getIntent();
        mMovieId = intent.getIntExtra(DetailActivity.DETAIL_MOVIE_INFO, ERROR_MOVIE_ID);

        if (mMovieId != ERROR_MOVIE_ID && NetworkUtil.isOnline(getActivity())) {
            queryMovieInfo(mMovieId);
        }

        return view;
    }

    private void queryMovieInfo(int movieId) {
        DataSyncIntentService.startService(getActivity(), movieId);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_ID, null, this);
    }

    @Override
    public Loader<List<Object>> onCreateLoader(int id, Bundle args) {
        return new DetailMovieDataLoader(getActivity(), mMovieId);
    }

    @Override
    public void onLoadFinished(Loader<List<Object>> loader, List<Object> data) {
        mDetailAdapter.swapData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Object>> loader) {
        mDetailAdapter.swapData(null);
    }

}
