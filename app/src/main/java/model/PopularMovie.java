package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 123 on 2016/8/29.
 */
public class PopularMovie {


    /**
     * page : 1
     * results : [{"adult":false,"backdrop_path":"/6bbZ6XyvgfjhQwbplnUh1LSj1ky.jpg","genre_ids":[18],"id":244786,"original_language":"en","original_title":"Whiplash","overview":"Under the direction of a ruthless instructor, a talented young drummer begins to pursue perfection at any cost, even his humanity.","release_date":"2014-10-10","poster_path":"/lIv1QinFqz4dlp5U4lQ6HaiskOZ.jpg","popularity":8.441533,"title":"Whiplash","video":false,"vote_average":8.5,"vote_count":856},{"adult":false,"backdrop_path":"/60cvl34Go8dvtDiHs9L82a79VXm.jpg","genre_ids":[10749,35,16,18,10751],"id":293299,"original_language":"en","original_title":"Feast","overview":"This Oscar-winning animated short film tells the story of one man's love life is seen through the eyes of his best friend and dog, Winston, and revealed bite by bite through the meals they share.","release_date":"2014-11-07","poster_path":"/4XFN435sO7t9sMiWGMtWcV9qfmq.jpg","popularity":2.406332,"title":"Feast","video":false,"vote_average":8.5,"vote_count":80},{"adult":false,"backdrop_path":"/W0MNr3XN95U5KLD9xIQD96YKcS.jpg","genre_ids":[878,35,28,14],"id":251516,"original_language":"en","original_title":"Kung Fury","overview":"During an unfortunate series of events, a friend of Kung Fury is assassinated by the most dangerous kung fu master criminal of all time, Adolf Hitler, a.k.a Kung Führer. Kung Fury decides to travel back in time to Nazi Germany in order to kill Hitler and end the Nazi empire once and for all.","release_date":"2015-05-28","poster_path":"/oJWzpGCLIj3uYa0ux19T2WwzTOf.jpg","popularity":4.224303,"title":"Kung Fury","video":false,"vote_average":8.5,"vote_count":64},{"adult":false,"backdrop_path":"/vPbAhbnXFn183UAqOLbaXDX5I2u.jpg","genre_ids":[878,12],"id":313106,"original_language":"en","original_title":"Doctor Who: The Day of the Doctor","overview":"In 2013, something terrible is awakening in London's National Gallery; in 1562, a murderous plot is afoot in Elizabethan England; and somewhere in space an ancient battle reaches its devastating conclusion. All of reality is at stake as the Doctor's own dangerous past comes back to haunt him.","release_date":"2013-11-23","poster_path":"/dKSwfLWxQVR37YOmZFb5S072P3G.jpg","popularity":2.553939,"title":"Doctor Who: The Day of the Doctor","video":false,"vote_average":8.4,"vote_count":55},{"adult":false,"backdrop_path":"/xu9zaAevzQ5nnrsXN6JcahLnG4i.jpg","genre_ids":[18,878],"id":157336,"original_language":"en","original_title":"Interstellar","overview":"Interstellar chronicles the adventures of a group of explorers who make use of a newly discovered wormhole to surpass the limitations on human space travel and conquer the vast distances involved in an interstellar voyage.","release_date":"2014-11-05","poster_path":"/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg","popularity":16.688744,"title":"Interstellar","video":false,"vote_average":8.4,"vote_count":2485},{"adult":false,"backdrop_path":"/cqn1ynw78Wan37jzs1Ckm7va97G.jpg","genre_ids":[16,10749,10751],"id":140420,"original_language":"en","original_title":"Paperman","overview":"An urban office worker finds that paper airplanes are instrumental in meeting a girl in ways he never expected.","release_date":"2012-11-02","poster_path":"/xBo8dd2zUbMvcypwScDN3mpN7IZ.jpg","popularity":2.684451,"title":"Paperman","video":false,"vote_average":8.4,"vote_count":162},{"adult":false,"backdrop_path":"/sFQ10h9DnjOYIF4HjtLQuZ8pnb4.jpg","genre_ids":[16,10751],"id":13042,"original_language":"en","original_title":"Presto","overview":"Dignity. Poise. Mystery. We expect nothing less from the great turn-of-the-century magician, Presto. But when Presto neglects to feed his rabbit one too many times, the magician finds he isn't the only one with a few tricks up his sleeve!","release_date":"2008-06-26","poster_path":"/A2rxR8g3y6kcjIoR2fcwtq9eppc.jpg","popularity":1.929923,"title":"Presto","video":false,"vote_average":8.3,"vote_count":164},{"adult":false,"backdrop_path":"/icWOFbzKGyU35nuzIHExYSjnLcc.jpg","genre_ids":[16,10751,14],"id":110416,"original_language":"en","original_title":"Song of the Sea","overview":"The story of the last Seal Child\u2019s journey home. After their mother\u2019s disappearance, Ben and Saoirse are sent to live with Granny in the city. When they resolve to return to their home by the sea, their journey becomes a race against time as they are drawn into a world Ben knows only from his mother\u2019s folktales. But this is no bedtime story; these fairy folk have been in our world far too long. It soon becomes clear to Ben that Saoirse is the key to their survival.","release_date":"2014-12-19","poster_path":"/uvNv23Arf2ZYtimiStSB2c1DAEX.jpg","popularity":2.34488,"title":"Song of the Sea","video":false,"vote_average":8.3,"vote_count":60},{"adult":false,"backdrop_path":"/6Vb5tERWpWYlIeISYH1MDcmt499.jpg","genre_ids":[18],"id":265177,"original_language":"en","original_title":"Mommy","overview":"A widowed single mother, raising her violent son alone, finds new hope when a mysterious neighbor inserts herself into their household.","release_date":"2014-09-19","poster_path":"/7Oq8T5XsvwgsJjc1btukMRVP4K3.jpg","popularity":1.952857,"title":"Mommy","video":false,"vote_average":8.2,"vote_count":119},{"adult":false,"backdrop_path":"/bHarw8xrmQeqf3t8HpuMY7zoK4x.jpg","genre_ids":[878,14,12],"id":118340,"original_language":"en","original_title":"Guardians of the Galaxy","overview":"Light years from Earth, 26 years after being abducted, Peter Quill finds himself the prime target of a manhunt after discovering an orb wanted by Ronan the Accuser.","release_date":"2014-08-01","poster_path":"/9gm3lL8JMTTmc3W4BmNMCuRLdL8.jpg","popularity":14.51119,"title":"Guardians of the Galaxy","video":false,"vote_average":8.2,"vote_count":2706},{"adult":false,"backdrop_path":"/xBKGJQsAIeweesB79KC89FpBrVr.jpg","genre_ids":[18,80],"id":278,"original_language":"en","original_title":"The Shawshank Redemption","overview":"Framed in the 1940s for the double murder of his wife and her lover, upstanding banker Andy Dufresne begins a new life at the Shawshank prison, where he puts his accounting skills to work for an amoral warden. During his long stretch in prison, Dufresne comes to be admired by the other inmates -- including an older prisoner named Red -- for his integrity and unquenchable sense of hope.","release_date":"1994-09-14","poster_path":"/9O7gLzmreU0nGkIB6K3BsJbzvNv.jpg","popularity":4.054268,"title":"The Shawshank Redemption","video":false,"vote_average":8.2,"vote_count":3852},{"adult":false,"backdrop_path":"/qQzxMLMJMmkEAB6cYTEa9n2Emvz.jpg","genre_ids":[16,14,10751,18],"id":110420,"original_language":"ja","original_title":"Ookami kodomo no Ame to Yuki","overview":"Hana, a nineteen-year-old college student, falls in love with a man only for him to reveal his secret; he is a Wolf Man. Eventually the couple bear two children together; a son and daughter they name Ame and Yuki who both inherit the ability to transform into wolves from their father. When the man Hana fell in love with suddenly dies, she makes the decision to move to a rural town isolated from society to continue raising the children in protection.","release_date":"2012-08-29","poster_path":"/rDMxjCYEVnvLC4nsBpB6wjL0LDy.jpg","popularity":2.046142,"title":"Wolf Children","video":false,"vote_average":8.2,"vote_count":76},{"adult":false,"backdrop_path":"/qLxZZuENWJiBrmOD17Wf3qAHGQb.jpg","genre_ids":[18,10749],"id":237791,"original_language":"pt","original_title":"Hoje Eu Quero Voltar Sozinho","overview":"Leonardo is a blind teenager searching for independence. His everyday life, the relationship with his best friend, Giovana, and the way he sees the world change completely with the arrival of Gabriel.","release_date":"2014-03-28","poster_path":"/oAPCsQiWV6YUd0Gt62BOwb8aSth.jpg","popularity":1.746087,"title":"The Way He Looks","video":false,"vote_average":8.2,"vote_count":55},{"adult":false,"backdrop_path":"/lH2Ga8OzjU1XlxJ73shOlPx6cRw.jpg","genre_ids":[18],"id":389,"original_language":"en","original_title":"12 Angry Men","overview":"The defense and the prosecution have rested and the jury is filing into the jury room to decide if a young Spanish-American is guilty or innocent of murdering his father. What begins as an open and shut case soon becomes a mini-drama of each of the jurors' prejudices and preconceptions about the trial, the accused, and each other.","release_date":"1957-04-10","poster_path":"/qcL1YfkCxfhsdO6sDDJ0PpzMF9n.jpg","popularity":3.04376,"title":"12 Angry Men","video":false,"vote_average":8.2,"vote_count":675},{"adult":false,"backdrop_path":"/uIhEU2VUVgez3tKyPmMG9pf1q0g.jpg","genre_ids":[14,16,18],"id":149871,"original_language":"ja","original_title":"Kaguya Hime no Monogatari","overview":"Found inside a shining stalk of bamboo by an old bamboo cutter and his wife, a tiny girl grows rapidly into an exquisite young lady. The mysterious young princess enthralls all who encounter her - but ultimately she must confront her fate, the punishment for her crime.","release_date":"2013-11-23","poster_path":"/11Az4sMt1C9sm8atgB199Z0BsIQ.jpg","popularity":2.902988,"title":"The Tale of the Princess Kaguya","video":false,"vote_average":8.1,"vote_count":70},{"adult":false,"backdrop_path":"/6xKCYgH16UuwEGAyroLU6p8HLIn.jpg","genre_ids":[80,18],"id":238,"original_language":"en","original_title":"The Godfather","overview":"The story spans the years from 1945 to 1955 and chronicles the fictional Italian-American Corleone crime family. When organized crime family patriarch Vito Corleone barely survives an attempt on his life, his youngest son, Michael, steps in to take care of the would-be killers, launching a campaign of bloody revenge.","release_date":"1972-03-15","poster_path":"/d4KNaTrltq6bpkFS01pYtyXa09m.jpg","popularity":3.50568,"title":"The Godfather","video":false,"vote_average":8.1,"vote_count":2451},{"adult":false,"backdrop_path":"/ddlMODFJUjvhzrymuW7O7KPuhVL.jpg","genre_ids":[18],"id":169813,"original_language":"en","original_title":"Short Term 12","overview":"A 20-something supervising staff member of a residential treatment facility navigates the troubled waters of that world alongside her co-worker and longtime boyfriend.","release_date":"2013-08-23","poster_path":"/wYkiNNMM1O5c2yEcj8Lf9UbaB1a.jpg","popularity":2.287634,"title":"Short Term 12","video":false,"vote_average":8.1,"vote_count":122},{"adult":false,"backdrop_path":"/zXp2ydvhO9qGzpIsb1CWeKnn5yg.jpg","genre_ids":[80,18],"id":654,"original_language":"en","original_title":"On the Waterfront","overview":"Terry Malloy dreams about being a prize fighter, while tending his pigeons and running errands at the docks for Johnny Friendly, the corrupt boss of the dockers union. Terry witnesses a murder by two of Johnny's thugs, and later meets the dead man's sister and feels responsible for his death. She introduces him to Father Barry, who tries to force him to provide information for the courts that will smash the dock racketeers.","release_date":"1954-06-22","poster_path":"/xXRgZKrdIT1GmTssy4EJoax725A.jpg","popularity":0.702477,"title":"On the Waterfront","video":false,"vote_average":8.1,"vote_count":60},{"adult":false,"backdrop_path":"/fii9tPZTpy75qOCJBulWOb0ifGp.jpg","genre_ids":[36,18,53,10752],"id":205596,"original_language":"en","original_title":"The Imitation Game","overview":"Based on the real life story of legendary cryptanalyst Alan Turing, the film portrays the nail-biting race against time by Turing and his brilliant team of code-breakers at Britain's top-secret Government Code and Cypher School at Bletchley Park, during the darkest days of World War II.","release_date":"2014-11-14","poster_path":"/noUp0XOqIcmgefRnRZa1nhtRvWO.jpg","popularity":15.06502,"title":"The Imitation Game","video":false,"vote_average":8.1,"vote_count":1222},{"adult":false,"backdrop_path":"/ihWaJZCUIon2dXcosjQG2JHJAPN.jpg","genre_ids":[18,35],"id":77338,"original_language":"fr","original_title":"Intouchables","overview":"A true story of two men who should never have met - a quadriplegic aristocrat who was injured in a paragliding accident and a young man from the projects.","release_date":"2011-11-02","poster_path":"/4mFsNQwbD0F237Tx7gAPotd0nbJ.jpg","popularity":3.972343,"title":"The Intouchables","video":false,"vote_average":8.1,"vote_count":1465}]
     * total_pages : 166
     * total_results : 3316
     */
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
