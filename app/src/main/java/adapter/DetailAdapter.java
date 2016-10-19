package adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import data.dao.MovieDetailInfo;
import data.dao.Reviews;
import data.dao.Videos;


/**
 * Created by 123 on 2016/9/22.
 */

public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> mAdapterData;
    private Context mContext;

    private static final int VIEWTYPE_ERROR = 0x1110;
    private static final int VIEWTYPE_HEADER = 0x1111;
    private static final int VIEWTYPE_TRAILER_HEADER = 0x1112;
    private static final int VIEWTYPE_TRAILER_CONTENT = 0x1113;
    private static final int VIEWTYPE_REVIEWS_HEADER = 0x1114;
    private static final int VIEWTYPE_REVIEWS_CONTENT = 0x1115;

    public DetailAdapter(List<Object> data, Context context) {
        mAdapterData = data;
        mContext = context;
    }

    public void swapData(List<Object> data) {
        mAdapterData = data;
        notifyDataSetChanged();
    }

    public static class TrailerHear {
    }

    public static class ReviewHeader {
    }

    @Override
    public int getItemViewType(int position) {
        Object data = mAdapterData.get(position);
        if (data instanceof MovieDetailInfo) {
            return VIEWTYPE_HEADER;
        } else if (data instanceof TrailerHear) {
            return VIEWTYPE_TRAILER_HEADER;
        } else if (data instanceof Videos) {
            return VIEWTYPE_TRAILER_CONTENT;
        } else if (data instanceof ReviewHeader) {
            return VIEWTYPE_REVIEWS_HEADER;
        } else if (data instanceof Reviews) {
            return VIEWTYPE_REVIEWS_CONTENT;
        }
        return VIEWTYPE_ERROR;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case VIEWTYPE_HEADER:
                view = inflater.inflate(R.layout.fragment_detail_header, parent, false);
                viewHolder = new HeaderViewHolder(view);
                break;

            case VIEWTYPE_TRAILER_HEADER:
                view = inflater.inflate(R.layout.layout_title, parent, false);
                viewHolder = new TrailerHeaderViewHolder(view);
                break;

            case VIEWTYPE_TRAILER_CONTENT:
                view = inflater.inflate(R.layout.layout_trailer_content, parent, false);
                viewHolder = new TrailerContentViewHolder(view);
                break;

            case VIEWTYPE_REVIEWS_HEADER:
                view = inflater.inflate(R.layout.layout_title, parent, false);
                viewHolder = new ReviewHeaderViewHolder(view);
                break;

            case VIEWTYPE_REVIEWS_CONTENT:
                view = inflater.inflate(R.layout.layout_reviews_content, parent, false);
                viewHolder = new ReviewsViewHolder(view);
                break;

            default:
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object data = mAdapterData.get(position);
        if (holder instanceof HeaderViewHolder) {
            bindDataToHeaderView(data, holder);
            return;
        }

        if (holder instanceof TrailerHeaderViewHolder) {
            TrailerHeaderViewHolder trailerHeaderViewHolder = (TrailerHeaderViewHolder) holder;
            trailerHeaderViewHolder.mHeaderTv.setText("Trailer: ");
            return;
        }

        if (holder instanceof TrailerContentViewHolder) {
            bindDataToTrailerContentView(data, holder);
            return;
        }

        if (holder instanceof ReviewHeaderViewHolder) {
            ReviewHeaderViewHolder reviewHeaderViewHolder = (ReviewHeaderViewHolder) holder;
            reviewHeaderViewHolder.mHeaderTv.setText("Reviews: ");
            return;
        }

        if (holder instanceof ReviewsViewHolder) {
            ReviewsViewHolder reviewsViewHolder = (ReviewsViewHolder) holder;
            Reviews info = (Reviews) data;
            reviewsViewHolder.mReviewerName.setText("A movie review by " + info.getAuthor());
            reviewsViewHolder.mReviewerContent.setText(info.getContent());
            return;
        }
    }

    @Override
    public int getItemCount() {
        return mAdapterData.size();
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView mMovieTitleTv;
        private ImageView mMovieImageImg;
        private TextView mReleaseDateTv;
        private TextView mRunTimeTv;
        private TextView mVoteAeverageTv;
        private ToggleButton mMarkAsFavoriteBtn;
        private TextView mMovieOverView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mMovieTitleTv = (TextView) itemView.findViewById(R.id.movie_title);
            mMovieImageImg = (ImageView) itemView.findViewById(R.id.movie_img);
            mReleaseDateTv = (TextView) itemView.findViewById(R.id.movie_date_time);
            mRunTimeTv = (TextView) itemView.findViewById(R.id.movie_runtime);
            mVoteAeverageTv = (TextView) itemView.findViewById(R.id.movie_vote_average);
            mMarkAsFavoriteBtn = (ToggleButton) itemView.findViewById(R.id.mark_as_favorite);
            mMovieOverView = (TextView) itemView.findViewById(R.id.movie_overview);
        }
    }

    public void bindDataToHeaderView(Object data, RecyclerView.ViewHolder holder) {
        MovieDetailInfo info = (MovieDetailInfo) data;
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        headerViewHolder.mMovieTitleTv.setText(info.getTitle());

        Picasso.with(mContext).load(MovieAdapter.POPULAR_MOVIE + info.getPosterPath())
                .into(headerViewHolder.mMovieImageImg);

        headerViewHolder.mReleaseDateTv.setText(info.getReleaseDate());
        headerViewHolder.mRunTimeTv.setText(info.getRuntime() + "min");
        headerViewHolder.mVoteAeverageTv.setText(String.valueOf(info.getVoteAverage()) + "/10");
        headerViewHolder.mMovieOverView.setText(info.getOverview());
        headerViewHolder.mMarkAsFavoriteBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    private void bindDataToTrailerContentView(Object data, RecyclerView.ViewHolder holder) {
        TrailerContentViewHolder trailerContentViewHolder = (TrailerContentViewHolder) holder;
        final Videos info = (Videos) data;
        trailerContentViewHolder.mTrailerNumberTv.setText(info.getName());
        trailerContentViewHolder.mTrailerLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMedia(Uri.parse("http://www.youtube.com/watch?v=" + info.getKey()));
            }
        });
    }

    public void playMedia(Uri file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(file);
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(intent);
        }
    }

    public static class TrailerHeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView mHeaderTv;

        public TrailerHeaderViewHolder(View itemView) {
            super(itemView);
            mHeaderTv = (TextView) itemView.findViewById(R.id.header);
        }
    }

    public static class TrailerContentViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mTrailerLl;
        private TextView mTrailerNumberTv;

        public TrailerContentViewHolder(View itemView) {
            super(itemView);
            mTrailerLl = (LinearLayout) itemView.findViewById(R.id.trailer);
            mTrailerNumberTv = (TextView) itemView.findViewById(R.id.trailer_number);
        }
    }

    public static class ReviewHeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView mHeaderTv;

        public ReviewHeaderViewHolder(View itemView) {
            super(itemView);
            mHeaderTv = (TextView) itemView.findViewById(R.id.header);
        }
    }

    public static class ReviewsViewHolder extends RecyclerView.ViewHolder {
        private TextView mReviewerName;
        private TextView mReviewerContent;

        public ReviewsViewHolder(View itemView) {
            super(itemView);
            mReviewerName = (TextView) itemView.findViewById(R.id.reviewer_name);
            mReviewerContent = (TextView) itemView.findViewById(R.id.reviewer_content);
        }
    }
}
