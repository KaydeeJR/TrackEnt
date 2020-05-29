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

public class EndedTVShowsWatchListItemAdapter extends RecyclerView.Adapter
{
    private List<TVShows> endedTVShowsWatchList;
    private LayoutInflater layoutInflater;
    private EndedTVShowsWatchListListener endedTVShowsWatchListListener;

    EndedTVShowsWatchListItemAdapter(Context context,
        EndedTVShowsWatchListListener endedTVShowsWatchListListener)
    {
        layoutInflater = LayoutInflater.from(context);
        endedTVShowsWatchList = new ArrayList<>();
        this.endedTVShowsWatchListListener = endedTVShowsWatchListListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = layoutInflater.inflate(R.layout.cardview_ended_tv_shows_watchlist, parent
            , false);
        return new EndedTVShowsWatchListItemViewHolder(itemView, endedTVShowsWatchListListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        if (endedTVShowsWatchList.size() > 0)
        {
            TVShows currentTVShow = endedTVShowsWatchList.get(position);
            ((EndedTVShowsWatchListItemViewHolder) holder).endedWatchListTitle.setText(currentTVShow.getTvShowName());
            ((EndedTVShowsWatchListItemViewHolder) holder).seasonNumber.setText(String.valueOf(currentTVShow.getSeasonNumber()));
            ((EndedTVShowsWatchListItemViewHolder) holder).episodeNumber.setText(String.valueOf(currentTVShow.getEpisodeNumber()));
            ((EndedTVShowsWatchListItemViewHolder) holder).watchListThumbnail.requestLayout();
            int width =
                ((EndedTVShowsWatchListItemViewHolder) holder).watchListThumbnail.getLayoutParams().width;
            int height =
                ((EndedTVShowsWatchListItemViewHolder) holder).watchListThumbnail.getLayoutParams().height;
            if (currentTVShow.getTvShowImageFilePath() != null)
            {
                File imageFile = new File(currentTVShow.getTvShowImageFilePath());
                long lastEdited = imageFile.lastModified();
                RequestOptions requestOptions = new RequestOptions();
                Glide.with(((EndedTVShowsWatchListItemViewHolder) holder).watchListThumbnail).load(new File(currentTVShow.getTvShowImageFilePath())).placeholder(R.drawable.tv_show_neon_sign_retro_tv).error(R.drawable.tv_show_neon_sign_retro_tv).centerInside().apply(requestOptions.override(width, height)).diskCacheStrategy(DiskCacheStrategy.ALL).signature(new ObjectKey(String.valueOf(lastEdited))).into(((EndedTVShowsWatchListItemViewHolder) holder).watchListThumbnail);
            } else
            {
                Glide.with(((EndedTVShowsWatchListItemViewHolder) holder).watchListThumbnail).load(R.drawable.tv_show_neon_sign_retro_tv).into(((EndedTVShowsWatchListItemViewHolder) holder).watchListThumbnail);
            }

        }
    }

    @Override
    public int getItemCount()
    {
        return endedTVShowsWatchList.size();
    }

    void setEndedTVShowsWatchList(List<TVShows> endedTVShowsWatchList)
    {
        this.endedTVShowsWatchList = endedTVShowsWatchList;
        notifyDataSetChanged();
    }

    TVShows getEndedTVShowAtPosition(int position)
    {
        return endedTVShowsWatchList.get(position);
    }


    public interface EndedTVShowsWatchListListener
    {
        void onEndedTVShowsPopUpMenuClick(View view, int pos);
    }

    static class EndedTVShowsWatchListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView endedWatchListTitle, seasonNumber, episodeNumber;
        private ImageButton watchListCardButton;
        private ImageView watchListThumbnail;
        private EndedTVShowsWatchListListener tvShowsWatchListListener;

        EndedTVShowsWatchListItemViewHolder(@NonNull View itemView,
            EndedTVShowsWatchListListener listListener)
        {
            super(itemView);
            endedWatchListTitle = itemView.findViewById(R.id.watchList_ended_Name);
            seasonNumber = itemView.findViewById(R.id.watchlist_ended_season_number);
            episodeNumber = itemView.findViewById(R.id.watchlist_ended_episode_number);
            watchListCardButton = itemView.findViewById(R.id.three_dots_ended_watchlist_menu);
            watchListThumbnail = itemView.findViewById(R.id.watchList_ended_Thumbnail);
            this.tvShowsWatchListListener = listListener;
            watchListCardButton.setTag(this);
            watchListCardButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            tvShowsWatchListListener.onEndedTVShowsPopUpMenuClick(watchListCardButton,
                getAdapterPosition());
        }
    }
}
