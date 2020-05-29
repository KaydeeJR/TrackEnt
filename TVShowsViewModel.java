package com.example.trackent.ui.tv_shows;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.trackent.EntertainmentRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TVShowsViewModel extends AndroidViewModel{
    private EntertainmentRepository entertainmentRepository;
    private LiveData<List<TVShows>> allTVShows;

    public TVShowsViewModel(@NonNull Application application) {
        super(application);
        entertainmentRepository = new EntertainmentRepository(application);
        allTVShows = entertainmentRepository.getAllTVShows();
    }

    LiveData<List<TVShows>> getAllTVShows() {
        return allTVShows;
    }
    public AtomicInteger getNumberOfTVShows() throws InterruptedException {
        return entertainmentRepository.getNumberOfTVShows();
    }

    boolean addNewTVShow(TVShows tvShow) {
        entertainmentRepository.insertTVShow(tvShow);
        return true;
    }

    public void deleteTVShow(TVShows tvShow) {
        entertainmentRepository.deleteTVShow(tvShow);
    }

    public void deleteAllTVShows() {
        entertainmentRepository.deleteAllTVShows();
    }

    public boolean updateTVShowsTable(TVShows tvShows) {
        entertainmentRepository.updateTVShowsTable(tvShows);
        return true;
    }
    void addTVShowImageFilePath(int tvShowID, String tvShowImageFilePath) {
        entertainmentRepository.insertTVShowsImageFilePath(tvShowID, tvShowImageFilePath);
    }

}
