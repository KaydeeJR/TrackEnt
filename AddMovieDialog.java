package com.example.trackent.ui.movies;

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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackent.MainActivity;
import com.example.trackent.R;

import java.util.Locale;
import java.util.Objects;

public class AddMovieDialog extends DialogFragment
{
    private static final String TAG = "movie_dialog";
    private static MoviesListAdapter moviesListAdapter;
    private boolean EditTextEmpty, isInserted;
    private EditText addMovieTitle;
    private MovieViewModel movieViewModel;
    private Movies movie;
    private RecyclerView recyclerView;
    private TextView textView;
    private ImageView imageView;
    private Context applicationContext;

    private AddMovieDialog(RecyclerView movieRecyclerView, ImageView movieImageView,
        TextView movieTextView)
    {
        this.recyclerView = movieRecyclerView;
        this.imageView = movieImageView;
        this.textView = movieTextView;
    }

    static void displayAddMovieDialog(FragmentManager fragmentManager, MoviesListAdapter adapter,
        RecyclerView movieRecyclerView, TextView movieTextView, ImageView movieImageView)
    {
        AddMovieDialog addMovieDialog = new AddMovieDialog(movieRecyclerView, movieImageView,
            movieTextView);
        addMovieDialog.show(fragmentManager, TAG);
        moviesListAdapter = adapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        applicationContext = MainActivity.getContextOfApplication();
    }

    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.movie_dialog_box, container);
        setCancelable(false);
        Toolbar movieToolbar = view.findViewById(R.id.dialog_toolbar);
        addMovieTitle = view.findViewById(R.id.enter_movie_title);
        DatePicker datePickerMovies = view.findViewById(R.id.release_date);
        movieToolbar.setNavigationOnClickListener(v -> dismiss());
        movieToolbar.setTitle("Add New Movie");
        movieToolbar.inflateMenu(R.menu.save_button);
        movieToolbar.setOnMenuItemClickListener(item ->
        {
            CheckEditTextEmptyFAB();  // check if movie name edit text is empty
            if (EditTextEmpty)
            {
                // if all fields are filled then:
                int day = datePickerMovies.getDayOfMonth();
                int month = datePickerMovies.getMonth() + 1;
                int year = datePickerMovies.getYear();
                String name = addMovieTitle.getText().toString();
                String date = dayToDate(day);
                String monthName = ShowMonthName(month);
                String releaseDate = date + " " + monthName + " " + year;
                String paddedDate = appendZeroToDay(day);
                String releaseDateShort = paddedDate + " " + month + " " + year;
                movie = new Movies(name, releaseDate, releaseDateShort);
                isInserted = movieViewModel.addMovie(movie);
                if (isInserted)
                {
                    dismiss();
                } else
                {
                    Toast.makeText(getContext(), "The movie has not been added",
                        Toast.LENGTH_SHORT).show();
                }
            } else
            {
                Toast.makeText(getContext(), "Please fill in all the required details",
                    Toast.LENGTH_SHORT).show();
            }
            EmptyEditTextAfterDataInsertFAB();
            return true;
        });
        return view;
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

    public void onResume()
    {
        super.onResume();
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
        moviesListAdapter.notifyDataSetChanged();
        try
        {
            if (movieViewModel.getNumberOfMovies().get() <= 0)
            {
                recyclerView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
            } else if (movieViewModel.getNumberOfMovies().get() > 0)
            {
                recyclerView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
            }
        } catch (InterruptedException e)
        {
            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        moviesListAdapter.notifyDataSetChanged();
        try
        {
            if (movieViewModel.getNumberOfMovies().get() <= 0)
            {
                recyclerView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
            } else if (movieViewModel.getNumberOfMovies().get() > 0)
            {
                recyclerView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
            }
        } catch (InterruptedException e)
        {
            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void CheckEditTextEmptyFAB()
    {
        String movieNameHolder = addMovieTitle.getText().toString();
        EditTextEmpty = !TextUtils.isEmpty(movieNameHolder);
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

    private void EmptyEditTextAfterDataInsertFAB()
    {
        addMovieTitle.getText().clear();
    }
}
