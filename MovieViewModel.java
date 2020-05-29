package com.example.trackent.ui.movies;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.trackent.EntertainmentRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MovieViewModel extends AndroidViewModel{
    private EntertainmentRepository entertainmentRepository;
    private LiveData<List<Movies>> allMovies;

    public MovieViewModel(Application application) {
        super(application);
        entertainmentRepository = new EntertainmentRepository(application);
        allMovies = entertainmentRepository.getAllMovies();
    }

    LiveData<List<Movies>> getAllMovies() {
        return allMovies;
    }

    public AtomicInteger getNumberOfMovies() throws InterruptedException {
        return entertainmentRepository.getNumberOfMovies();
    }

    boolean addMovie(Movies movie) {
        entertainmentRepository.insert(movie);
        return true;
    }

    void addMovieImageFilePath(int movieID, String movieImageFilePath) {
         entertainmentRepository.insertMovieImageFilePath(movieID, movieImageFilePath);
     }
    public void deleteMovie(Movies movie) {
        entertainmentRepository.delete(movie);
    }

    public void deleteAllMovies() {
        entertainmentRepository.deleteAllMovies();
    }
}
