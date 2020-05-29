package com.example.trackent.ui.movies;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trackent.MainActivity;
import com.example.trackent.R;
import com.example.trackent.databinding.FragmentMoviesBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MovieFragment extends Fragment implements LifecycleOwner,
    MoviesListAdapter.MovieListAdapterListener
{
    private static final int PICK_IMAGE_REQUEST = 2290;
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 1873;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private MovieViewModel movieViewModel;
    private RecyclerView moviesRecyclerView;
    private CoordinatorLayout coordinatorLayout;
    private Movies movie;
    private Uri selectedImage;
    private Context applicationContext;
    private MoviesListAdapter moviesListAdapter;
    private LiveData<List<Movies>> moviesList;
    private ImageView imageView;
    private TextView textView;
    private File movieImagesFolder;

    public MovieFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        moviesList = movieViewModel.getAllMovies();
        applicationContext = MainActivity.getContextOfApplication();
        moviesListAdapter = new MoviesListAdapter(applicationContext, this);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState)
    {
        // setting the layout
        final FragmentMoviesBinding fragmentMoviesBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_movies, container, false);
        coordinatorLayout = fragmentMoviesBinding.moviesCoordinatorLayout;
        // layout has been set

        // Referencing views & setting animations
        moviesRecyclerView = fragmentMoviesBinding.movieRecyclerView;
        moviesRecyclerView.setLayoutManager(new LinearLayoutManager(applicationContext));
        moviesRecyclerView.setHasFixedSize(true);
        moviesList.observe(getViewLifecycleOwner(), moviesListAdapter::setMovieList);
        moviesRecyclerView.setAdapter(moviesListAdapter);
        imageView = fragmentMoviesBinding.doneImageMoviesFragment;
        textView = fragmentMoviesBinding.doneMessageMoviesFragment;
        // End of Recycler code block

        //FAB Block
        FloatingActionButton floatingActionButton = fragmentMoviesBinding.fabMain;
        floatingActionButton.setOnClickListener(view ->
        {
            AddMovieDialog.displayAddMovieDialog(getParentFragmentManager(), moviesListAdapter,
                moviesRecyclerView, textView, imageView);
            moviesListAdapter.notifyDataSetChanged();
            try
            {
                hideRecyclerView(movieViewModel.getNumberOfMovies());
            } catch (InterruptedException e)
            {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
        // end of FAB block
        // Scroll listener for RecyclerView
        moviesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && floatingActionButton.getVisibility() == View.VISIBLE)
                {
                    floatingActionButton.hide();
                } else if (dy < 0 && floatingActionButton.getVisibility() != View.VISIBLE)
                {
                    floatingActionButton.show();
                }
            }
        });
        // create the ItemTouchHelper and attach the ItemTouchHelper to the RecyclerView.
        ItemTouchHelper itemTouchHelper =
            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                @NonNull RecyclerView.ViewHolder viewHolder,
                @NonNull RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
            {
                int position = viewHolder.getAdapterPosition();
                movie = moviesListAdapter.getMovieAtPosition(position);
                String imageFilePath = movie.getMovieImageFilePath();
                int movieID = movie.getMovieID();
                Bitmap imageBitmap = BitmapFactory.decodeFile(imageFilePath);
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Watched",
                    Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(Color.WHITE);
                moviesListAdapter.notifyItemRemoved(position);
                movieViewModel.deleteMovie(movie);
                try
                {
                    hideRecyclerView(movieViewModel.getNumberOfMovies());
                } catch (InterruptedException e)
                {
                    Toast.makeText(applicationContext, "Something went wrong",
                        Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                // also delete image file if it exists
                if (imageFilePath != null)
                {
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
                                        if (imageFiles.getAbsolutePath().matches(imageFilePath))
                                            //noinspection ResultOfMethodCallIgnored
                                            imageFiles.delete();
                                    }
                                }
                            }
                        }
                    }
                }
                snackbar.setAction("UNDO DELETE", v ->
                {
                    movieViewModel.addMovie(movie);
                    if (imageFilePath != null)
                    {
                        File imageFile = new File(imageFilePath);
                        movieViewModel.addMovieImageFilePath(movieID, imageFilePath);
                        FileOutputStream fileOutputStream;
                        BufferedOutputStream outputStream = null;
                        try
                        {
                            fileOutputStream = new FileOutputStream(imageFile);
                            outputStream = new BufferedOutputStream(fileOutputStream);
                            if (imageBitmap != null)
                            {
                                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50,
                                    fileOutputStream);
                            }
                        } catch (FileNotFoundException fileNotFoundException)
                        {
                            Toast.makeText(applicationContext, "File Not Found",
                                Toast.LENGTH_SHORT).show();
                            fileNotFoundException.printStackTrace();
                        }
                        try
                        {
                            if (outputStream != null)
                            {
                                outputStream.close();
                            }
                        } catch (IOException ioException)
                        {
                            Toast.makeText(applicationContext, "Something went wrong",
                                Toast.LENGTH_SHORT).show();
                            ioException.printStackTrace();
                        }
                    }
                    try
                    {
                        hideRecyclerView(movieViewModel.getNumberOfMovies());
                    } catch (InterruptedException e)
                    {
                        Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
                snackbar.show();
            }
        });
        itemTouchHelper.attachToRecyclerView(moviesRecyclerView);
        moviesListAdapter.notifyDataSetChanged();
        try

        {
            hideRecyclerView(movieViewModel.getNumberOfMovies());
        } catch (InterruptedException e)

        {
            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return fragmentMoviesBinding.getRoot();
    }

    public void onStart()
    {
        super.onStart();
        final Handler handler = new Handler();
        // Do something after 1s = 1000ms
        handler.postDelayed(() ->
        {
            try
            {
                hideRecyclerView(movieViewModel.getNumberOfMovies());
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
        // Do something after 1s = 1000ms
        handler.postDelayed(() ->
        {
            try
            {
                hideRecyclerView(movieViewModel.getNumberOfMovies());
            } catch (InterruptedException e)
            {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }, 1000);
    }

    private void hideRecyclerView(AtomicInteger numberOfMovies)
    {
        if (numberOfMovies.get() <= 0)
        {
            moviesRecyclerView.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        } else if (numberOfMovies.get() > 0)
        {
            moviesRecyclerView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater)
    {
        // SEARCH FUNCTIONALITY ONLY
        inflater.inflate(R.menu.menu_movies_tv_shows, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                if (query.length() != 0)
                {
                    moviesListAdapter.getFilter().filter(query);
                    searchView.clearFocus();
                } else
                {
                    movieViewModel.getAllMovies().observe(getViewLifecycleOwner(),
                        moviesListAdapter::setMovieList);
                    searchView.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                if (newText.length() != 0)
                {
                    moviesListAdapter.getFilter().filter(newText);
                } else
                {
                    movieViewModel.getAllMovies().observe(getViewLifecycleOwner(),
                        moviesListAdapter::setMovieList);
                }
                return true;
            }
        });
        searchView.setOnCloseListener(() ->
        {
            searchView.clearFocus();
            return true;
        });
    }

    @Override
    public void onPopupMenuClick(View view, int pos)
    {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater menuInflater = popup.getMenuInflater();
        menuInflater.inflate(R.menu.movie_cards_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item ->
        {
            switch (item.getItemId())
            {
                case R.id.action_update_movie:
                    Movies currentMovie = moviesListAdapter.getMovieAtPosition(pos);
                    String movieName = currentMovie.getMovieTitle();
                    EditMovieDialog.displayEditMovieDialog(getParentFragmentManager(), movieName,
                        currentMovie);
                    break;
                case R.id.action_set_movie_icon:
                    if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        // Request the permission
                        requestPermissions(PERMISSIONS_STORAGE,
                            MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                    } else
                    {
                        // Permission has already been granted
                        // Create intent to access all images in the device.
                        Intent imagePickerIntent = new Intent(Intent.ACTION_GET_CONTENT,
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        imagePickerIntent.setDataAndType(selectedImage, "image/*");
                        imagePickerIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        // Start the Intent
                        startActivityForResult(imagePickerIntent, PICK_IMAGE_REQUEST);
                        // create a folder for app images
                        movieImagesFolder = applicationContext.getDir("MovieImages",
                            Context.MODE_PRIVATE);
                        if (!movieImagesFolder.exists())
                        {
                            //noinspection ResultOfMethodCallIgnored
                            movieImagesFolder.mkdirs();
                        }
                    }
                    break;
                default:
                    return false;
            }
            return true;
        });
        popup.show();
    }

    @Override
    public void getMovie(View view, int pos)
    {
        movie = moviesListAdapter.getMovieAtPosition(pos);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults)
    {
        // If request is granted, the result arrays are not empty
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            // permission was granted
            Intent imagePickerIntent = new Intent(Intent.ACTION_GET_CONTENT,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            imagePickerIntent.setDataAndType(selectedImage, "image/*");
            imagePickerIntent.addCategory(Intent.CATEGORY_OPENABLE);
            // Start the Intent
            startActivityForResult(imagePickerIntent, PICK_IMAGE_REQUEST);
            // create a folder for movie images in files directory
            movieImagesFolder = applicationContext.getDir("MovieImages", Context.MODE_PRIVATE);
            if (!movieImagesFolder.exists())
            {
                //noinspection ResultOfMethodCallIgnored
                movieImagesFolder.mkdirs();
            }
        } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
        {
            // permission denied
            Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    // other 'case' lines to check for other
    // permissions this app might request.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try
        {
            // When an Image is picked
            getActivity();
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null)
            {
                selectedImage = data.getData(); // retrieves image path from storage
                Movies currentMovie = movie;
                if (selectedImage != null)
                {
                    Thread copyImage = new Thread(() ->
                    {
                        //save a copy of Image in movie image folder
                        //Open a stream on to the content associated with a content URI. (channel)
                        //Input stream refers to the flow of bytes from an input source e.g. uri
                        BufferedInputStream openInputStream = null;
                        InputStream inputStream;
                        try
                        {
                            inputStream = applicationContext.getContentResolver().
                                openInputStream(selectedImage);
                            if (inputStream != null)
                            {
                                openInputStream = new BufferedInputStream(inputStream);
                            }
                        } catch (FileNotFoundException e)
                        {
                            Toast.makeText(applicationContext, "File Not Found",
                                Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        if (openInputStream != null)
                        {
                            //Creates Bitmap object from stream
                            Bitmap imageBitmap = BitmapFactory.decodeStream(openInputStream, null
                                , null);
                            try
                            {
                                openInputStream.close();
                            } catch (IOException ex)
                            {
                                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                                ex.printStackTrace();
                            }
                            File mediaFile =
                                new File(movieImagesFolder.getAbsolutePath() + File.separator + currentMovie.getMovieTitle() + ".jpg");
                            if (mediaFile.exists())
                            {
                                //noinspection ResultOfMethodCallIgnored
                                mediaFile.delete(); //DELETE existing file
                                mediaFile =
                                    new File(movieImagesFolder.getAbsolutePath() + File.separator + currentMovie.getMovieTitle() + ".jpg");
                            }
                            FileOutputStream fileOutputStream;
                            BufferedOutputStream outputStream = null;
                            try
                            {
                                //create output stream for writing data to image file
                                fileOutputStream = new FileOutputStream(mediaFile);
                                outputStream = new BufferedOutputStream(fileOutputStream);
                                if (imageBitmap != null)
                                {
                                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50,
                                        fileOutputStream);
                                }
                            } catch (FileNotFoundException fileNotFoundException)
                            {
                                Toast.makeText(applicationContext, "File Not Found",
                                    Toast.LENGTH_SHORT).show();
                                fileNotFoundException.printStackTrace();
                            }
                            try
                            {
                                if (outputStream != null)
                                {
                                    outputStream.close();
                                }
                            } catch (IOException ioException)
                            {
                                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                                ioException.printStackTrace();
                            }
                            movieViewModel.addMovieImageFilePath(currentMovie.getMovieID(),
                                mediaFile.getAbsolutePath());
                        }
                    });
                    copyImage.start();
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
            {
                Toast.makeText(applicationContext, "You haven't picked an Image",
                    Toast.LENGTH_LONG).show();
            }
        } catch (Exception e)
        {
            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}