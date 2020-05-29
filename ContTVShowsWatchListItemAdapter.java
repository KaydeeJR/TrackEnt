package com.example.trackent.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.trackent.R;
import com.example.trackent.ui.tv_shows.TVShows;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ContTVShowsWatchListItemAdapter extends RecyclerView.Adapter
{
    private List<TVShows> continuingTVShowsWatchList;
    private LayoutInflater layoutInflater;
    private ContinuingTVShowsWatchListListener continuingTVShowsWatchListListener;

    ContTVShowsWatchListItemAdapter(Context context,
        ContinuingTVShowsWatchListListener tvShowsWatchListListener)
    {
        layoutInflater = LayoutInflater.from(context);
        this.continuingTVShowsWatchListListener = tvShowsWatchListListener;
        continuingTVShowsWatchList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = layoutInflater.inflate(R.layout.cardview_tv_shows_watchlist_item_layout,
            parent, false);
        return new ContTVShowsWatchListItemViewHolder(itemView, continuingTVShowsWatchListListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        if (continuingTVShowsWatchList.size() > 0)
        {
            TVShows currentTVShow = continuingTVShowsWatchList.get(position);
            ((ContTVShowsWatchListItemViewHolder) holder).watchListTitle.setText(currentTVShow.getTvShowName());
            ((ContTVShowsWatchListItemViewHolder) holder).seasonNumber.setText(String.valueOf(currentTVShow.getSeasonNumber()));
            ((ContTVShowsWatchListItemViewHolder) holder).episodeNumber.setText(String.valueOf(currentTVShow.getEpisodeNumber()));
            ((ContTVShowsWatchListItemViewHolder) holder).watchListThumbnail.requestLayout();
            int width =
                ((ContTVShowsWatchListItemViewHolder) holder).watchListThumbnail.getLayoutParams().width;
            int height =
                ((ContTVShowsWatchListItemViewHolder) holder).watchListThumbnail.getLayoutParams().height;
            if (currentTVShow.getTvShowImageFilePath() != null)
            {
                File imageFile = new File(currentTVShow.getTvShowImageFilePath());
                long lastEdited = imageFile.lastModified();
                RequestOptions requestOptions = new RequestOptions();
                Glide.with(((ContTVShowsWatchListItemViewHolder) holder).watchListThumbnail).load(new File(currentTVShow.getTvShowImageFilePath())).placeholder(R.drawable.tv_show_neon_sign_retro_tv).error(R.drawable.tv_show_neon_sign_retro_tv).centerInside().apply(requestOptions.override(width, height)).diskCacheStrategy(DiskCacheStrategy.ALL).signature(new ObjectKey(String.valueOf(lastEdited))).into(((ContTVShowsWatchListItemViewHolder) holder).watchListThumbnail);
            } else
            {
                Glide.with(((ContTVShowsWatchListItemViewHolder) holder).watchListThumbnail).load(R.drawable.tv_show_neon_sign_retro_tv).into(((ContTVShowsWatchListItemViewHolder) holder).watchListThumbnail);
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return continuingTVShowsWatchList.size();
    }

    void setContinuingTVShowsWatchList(List<TVShows> continuingTVShowsWatchList)
    {
        this.continuingTVShowsWatchList = continuingTVShowsWatchList;
        notifyDataSetChanged();
    }

    TVShows getContTVShowAtPosition(int position)
    {
        return continuingTVShowsWatchList.get(position);
    }


    public interface ContinuingTVShowsWatchListListener
    {
        void onContTVShowsPopUpMenuClick(View view, int pos);
    }

    static class ContTVShowsWatchListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView watchListTitle, seasonNumber, episodeNumber;
        private ImageButton watchListCardButton;
        private ImageView watchListThumbnail;
        private ContinuingTVShowsWatchListListener contTVShowsWatchListListener;

        ContTVShowsWatchListItemViewHolder(@NonNull View itemView,
            ContinuingTVShowsWatchListListener listListener)
        {
            super(itemView);
            watchListTitle = itemView.findViewById(R.id.weekly_watchlist_name);
            seasonNumber = itemView.findViewById(R.id.weekly_watchlist_season_number);
            episodeNumber = itemView.findViewById(R.id.weekly_watchlist_episode_number);
            watchListCardButton = itemView.findViewById(R.id.three_dots_weekly_watchlist_menu);
            watchListThumbnail = itemView.findViewById(R.id.weekly_watchlist_thumbnail);
            this.contTVShowsWatchListListener = listListener;
            watchListCardButton.setTag(this);
            watchListCardButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            contTVShowsWatchListListener.onContTVShowsPopUpMenuClick(watchListCardButton,
                getAdapterPosition());

        }
    }
}
