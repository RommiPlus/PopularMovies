package util;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import adapter.DetailAdapter.*;
import adapter.DetailAdapter.ReviewHeader;
import data.MovieContract.MovieDetailEntry;
import data.MovieContract.MovieReviewsEntry;
import data.MovieContract.MovieVideoEntry;
import data.dao.MovieDetailInfo;
import data.dao.Reviews;
import data.dao.Videos;

/**
 * Created by 123 on 2016/10/19.
 */

public class DetailMovieDataLoader extends AsyncTaskLoader<List<Object>> {

    final ForceLoadContentObserver mObserver;
    private int mMovieId;

    public DetailMovieDataLoader(Context context, int movieId) {
        super(context);
        mObserver = new ForceLoadContentObserver();
        mMovieId = movieId;
    }

    @Override
    public List<Object> loadInBackground() {
        MovieDetailInfo info = CursorUlities.cursorToMovieDetailInfo(getMovieDetailCursor());
        if (info == null) {
            throw new IllegalArgumentException("Movie detail info should not be null");
        }

        List<Videos> videos = CursorUlities.cursorToMovieVideos(getMovieVideoCursor());
        List<Reviews> reviews = CursorUlities.cursorToMovieReivews(getMovieReviewsCursor());

        ArrayList<Object> list = new ArrayList<>();
        list.add(info);
        list.add(new TrailerHear());
        if (videos != null) {
            list.addAll(videos);
        }

        list.add(new ReviewHeader());
        if (reviews != null) {
            list.addAll(reviews);
        }
        return list;
    }

    @Override
    public void deliverResult(List<Object> data) {
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
    }

    public Cursor getMovieDetailCursor() {
        Cursor cursor = getContext().getContentResolver()
                .query(MovieDetailEntry.CONTENT_URI,
                        null,
                        MovieDetailEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(mMovieId)},
                        null);

        return cursor;
    }

    public Cursor getMovieVideoCursor() {
        Cursor cursor = getContext().getContentResolver()
                .query(MovieVideoEntry.CONTENT_URI,
                        null,
                        MovieVideoEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(mMovieId)},
                        null);

        return cursor;
    }

    public Cursor getMovieReviewsCursor() {
        Cursor cursor = getContext().getContentResolver()
                .query(MovieReviewsEntry.CONTENT_URI,
                        null,
                        MovieReviewsEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(mMovieId)},
                        null);

        registerContentObserver(cursor);
        return cursor;
    }

    public void registerContentObserver(Cursor cursor) {
        if (cursor != null) {
            try {
                // Ensure the cursor window is filled.
                cursor.getCount();
                cursor.registerContentObserver(mObserver);
            } catch (RuntimeException ex) {
                cursor.close();
                throw ex;
            }
        }
    }


}
