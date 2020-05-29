package com.example.trackent;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.trackent.ui.movies.Movies;
import com.example.trackent.ui.tv_shows.TVShows;

import java.util.List;

@Dao
public interface EntertainmentDAO{
    @Insert()
    void insert(Movies movies);

    @Delete
    void delete(Movies movies);

    @Query("DELETE FROM movies_table")
    void deleteAllMovies();

    @Query("SELECT * FROM movies_table ORDER BY MovieID DESC")
    LiveData<List<Movies>> getAllMovies();

    @Query("SELECT COUNT(MovieID) FROM movies_table")
    int getNumberOfMovies();

    @Query("SELECT * FROM movies_table " +
            "WHERE CAST(substr(Release_Date_Short, -4)AS INTEGER) < CAST(ltrim(substr(strftime('%d %m %Y', 'NOW'),-4),'0')AS INTEGER)" +
            "UNION " +
            "SELECT * FROM movies_table WHERE CAST(trim(substr(Release_Date_Short, -7,2))AS INTEGER) " +
            "<= CAST(ltrim(substr(strftime('%d %m %Y', 'NOW'), -7,2),'0')AS INTEGER)" +
            "AND CAST(substr(Release_Date_Short, -4)AS INTEGER) " +
            "== CAST(ltrim(substr(strftime('%d %m %Y', 'NOW'), -4), '0')AS INTEGER) ORDER BY MovieID DESC")
    LiveData<List<Movies>> getMoviesWatchList();

    @Query("SELECT COUNT(MovieID) FROM (SELECT MovieID FROM movies_table " +
            "WHERE CAST(substr(Release_Date_Short, -4)AS INTEGER) < CAST(ltrim(substr(strftime('%d %m %Y', 'NOW'),-4),'0')AS INTEGER)" +
            "UNION " +
            "SELECT MovieID FROM movies_table WHERE " +
            "CAST(trim(substr(Release_Date_Short, -7,2))AS INTEGER) <= CAST(ltrim(substr(strftime('%d %m %Y', 'NOW'), -7,2),'0')AS INTEGER)" +
            "AND CAST(substr(Release_Date_Short, -4)AS INTEGER) == CAST(ltrim(substr(strftime('%d %m %Y', 'NOW'), -4), '0')AS INTEGER))")
    int getNumberOfMoviesInWatchList();

    @Query("UPDATE movies_table SET Movie_image_file_path =:movieFilePath WHERE MovieID=:movieID")
    void insertMovieImageFilePath(int movieID, String movieFilePath);
    @Insert()
    void insertTVShow(TVShows tvShow);

    @Delete
    void deleteTVShow(TVShows tvShow);

    @Update()
    void updateTVShowsTable(TVShows tvShows);

    @Query("DELETE FROM TV_Shows_table")
    void deleteAllTVShows();

    @Query("SELECT * FROM TV_Shows_table ORDER BY TV_Show_name ASC")
    LiveData<List<TVShows>> getAllTVShows();

    @Query("SELECT COUNT(TV_Show_ID) FROM TV_Shows_table")
    int getNumberOfTVShows();

    @Query("SELECT * FROM TV_Shows_table WHERE Release_Date IS NULL ORDER BY TV_Show_ID DESC")
    LiveData<List<TVShows>> getAllEndedTVShows();

    @Query("SELECT COUNT(TV_Show_ID) FROM TV_Shows_table WHERE Release_Date IS NULL")
    int getNumberOfEndedTVShows();

    @Query("SELECT * FROM TV_Shows_table WHERE Release_Date IS NOT NULL AND CAST(trim(substr(Release_Date_Short, 1, 2))AS INTEGER)" +
            " == CAST(ltrim(substr(strftime('%d %m %Y', 'NOW'),1,2),'0')AS INTEGER) AND CAST(trim(substr(Release_Date_Short, -7,2))AS INTEGER)" +
            "== CAST(ltrim(substr(strftime('%d %m %Y', 'NOW'), -7,2),'0')AS INTEGER)")
    LiveData<List<TVShows>> getTodaysEntertainmentSchedule();

    @Query("SELECT COUNT(TV_Show_ID) FROM TV_Shows_table WHERE Release_Date IS NOT NULL AND CAST(trim(substr(Release_Date_Short, 1, 2))AS INTEGER)" +
            " == CAST(ltrim(substr(strftime('%d %m %Y', 'NOW'),1,2),'0')AS INTEGER) AND CAST(trim(substr(Release_Date_Short, -7,2))AS INTEGER)" +
            "== CAST(ltrim(substr(strftime('%d %m %Y', 'NOW'), -7,2),'0')AS INTEGER)")
    int getNumberOfTodaysSeries();

    @Query("SELECT * FROM TV_Shows_table WHERE Release_Date IS NOT NULL AND CAST(trim(substr(Release_Date_Short,-7, 2))AS INTEGER)" +
            " == CAST(ltrim(substr(strftime('%d %m %Y', 'NOW'), -7, 2),'0')AS INTEGER) AND CAST(trim(substr(Release_Date_Short, 1,2))AS INTEGER)" +
            "< CAST(ltrim(substr(strftime('%d %m %Y', 'NOW'), 1,2),'0')AS INTEGER) " +
            "UNION " +
            "SELECT * FROM TV_Shows_table WHERE Release_Date IS NOT NULL " +
            "AND CAST(trim(substr(Release_Date_Short, -7,2))AS INTEGER) < CAST(ltrim(substr(strftime('%d %m %Y', 'NOW'), -7,2),'0')AS INTEGER)")
    LiveData<List<TVShows>> getTVShowsWatchList();

    @Query("SELECT COUNT(TV_Show_ID) FROM (SELECT * FROM TV_Shows_table WHERE Release_Date IS NOT NULL AND CAST(trim(substr(Release_Date_Short,-7, 2))AS INTEGER)" +
            " == CAST(ltrim(substr(strftime('%d %m %Y', 'NOW'), -7, 2),'0')AS INTEGER) AND CAST(trim(substr(Release_Date_Short, 1,2))AS INTEGER)" +
            "< CAST(ltrim(substr(strftime('%d %m %Y', 'NOW'), 1,2),'0')AS INTEGER) " +
            "UNION " +
            "SELECT * FROM TV_Shows_table WHERE Release_Date IS NOT NULL " +
            "AND CAST(trim(substr(Release_Date_Short, -7,2))AS INTEGER) < CAST(ltrim(substr(strftime('%d %m %Y', 'NOW'), -7,2),'0')AS INTEGER))")
    int getNumberOfTVShowsWatchList();

    @Query("UPDATE TV_Shows_table SET TV_Show_Image_File_Path =:tvShowFilePath WHERE " +
        "TV_Show_ID=:tvShowID")
    void insertTVShowImageFilePath(int tvShowID, String tvShowFilePath);
}