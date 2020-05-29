package com.example.trackent.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackent.MainActivity;
import com.example.trackent.R;
import com.example.trackent.databinding.FragmentWatchlistBinding;
import com.example.trackent.ui.movies.MovieViewModel;
import com.example.trackent.ui.movies.Movies;
import com.example.trackent.ui.tv_shows.TVShows;
import com.example.trackent.ui.tv_shows.TVShowsViewModel;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class WatchListFragment extends Fragment implements LifecycleOwner, ContTVShowsWatchListItemAdapter.ContinuingTVShowsWatchListListener,
        EndedTVShowsWatchListItemAdapter.EndedTVShowsWatchListListener, MoviesWatchListItemAdapter.MoviesWatchListListener{
    private MovieViewModel movieViewModel;
    private TVShowsViewModel tvShowsViewModel;
    private MoviesWatchListItemAdapter moviesWatchListItemAdapter;
    private ContTVShowsWatchListItemAdapter contTVShowsWatchListItemAdapter;
    private EndedTVShowsWatchListItemAdapter endedTVShowsWatchListItemAdapter;
    private RecyclerView moviesWatchListRecyclerView, contTVShowsRecyclerView,
            endedTVShowsRecyclerView;
    private ImageView movieDoneImage, contDoneImage, endedDoneImage;
    private TextView movieDoneText, contDoneText, endedDoneText;
    private HomeViewModel homeViewModel;
    private LiveData<List<Movies>> moviesWatchList;
    private LiveData<List<TVShows>> continuingTVShowsWatchList, endedTVShowsWatchList;
    private Context applicationContext;


    static WatchListFragment newInstance() {
        return new WatchListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moviesWatchListItemAdapter = new MoviesWatchListItemAdapter(this.getContext(), this);
        contTVShowsWatchListItemAdapter = new ContTVShowsWatchListItemAdapter(this.getContext(), this);
        endedTVShowsWatchListItemAdapter = new EndedTVShowsWatchListItemAdapter(this.getContext(), this);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        tvShowsViewModel = new ViewModelProvider(this).get(TVShowsViewModel.class);
        moviesWatchList = homeViewModel.getMoviesWatchList();
        continuingTVShowsWatchList = homeViewModel.getContinuingTVShowsWatchList();
        endedTVShowsWatchList = homeViewModel.getAllEndedTVShowsWatchList();
        applicationContext = MainActivity.getContextOfApplication();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FragmentWatchlistBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_watchlist, container, false);
        // OBSERVE ANY CHANGES TO DATA
        continuingTVShowsWatchList.observe(getViewLifecycleOwner(), (List<TVShows> continuingTVShows) ->
                contTVShowsWatchListItemAdapter.setContinuingTVShowsWatchList(continuingTVShows));
        endedTVShowsWatchList.observe(getViewLifecycleOwner(), (List<TVShows> endedTVShows) ->
                endedTVShowsWatchListItemAdapter.setEndedTVShowsWatchList(endedTVShows));
        moviesWatchList.observe(getViewLifecycleOwner(), moviesWatchListItemAdapter::setMovieList);
        //MOVIES
        moviesWatchListRecyclerView = binding.myMoviesWatchlistRecyclerView;
        movieDoneImage = binding.doneImageMovies;
        movieDoneText = binding.doneMessageMovies;
        moviesWatchListRecyclerView.setHasFixedSize(true);
        moviesWatchListRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        // TV SHOWS
        // CONTINUING TV SHOWS
        contDoneImage = binding.doneImageWeekly;
        contDoneText = binding.doneMessageWeekly;
        contTVShowsRecyclerView = binding.weeklyTvShowsRecyclerView;
        contTVShowsRecyclerView.setHasFixedSize(true);
        contTVShowsRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        // ENDED TV SHOWS
        endedDoneImage = binding.doneImageBinge;
        endedDoneText = binding.doneMessageBinge;
        endedTVShowsRecyclerView = binding.bingeWorthyTvShowsRecyclerView;
        endedTVShowsRecyclerView.setHasFixedSize(true);
        endedTVShowsRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        // SET ADAPTERS
        endedTVShowsRecyclerView.setAdapter(endedTVShowsWatchListItemAdapter);
        contTVShowsRecyclerView.setAdapter(contTVShowsWatchListItemAdapter);
        moviesWatchListRecyclerView.setAdapter(moviesWatchListItemAdapter);
        moviesWatchListItemAdapter.notifyDataSetChanged();
        contTVShowsWatchListItemAdapter.notifyDataSetChanged();
        endedTVShowsWatchListItemAdapter.notifyDataSetChanged();
        try {
            hideMovieRecyclerView(homeViewModel.getNumberOfMoviesInWatchList());
        } catch (InterruptedException e) {
            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        try {
            hideWeeklyTVShowsRecyclerView(homeViewModel.getNumberOfContInWatchList());
        } catch (InterruptedException e) {
            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        try {
            hideEndedTVShowsRecyclerView(homeViewModel.getNumberOfEndedInWatchList());
        } catch (InterruptedException e) {
            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return binding.getRoot();
    }

    public void onStart() {
        super.onStart();
        final Handler handler = new Handler();
        // Do something after 1s = 1000ms
        handler.postDelayed(() -> {
            try {
                hideMovieRecyclerView(homeViewModel.getNumberOfMoviesInWatchList());
            } catch (InterruptedException e) {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            try {
                hideWeeklyTVShowsRecyclerView(homeViewModel.getNumberOfContInWatchList());
            } catch (InterruptedException e) {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            try {
                hideEndedTVShowsRecyclerView(homeViewModel.getNumberOfEndedInWatchList());
            } catch (InterruptedException e) {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }, 1000);
    }

    public void onResume() {
        super.onResume();
        final Handler handler = new Handler();
        // Do something after 1s = 1000ms
        handler.postDelayed(() -> {
            try {
                hideMovieRecyclerView(homeViewModel.getNumberOfMoviesInWatchList());
            } catch (InterruptedException e) {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            try {
                hideWeeklyTVShowsRecyclerView(homeViewModel.getNumberOfContInWatchList());
            } catch (InterruptedException e) {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            try {
                hideEndedTVShowsRecyclerView(homeViewModel.getNumberOfEndedInWatchList());
            } catch (InterruptedException e) {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }, 1000);
    }

    private void hideMovieRecyclerView(AtomicInteger numberOfMoviesInWatchList) {
        if (numberOfMoviesInWatchList.get() <= 0) {
            moviesWatchListRecyclerView.setVisibility(View.INVISIBLE);
            movieDoneImage.setVisibility(View.VISIBLE);
            movieDoneText.setVisibility(View.VISIBLE);
        } else if (numberOfMoviesInWatchList.get() > 0) {
            moviesWatchListRecyclerView.setVisibility(View.VISIBLE);
            movieDoneImage.setVisibility(View.INVISIBLE);
            movieDoneText.setVisibility(View.INVISIBLE);
        }
    }

    private void hideWeeklyTVShowsRecyclerView(AtomicInteger numberOfWeeklyTVShows) {
        if (numberOfWeeklyTVShows.get() <= 0) {
            contTVShowsRecyclerView.setVisibility(View.INVISIBLE);
            contDoneImage.setVisibility(View.VISIBLE);
            contDoneText.setVisibility(View.VISIBLE);
        } else if (numberOfWeeklyTVShows.get() > 0) {
            contTVShowsRecyclerView.setVisibility(View.VISIBLE);
            contDoneImage.setVisibility(View.INVISIBLE);
            contDoneText.setVisibility(View.INVISIBLE);
        }
    }

    private void hideEndedTVShowsRecyclerView(AtomicInteger numberOfEndedTVShows) {
        if (numberOfEndedTVShows.get() <= 0) {
            endedTVShowsRecyclerView.setVisibility(View.INVISIBLE);
            endedDoneImage.setVisibility(View.VISIBLE);
            endedDoneText.setVisibility(View.VISIBLE);
        } else if (numberOfEndedTVShows.get() > 0) {
            endedTVShowsRecyclerView.setVisibility(View.VISIBLE);
            endedDoneImage.setVisibility(View.INVISIBLE);
            endedDoneText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onEndedTVShowsPopUpMenuClick(View view, int pos) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater menuInflater = popup.getMenuInflater();
        menuInflater.inflate(R.menu.menu_ended_tv_shows_watchlist, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_watched_tv_show:
                    TVShows currentTVShow = endedTVShowsWatchListItemAdapter.getEndedTVShowAtPosition(pos);
                    int seasonNumber = currentTVShow.getSeasonNumber();
                    int episodeNumber = currentTVShow.getEpisodeNumber();
                    NextEpisodeDialogFragment.displayAddNextEpisodeFragment(getParentFragmentManager(), currentTVShow,
                            seasonNumber, episodeNumber);
                    break;
                case R.id.action_completed:
                    currentTVShow = endedTVShowsWatchListItemAdapter.getEndedTVShowAtPosition(pos);
                    String imageFilePath = currentTVShow.getTvShowImageFilePath();
                    tvShowsViewModel.deleteTVShow(currentTVShow);
                    endedTVShowsWatchListItemAdapter.notifyDataSetChanged();
                    try {
                        hideEndedTVShowsRecyclerView(homeViewModel.getNumberOfEndedInWatchList());
                    } catch (InterruptedException e) {
                        Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    if(imageFilePath != null)
                    {
                        File filesDirectory = new File(Objects.requireNonNull(applicationContext.getFilesDir().getParent()));
                        File[] filesInFileDirectory = filesDirectory.listFiles();
                        if (filesInFileDirectory != null)
                        {
                            for (File folder : filesInFileDirectory)
                            {
                                if (folder.getName().matches("app_TVShowImages"))
                                {
                                    if (folder.listFiles() != null)
                                    {
                                        for (File imageFiles : Objects.requireNonNull(folder.listFiles()))
                                        {
                                            if (imageFiles.getAbsolutePath().matches(imageFilePath))
                                                //noinspection ResultOfMethodCallIgnored
                                                imageFiles.delete();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
            }
            return true;
        });
        popup.show();
    }

    @Override
    public void onMoviesWatchListPopUpMenuClick(View view, int pos) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater menuInflater = popup.getMenuInflater();
        menuInflater.inflate(R.menu.menu_movies_watchlist, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            Movies currentMovie = moviesWatchListItemAdapter.getMovieAtPosition(pos);
            String imageFilePath = currentMovie.getMovieImageFilePath();
            movieViewModel.deleteMovie(currentMovie);
            moviesWatchListItemAdapter.notifyDataSetChanged();
            try {
                hideMovieRecyclerView(homeViewModel.getNumberOfMoviesInWatchList());
            } catch (InterruptedException e) {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            if(imageFilePath != null)
            {
                File filesDirectory = new File(Objects.requireNonNull(applicationContext.getFilesDir().getParent()));
                File[] filesInFileDirectory = filesDirectory.listFiles();
                if (filesInFileDirectory != null)
                {
                    for (File folder : filesInFileDirectory)
                    {
                        if (folder.getName().matches("app_MovieImages"))
                        {
                            if (folder.listFiles() != null)
                            {
                                for (File imageFiles : Objects.requireNonNull(folder.listFiles()))
                                {
                                    if (imageFiles.getAbsolutePath().matches(imageFilePath))
                                        //noinspection ResultOfMethodCallIgnored
                                        imageFiles.delete();
                                }
                            }
                        }
                    }
                }
            }
            return true;
        });
        popup.show();
    }

    @Override
    public void onContTVShowsPopUpMenuClick(View view, int pos) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater menuInflater = popup.getMenuInflater();
        menuInflater.inflate(R.menu.menu_cont_tv_shows_watchlist, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_watched_tv_show:
                    TVShows currentTVShow = contTVShowsWatchListItemAdapter.getContTVShowAtPosition(pos);
                    // get new episode
                    int watchedEpisode = currentTVShow.getEpisodeNumber();
                    currentTVShow.setEpisodeNumber(incrementEpisode(watchedEpisode));
                    tvShowsViewModel.updateTVShowsTable(currentTVShow);
                    contTVShowsWatchListItemAdapter.notifyDataSetChanged();
                    try {
                        hideWeeklyTVShowsRecyclerView(homeViewModel.getNumberOfContInWatchList());
                    } catch (InterruptedException e) {
                        Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    // get new release date
                    String releaseDateShort = currentTVShow.getReleaseDateShort();
                    int newDate = incrementDateByAWeek(releaseDateShort);
                    int currentMonth = getCurrentMonth(releaseDateShort);
                    int currentYear = getCurrentYear(releaseDateShort);
                    int numberOfDaysInMonth = getDaysInMonth(currentMonth, releaseDateShort);
                    if (((newDate - numberOfDaysInMonth) <= 0) && (currentMonth - 12) < 0) {
                        //still in the same month and in the same year
                        String newReleaseDate = String.format("%s%6s%6s", dayToDate(newDate),
                                ShowMonthName(currentMonth), String.valueOf(currentYear));
                        String newReleaseDateShort = appendZeroToDay(newDate) + " " + currentMonth + " " + currentYear;
                        currentTVShow.setReleaseDateShort(newReleaseDateShort);
                        currentTVShow.setReleaseDate(newReleaseDate);
                        tvShowsViewModel.updateTVShowsTable(currentTVShow);
                        contTVShowsWatchListItemAdapter.notifyDataSetChanged();
                        try {
                            hideWeeklyTVShowsRecyclerView(homeViewModel.getNumberOfContInWatchList());
                        } catch (InterruptedException e) {
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    } else if (newDate - numberOfDaysInMonth > 0) {
                        // while incrementing date by a week, if the new date happens to lie in a new month then
                        //change months
                        int newDateOfNewMonth = newDate - numberOfDaysInMonth;
                        int newMonth = currentMonth + 1; //it is a new month
                        if (newMonth - 12 > 0) {
                            //while incrementing the month, if the new month happens to lie in a new year then
                            // change years. e.g. if current month is december i.e. 12 then new month becomes
                            // 13 i.e. January of the new year
                            int newMonthOfNewYear = newMonth - 12;
                            int newYear = currentYear + 1;
                            String newReleaseDate = String.format("%s%6s%6s", dayToDate(newDateOfNewMonth),
                                    ShowMonthName(newMonthOfNewYear), String.valueOf(newYear));
                            String newReleaseDateShort = appendZeroToDay(newDateOfNewMonth) + " " + newMonthOfNewYear + " " + newYear;
                            currentTVShow.setReleaseDateShort(newReleaseDateShort);
                            currentTVShow.setReleaseDate(newReleaseDate);
                            tvShowsViewModel.updateTVShowsTable(currentTVShow);
                            contTVShowsWatchListItemAdapter.notifyDataSetChanged();
                            try {
                                hideWeeklyTVShowsRecyclerView(homeViewModel.getNumberOfContInWatchList());
                            } catch (InterruptedException e) {
                                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        } else if (newMonth - 12 < 0) {
                            //still within the same year
                            String newReleaseDate = String.format("%s%6s%6s", dayToDate(newDateOfNewMonth),
                                    ShowMonthName(newMonth), String.valueOf(currentYear));
                            String newReleaseDateShort = appendZeroToDay(newDateOfNewMonth) + " " + newMonth + " " + currentYear;
                            currentTVShow.setReleaseDateShort(newReleaseDateShort);
                            currentTVShow.setReleaseDate(newReleaseDate);
                            tvShowsViewModel.updateTVShowsTable(currentTVShow);
                            contTVShowsWatchListItemAdapter.notifyDataSetChanged();
                            try {
                                hideWeeklyTVShowsRecyclerView(homeViewModel.getNumberOfContInWatchList());
                            } catch (InterruptedException e) {
                                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                case R.id.action_completed:
                    currentTVShow = contTVShowsWatchListItemAdapter.getContTVShowAtPosition(pos);
                    String imageFilePath = currentTVShow.getTvShowImageFilePath();
                    tvShowsViewModel.deleteTVShow(currentTVShow);
                    contTVShowsWatchListItemAdapter.notifyDataSetChanged();
                    try {
                        hideWeeklyTVShowsRecyclerView(homeViewModel.getNumberOfContInWatchList());
                    } catch (InterruptedException e) {
                        Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    if(imageFilePath != null)
                    {
                        File filesDirectory = new File(Objects.requireNonNull(applicationContext.getFilesDir().getParent()));
                        File[] filesInFileDirectory = filesDirectory.listFiles();
                        if (filesInFileDirectory != null)
                        {
                            for (File folder : filesInFileDirectory)
                            {
                                if (folder.getName().matches("app_TVShowImages"))
                                {
                                    if (folder.listFiles() != null)
                                    {
                                        for (File imageFiles : Objects.requireNonNull(folder.listFiles()))
                                        {
                                            if (imageFiles.getAbsolutePath().matches(imageFilePath))
                                                //noinspection ResultOfMethodCallIgnored
                                                imageFiles.delete();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                case R.id.action_postpone:
                    currentTVShow = contTVShowsWatchListItemAdapter.getContTVShowAtPosition(pos);
                    releaseDateShort = currentTVShow.getReleaseDateShort();
                    newDate = incrementDateByAWeek(releaseDateShort);
                    currentMonth = getCurrentMonth(releaseDateShort);
                    currentYear = getCurrentYear(releaseDateShort);
                    numberOfDaysInMonth = getDaysInMonth(currentMonth, releaseDateShort);
                    if (((newDate - numberOfDaysInMonth) <= 0) && (currentMonth - 12) < 0) {
                        //still in the same month and in the same year
                        String newReleaseDate = String.format("%s%6s%6s", dayToDate(newDate),
                                ShowMonthName(currentMonth), String.valueOf(currentYear));
                        String newReleaseDateShort = appendZeroToDay(newDate) + " " + currentMonth + " " + currentYear;
                        currentTVShow.setReleaseDateShort(newReleaseDateShort);
                        currentTVShow.setReleaseDate(newReleaseDate);
                        tvShowsViewModel.updateTVShowsTable(currentTVShow);
                        contTVShowsWatchListItemAdapter.notifyDataSetChanged();
                        try {
                            hideWeeklyTVShowsRecyclerView(homeViewModel.getNumberOfContInWatchList());
                        } catch (InterruptedException e) {
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    } else if (newDate - numberOfDaysInMonth > 0) {
                        // while incrementing date by a week, if the new date happens to lie in a new month then
                        //change months
                        int newDateOfNewMonth = newDate - numberOfDaysInMonth;
                        int newMonth = currentMonth + 1; //it is a new month
                        if (newMonth - 12 > 0) {
                            //while incrementing the month, if the new month happens to lie in a new year then
                            // change years. e.g. if current month is december i.e. 12 then new month becomes
                            // 13 i.e. January of the new year
                            int newMonthOfNewYear = newMonth - 12;
                            int newYear = currentYear + 1;
                            String newReleaseDate = String.format("%s%6s%6s", dayToDate(newDateOfNewMonth),
                                    ShowMonthName(newMonthOfNewYear), String.valueOf(newYear));
                            String newReleaseDateShort = appendZeroToDay(newDateOfNewMonth) + " " + newMonthOfNewYear + " " + newYear;
                            currentTVShow.setReleaseDateShort(newReleaseDateShort);
                            currentTVShow.setReleaseDate(newReleaseDate);
                            tvShowsViewModel.updateTVShowsTable(currentTVShow);
                            contTVShowsWatchListItemAdapter.notifyDataSetChanged();
                            try {
                                hideWeeklyTVShowsRecyclerView(homeViewModel.getNumberOfContInWatchList());
                            } catch (InterruptedException e) {
                                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        } else if (newMonth - 12 < 0) {
                            //still within the same year
                            String newReleaseDate = String.format("%s%6s%6s", dayToDate(newDateOfNewMonth),
                                    ShowMonthName(newMonth), String.valueOf(currentYear));
                            String newReleaseDateShort = appendZeroToDay(newDateOfNewMonth) + " " + newMonth + " " + currentYear;
                            currentTVShow.setReleaseDateShort(newReleaseDateShort);
                            currentTVShow.setReleaseDate(newReleaseDate);
                            tvShowsViewModel.updateTVShowsTable(currentTVShow);
                            contTVShowsWatchListItemAdapter.notifyDataSetChanged();
                            try {
                                hideWeeklyTVShowsRecyclerView(homeViewModel.getNumberOfContInWatchList());
                            } catch (InterruptedException e) {
                                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
            }
            return true;
        });
        popup.show();
    }

    private int incrementDateByAWeek(String releaseDateShort) {
        String date = releaseDateShort.substring(0, 2); // fetched the date from string
        int stringDateToInt = Integer.parseInt(date.trim()); // get rid of whitespaces
        return stringDateToInt + 7; // add a week to current day
    }

    private int getCurrentMonth(String releaseDateShort) {
        // ranges from 1 to 12
        // fetches two characters that represent the month number
        String monthWithZero = releaseDateShort.substring(releaseDateShort.length() - 7, releaseDateShort.length() - 5);
        // trim to remove any whitespace in the month string
        String monthNumber = monthWithZero.trim();
        //convert string month number to integer
        return Integer.parseInt(monthNumber);
    }

    private int getDaysInMonth(int monthNum, String releaseDateShort) {
        int numberOfDays = 31;
        // month number ranges from 1 to 12
        if (monthNum == 4 || monthNum == 6 || monthNum == 9 || monthNum == 11) {
            //April, June, September, November
            numberOfDays = 30;
            return numberOfDays;
        } else if (monthNum == 2) {
            //February
            if (checkIfLeapYear(releaseDateShort)) {
                numberOfDays = 29;
                return numberOfDays;
            } else if (!checkIfLeapYear(releaseDateShort)) {
                numberOfDays = 28;
                return numberOfDays;
            }
        }
        return numberOfDays;
    }

    private int getCurrentYear(String releaseDateShort) {
        String year = releaseDateShort.substring(releaseDateShort.length() - 4);
        return Integer.parseInt(year);
    }

    private Boolean checkIfLeapYear(String releaseDateShort) {
        // fetch year from string release date
        String year = releaseDateShort.substring(releaseDateShort.length() - 4);
        int yearNumber = Integer.parseInt(year);
        // YES it is a leap year
        return yearNumber % 4 == 0;
    }

    private int incrementEpisode(int watchedEpisode) {
        return watchedEpisode + 1;
    }

    private String ShowMonthName(int month) {
        String monthName = "";
        if (month > 0 && month < 13) {
            switch (month) {
                case 1:
                    monthName = "January";
                    break;
                case 2:
                    monthName = "February";
                    break;
                case 3:
                    monthName = "March";
                    break;
                case 4:
                    monthName = "April";
                    break;
                case 5:
                    monthName = "May";
                    break;
                case 6:
                    monthName = "June";
                    break;
                case 7:
                    monthName = "July";
                    break;
                case 8:
                    monthName = "August";
                    break;
                case 9:
                    monthName = "September";
                    break;
                case 10:
                    monthName = "October";
                    break;
                case 11:
                    monthName = "November";
                    break;
                case 12:
                    monthName = "December";
                    break;

            }
        }
        return monthName;
    }

    private String dayToDate(int day) {
        String date;
        if (day == 1 || day == 21 || day == 31) {
            date = day + "st";
            return date;
        } else if (day == 2 || day == 22) {
            date = day + "nd";
            return date;
        } else if (day == 3 || day == 23) {
            date = day + "rd";
            return date;
        } else {
            date = day + "th";
            return date;
        }
    }

    private String appendZeroToDay(int day) {
        if (day > 0 && day < 10) {
            // Add one Leading zero
            return String.format(Locale.ENGLISH, "%01d", day);
        } else {
            return String.valueOf(day);
        }
    }
}