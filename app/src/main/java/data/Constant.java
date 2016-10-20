package data;

/**
 * Created by 123 on 2016/8/30.
 */
public class Constant {

    // Construct the URL for the OpenPopularMovie query
    // Possible parameters are avaiable at OWM's forecast API page, at
    // https://www.themoviedb.org/
    public static final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String API_KEY = "api_key";
    public static final String PAGE = "page";

    // 	ISO 639-1 code.
    public static final String LANGUAGE = "language";

    private static final int NOT_CHECKED = 0;
    private static final int CHECKED = 1;

    public static final int NOT_UNSTAR = NOT_CHECKED;
    public static final int IS_STAR = CHECKED;

    public static final int NOT_POP_MOVIE = NOT_CHECKED;
    public static final int IS_POP_MOVIE = CHECKED;

    public static final int NOT_TOP_RANKED = NOT_CHECKED;
    public static final int IS_TOP_RANKED = CHECKED;
}
