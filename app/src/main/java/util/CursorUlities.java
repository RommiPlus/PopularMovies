package util;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import data.MovieContract.MovieDetailEntry;
import data.MovieContract.MovieReviewsEntry;
import data.MovieContract.MovieVideoEntry;
import data.dao.MovieDetailInfo;
import data.dao.Reviews;
import data.dao.Videos;

/**
 * Created by 123 on 2016/10/19.
 */

public class CursorUlities {
    public static MovieDetailInfo cursorToMovieDetailInfo(Cursor cursor) {
        if (cursor.moveToFirst()) {
            MovieDetailInfo info = new MovieDetailInfo();
            info.setId(cursor.getInt(cursor.getColumnIndex(MovieDetailEntry._ID)));
            info.setMovieId(cursor.getInt(cursor.getColumnIndex(MovieDetailEntry.COLUMN_MOVIE_ID)));
            info.setRuntime(cursor.getInt(cursor.getColumnIndex(MovieDetailEntry.COLUMN_RUNTIME)));
            info.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieDetailEntry.COLUMN_RELEASE_DATE)));
            info.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieDetailEntry.COLUMN_POSTER_PATH)));
            info.setOverview(cursor.getString(cursor.getColumnIndex(MovieDetailEntry.COLUMN_OVERVIEW)));
            info.setStar(cursor.getInt(cursor.getColumnIndex(MovieDetailEntry.COLUMN_STAR)));
            info.setPopMovie(cursor.getInt(cursor.getColumnIndex(MovieDetailEntry.COLUMN_POP_MOVIES)));
            info.setTopRanked(cursor.getInt(cursor.getColumnIndex(MovieDetailEntry.COLUMN_TOP_RANKED)));
            info.setTitle(cursor.getString(cursor.getColumnIndex(MovieDetailEntry.COLUMN_TITLE)));
            info.setVoteAverage(cursor.getFloat(cursor.getColumnIndex(MovieDetailEntry.COLUMN_VOTE_AVERAGE)));
            return info;
        }
        return null;
    }

    public static List<Reviews> cursorToMovieReivews(Cursor cursor) {
        List<Reviews> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Reviews info = new Reviews();
                info.setId(cursor.getInt(cursor.getColumnIndex(MovieReviewsEntry._ID)));
                info.setMovieId(cursor.getInt(cursor.getColumnIndex(MovieReviewsEntry.COLUMN_MOVIE_ID)));
                info.setReviewId(cursor.getInt(cursor.getColumnIndex(MovieReviewsEntry.COLUMN_REVIEW_ID)));
                info.setAuthor(cursor.getString(cursor.getColumnIndex(MovieReviewsEntry.COLUMN_AUTHOR)));
                info.setContent(cursor.getString(cursor.getColumnIndex(MovieReviewsEntry.COLUMN_CONTENT)));
                list.add(info);
            } while (cursor.moveToNext());

            return list;
        }
        return null;
    }

    public static List<Videos> cursorToMovieVideos(Cursor cursor) {
        List<Videos> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Videos info = new Videos();
                info.setId(cursor.getInt(cursor.getColumnIndex(MovieVideoEntry._ID)));
                info.setMovieId(cursor.getInt(cursor.getColumnIndex(MovieVideoEntry.COLUMN_MOVIE_ID)));
                info.setVideoId(cursor.getInt(cursor.getColumnIndex(MovieVideoEntry.COLUMN_VIDEO_ID)));
                info.setKey(cursor.getString(cursor.getColumnIndex(MovieVideoEntry.COLUMN_KEY)));
                info.setName(cursor.getString(cursor.getColumnIndex(MovieVideoEntry.COLUMN_NAME)));
                list.add(info);
            } while (cursor.moveToNext());

            return list;
        }
        return null;
    }




}
