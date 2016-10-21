package util;

import android.content.ContentValues;
import android.content.Context;

import data.MovieContract;

/**
 * Created by 123 on 2016/10/20.
 */

public class Uilities {

    public static void updateStarMovie(Context context, int isStar, int movieId) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieDetailEntry.COLUMN_STAR, isStar);
        context.getContentResolver().update(
                MovieContract.MovieDetailEntry.CONTENT_URI,
                values,
                MovieContract.MovieDetailEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(movieId)});
    }
}
