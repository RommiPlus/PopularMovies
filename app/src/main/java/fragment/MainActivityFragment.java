package fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.popularmovies.MainActivity;
import com.popularmovies.R;

import adapter.MoviePreviewAdapter;
import data.Constant;
import data.MovieContract.MovieDetailEntry;
import sync.MovieSyncAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private int mPosition = GridView.INVALID_POSITION;
    private GridView mMovieGv;
    private MoviePreviewAdapter mMovieAdapter;

    private final int UNIQUE_LOADER = 0;
    private boolean mIsFirstLoad = true;
    private final String SCROLLED_POSITION = "SCROLLED_POSITION";

    public MainActivityFragment() {
    }

    public interface OnItemChangedListener {
        void onItemClicked(int movieId);

        void onNewDataReady(int movieId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(SCROLLED_POSITION)) {
            mPosition = savedInstanceState.getInt(SCROLLED_POSITION);
        }

        mMovieAdapter = new MoviePreviewAdapter(getContext(), null, 0);
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mMovieGv = (GridView) view.findViewById(R.id.movie_gv);
        mMovieGv.setAdapter(mMovieAdapter);
        mMovieGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPosition = position;
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                int movieId = c.getInt(c.getColumnIndex(MovieDetailEntry.COLUMN_MOVIE_ID));

                if (mIsFirstLoad) {
                    ((MainActivity) getActivity()).onNewDataReady(movieId);
                    mIsFirstLoad = false;
                } else {
                    ((MainActivity) getActivity()).onItemClicked(movieId);
                }
            }
        });
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SCROLLED_POSITION, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(UNIQUE_LOADER, null, this);
    }

    public void onOderChanged() {
        mIsFirstLoad = true;
        mPosition = 0;

        updatePopularMovies();
        getLoaderManager().restartLoader(UNIQUE_LOADER, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        updatePopularMovies();
    }

    private void updatePopularMovies() {
        MovieSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String orderInfo = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.order), getString(R.string.popular_movie));

        String selection;
        String[] selectionArgs;
        if (orderInfo.equals(getString(R.string.popular_movie))) {
            selection = MovieDetailEntry.COLUMN_POP_MOVIES + " = ?";
            selectionArgs = new String[]{String.valueOf(Constant.IS_POP_MOVIE)};
        } else if (orderInfo.equals(getString(R.string.top_rated))) {
            selection = MovieDetailEntry.COLUMN_TOP_RANKED + " = ?";
            selectionArgs = new String[]{String.valueOf(Constant.IS_TOP_RANKED)};
        } else {
            selection = MovieDetailEntry.COLUMN_STAR + " = ?";
            selectionArgs = new String[]{String.valueOf(Constant.IS_STAR)};
        }

        return new CursorLoader(getContext(),
                MovieDetailEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);

        if (mPosition == GridView.INVALID_POSITION) {
            mPosition = 0;
        }

        if (mIsFirstLoad && data.moveToFirst()) {
            mMovieGv.performItemClick(mMovieGv.getAdapter().getView(mPosition, null, null),
                    mPosition,
                    mMovieGv.getAdapter().getItemId(mPosition));

        }

        mMovieGv.smoothScrollToPosition(mPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }


}
