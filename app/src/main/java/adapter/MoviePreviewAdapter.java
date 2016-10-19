package adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.popularmovies.R;
import com.squareup.picasso.Picasso;

import data.MovieContract;

import static adapter.MovieAdapter.POPULAR_MOVIE;

/**
 * Created by 123 on 2016/10/11.
 */
public class MoviePreviewAdapter extends CursorAdapter {

    public MoviePreviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView mMovieImageIv;


        public ImageViewHolder(View itemView) {
            super(itemView);
            mMovieImageIv = (ImageView) itemView.findViewById(R.id.movie_image);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.movie_item, parent, false);

        RecyclerView.ViewHolder viewHolder = new ImageViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String path = POPULAR_MOVIE + cursor.getString(
                cursor.getColumnIndex(MovieContract.MovieDetailEntry.COLUMN_POSTER_PATH));

        ImageViewHolder viewHolder = (ImageViewHolder) view.getTag();

        Picasso.with(context)
                .load(path)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(viewHolder.mMovieImageIv);
    }
}
