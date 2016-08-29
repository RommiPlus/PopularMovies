package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.popularmovies.DetailActivity;
import com.popularmovies.R;
import com.squareup.picasso.Picasso;

import adapter.MovieAdapter;
import model.PopularMovie;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getBundleExtra(DetailActivity.EXTRA_BUNDLE);
        PopularMovie.ResultsBean data = (PopularMovie.ResultsBean) bundle.get(DetailActivity.DETAIL_MOVIE_INFO);

        if (data != null) {
            ((TextView)view.findViewById(R.id.movie_title)).setText(data.getTitle());
            Picasso.with(getContext()).load(
                    MovieAdapter.POPULAR_MOVIE + data.getPosterPath())
                    .into((ImageView)view.findViewById(R.id.movie_img));
            ((TextView)view.findViewById(R.id.movie_date_time)).setText(data.getReleaseDate());
            ((TextView)view.findViewById(R.id.movie_vote_average)).setText(String.valueOf(data.getVoteAverage()));
            ((TextView)view.findViewById(R.id.movie_overview)).setText(data.getOverview());
        }
        return view;
    }
}
