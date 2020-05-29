package com.example.trackent.ui.tv_shows;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.trackent.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TVShowsListAdapter extends RecyclerView.Adapter implements Filterable
{
    private static final int CONTINUING_TV_SHOWS_VIEW_TYPE = 0;
    private static final int ENDED_TV_SHOWS_VIEW_TYPE = 1;
    private final LayoutInflater mInflater;
    private List<TVShows> allTVShows;
    private List<TVShows> filteredTVShows;
    private TVShowsListAdapterListener tvShowsListAdapterListener;

    TVShowsListAdapter(Context context, TVShowsListAdapterListener listener)
    {
        mInflater = LayoutInflater.from(context);
        this.allTVShows = new ArrayList<>();
        this.filteredTVShows = new ArrayList<>();
        tvShowsListAdapterListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView;
        if (viewType == CONTINUING_TV_SHOWS_VIEW_TYPE)
        {
            itemView = mInflater.inflate(R.layout.
                cardview_continuing_tv_shows_layout, parent, false);
            return new ContinuingTVViewHolder(itemView, tvShowsListAdapterListener);
        } else if (viewType == ENDED_TV_SHOWS_VIEW_TYPE)
        {
            itemView = mInflater.inflate(R.layout.cardview_ended_tv_shows_layout, parent, false);
            return new EndedTVViewHolder(itemView, tvShowsListAdapterListener);
        }
        return new ContinuingTVViewHolder(mInflater.inflate(R.layout.
            cardview_continuing_tv_shows_layout, parent, false), tvShowsListAdapterListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position)
    {
        if (allTVShows != null)
        {
            switch (getItemViewType(position))
            {
                case CONTINUING_TV_SHOWS_VIEW_TYPE:
                    TVShows currentContinuingTVShow = filteredTVShows.get(position);
                    ((ContinuingTVViewHolder) holder).continuingTitle.setText(currentContinuingTVShow.getTvShowName());
                    ((ContinuingTVViewHolder) holder).
                        currentSeason.setText(String.valueOf(currentContinuingTVShow.getSeasonNumber()));
                    ((ContinuingTVViewHolder) holder).
                        upcomingEpisode.setText(String.valueOf(currentContinuingTVShow.getEpisodeNumber()));
                    ((ContinuingTVViewHolder) holder).releaseDate.setText(currentContinuingTVShow.getReleaseDate());
                    ((ContinuingTVViewHolder) holder).continuingThumbnail.requestLayout();
                    int contWidth =
                        ((ContinuingTVViewHolder) holder).continuingThumbnail.getLayoutParams().width;
                    int contHeight =
                        ((ContinuingTVViewHolder) holder).continuingThumbnail.getLayoutParams().height;
                    if (currentContinuingTVShow.getTvShowImageFilePath() != null)
                    {
                        File imageFile = new File(currentContinuingTVShow.getTvShowImageFilePath());
                        long lastEdited = imageFile.lastModified();
                        RequestOptions requestOptions = new RequestOptions();
                        Glide.with(((ContinuingTVViewHolder) holder).continuingThumbnail).load(new File(currentContinuingTVShow.getTvShowImageFilePath())).placeholder(R.drawable.tv_show_neon_sign_retro_tv).error(R.drawable.tv_show_neon_sign_retro_tv).centerInside().apply(requestOptions.override(contWidth, contHeight)).diskCacheStrategy(DiskCacheStrategy.ALL).signature(new ObjectKey(String.valueOf(lastEdited))).into(((ContinuingTVViewHolder) holder).continuingThumbnail);
                    } else
                    {
                        Glide.with(((ContinuingTVViewHolder) holder).continuingThumbnail).load(R.drawable.tv_show_neon_sign_retro_tv).into(((ContinuingTVViewHolder) holder).continuingThumbnail);
                    }
                    break;
                case ENDED_TV_SHOWS_VIEW_TYPE:
                    TVShows currentEndedTVShow = filteredTVShows.get(position);
                    ((EndedTVViewHolder) holder).endedTitle.setText(currentEndedTVShow.getTvShowName());
                    ((EndedTVViewHolder) holder).currentSeason.setText(String.valueOf(currentEndedTVShow.getSeasonNumber()));
                    ((EndedTVViewHolder) holder).currentEpisode.setText(String.valueOf(currentEndedTVShow.getEpisodeNumber()));
                    ((EndedTVViewHolder) holder).noOfSeasons.setText(String.valueOf(currentEndedTVShow.getNoOfSeasons()));
                    ((EndedTVViewHolder) holder).endedThumbnail.requestLayout();
                    int endedWidth =
                        ((EndedTVViewHolder) holder).endedThumbnail.getLayoutParams().width;
                    int endedHeight =
                        ((EndedTVViewHolder) holder).endedThumbnail.getLayoutParams().height;
                    if (currentEndedTVShow.getTvShowImageFilePath() != null)
                    {
                        File imageFile = new File(currentEndedTVShow.getTvShowImageFilePath());
                        long lastEdited = imageFile.lastModified();
                        RequestOptions requestOptions = new RequestOptions();
                        Glide.with(((EndedTVViewHolder) holder).endedThumbnail).load(new File(currentEndedTVShow.getTvShowImageFilePath())).placeholder(R.drawable.tv_show_neon_sign_retro_tv).error(R.drawable.tv_show_neon_sign_retro_tv).centerInside().apply(requestOptions.override(endedWidth, endedHeight)).diskCacheStrategy(DiskCacheStrategy.ALL).signature(new ObjectKey(String.valueOf(lastEdited))).into(((EndedTVViewHolder) holder).endedThumbnail);
                    } else
                    {
                        Glide.with(((EndedTVViewHolder) holder).endedThumbnail).load(R.drawable.tv_show_neon_sign_retro_tv).into(((EndedTVViewHolder) holder).endedThumbnail);
                    }
                    break;
            }
        }
    }

    void setTVShowsList(List<TVShows> tvShowsList)
    {
        this.allTVShows = tvShowsList;
        this.filteredTVShows = tvShowsList;
        notifyDataSetChanged();
    }

    TVShows getTVShowAtPosition(int position)
    {
        return allTVShows.get(position);
    }

    public int getItemViewType(int position)
    {
        if (allTVShows.get(position).getReleaseDate() == null)
        {
            return ENDED_TV_SHOWS_VIEW_TYPE;
        } else
        {
            return CONTINUING_TV_SHOWS_VIEW_TYPE;
        }
    }

    @Override
    public int getItemCount()
    {
        return filteredTVShows.size();
    }

    @Override
    public Filter getFilter()
    {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                if (constraint.length() == 0)
                {
                    filteredTVShows = allTVShows;
                } else
                {
                    List<TVShows> TVShowsFiltered = new ArrayList<>();
                    constraint = constraint.toString().toLowerCase().trim();
                    for (TVShows item : allTVShows)
                    {
                        if (item.getTvShowName().toLowerCase(Locale.getDefault()).contains(constraint))
                        {
                            TVShowsFiltered.add(item);
                        }
                    }
                    filteredTVShows = TVShowsFiltered;
                }
                final FilterResults results = new FilterResults();
                results.values = filteredTVShows;
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                filteredTVShows = (List<TVShows>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface TVShowsListAdapterListener
    {
        void onPopupMenuClick(View view, int pos);

        void getTVShow(View view, int pos);
    }

    static class ContinuingTVViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        CardView cardView;
        TextView continuingTitle, currentSeason, upcomingEpisode, releaseDate;
        private ImageButton overFlowMenu;
        private ImageView continuingThumbnail;
        private TVShowsListAdapterListener tvShowsListAdapterListener;

        ContinuingTVViewHolder(View itemView, TVShowsListAdapterListener listAdapterListener)
        {
            super(itemView);
            cardView = itemView.findViewById(R.id.cont_tv_shows_card_view);
            continuingTitle = itemView.findViewById(R.id.series_title_continuing);
            currentSeason = itemView.findViewById(R.id.current_season_continuing);
            upcomingEpisode = itemView.findViewById(R.id.upcoming_episode_continuing);
            releaseDate = itemView.findViewById(R.id.series_release_date);
            overFlowMenu = itemView.findViewById(R.id.series_card_button);
            continuingThumbnail = itemView.findViewById(R.id.series_thumbnail);
            this.tvShowsListAdapterListener = listAdapterListener;
            overFlowMenu.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            tvShowsListAdapterListener.onPopupMenuClick(overFlowMenu, getAdapterPosition());
            tvShowsListAdapterListener.getTVShow(cardView, getAdapterPosition());
        }
    }

    static class EndedTVViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        final TextView endedTitle;
        final TextView currentSeason;
        final TextView currentEpisode;
        final TextView noOfSeasons;
        CardView endedCardview;
        private ImageView endedThumbnail;
        private ImageButton overFlowMenu;
        private TVShowsListAdapterListener tvShowsListAdapterListener;

        EndedTVViewHolder(View itemView, TVShowsListAdapterListener listAdapterListener)
        {
            super(itemView);
            endedCardview = itemView.findViewById(R.id.ended_tv_show_card_view);
            endedTitle = itemView.findViewById(R.id.series_title_ended);
            currentSeason = itemView.findViewById(R.id.current_season_ended);
            currentEpisode = itemView.findViewById(R.id.current_episode_ended);
            noOfSeasons = itemView.findViewById(R.id.no_of_seasons);
            overFlowMenu = itemView.findViewById(R.id.series_card_button);
            endedThumbnail = itemView.findViewById(R.id.series_thumbnail);
            this.tvShowsListAdapterListener = listAdapterListener;
            overFlowMenu.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            tvShowsListAdapterListener.onPopupMenuClick(overFlowMenu, getAdapterPosition());
            tvShowsListAdapterListener.getTVShow(endedCardview, getAdapterPosition());
        }
    }
}
