package com.example.trackent.ui.movies;

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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.trackent.R;

import java.util.Locale;
import java.util.Objects;

public class EditMovieDialog extends DialogFragment{
    private static final String TAG = "movie_dialog";
    private boolean EditTextEmpty, isInserted;
    private EditText editMovieTitle;
    private MovieViewModel movieViewModel;
    private Movies movie;
    private Movies selectedMovie;
    private String nameOfMovie;

    public EditMovieDialog() {
    }

    static void displayEditMovieDialog(FragmentManager fragmentManager, String movieName, Movies currentMovie) {
        EditMovieDialog editMovieDialog = new EditMovieDialog();
        editMovieDialog.show(fragmentManager, TAG);
        editMovieDialog.nameOfMovie = movieName;
        editMovieDialog.selectedMovie = currentMovie;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.movie_dialog_box, container);
        setCancelable(false);
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        editMovieTitle = view.findViewById(R.id.enter_movie_title);
        editMovieTitle.setText(nameOfMovie);
        DatePicker datePickerFAB = view.findViewById(R.id.release_date);
        // Toolbar
        Toolbar movieToolbar = view.findViewById(R.id.dialog_toolbar);
        movieToolbar.setTitle("Edit Movie");
        movieToolbar.inflateMenu(R.menu.save_button);
        movieToolbar.setOnMenuItemClickListener(item -> {
            // when SAVE button is clicked
            CheckEditTextEmpty();  // check if movie name edit text is empty
            if (EditTextEmpty) {  // if the edit text is filled then
                int day = datePickerFAB.getDayOfMonth();
                int month = datePickerFAB.getMonth() + 1;
                int year = datePickerFAB.getYear();
                String name = editMovieTitle.getText().toString();
                String date = dayToDate(day);
                String monthName = ShowMonthName(month);
                String releaseDate = date + " " + monthName + " " + year;
                String paddedDate = appendZeroToDay(day);
                String releaseDateShort = paddedDate + " " + month + " " + year;
                movieViewModel.deleteMovie(selectedMovie);
                movie = new Movies(name, releaseDate, releaseDateShort);
                isInserted = movieViewModel.addMovie(movie);  // returns true or false
                if (isInserted = true) {
                    Toast.makeText(getContext(), "Movie details have been updated ",
                            Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "The movie has not been updated",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please fill in the required details",
                        Toast.LENGTH_SHORT).show();
            }
            EmptyEditTextAfterDataInsert(); // clear the edit text after details have been saved
            return true;
        });
        // if X button is clicked:
        movieToolbar.setNavigationOnClickListener(v -> dismiss());
        return view; // end of onCreateView()
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
        String movieNameHolder = editMovieTitle.getText().toString();
        EditTextEmpty = !TextUtils.isEmpty(movieNameHolder);
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

    private void EmptyEditTextAfterDataInsert() {
        editMovieTitle.getText().clear();
    }
}
