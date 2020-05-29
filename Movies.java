package com.example.trackent.ui.movies;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movies_table")
public class Movies{
    //variables
    @ColumnInfo(name = "MovieID")
    @PrimaryKey(autoGenerate = true)
    private int movieID;
    @NonNull
    @ColumnInfo(name = "Movie_Title")
    private String movieTitle;
    @NonNull
    @ColumnInfo(name = "Release_Date")
    private String releaseDate;
    @ColumnInfo(name = "Release_Date_Short")
    private String releaseDateShort;
    @ColumnInfo(name = "Movie_Image_File_Path", typeAffinity = ColumnInfo.TEXT)
    private String movieImageFilePath;

    // constructors to initialize each movie object
    public Movies(@NonNull String movieTitle, @NonNull String releaseDate,
                  @NonNull String releaseDateShort) {
        this.movieTitle = movieTitle;
        this.releaseDate = releaseDate;
        this.releaseDateShort = releaseDateShort;
    }

    public String getMovieImageFilePath() {
        return movieImageFilePath;
    }

    public void setMovieImageFilePath(String movieImageFilePath) {
        this.movieImageFilePath = movieImageFilePath;
    }

    @NonNull
    public String getMovieTitle() {
        return movieTitle;
    }

    // properties
    public void setMovieTitle(@NonNull String movieTitle) {
        this.movieTitle = movieTitle;
    }

    @NonNull
    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(@NonNull String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseDateShort() {
        return releaseDateShort;
    }

    public void setReleaseDateShort(String releaseDateShort) {
        this.releaseDateShort = releaseDateShort;
    }

    public int getMovieID() {
        return movieID;
    }

    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }
}
