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

public class TodayListAdapter extends RecyclerView.Adapter{
    private final LayoutInflater mInflater;
    private List<TVShows> todaysList;
    private TodayListAdapterListener todayListAdapterListener;

    TodayListAdapter(Context context, TodayListAdapterListener todayListAdapterListener) {
        this.todaysList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        this.todayListAdapterListener = todayListAdapterListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.
                cardview_continuing_tv_shows_layout, parent, false);
        return new ContinuingTVViewHolder(itemView, todayListAdapterListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (todaysList.size() > 0) {
            TVShows currentContinuingTVShow = todaysList.get(position);
            ((ContinuingTVViewHolder) holder).continuingTitle
                    .setText(currentContinuingTVShow.getTvShowName());
            ((ContinuingTVViewHolder) holder).releaseDate.setText
                    (currentContinuingTVShow.getReleaseDate());
            ((ContinuingTVViewHolder) holder).
                    currentSeason.setText
                    (String.valueOf(currentContinuingTVShow.getSeasonNumber()));
            ((ContinuingTVViewHolder) holder).
                    upcomingEpisode.setText
                    (String.valueOf(currentContinuingTVShow.getEpisodeNumber()));
            ((ContinuingTVViewHolder) holder).continuingThumbnail.requestLayout();
            int width =
                ((ContinuingTVViewHolder) holder).continuingThumbnail.getLayoutParams().width;
            int height =
                ((ContinuingTVViewHolder) holder).continuingThumbnail.getLayoutParams().height;
            if (currentContinuingTVShow.getTvShowImageFilePath() != null){
                File imageFile = new File(currentContinuingTVShow.getTvShowImageFilePath());
                long lastEdited = imageFile.lastModified();
                RequestOptions requestOptions = new RequestOptions();
                Glide.with(((ContinuingTVViewHolder) holder).continuingThumbnail).load(new File(currentContinuingTVShow.getTvShowImageFilePath())).placeholder(R.drawable.tv_show_neon_sign_retro_tv).error(R.drawable.tv_show_neon_sign_retro_tv).centerInside().apply(requestOptions.override(width, height)).diskCacheStrategy(DiskCacheStrategy.ALL).signature(new ObjectKey(String.valueOf(lastEdited))).into(((ContinuingTVViewHolder) holder).continuingThumbnail);
            } else
            {
                Glide.with(((ContinuingTVViewHolder) holder).continuingThumbnail).load(R.drawable.tv_show_neon_sign_retro_tv).into(((ContinuingTVViewHolder) holder).continuingThumbnail);
            }
        }
    }

    @Override
    public int getItemCount() {
        return todaysList.size();
    }

    void setTodaysWatchList(List<TVShows> todaysWatchList) {
        this.todaysList = todaysWatchList;
        notifyDataSetChanged();
    }
    TVShows getTVShowAtPosition(int position) {
        return todaysList.get(position);
    }

    public interface TodayListAdapterListener{
        void onThreeDotsMenuClick(View view, int pos);
    }

    static class ContinuingTVViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView continuingTitle;
        TextView currentSeason;
        TextView upcomingEpisode;
        TextView releaseDate;
        private ImageButton overFlowMenu;
        private ImageView continuingThumbnail;
        private TodayListAdapterListener todayListAdapterListener;

        ContinuingTVViewHolder(View itemView, TodayListAdapterListener todayListAdapterListener) {
            super(itemView);
            continuingTitle = itemView.findViewById(R.id.series_title_continuing);
            currentSeason = itemView.findViewById(R.id.current_season_continuing);
            upcomingEpisode = itemView.findViewById(R.id.upcoming_episode_continuing);
            releaseDate = itemView.findViewById(R.id.series_release_date);
            overFlowMenu = itemView.findViewById(R.id.series_card_button);
            continuingThumbnail = itemView.findViewById(R.id.series_thumbnail);
            this.todayListAdapterListener = todayListAdapterListener;
            overFlowMenu.setTag(this);
            overFlowMenu.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            todayListAdapterListener.onThreeDotsMenuClick(overFlowMenu, getAdapterPosition());
        }
    }
}
