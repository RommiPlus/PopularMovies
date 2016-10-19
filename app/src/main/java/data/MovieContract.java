package data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Administrator on 2016/10/6.
 */

public class MovieContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.android.movie.popular.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE_DETAIL = MovieDetailEntry.TABLE_NAME;
    public static final String PATH_MOVIE_REVIEW = MovieReviewsEntry.TABLE_NAME;
    public static final String PATH_MOVIE_VIDEO = MovieVideoEntry.TABLE_NAME;

    public static final class MovieDetailEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon().appendPath(PATH_MOVIE_DETAIL).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_DETAIL;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_DETAIL;

        // Table name
        public static final String TABLE_NAME = "movie_detail";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";

        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_POSTER_PATH = "poster_path";

        // When query movie data by this column, order movie data by release date desc.
        public static final String COLUMN_STAR = "star";

        public static Uri buildMovieDetailUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getMovieIdInfoFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class MovieReviewsEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon().appendPath(PATH_MOVIE_REVIEW).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_REVIEW;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_REVIEW;

        // Table name
        public static final String TABLE_NAME = "movie_reviews";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";

        public static Uri buildMovieReviewsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class MovieVideoEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon().appendPath(PATH_MOVIE_VIDEO).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_VIDEO;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_VIDEO;

        // Table name
        public static final String TABLE_NAME = "movie_video";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_VIDEO_ID = "video_id";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";

        public static Uri buildMovieVideoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
