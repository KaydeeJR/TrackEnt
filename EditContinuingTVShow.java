package com.example.trackent.ui.tv_shows;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.trackent.R;
import com.example.trackent.databinding.AddContinuingTvShowDialogBinding;

import java.util.Locale;
import java.util.Objects;

public class EditContinuingTVShow extends DialogFragment{
    private static final String TAG = "edit_continuing_tv_show";
    private TVShowsViewModel tvShowsViewModel;
    private EditText editContTVShow, editSeason, editEpisode;
    private Boolean isInserted, EditTextEmpty;
    private String nameOfTVShow;
    private int season, episode;
    private TVShows selectedTVShow;

    public EditContinuingTVShow() {
    }

    static void displayEditContinuingTVShow(FragmentManager fragmentManager,
                                            String TVShowName, int currentSeason,
                                            int currentEpisode, TVShows currentTVShow) {
        EditContinuingTVShow editContinuingTVShow = new EditContinuingTVShow();
        editContinuingTVShow.show(fragmentManager, TAG);
        editContinuingTVShow.nameOfTVShow = TVShowName;
        editContinuingTVShow.season = currentSeason;
        editContinuingTVShow.episode = currentEpisode;
        editContinuingTVShow.selectedTVShow = currentTVShow;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvShowsViewModel = new ViewModelProvider(this).get(TVShowsViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        AddContinuingTvShowDialogBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.add_continuing_tv_show_dialog, container, false);
        // REFERENCES TO VIEWS
        Toolbar continuingToolbar = binding.continuingSeriesDialogToolbar;
        editContTVShow = binding.enterContSeriesTitle;
        editSeason = binding.enterContSeasonNumber;
        editEpisode = binding.enterContEpisodeNumber;
        DatePicker datePickerTVShows = binding.seriesReleaseDate;
// SETTING INITIAL VALUES
        editContTVShow.setText(nameOfTVShow);
        editSeason.setText(String.valueOf(season));
        editEpisode.setText(String.valueOf(episode));
        continuingToolbar.setTitle("Edit TV Show");
        continuingToolbar.setNavigationOnClickListener(v -> dismiss());
        continuingToolbar.inflateMenu(R.menu.save_button);
        continuingToolbar.setOnMenuItemClickListener(item -> {
            CheckEditTextEmpty();
            if (EditTextEmpty) {
                String tvShowName = editContTVShow.getText().toString();
                int seasonNumber = Integer.parseInt(editSeason.getText().toString());
                int episodeNumber = Integer.parseInt(editEpisode.getText().toString());
                int day = (datePickerTVShows.getDayOfMonth());
                int month = (datePickerTVShows.getMonth() + 1);
                int year = (datePickerTVShows.getYear());
                String monthName = ShowMonthName(month);
                String date = dayToDate(day);
                String releaseDate = date + " " + monthName + " " + year;
                String paddedDate = appendZeroToDay(day);
                String releaseDateShort = paddedDate + " " + month + " " + year;
                selectedTVShow.setTvShowName(tvShowName);
                selectedTVShow.setSeasonNumber(seasonNumber);
                selectedTVShow.setEpisodeNumber(episodeNumber);
                selectedTVShow.setReleaseDate(releaseDate);
                selectedTVShow.setReleaseDateShort(releaseDateShort);
                selectedTVShow.setNoOfSeasons(seasonNumber);
                isInserted = tvShowsViewModel.updateTVShowsTable(selectedTVShow);
                if (checkIsInserted()) {
                    Toast.makeText(getContext(), "TV Show details updated",
                            Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "TV Show details have not been updated",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please fill in all the required details",
                        Toast.LENGTH_SHORT).show();
            }
            EmptyEditTextAfterInsert();
            return true;
        });
        setCancelable(false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_slide);
        }
    }

    private void CheckEditTextEmpty() {
        String tvShowNameHolder = editContTVShow.getText().toString();
        String seasonNoHolder = editSeason.getText().toString();
        String episodeHolder = editEpisode.getText().toString();
        EditTextEmpty = !TextUtils.isEmpty(tvShowNameHolder) && !TextUtils.isEmpty(seasonNoHolder) &&
                !TextUtils.isEmpty(episodeHolder);
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

    private void EmptyEditTextAfterInsert() {
        editContTVShow.getText().clear();
        editSeason.getText().clear();
        editEpisode.getText().clear();
    }

    private boolean checkIsInserted() {
        return isInserted;
    }
}
