package com.example.trackent.ui.tv_shows;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TV_Shows_table")
public class TVShows{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "TV_Show_ID")
    private int tvShowID;
    @ColumnInfo(name = "TV_Show_name")
    private String tvShowName;
    @ColumnInfo(name = "Season_Number")
    private int seasonNumber;
    @ColumnInfo(name = "Episode_Number")
    private int episodeNumber;
    @ColumnInfo(name = "Release_Date")
    private String releaseDate;
    @ColumnInfo(name = "Release_Date_Short")
    private String releaseDateShort;
    @ColumnInfo(name = "Number_of_Seasons")
    private int noOfSeasons;
    @ColumnInfo(name = "TV_Show_Image_File_Path", typeAffinity = ColumnInfo.TEXT)
    private String tvShowImageFilePath;

    public TVShows(String tvShowName, int seasonNumber,
                   int episodeNumber, String releaseDate, int noOfSeasons,
                   String releaseDateShort) {
        this.tvShowName = tvShowName;
        this.seasonNumber = seasonNumber;
        this.episodeNumber = episodeNumber;
        this.releaseDate = releaseDate;
        this.noOfSeasons = noOfSeasons;
        this.releaseDateShort = releaseDateShort;
    }
    public String getTvShowImageFilePath() {
        return tvShowImageFilePath;
    }

    public void setTvShowImageFilePath(String tvShowImageFilePath) {
        this.tvShowImageFilePath = tvShowImageFilePath;
    }

    public String getTvShowName() {
        return tvShowName;
    }

    void setTvShowName(String tvShowName) {
        this.tvShowName = tvShowName;
    }

    public int getTvShowID() {
        return tvShowID;
    }

    public void setTvShowID(int tvShowID) {
        this.tvShowID = tvShowID;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseDateShort() {
        return releaseDateShort;
    }

    public void setReleaseDateShort(String releaseDateShort) {
        this.releaseDateShort = releaseDateShort;
    }

    public int getNoOfSeasons() {
        return noOfSeasons;
    }

    void setNoOfSeasons(int noOfSeasons) {
        this.noOfSeasons = noOfSeasons;
    }
}