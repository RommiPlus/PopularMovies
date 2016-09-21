package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 123 on 2016/8/29.
 */
public class PopularMovie {

    private int page;
    @SerializedName("total_pages")
    private int totalPages;
    @SerializedName("total_results")
    private int totalResults;

    /**
     * adult : false
     * backdrop_path : /6bbZ6XyvgfjhQwbplnUh1LSj1ky.jpg
     * genre_ids : [18]
     * id : 244786
     * original_language : en
     * original_title : Whiplash
     * overview : Under the direction of a ruthless instructor, a talented young drummer begins to pursue perfection at any cost, even his humanity.
     * release_date : 2014-10-10
     * poster_path : /lIv1QinFqz4dlp5U4lQ6HaiskOZ.jpg
     * popularity : 8.441533
     * title : Whiplash
     * video : false
     * vote_average : 8.5
     * vote_count : 856
     */
    private List<ResultsBean> results;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean implements Parcelable {
        private boolean adult;
        @SerializedName("backdrop_path")
        private String backdropPath;
        private int id;
        @SerializedName("original_language")
        private String originalLanguage;
        @SerializedName("original_title")
        private String originalTitle;
        private String overview;
        @SerializedName("release_date")
        private String releaseDate;
        @SerializedName("poster_path")
        private String posterPath;
        private double popularity;
        private String title;
        private boolean video;
        @SerializedName("vote_average")
        private double voteAverage;
        @SerializedName("vote_count")
        private int voteCount;
        @SerializedName("genre_ids")
        private List<Integer> genreIds;

        public ResultsBean(Parcel in) {
            adult = in.readByte() != 0;
            backdropPath = in.readString();
            id = in.readInt();
            originalLanguage = in.readString();
            originalTitle = in.readString();
            overview = in.readString();
            releaseDate = in.readString();
            posterPath = in.readString();
            popularity = in.readDouble();
            title = in.readString();
            video = in.readByte() != 0;
            voteAverage = in.readDouble();
            voteCount = in.readInt();
        }

        public boolean isAdult() {
            return adult;
        }

        public void setAdult(boolean adult) {
            this.adult = adult;
        }

        public String getBackdropPath() {
            return backdropPath;
        }

        public void setBackdropPath(String backdropPath) {
            this.backdropPath = backdropPath;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getOriginalLanguage() {
            return originalLanguage;
        }

        public void setOriginalLanguage(String originalLanguage) {
            this.originalLanguage = originalLanguage;
        }

        public String getOriginalTitle() {
            return originalTitle;
        }

        public void setOriginalTitle(String originalTitle) {
            this.originalTitle = originalTitle;
        }

        public String getOverview() {
            return overview;
        }

        public void setOverview(String overview) {
            this.overview = overview;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

        public String getPosterPath() {
            return posterPath;
        }

        public void setPosterPath(String posterPath) {
            this.posterPath = posterPath;
        }

        public double getPopularity() {
            return popularity;
        }

        public void setPopularity(double popularity) {
            this.popularity = popularity;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isVideo() {
            return video;
        }

        public void setVideo(boolean video) {
            this.video = video;
        }

        public double getVoteAverage() {
            return voteAverage;
        }

        public void setVoteAverage(double voteAverage) {
            this.voteAverage = voteAverage;
        }

        public int getVoteCount() {
            return voteCount;
        }

        public void setVoteCount(int voteCount) {
            this.voteCount = voteCount;
        }

        public List<Integer> getGenreIds() {
            return genreIds;
        }

        public void setGenreIds(List<Integer> genreIds) {
            this.genreIds = genreIds;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte((byte) (adult ? 1 : 0));
            dest.writeString(backdropPath);
            dest.writeInt(id);
            dest.writeString(originalLanguage);
            dest.writeString(originalTitle);
            dest.writeString(overview);
            dest.writeString(releaseDate);
            dest.writeString(posterPath);
            dest.writeDouble(popularity);
            dest.writeString(title);
            dest.writeByte((byte) (video ? 1 : 0));
            dest.writeDouble(voteAverage);
            dest.writeInt(voteCount);
        }

        public static final Creator<ResultsBean> CREATOR = new Creator<ResultsBean>() {
            @Override
            public ResultsBean createFromParcel(Parcel in) {
                return new ResultsBean(in);
            }

            @Override
            public ResultsBean[] newArray(int size) {
                return new ResultsBean[size];
            }
        };
    }
}
