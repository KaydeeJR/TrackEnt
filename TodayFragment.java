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
import com.example.trackent.databinding.FragmentTodayBinding;
import com.example.trackent.ui.tv_shows.TVShows;
import com.example.trackent.ui.tv_shows.TVShowsViewModel;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class TodayFragment extends Fragment implements LifecycleOwner,
    TodayListAdapter.TodayListAdapterListener
{
    private TodayListAdapter todayListAdapter;
    private TVShowsViewModel tvShowsViewModel;
    private RecyclerView todayRecyclerView;
    private ImageView todayDoneImage;
    private TextView todayDoneMessage;
    private HomeViewModel homeViewModel;
    private LiveData<List<TVShows>> todaysList;
    private Context applicationContext;


    static TodayFragment newInstance()
    {
        return new TodayFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        todayListAdapter = new TodayListAdapter(this.getContext(), this);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        tvShowsViewModel = new ViewModelProvider(this).get(TVShowsViewModel.class);
        todaysList = homeViewModel.getTodaysList();
        applicationContext = MainActivity.getContextOfApplication();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState)
    {
        FragmentTodayBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_today,
            container, false);
        todayRecyclerView = binding.todayRecyclerView;
        todayDoneImage = binding.doneImageToday;
        todayDoneMessage = binding.doneMessageToday;
        todayRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        todayRecyclerView.setHasFixedSize(true);
        todaysList.observe(getViewLifecycleOwner(), todayListAdapter::setTodaysWatchList);
        todayRecyclerView.setAdapter(todayListAdapter);
        todayListAdapter.notifyDataSetChanged();
        try
        {
            hideRecyclerView(homeViewModel.getNumberOfTodaysShows());
        } catch (InterruptedException e)
        {
            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return binding.getRoot();
    }

    public void onStart()
    {
        super.onStart();
        final Handler handler = new Handler();
        handler.postDelayed(() ->
        {
            try
            {
                // DELAYED 1 SECOND
                hideRecyclerView(homeViewModel.getNumberOfTodaysShows());
            } catch (InterruptedException e)
            {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }, 1000);
    }

    public void onResume()
    {
        super.onResume();
        final Handler handler = new Handler();
        handler.postDelayed(() ->
        {
            try
            {
                // DELAYED 1 SECOND
                hideRecyclerView(homeViewModel.getNumberOfTodaysShows());
            } catch (InterruptedException e)
            {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }, 1000);
    }

    private void hideRecyclerView(AtomicInteger numberOfTodays)
    {
        if (numberOfTodays.get() <= 0)
        {
            todayRecyclerView.setVisibility(View.INVISIBLE);
            todayDoneImage.setVisibility(View.VISIBLE);
            todayDoneMessage.setVisibility(View.VISIBLE);
        } else if (numberOfTodays.get() > 0)
        {
            todayRecyclerView.setVisibility(View.VISIBLE);
            todayDoneImage.setVisibility(View.INVISIBLE);
            todayDoneMessage.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onThreeDotsMenuClick(View view, int pos)
    {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater menuInflater = popup.getMenuInflater();
        menuInflater.inflate(R.menu.menu_cont_tv_shows_watchlist, popup.getMenu());
        popup.setOnMenuItemClickListener(item ->
        {
            switch (item.getItemId())
            {
                case R.id.action_watched_tv_show:
                    TVShows currentTVShow = todayListAdapter.getTVShowAtPosition(pos);
                    // get new episode
                    int watchedEpisode = currentTVShow.getEpisodeNumber();
                    currentTVShow.setEpisodeNumber(incrementEpisode(watchedEpisode));
                    tvShowsViewModel.updateTVShowsTable(currentTVShow);
                    todayListAdapter.notifyDataSetChanged();
                    try
                    {
                        hideRecyclerView(homeViewModel.getNumberOfTodaysShows());
                    } catch (InterruptedException e)
                    {
                        Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    // get new release date
                    String releaseDateShort = currentTVShow.getReleaseDateShort();
                    int newDate = incrementDateByAWeek(releaseDateShort);
                    int currentMonth = getCurrentMonth(releaseDateShort);
                    int currentYear = getCurrentYear(releaseDateShort);
                    int numberOfDaysInMonth = getDaysInMonth(currentMonth, releaseDateShort);
                    if (((newDate - numberOfDaysInMonth) <= 0) && (currentMonth - 12) < 0)
                    {
                        //still in the same month and in the same year
                        String newReleaseDate = String.format("%s%6s%6s", dayToDate(newDate),
                            ShowMonthName(currentMonth), String.valueOf(currentYear));
                        String newReleaseDateShort =
                            appendZero(newDate) + " " + appendZero(currentMonth) + " " + currentYear;
                        currentTVShow.setReleaseDateShort(newReleaseDateShort);
                        currentTVShow.setReleaseDate(newReleaseDate);
                        tvShowsViewModel.updateTVShowsTable(currentTVShow);
                        todayListAdapter.notifyDataSetChanged();
                        try
                        {
                            hideRecyclerView(homeViewModel.getNumberOfTodaysShows());
                        } catch (InterruptedException e)
                        {
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    } else if (newDate - numberOfDaysInMonth > 0)
                    {
                        // while incrementing date by a week, if the new date happens to lie in a
                        // new month then
                        //change months
                        int newDateOfNewMonth = newDate - numberOfDaysInMonth;
                        int newMonth = currentMonth + 1; //it is a new month
                        if (newMonth - 12 > 0)
                        {
                            //while incrementing the month, if the new month happens to lie in a
                            // new year then
                            // change years. e.g. if current month is december i.e. 12 then new
                            // month becomes
                            // 13 i.e. January of the new year
                            int newMonthOfNewYear = newMonth - 12;
                            int newYear = currentYear + 1;
                            String newReleaseDate = String.format("%s%6s%6s",
                                dayToDate(newDateOfNewMonth), ShowMonthName(newMonthOfNewYear),
                                String.valueOf(newYear));
                            String newReleaseDateShort =
                                appendZero(newDateOfNewMonth) + " " + appendZero(newMonthOfNewYear) + " " + newYear;
                            currentTVShow.setReleaseDateShort(newReleaseDateShort);
                            currentTVShow.setReleaseDate(newReleaseDate);
                            tvShowsViewModel.updateTVShowsTable(currentTVShow);
                            todayListAdapter.notifyDataSetChanged();
                            try
                            {
                                hideRecyclerView(homeViewModel.getNumberOfTodaysShows());
                            } catch (InterruptedException e)
                            {
                                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        } else if (newMonth - 12 < 0)
                        {
                            //still within the same year
                            String newReleaseDate = String.format("%s%6s%6s",
                                dayToDate(newDateOfNewMonth), ShowMonthName(newMonth),
                                String.valueOf(currentYear));
                            String newReleaseDateShort =
                                appendZero(newDateOfNewMonth) + " " + appendZero(newMonth) + " " + currentYear;
                            currentTVShow.setReleaseDateShort(newReleaseDateShort);
                            currentTVShow.setReleaseDate(newReleaseDate);
                            tvShowsViewModel.updateTVShowsTable(currentTVShow);
                            todayListAdapter.notifyDataSetChanged();
                            try
                            {
                                hideRecyclerView(homeViewModel.getNumberOfTodaysShows());
                            } catch (InterruptedException e)
                            {
                                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                case R.id.action_completed:
                    currentTVShow = todayListAdapter.getTVShowAtPosition(pos);
                    tvShowsViewModel.deleteTVShow(currentTVShow);
                    todayListAdapter.notifyDataSetChanged();
                    try
                    {
                        hideRecyclerView(homeViewModel.getNumberOfTodaysShows());
                    } catch (InterruptedException e)
                    {
                        Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    break;
                case R.id.action_postpone:
                    currentTVShow = todayListAdapter.getTVShowAtPosition(pos);
                    releaseDateShort = currentTVShow.getReleaseDateShort();
                    newDate = incrementDateByAWeek(releaseDateShort);
                    currentMonth = getCurrentMonth(releaseDateShort);
                    currentYear = getCurrentYear(releaseDateShort);
                    numberOfDaysInMonth = getDaysInMonth(currentMonth, releaseDateShort);
                    if (((newDate - numberOfDaysInMonth) <= 0) && (currentMonth - 12) < 0)
                    {
                        //still in the same month and in the same year
                        String newReleaseDate = String.format("%s%6s%6s", dayToDate(newDate),
                            ShowMonthName(currentMonth), String.valueOf(currentYear));
                        String newReleaseDateShort =
                            appendZero(newDate) + " " + appendZero(currentMonth) + " " + currentYear;
                        currentTVShow.setReleaseDateShort(newReleaseDateShort);
                        currentTVShow.setReleaseDate(newReleaseDate);
                        tvShowsViewModel.updateTVShowsTable(currentTVShow);
                        todayListAdapter.notifyDataSetChanged();
                        try
                        {
                            hideRecyclerView(homeViewModel.getNumberOfTodaysShows());
                        } catch (InterruptedException e)
                        {
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    } else if (newDate - numberOfDaysInMonth > 0)
                    {
                        // while incrementing date by a week, if the new date happens to lie in a
                        // new month then
                        //change months
                        int newDateOfNewMonth = newDate - numberOfDaysInMonth;
                        int newMonth = currentMonth + 1; //it is a new month
                        if (newMonth - 12 > 0)
                        {
                            //while incrementing the month, if the new month happens to lie in a
                            // new year then
                            // change years. e.g. if current month is december i.e. 12 then new
                            // month becomes
                            // 13 i.e. January of the new year
                            int newMonthOfNewYear = newMonth - 12;
                            int newYear = currentYear + 1;
                            String newReleaseDate = String.format("%s%6s%6s",
                                dayToDate(newDateOfNewMonth), ShowMonthName(newMonthOfNewYear),
                                String.valueOf(newYear));
                            String newReleaseDateShort =
                                appendZero(newDateOfNewMonth) + " " + appendZero(newMonthOfNewYear) + " " + newYear;
                            currentTVShow.setReleaseDateShort(newReleaseDateShort);
                            currentTVShow.setReleaseDate(newReleaseDate);
                            tvShowsViewModel.updateTVShowsTable(currentTVShow);
                            todayListAdapter.notifyDataSetChanged();
                            try
                            {
                                hideRecyclerView(homeViewModel.getNumberOfTodaysShows());
                            } catch (InterruptedException e)
                            {
                                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        } else if (newMonth - 12 < 0)
                        {
                            //still within the same year
                            String newReleaseDate = String.format("%s%6s%6s",
                                dayToDate(newDateOfNewMonth), ShowMonthName(newMonth),
                                String.valueOf(currentYear));
                            String newReleaseDateShort =
                                appendZero(newDateOfNewMonth) + " " + appendZero(newMonth) + " " + currentYear;
                            currentTVShow.setReleaseDateShort(newReleaseDateShort);
                            currentTVShow.setReleaseDate(newReleaseDate);
                            tvShowsViewModel.updateTVShowsTable(currentTVShow);
                            todayListAdapter.notifyDataSetChanged();
                            try
                            {
                                hideRecyclerView(homeViewModel.getNumberOfTodaysShows());
                            } catch (InterruptedException e)
                            {
                                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
            }
            return true;
        });
        try
        {
            hideRecyclerView(homeViewModel.getNumberOfTodaysShows());
        } catch (InterruptedException e)
        {
            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        popup.show();
    }

    private int incrementEpisode(int watchedEpisode)
    {
        return watchedEpisode + 1;
    }

    private int incrementDateByAWeek(String releaseDateShort)
    {
        String date = releaseDateShort.substring(0, 2); // fetched the date from string
        int stringDateToInt = Integer.parseInt(date.trim()); // get rid of whitespaces
        return stringDateToInt + 7; // add a week to current day
    }

    private int getCurrentMonth(String releaseDateShort)
    {
        // ranges from 1 to 12
        // fetches two characters that represent the month number
        String monthWithZero = releaseDateShort.substring(releaseDateShort.length() - 7,
            releaseDateShort.length() - 5);
        // trim to remove any whitespace in the month string
        String monthNumber = monthWithZero.trim();
        //convert string month number to integer
        return Integer.parseInt(monthNumber);
    }

    private int getDaysInMonth(int monthNum, String releaseDateShort)
    {
        int numberOfDays = 31;
        // month number ranges from 1 to 12
        if (monthNum == 4 || monthNum == 6 || monthNum == 9 || monthNum == 11)
        {
            //April, June, September, November
            numberOfDays = 30;
            return numberOfDays;
        } else if (monthNum == 2)
        {
            //February
            if (checkIfLeapYear(releaseDateShort))
            {
                numberOfDays = 29;
                return numberOfDays;
            } else if (!checkIfLeapYear(releaseDateShort))
            {
                numberOfDays = 28;
                return numberOfDays;
            }
        }
        return numberOfDays;
    }

    private int getCurrentYear(String releaseDateShort)
    {
        String year = releaseDateShort.substring(releaseDateShort.length() - 4);
        return Integer.parseInt(year);
    }

    private Boolean checkIfLeapYear(String releaseDateShort)
    {
        // fetch year from string release date
        String year = releaseDateShort.substring(releaseDateShort.length() - 4);
        int yearNumber = Integer.parseInt(year);
        // YES it is a leap year
        return yearNumber % 4 == 0;
    }

    private String ShowMonthName(int month)
    {
        String monthName = "";
        if (month > 0 && month < 13)
        {
            switch (month)
            {
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

    private String dayToDate(int day)
    {
        String date;
        if (day == 1 || day == 21 || day == 31)
        {
            date = day + "st";
            return date;
        } else if (day == 2 || day == 22)
        {
            date = day + "nd";
            return date;
        } else if (day == 3 || day == 23)
        {
            date = day + "rd";
            return date;
        } else
        {
            date = day + "th";
            return date;
        }
    }

    private String appendZero(int dayOrMonth)
    {
        if (dayOrMonth > 0 && dayOrMonth < 10)
        {
            // Add one Leading zero
            return String.format(Locale.ENGLISH, "%01d", dayOrMonth);
        } else
        {
            return String.valueOf(dayOrMonth);
        }
    }
}