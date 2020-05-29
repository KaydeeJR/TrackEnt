package com.example.trackent.ui.tv_shows;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackent.MainActivity;
import com.example.trackent.R;
import com.example.trackent.databinding.AddContinuingTvShowDialogBinding;

import java.util.Locale;
import java.util.Objects;

public class AddContinuingTVShow extends DialogFragment
{
    private static final String TAG = "new_continuing_tv_show";
    private EditText addNewContinuingShow, enterSeasonNo, enterEpisodeNo;
    private Boolean EditTextEmpty, isInserted;
    private TVShows tvShows;
    private TVShowsViewModel tvShowsViewModel;
    private RecyclerView recyclerView;
    private ImageView imageView;
    private TextView textView;
    private Context applicationContext;
    private static TVShowsListAdapter tvShowsListAdapter;

    private AddContinuingTVShow(RecyclerView recyclerView, ImageView imageView, TextView textView)
    {
        this.recyclerView = recyclerView;
        this.imageView = imageView;
        this.textView = textView;
    }

    static void displayAddContinuingTVShow(FragmentManager fragmentManager,
        TVShowsListAdapter adapter, RecyclerView contRecyclerView, TextView contTextView,
        ImageView contImageView)
    {
        AddContinuingTVShow addContinuingTVShow = new AddContinuingTVShow(contRecyclerView,
            contImageView, contTextView);
        addContinuingTVShow.show(fragmentManager, TAG);
        tvShowsListAdapter = adapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        tvShowsViewModel = new ViewModelProvider(this).get(TVShowsViewModel.class);
        applicationContext = MainActivity.getContextOfApplication();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        final AddContinuingTvShowDialogBinding binding = DataBindingUtil.inflate(inflater,
            R.layout.add_continuing_tv_show_dialog, container, false);
        // REFERENCES TO VIEWS
        addNewContinuingShow = binding.enterContSeriesTitle;
        enterSeasonNo = binding.enterContSeasonNumber;
        enterEpisodeNo = binding.enterContEpisodeNumber;
        DatePicker datePickerTVShows = binding.seriesReleaseDate;
        Toolbar continuingToolbar = binding.continuingSeriesDialogToolbar;
        //SETTING VIEW PROPERTIES
        continuingToolbar.setTitle("Add New TV Show");
        continuingToolbar.setNavigationOnClickListener(v -> AddContinuingTVShow.this.dismiss());
        continuingToolbar.inflateMenu(R.menu.save_button);
        continuingToolbar.setOnMenuItemClickListener(item ->
        {
            CheckEditTextEmpty();
            if (EditTextEmpty)
            {
                String tvShowName = addNewContinuingShow.getText().toString();
                int seasonNumber = Integer.parseInt(enterSeasonNo.getText().toString());
                int episodeNumber = Integer.parseInt(enterEpisodeNo.getText().toString());
                int day = (datePickerTVShows.getDayOfMonth());
                int month = (datePickerTVShows.getMonth() + 1);
                int year = (datePickerTVShows.getYear());
                String monthName = ShowMonthName(month);
                String date = dayToDate(day);
                String releaseDate = date + " " + monthName + " " + year;
                String paddedDate = appendZeroToDay(day);
                String releaseDateShort = paddedDate + " " + month + " " + year;
                tvShows = new TVShows(tvShowName, seasonNumber, episodeNumber, releaseDate,
                    seasonNumber, releaseDateShort);
                isInserted = tvShowsViewModel.addNewTVShow(tvShows);
                tvShowsListAdapter.notifyDataSetChanged();
                if (checkIsInserted())
                {
                    Toast.makeText(getContext(), "New TV Show added successfully",
                        Toast.LENGTH_SHORT).show();
                    dismiss();
                } else
                {
                    Toast.makeText(getContext(), "The TV Show has not been added",
                        Toast.LENGTH_SHORT).show();
                }
            } else
            {
                Toast.makeText(getContext(), "Please fill in all the required details",
                    Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        setCancelable(false);
        EmptyEditTextAfterInsert();
        return binding.getRoot();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_slide);
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        try
        {
            if (tvShowsViewModel.getNumberOfTVShows().get() <= 0)
            {
                recyclerView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
            } else if (tvShowsViewModel.getNumberOfTVShows().get() > 0)
            {
                recyclerView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
            }
        } catch (InterruptedException e)
        {
            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        try
        {
            if (tvShowsViewModel.getNumberOfTVShows().get() <= 0)
            {
                recyclerView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
            } else if (tvShowsViewModel.getNumberOfTVShows().get() > 0)
            {
                recyclerView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
            }
        } catch (InterruptedException e)
        {
            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
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

    private String appendZeroToDay(int day)
    {
        if (day > 0 && day < 10)
        {
            // Add one Leading zero
            return String.format(Locale.ENGLISH, "%01d", day);
        } else
        {
            return String.valueOf(day);
        }
    }

    private void CheckEditTextEmpty()
    {
        String tvShowNameHolder = addNewContinuingShow.getText().toString();
        String seasonNoHolder = enterSeasonNo.getText().toString();
        String episodeHolder = enterEpisodeNo.getText().toString();
        EditTextEmpty =
            !TextUtils.isEmpty(tvShowNameHolder) && !TextUtils.isEmpty(seasonNoHolder) && !TextUtils.isEmpty(episodeHolder);
    }

    private void EmptyEditTextAfterInsert()
    {
        addNewContinuingShow.getText().clear();
        enterSeasonNo.getText().clear();
        enterEpisodeNo.getText().clear();
    }

    private boolean checkIsInserted()
    {
        return isInserted;
    }
}
