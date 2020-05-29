package com.example.trackent.ui.home;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.trackent.MainActivity;
import com.example.trackent.R;
import com.example.trackent.ui.movies.MovieViewModel;
import com.example.trackent.ui.tv_shows.TVShowsViewModel;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.Objects;

public class HomeFragment extends Fragment
{
    private MovieViewModel movieViewModel;
    private TVShowsViewModel tvShowsViewModel;
    private DialogInterface.OnClickListener dialogClickListener;
    private Context applicationContext;

    //Parent Fragment to Today and WatchList Fragments
    public HomeFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        applicationContext = MainActivity.getContextOfApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ViewPager viewPager = rootView.findViewById(R.id.view_pager);
        setupViewPager(viewPager);
        TabLayout tabLayout = rootView.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        tvShowsViewModel = new ViewModelProvider(this).get(TVShowsViewModel.class);
        return rootView;
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager)
    {
        FragmentPagerAdapter fragmentAdapter = new FragmentPagerAdapter(getChildFragmentManager());
        fragmentAdapter.addFragment(TodayFragment.newInstance(), "Today");
        fragmentAdapter.addFragment(WatchListFragment.newInstance(), "WatchList");
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setOffscreenPageLimit(2);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.clear_movies_list:
                try
                {
                    if (movieViewModel.getNumberOfMovies().get() <= 0)
                    {
                        Toast.makeText(MainActivity.getContextOfApplication(),
                            "Movie List is " + "empty", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        dialogClickListener = (dialog, which) ->
                        {
                            switch (which)
                            {
                                case DialogInterface.BUTTON_POSITIVE:
                                    movieViewModel.deleteAllMovies();
                                    File filesDirectory =
                                        new File(Objects.requireNonNull(applicationContext.getFilesDir().getParent()));
                                    File[] filesInFileDirectory = filesDirectory.listFiles();
                                    if (filesInFileDirectory != null)
                                    {
                                        for (File folder : filesInFileDirectory)
                                        {
                                            if (folder.getName().matches("app_MovieImages"))
                                            {
                                                if (folder.listFiles() != null)
                                                {
                                                    for (File imageFiles :
                                                        Objects.requireNonNull(folder.listFiles()))
                                                    {
                                                        //noinspection ResultOfMethodCallIgnored
                                                        imageFiles.delete();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Toast.makeText(MainActivity.getContextOfApplication(),
                                        "All " + "Movies Have Been Deleted", Toast.LENGTH_SHORT).show();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialog.cancel();
                                    break;
                            }
                        };
                        showAlertDialog();
                    }
                } catch (InterruptedException e)
                {
                    Toast.makeText(applicationContext, "Something went wrong",
                        Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case R.id.clear_tv_shows_list:
                try
                {
                    if (tvShowsViewModel.getNumberOfTVShows().get() <= 0)
                    {
                        Toast.makeText(MainActivity.getContextOfApplication(), "TV Shows List is "
                            + "empty", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        dialogClickListener = (dialog, which) ->
                        {
                            switch (which)
                            {
                                case DialogInterface.BUTTON_POSITIVE:
                                    tvShowsViewModel.deleteAllTVShows();
                                    File filesDirectory =
                                        new File(Objects.requireNonNull(applicationContext.getFilesDir().getParent()));
                                    File[] filesInFileDirectory = filesDirectory.listFiles();
                                    if (filesInFileDirectory != null)
                                    {
                                        for (File folder : filesInFileDirectory)
                                        {
                                            if (folder.getName().matches("app_TVShowImages"))
                                            {
                                                if (folder.listFiles() != null)
                                                {
                                                    for (File imageFiles :
                                                        Objects.requireNonNull(folder.listFiles()))
                                                    {
                                                        //noinspection ResultOfMethodCallIgnored
                                                        imageFiles.delete();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Toast.makeText(getContext(), "All TV Shows Have Been Deleted"
                                        , Toast.LENGTH_SHORT).show();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialog.cancel();
                                    break;
                            }
                        };
                        showAlertDialog();
                    }
                } catch (InterruptedException e)
                {
                    Toast.makeText(applicationContext, "Something went wrong",
                        Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAlertDialog()
    {
        androidx.appcompat.app.AlertDialog.Builder builder =
            new androidx.appcompat.app.AlertDialog.Builder(Objects.requireNonNull(getContext()),
                R.style.MyAlertDialogStyle);
        builder.setMessage(R.string.alert_dialog_message).setPositiveButton(R.string.alert_dialog_positive_button, dialogClickListener).setNegativeButton(R.string.alert_dialog_negative_button, dialogClickListener).show();
        builder.setCancelable(false);
    }
}
