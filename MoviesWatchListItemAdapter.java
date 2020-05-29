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
import com.example.trackent.ui.movies.Movies;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MoviesWatchListItemAdapter extends RecyclerView.Adapter{
    private LayoutInflater layoutInflater;
    private List<Movies> moviesWatchList;
    private MoviesWatchListListener moviesWatchListListener;

    MoviesWatchListItemAdapter(Context context, MoviesWatchListListener moviesWatchListListener) {
        layoutInflater = LayoutInflater.from(context);
        moviesWatchList = new ArrayList<>();
        this.moviesWatchListListener = moviesWatchListListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.cardview_movies_watchlist_layout, parent, false);
        return new MovieWatchListItemViewHolder(itemView, moviesWatchListListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (moviesWatchList.size() > 0) {
            Movies currentMovie = moviesWatchList.get(position);
            ((MovieWatchListItemViewHolder) holder).watchListMovieTitle.setText(currentMovie.getMovieTitle());
            ((MovieWatchListItemViewHolder) holder).watchListThumbnail.requestLayout();
            int width = ((MovieWatchListItemViewHolder) holder).watchListThumbnail.getLayoutParams().width;
            int height = ((MovieWatchListItemViewHolder) holder).watchListThumbnail.getLayoutParams().height;
            if (currentMovie.getMovieImageFilePath() != null) {
                File imageFile = new File(currentMovie.getMovieImageFilePath());
                long lastEdited = imageFile.lastModified();
                RequestOptions requestOptions = new RequestOptions();
                Glide.with(((MovieWatchListItemViewHolder) holder).watchListThumbnail)
                        .load(new File(currentMovie.getMovieImageFilePath()))
                        .placeholder(R.drawable.movie_movie)
                        .error(R.drawable.movie_movie)
                        .centerInside()
                        .apply(requestOptions.override(width, height))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .signature(new ObjectKey(String.valueOf(lastEdited)))
                        .into(((MovieWatchListItemViewHolder) holder).watchListThumbnail);
            } else {
                Glide.with(((MovieWatchListItemViewHolder) holder).watchListThumbnail)
                        .load(R.drawable.movie_movie)
                        .into(((MovieWatchListItemViewHolder) holder).watchListThumbnail);
            }
        }
    }

    void setMovieList(List<Movies> movies) {
        moviesWatchList = movies;
        notifyDataSetChanged();
    }

    Movies getMovieAtPosition(int position) {
        return moviesWatchList.get(position);
    }

    @Override
    public int getItemCount() {
        return moviesWatchList.size();
    }

    public interface MoviesWatchListListener{
        void onMoviesWatchListPopUpMenuClick(View view, int pos);
    }

    static class MovieWatchListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageButton watchListCardButton;
        private ImageView watchListThumbnail;
        private TextView watchListMovieTitle;
        private MoviesWatchListListener moviesWatchListListener;

        MovieWatchListItemViewHolder(@NonNull View itemView, MoviesWatchListListener moviesWatchListListener) {
            super(itemView);
            watchListCardButton = itemView.findViewById(R.id.three_dots_movies_watchlist_menu);
            watchListThumbnail = itemView.findViewById(R.id.movies_watchList_thumbnail);
            watchListMovieTitle = itemView.findViewById(R.id.movies_watchlist_title);
            this.moviesWatchListListener = moviesWatchListListener;
            watchListCardButton.setTag(this);
            watchListCardButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            moviesWatchListListener.onMoviesWatchListPopUpMenuClick(watchListCardButton, getAdapterPosition());
        }
    }
}
