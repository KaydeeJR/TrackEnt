package com.example.trackent.ui.movies;

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

public class MoviesListAdapter extends RecyclerView.Adapter implements Filterable{
    private final LayoutInflater layoutInflater;
    private List<Movies> movieList;  // cached copy of movies
    private MovieListAdapterListener movieListAdapterListener;
    private List<Movies> moviesListFiltered;


    MoviesListAdapter(Context context, MovieListAdapterListener listener) {
        // view group is a view that can contain other views i.e. its children
        layoutInflater = LayoutInflater.from(context);
        movieListAdapterListener = listener;
        movieList = new ArrayList<>();
        moviesListFiltered = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.cardview_movies_layout, parent, false);
        return new MovieViewHolder(itemView, movieListAdapterListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (movieList != null) {
            Movies currentMovie = moviesListFiltered.get(position);
            ((MovieViewHolder) holder).movieTitle.setText(currentMovie.getMovieTitle());
            ((MovieViewHolder) holder).releaseDate.setText(currentMovie.getReleaseDate());
            ((MovieViewHolder) holder).movieThumbnail.requestLayout();
            int width = ((MovieViewHolder) holder).movieThumbnail.getLayoutParams().width;
            int height = ((MovieViewHolder) holder).movieThumbnail.getLayoutParams().height;
            if (currentMovie.getMovieImageFilePath() != null) {
                File imageFile = new File(currentMovie.getMovieImageFilePath());
                long lastEdited = imageFile.lastModified();
                RequestOptions requestOptions = new RequestOptions();
                Glide.with(((MovieViewHolder) holder).movieThumbnail)
                        .load(new File(currentMovie.getMovieImageFilePath()))
                        .placeholder(R.drawable.movie_movie)
                        .error(R.drawable.movie_movie)
                        .centerInside()
                        .apply(requestOptions.override(width, height))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .signature(new ObjectKey(String.valueOf(lastEdited)))
                        .into(((MovieViewHolder) holder).movieThumbnail);
            } else {
                Glide.with(((MovieViewHolder) holder).movieThumbnail)
                        .load(R.drawable.movie_movie)
                        .into(((MovieViewHolder) holder).movieThumbnail);
            }
        }
    }

    void setMovieList(List<Movies> movies) {
        this.movieList = movies;
        this.moviesListFiltered = movies;
        notifyDataSetChanged();
    }

    Movies getMovieAtPosition(int position) {
        return movieList.get(position);
    }
    @Override
    public int getItemCount() {
        return moviesListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint.length() == 0) {
                    moviesListFiltered = movieList;
                } else {
                    List<Movies> filteredMovies = new ArrayList<>();
                    constraint = constraint.toString().toLowerCase().trim();
                    for (Movies item : movieList) {
                        if (item.getMovieTitle().toLowerCase(Locale.getDefault()).contains(constraint)) {
                            filteredMovies.add(item);
                        }
                    }
                    moviesListFiltered = filteredMovies;
                }
                final FilterResults results = new FilterResults();
                results.values = moviesListFiltered;
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                moviesListFiltered = (List<Movies>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface MovieListAdapterListener{
        void onPopupMenuClick(View view, int pos);
        void getMovie(View view, int pos);
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView movieTitle, releaseDate;
        private CardView cardView;
        private ImageView movieThumbnail;
        private ImageButton cardMenu;
        private MovieListAdapterListener movieListAdapterListener;


        MovieViewHolder(@NonNull final View itemView, MovieListAdapterListener listAdapterListener) {
            super(itemView);
            cardView = itemView.findViewById(R.id.movies_card_view);
            movieThumbnail = itemView.findViewById(R.id.thumbnail);
            movieTitle = itemView.findViewById(R.id.movie_title);
            releaseDate = itemView.findViewById(R.id.release_date);
            cardMenu = itemView.findViewById(R.id.card_button);
            this.movieListAdapterListener = listAdapterListener;
            cardMenu.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            movieListAdapterListener.onPopupMenuClick(cardMenu, getAdapterPosition());
            movieListAdapterListener.getMovie(cardView, getAdapterPosition());
        }
    }
}