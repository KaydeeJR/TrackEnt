package com.example.trackent.ui.home;

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
import com.example.trackent.databinding.NextEpisodeAlertDialogBinding;
import com.example.trackent.ui.tv_shows.TVShows;
import com.example.trackent.ui.tv_shows.TVShowsViewModel;

import java.util.Objects;

public class NextEpisodeDialogFragment extends DialogFragment{
    private static final String TAG = "ended_tv_show_next_episode";
    private EditText seasonNumber, episodeNumber;
    private Boolean EditTextEmpty, isInserted;
    private TVShows endedTVShow;
    private int seasonNo, episodeNo;
    private TVShowsViewModel tvShowsViewModel;

    static void displayAddNextEpisodeFragment(FragmentManager fragmentManager, TVShows currentTVShow,
                                              int seasonNumber, int episodeNumber) {
        NextEpisodeDialogFragment nextEpisodeDialogFragment = new NextEpisodeDialogFragment();
        nextEpisodeDialogFragment.show(fragmentManager, TAG);
        nextEpisodeDialogFragment.endedTVShow = currentTVShow;
        nextEpisodeDialogFragment.seasonNo = seasonNumber;
        nextEpisodeDialogFragment.episodeNo = episodeNumber;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvShowsViewModel = new ViewModelProvider(this).get(TVShowsViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        NextEpisodeAlertDialogBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.next_episode_alert_dialog, container, false);
        seasonNumber = binding.watchlistEndedNextSeason;
        episodeNumber = binding.watchlistEndedNextEpisode;
        Toolbar nextEpisodeToolbar = binding.watchlistNextEpisodeToolbar;
        nextEpisodeToolbar.setTitle(R.string.next_episode);
        nextEpisodeToolbar.setNavigationOnClickListener(v -> dismiss());
        nextEpisodeToolbar.inflateMenu(R.menu.save_button);
        nextEpisodeToolbar.setOnMenuItemClickListener(item -> {
            CheckEditTextEmpty();
            if (EditTextEmpty) {
                seasonNo = Integer.parseInt(seasonNumber.getText().toString());
                episodeNo = Integer.parseInt(episodeNumber.getText().toString());
                endedTVShow.setSeasonNumber(seasonNo);
                endedTVShow.setEpisodeNumber(episodeNo);
                isInserted = tvShowsViewModel.updateTVShowsTable(endedTVShow);
                if (checkIfInserted()) {
                    Toast.makeText(getContext(), "TV Show Episode updated",
                            Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "The TV Show Episode has not been updated",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "Please fill in all the required details",
                        Toast.LENGTH_LONG).show();
            }
            return true;
        });
        setCancelable(false);
        EmptyEditTextAfterInsert();
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
        String seasonNoHolder = seasonNumber.getText().toString();
        String episodeHolder = episodeNumber.getText().toString();
        EditTextEmpty = !TextUtils.isEmpty(seasonNoHolder) || !TextUtils.isEmpty(episodeHolder);
    }

    private void EmptyEditTextAfterInsert() {
        seasonNumber.getText().clear();
        episodeNumber.getText().clear();
    }
    private boolean checkIfInserted(){
        return isInserted;
    }
}
