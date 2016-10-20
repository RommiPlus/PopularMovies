package data.dao;

/**
 * Created by Administrator on 2016/10/19.
 */

public class MovieDetailInfo {
    private int id;
    private int movieId;
    private int runtime;
    private String releaseDate;
    private String posterPath;
    private String overview;
    private int star;
    private int popMovie;
    private int topRanked;
    private String title;
    private float voteAverage;

    public int getPopMovie() {
        return popMovie;
    }

    public void setPopMovie(int popMovie) {
        this.popMovie = popMovie;
    }

    public int getTopRanked() {
        return topRanked;
    }

    public void setTopRanked(int topRanked) {
        this.topRanked = topRanked;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }
}
