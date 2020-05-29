package com.example.trackent.ui.tv_shows;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.trackent.R;
import com.example.trackent.databinding.AddEndedTvShowLayoutBinding;

import java.util.Objects;

public class EditEndedTVShow extends DialogFragment{
    private static final String TAG = "edit_ended_tv_show";
    private TVShowsViewModel tvShowsViewModel;
    private EditText editTVShowName, editSeason, editEpisode, editNoOfSeasons;
    private Boolean isInserted, EditTextEmpty;
    private String nameOfTVShow;
    private int season, episode, numberOfSeasons;
    private TVShows selectedTVShow;

    public EditEndedTVShow() {
    }

    static void displayEditEndedTVShow(FragmentManager fragmentManager,
                                       String TVShowName, int currentSeason,
                                       int currentEpisode, int noOfSeasons,
                                       TVShows currentTVShow) {
        EditEndedTVShow editEndedTVShow = new EditEndedTVShow();
        editEndedTVShow.show(fragmentManager, TAG);
        editEndedTVShow.nameOfTVShow = TVShowName;
        editEndedTVShow.season = currentSeason;
        editEndedTVShow.episode = currentEpisode;
        editEndedTVShow.numberOfSeasons = noOfSeasons;
        editEndedTVShow.selectedTVShow = currentTVShow;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvShowsViewModel = new ViewModelProvider(this).get(TVShowsViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        AddEndedTvShowLayoutBinding binding = DataBindingUtil.inflate(inflater, R.layout.add_ended_tv_show_layout, container, false);
        // REFERENCES TO VIEWS
        Toolbar endedToolbar = binding.endedSeriesDialogToolbar;
        editTVShowName = binding.enterEndedSeriesTitle;
        editSeason = binding.enterEndedSeasonNumber;
        editEpisode = binding.enterEndedEpisodeNumber;
        editNoOfSeasons = binding.enterNumberOfSeasons;
// SETTING INITIAL VALUES
        editTVShowName.setText(nameOfTVShow);
        editSeason.setText(String.valueOf(season));
        editEpisode.setText(String.valueOf(episode));
        editNoOfSeasons.setText(String.valueOf(numberOfSeasons));
        endedToolbar.setTitle("Edit TV Show");
        endedToolbar.setNavigationOnClickListener(v -> dismiss());
        endedToolbar.inflateMenu(R.menu.save_button);
        endedToolbar.setOnMenuItemClickListener(item -> {
            CheckEditTextEmpty();
            if (EditTextEmpty) {
                String tvShowName = editTVShowName.getText().toString();
                int seasonNumber = Integer.parseInt(editSeason.getText().toString());
                int episodeNumber = Integer.parseInt(editEpisode.getText().toString());
                int numberOfSeasons = Integer.parseInt(editNoOfSeasons.getText().toString());
                selectedTVShow.setTvShowName(tvShowName);
                selectedTVShow.setSeasonNumber(seasonNumber);
                selectedTVShow.setEpisodeNumber(episodeNumber);
                selectedTVShow.setNoOfSeasons(numberOfSeasons);
                isInserted = tvShowsViewModel.updateTVShowsTable(selectedTVShow);
                if (checkInserted()) {
                    Toast.makeText(getContext(), "TV Show Details Updated",
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
        String tvShowNameHolder = editTVShowName.getText().toString();
        String seasonNoHolder = editSeason.getText().toString();
        String episodeHolder = editEpisode.getText().toString();
        String noOfSeasonsHolder = editNoOfSeasons.getText().toString();
        EditTextEmpty = !TextUtils.isEmpty(tvShowNameHolder) && !TextUtils.isEmpty(seasonNoHolder) &&
                !TextUtils.isEmpty(episodeHolder) && !TextUtils.isEmpty(noOfSeasonsHolder);
    }

    private void EmptyEditTextAfterInsert() {
        editTVShowName.getText().clear();
        editSeason.getText().clear();
        editEpisode.getText().clear();
        editNoOfSeasons.getText().clear();

    }
    private boolean checkInserted(){
        return isInserted;
    }
}
