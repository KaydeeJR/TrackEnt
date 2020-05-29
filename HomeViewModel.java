package com.example.trackent.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.trackent.EntertainmentRepository;
import com.example.trackent.ui.movies.Movies;
import com.example.trackent.ui.tv_shows.TVShows;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeViewModel extends AndroidViewModel{
    private LiveData<List<TVShows>> todaysList;
    private LiveData<List<TVShows>> tvShowsWatchList;
    private LiveData<List<TVShows>> allEndedTVShows;
    private LiveData<List<Movies>> moviesWatchList;
    private EntertainmentRepository entertainmentRepository;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        // to be used in TodayFragment
        entertainmentRepository = new EntertainmentRepository(application);
        todaysList = entertainmentRepository.getTodaysEntertainment();
        tvShowsWatchList = entertainmentRepository.getTVShowsWatchList();
        allEndedTVShows = entertainmentRepository.getAllEndedTVShowsList();
        moviesWatchList = entertainmentRepository.getMoviesWatchList();
    }

    LiveData<List<TVShows>> getTodaysList() {
        return todaysList;
    }

    AtomicInteger getNumberOfTodaysShows() throws InterruptedException {
        return entertainmentRepository.getNumberOfTodaysSeries();
    }

    LiveData<List<TVShows>> getContinuingTVShowsWatchList() {
        return tvShowsWatchList;
    }

    AtomicInteger getNumberOfContInWatchList() throws InterruptedException {
        return entertainmentRepository.getNumberOfTVShowsWatchList();
    }

    LiveData<List<TVShows>> getAllEndedTVShowsWatchList() {
        return allEndedTVShows;
    }

    AtomicInteger getNumberOfEndedInWatchList() throws InterruptedException {
        return entertainmentRepository.getNumberOfEndedTVShows();
    }

    LiveData<List<Movies>> getMoviesWatchList() {
        return moviesWatchList;
    }

    AtomicInteger getNumberOfMoviesInWatchList() throws InterruptedException {
        return entertainmentRepository.getNumberOfMoviesInWatchList();
    }

}