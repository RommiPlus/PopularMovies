package util;

import android.content.ContentValues;
import android.content.Context;

import data.Constant;
import data.MovieContract;
import data.dao.MovieDetailInfo;

/**
 * Created by 123 on 2016/10/20.
 */

public class Uilities {

    public static void updateToStarMovie(Context context, final MovieDetailInfo info) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieDetailEntry.COLUMN_STAR, Constant.IS_STAR);
        context.getContentResolver().update(
                MovieContract.MovieDetailEntry.CONTENT_URI,
                values,
                MovieContract.MovieDetailEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(info.getMovieId())});
    }
}
