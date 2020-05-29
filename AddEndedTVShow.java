package com.example.trackent.ui.tv_shows;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.trackent.databinding.AddEndedTvShowLayoutBinding;

import java.util.Objects;

public class AddEndedTVShow extends DialogFragment
{
    private static final String TAG = "new_ended_tv_show";
    private EditText endedTVShowName, seasonNumber, episodeNumber, noOfSeasons;
    private Boolean EditTextEmpty, isInserted;
    private TVShows tvShows;
    private TVShowsViewModel tvShowsViewModel;
    private RecyclerView recyclerView;
    private ImageView imageView;
    private TextView textView;
    private static TVShowsListAdapter tvShowsListAdapter;
    private Context applicationContext;

    private AddEndedTVShow(RecyclerView recyclerView, ImageView imageView, TextView textView)
    {
        this.recyclerView = recyclerView;
        this.imageView = imageView;
        this.textView = textView;
    }

    static void displayAddEndedTVShow(FragmentManager fragmentManager, TVShowsListAdapter adapter
        , RecyclerView recyclerView, ImageView imageView, TextView textView)
    {
        AddEndedTVShow addEndedTVShow = new AddEndedTVShow(recyclerView, imageView, textView);
        addEndedTVShow.show(fragmentManager, TAG);
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
        final AddEndedTvShowLayoutBinding binding = DataBindingUtil.inflate(inflater,
            R.layout.add_ended_tv_show_layout, container, false);
        // REFERENCES TO VIEWS
        endedTVShowName = binding.enterEndedSeriesTitle;
        seasonNumber = binding.enterEndedSeasonNumber;
        episodeNumber = binding.enterEndedEpisodeNumber;
        noOfSeasons = binding.enterNumberOfSeasons;
        Toolbar endedToolbar = binding.endedSeriesDialogToolbar;
        endedToolbar.setTitle("Add New TV Show");
        endedToolbar.setNavigationOnClickListener(v -> dismiss());
        endedToolbar.inflateMenu(R.menu.save_button);
        endedToolbar.setOnMenuItemClickListener(item ->
        {
            CheckEditTextEmpty();
            if (EditTextEmpty)
            {
                String tvShowName = endedTVShowName.getText().toString();
                int seasonNo = Integer.parseInt(seasonNumber.getText().toString());
                int episodeNo = Integer.parseInt(episodeNumber.getText().toString());
                int numberOfSeasons = Integer.parseInt(noOfSeasons.getText().toString());
                tvShows = new TVShows(tvShowName, seasonNo, episodeNo, null, numberOfSeasons, null);
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

    private void CheckEditTextEmpty()
    {
        String tvShowNameHolder = endedTVShowName.getText().toString();
        String seasonNoHolder = seasonNumber.getText().toString();
        String episodeHolder = episodeNumber.getText().toString();
        String noOfSeasonsHolder = noOfSeasons.getText().toString();
        EditTextEmpty =
            !TextUtils.isEmpty(tvShowNameHolder) && !TextUtils.isEmpty(seasonNoHolder) && !TextUtils.isEmpty(episodeHolder) && !TextUtils.isEmpty(noOfSeasonsHolder);
    }

    private void EmptyEditTextAfterInsert()
    {
        endedTVShowName.getText().clear();
        seasonNumber.getText().clear();
        episodeNumber.getText().clear();
        noOfSeasons.getText().clear();
    }

    private boolean checkIsInserted()
    {
        return isInserted;
    }
}
