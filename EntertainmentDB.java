package com.example.trackent;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.trackent.ui.movies.Movies;
import com.example.trackent.ui.tv_shows.TVShows;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Movies.class, TVShows.class}, version = 2, exportSchema = false)
abstract class EntertainmentDB extends RoomDatabase{
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile EntertainmentDB ENTERTAINMENT_DB_INSTANCE;

    static EntertainmentDB getEntertainmentDbInstance(final Context context) {
        if (ENTERTAINMENT_DB_INSTANCE == null) {
            synchronized (EntertainmentDB.class) {
                if (ENTERTAINMENT_DB_INSTANCE == null) {
                    ENTERTAINMENT_DB_INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    EntertainmentDB.class,
                                    "Entertainment_Database")
                                    .fallbackToDestructiveMigration()
                                    .build();
                }
            }
        }
        return ENTERTAINMENT_DB_INSTANCE;
    }
    abstract EntertainmentDAO entertainmentDAO();
}