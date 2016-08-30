package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import model.PopularMovie;

/**
 * Load image from server
 */
public class MovieAdapter extends ArrayAdapter<PopularMovie.ResultsBean> {
    public static final String POPULAR_MOVIE = "http://image.tmdb.org/t/p/w185/";

    public MovieAdapter(Context context, List<PopularMovie.ResultsBean> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PopularMovie.ResultsBean resultsBean =  getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_item, parent, false);
        }

        ImageView iconView = (ImageView) convertView.findViewById(R.id.movie_image);

        // Add a new request to threadPool and when request callback set the the bitmap to ImageView
        Picasso.with(getContext())
                .load(POPULAR_MOVIE + resultsBean.getPosterPath())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(iconView);
        return convertView;
    }
}
