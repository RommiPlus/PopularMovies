package data;

import android.provider.BaseColumns;

/**
 * Created by Administrator on 2016/10/6.
 */

public class MovieContract {

    public static final class MovieDetailEntry implements BaseColumns {
        // Table name
        public static final String TABLE_NAME = "movie_detail";


        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";

        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        // When query movie data by this column, order movie data by release date desc.
        public static final String COLUMN_STAR = "star";
    }

    public static final class MovieReviewsEntry implements BaseColumns {
        // Table name
        public static final String TABLE_NAME = "movie_reviews";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
    }

    public static final class MovieVideoEntry implements BaseColumns {
        // Table name
        public static final String TABLE_NAME = "movie_video";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_VIDEO_ID = "video_id";
        public static final String COLUMN_KEY = "key";
    }
}
