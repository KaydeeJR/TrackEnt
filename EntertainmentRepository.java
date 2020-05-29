package com.example.trackent;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.trackent.ui.movies.Movies;
import com.example.trackent.ui.tv_shows.TVShows;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EntertainmentRepository{
    private EntertainmentDAO entertainmentDAO;
    private LiveData<List<Movies>> allMovies;
    private LiveData<List<TVShows>> allTVShows;
    private LiveData<List<TVShows>> todaysEntertainment;
    private LiveData<List<TVShows>> TVShowsWatchList;
    private LiveData<List<Movies>> moviesWatchList;
    private LiveData<List<TVShows>> allEndedTVShowsList;

    public EntertainmentRepository(Application application) {
        EntertainmentDB entertainmentDB =
                EntertainmentDB.getEntertainmentDbInstance(application);
        entertainmentDAO = entertainmentDB.entertainmentDAO();
        todaysEntertainment = entertainmentDAO.getTodaysEntertainmentSchedule();
        allTVShows = entertainmentDAO.getAllTVShows();
        allMovies = entertainmentDAO.getAllMovies();
        TVShowsWatchList = entertainmentDAO.getTVShowsWatchList();
        moviesWatchList = entertainmentDAO.getMoviesWatchList();
        allEndedTVShowsList = entertainmentDAO.getAllEndedTVShows();
    }

    public void insert(Movies movie) {
        EntertainmentDB.databaseWriteExecutor.execute(() -> entertainmentDAO.insert(movie));
    }

    public void delete(Movies movie) {
        EntertainmentDB.databaseWriteExecutor.execute(() -> entertainmentDAO.delete(movie));
    }

    public void deleteAllMovies() {
        EntertainmentDB.databaseWriteExecutor.execute(() -> entertainmentDAO.deleteAllMovies());
    }

    public LiveData<List<Movies>> getAllMovies() {
        return allMovies;
    }
    public AtomicInteger getNumberOfMovies() throws InterruptedException {
        final AtomicInteger movieCount = new AtomicInteger();
        Thread thread = new Thread(() -> {
            int num = entertainmentDAO.getNumberOfMovies();
            movieCount.set(num);
        });
        thread.setPriority(10);
        thread.start();
        thread.join();
        return movieCount;
    }

    public void insertMovieImageFilePath(int movieID, String movieFilePath) {
        EntertainmentDB.databaseWriteExecutor.execute(() ->
                entertainmentDAO.insertMovieImageFilePath(movieID, movieFilePath));

    }

    public void insertTVShow(TVShows tvshow) {
        EntertainmentDB.databaseWriteExecutor.execute(() ->
                entertainmentDAO.insertTVShow(tvshow));
    }

    public void deleteTVShow(TVShows tvShow) {
        EntertainmentDB.databaseWriteExecutor.execute(() ->
                entertainmentDAO.deleteTVShow(tvShow));
    }

    public void deleteAllTVShows() {
        EntertainmentDB.databaseWriteExecutor.execute(() ->
                entertainmentDAO.deleteAllTVShows());
    }

    public void updateTVShowsTable(TVShows tvShows) {
        EntertainmentDB.databaseWriteExecutor.execute(() ->
                entertainmentDAO.updateTVShowsTable(tvShows));
    }

    public LiveData<List<TVShows>> getAllTVShows() {
        return allTVShows;
    }

    public AtomicInteger getNumberOfTVShows() throws InterruptedException {
        final AtomicInteger tvShowsCount = new AtomicInteger();
        Thread thread = new Thread(() -> {
            int num = entertainmentDAO.getNumberOfTVShows();
            tvShowsCount.set(num);
        });
        thread.setPriority(10);
        thread.start();
        thread.join();
        return tvShowsCount;
    }

    public LiveData<List<TVShows>> getTodaysEntertainment() {
        return todaysEntertainment;
    }

    public AtomicInteger getNumberOfTodaysSeries() throws InterruptedException {
        final AtomicInteger todaysCount = new AtomicInteger();
        Thread thread = new Thread(() -> {
            int num = entertainmentDAO.getNumberOfTodaysSeries();
            todaysCount.set(num);
        });
        thread.setPriority(10);
        thread.start();
        thread.join();
        return todaysCount;
    }

    public LiveData<List<TVShows>> getTVShowsWatchList() {
        return TVShowsWatchList;
    }

    public AtomicInteger getNumberOfTVShowsWatchList() throws InterruptedException {
        final AtomicInteger tvShowsWatchListCount = new AtomicInteger();
        Thread thread = new Thread(() -> {
            int num = entertainmentDAO.getNumberOfTVShowsWatchList();
            tvShowsWatchListCount.set(num);
        });
        thread.setPriority(10);
        thread.start();
        thread.join();
        return tvShowsWatchListCount;
    }

    public LiveData<List<Movies>> getMoviesWatchList() {
        return moviesWatchList;
    }

    public AtomicInteger getNumberOfMoviesInWatchList() throws InterruptedException {
        final AtomicInteger movieInWatchListCount = new AtomicInteger();
        Thread thread = new Thread(() -> {
            int num = entertainmentDAO.getNumberOfMoviesInWatchList();
            movieInWatchListCount.set(num);
        });
        thread.setPriority(10);
        thread.start();
        thread.join();
        return movieInWatchListCount;
    }

    public LiveData<List<TVShows>> getAllEndedTVShowsList() {
        return allEndedTVShowsList;
    }

    public AtomicInteger getNumberOfEndedTVShows() throws InterruptedException {
        final AtomicInteger endedTVShowsCount = new AtomicInteger();
        Thread thread = new Thread(() -> {
            int num = entertainmentDAO.getNumberOfEndedTVShows();
            endedTVShowsCount.set(num);
        });
        thread.setPriority(10);
        thread.start();
        thread.join();
        return endedTVShowsCount;
    }

    public void insertTVShowsImageFilePath(int tvShowID, String tvShowFilePath) {
        EntertainmentDB.databaseWriteExecutor.execute(() ->
                entertainmentDAO.insertTVShowImageFilePath(tvShowID, tvShowFilePath));
    }
}
