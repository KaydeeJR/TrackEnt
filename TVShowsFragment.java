package com.example.trackent.ui.tv_shows;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
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
import com.example.trackent.ViewAnimation;
import com.example.trackent.databinding.FragmentTvShowsBinding;
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

public class TVShowsFragment extends Fragment implements LifecycleOwner,
    TVShowsListAdapter.TVShowsListAdapterListener
{
    private static final int PICK_IMAGE_REQUEST = 2290;
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 1873;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Context applicationContext;
    private Boolean isRotate = false;
    private Uri selectedImage;
    private TVShowsListAdapter tvShowsListAdapter;
    private RecyclerView tvShowsRecyclerView;
    private TVShows currentTVShow;
    private TVShowsViewModel tvShowsViewModel;
    private ImageView imageView;
    private TextView textView;
    private LiveData<List<TVShows>> tvShowsList;
    private File tvShowsImagesFolder;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        tvShowsListAdapter = new TVShowsListAdapter(this.getContext(), this);
        tvShowsViewModel = new ViewModelProvider(this).get(TVShowsViewModel.class);
        tvShowsList = tvShowsViewModel.getAllTVShows();
        applicationContext = MainActivity.getContextOfApplication();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState)
    {
        // OBSERVER IS SET ON DATA
        tvShowsList.observe(getViewLifecycleOwner(), tvShowsListAdapter::setTVShowsList);
        // SETTING THE LAYOUT:
        final FragmentTvShowsBinding binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_tv_shows, container, false);
        //REFERENCES TO VIEWS:
        CoordinatorLayout coordinatorLayout = binding.seriesCoordinatorLayout;
        FloatingActionButton mainFAB = binding.mainSeriesFab;
        FloatingActionButton continuingShowFAB = binding.addContinuingShow;
        FloatingActionButton endedShowFAB = binding.addEndedSeries;
        TextView continuingFABAddContinuingShow = binding.continuingTextView;
        TextView endedFABAddEndedShow = binding.endedTextView;
        tvShowsRecyclerView = binding.seriesRecyclerView;
        imageView = binding.doneImage;
        textView = binding.doneMessage;
        //RECYCLER VIEW PROPERTIES:
        tvShowsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tvShowsRecyclerView.setHasFixedSize(true);
        tvShowsRecyclerView.setAdapter(tvShowsListAdapter);
        // SETTING FAB PROPERTIES
        // INITIALLY THE CHILD FABs ARE HIDDEN
        ViewAnimation.init(continuingShowFAB, continuingFABAddContinuingShow);
        ViewAnimation.init(endedShowFAB, endedFABAddEndedShow);
        // ON CLICK LISTENER FOR MAIN FAB
        mainFAB.setOnClickListener(mainFABView ->
        {
            // WHEN CLICKED:
            // ROTATE MAIN FAB AND
            // SHOW ALL CHILD FABs AND THEIR TEXT VIEWS
            isRotate = ViewAnimation.rotateFab(mainFAB, !isRotate); // isRotate is True
            if (isRotate)
            {
                if (tvShowsListAdapter.getItemCount() != 0)
                {
                    tvShowsRecyclerView.setAlpha(0.1f);
                }
                ViewAnimation.showIn(continuingShowFAB, continuingFABAddContinuingShow);
                ViewAnimation.showIn(endedShowFAB, endedFABAddEndedShow);
            } else
            {
                tvShowsRecyclerView.setAlpha(1f);
                ViewAnimation.showOut(continuingShowFAB, continuingFABAddContinuingShow);
                ViewAnimation.showOut(endedShowFAB, endedFABAddEndedShow);
            }
        });
        continuingShowFAB.setOnClickListener(view ->
        {
            // ROTATE THE MAIN FAB BACK. isRotate IS BACK TO FALSE
            isRotate = false;
            tvShowsRecyclerView.setAlpha(1f);
            ViewAnimation.rotateFab(mainFAB, isRotate);
            ViewAnimation.showOut(continuingShowFAB, continuingFABAddContinuingShow);
            ViewAnimation.showOut(endedShowFAB, endedFABAddEndedShow);
            AddContinuingTVShow.displayAddContinuingTVShow(getParentFragmentManager(),
                tvShowsListAdapter, tvShowsRecyclerView, textView, imageView);
        });
        endedShowFAB.setOnClickListener(view ->
        {
            isRotate = false;
            tvShowsRecyclerView.setAlpha(1f);
            ViewAnimation.rotateFab(mainFAB, isRotate);
            ViewAnimation.showOut(continuingShowFAB, continuingFABAddContinuingShow);
            ViewAnimation.showOut(endedShowFAB, endedFABAddEndedShow);
            AddEndedTVShow.displayAddEndedTVShow(getParentFragmentManager(), tvShowsListAdapter,
                tvShowsRecyclerView, imageView, textView);
        });
        //  END OF FAB BLOCK
        // SCROLL LISTENER FOR RECYCLER VIEW
        tvShowsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && mainFAB.getVisibility() == View.VISIBLE && !isRotate)
                {
                    ViewAnimation.init(continuingShowFAB, continuingFABAddContinuingShow);
                    ViewAnimation.init(endedShowFAB, endedFABAddEndedShow);
                    mainFAB.hide();
                } else if (dy < 0 && mainFAB.getVisibility() != View.VISIBLE)
                {
                    ViewAnimation.init(continuingShowFAB, continuingFABAddContinuingShow);
                    ViewAnimation.init(endedShowFAB, endedFABAddEndedShow);
                    mainFAB.show();
                }
            }
        });
        // CREATE ItemTouchHelper AND ATTACH IT TO THE RECYCLER VIEW.
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
                TVShows tvShows = tvShowsListAdapter.getTVShowAtPosition(position);
                String imageFilePath = tvShows.getTvShowImageFilePath();
                int tvShowID = tvShows.getTvShowID();
                Bitmap imageBitmap = BitmapFactory.decodeFile(imageFilePath);
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Watched",
                    Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(Color.WHITE);
                tvShowsListAdapter.notifyItemRemoved(position);
                tvShowsViewModel.deleteTVShow(tvShows);
                // AFTER DELETING TV SHOW, CHECK IF LIST IS EMPTY
                // AND THEN HIDE OR SHOW RECYCLER VIEW
                try
                {
                    hideTVShowsRecyclerView(tvShowsViewModel.getNumberOfTVShows());
                } catch (InterruptedException e)
                {
                    Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_LONG).show();
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
                            if (folder.getName().matches("app_TVShowImages"))
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
                    tvShowsViewModel.addNewTVShow(tvShows);
                    if (imageFilePath != null)
                    {
                        File imageFile = new File(imageFilePath);
                        tvShowsViewModel.addTVShowImageFilePath(tvShowID, imageFilePath);
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
                                Toast.LENGTH_LONG).show();
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
                                Toast.LENGTH_LONG).show();
                            ioException.printStackTrace();
                        }
                    }
                    try
                    {
                        hideTVShowsRecyclerView(tvShowsViewModel.getNumberOfTVShows());
                    } catch (InterruptedException e)
                    {
                        Toast.makeText(applicationContext, "Something went wrong",
                            Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                });
                snackbar.show();
            }
        });
        itemTouchHelper.attachToRecyclerView(tvShowsRecyclerView);
        tvShowsListAdapter.notifyDataSetChanged();
        try
        {
            hideTVShowsRecyclerView(tvShowsViewModel.getNumberOfTVShows());
        } catch (InterruptedException e)
        {
            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_LONG).show();
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
            // DELAYED WITH 1 SECOND SO THAT DATA IS FETCHED FROM DATABASE
            try
            {
                hideTVShowsRecyclerView(tvShowsViewModel.getNumberOfTVShows());
            } catch (InterruptedException e)
            {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_LONG).show();
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
            //DELAY ALLOWS DATA TO BE LOADED FROM ROOM
            try
            {
                hideTVShowsRecyclerView(tvShowsViewModel.getNumberOfTVShows());
            } catch (InterruptedException e)
            {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }, 1000);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater)
    {
        // SEARCH BUTTON FUNCTIONALITY
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
                    tvShowsListAdapter.getFilter().filter(query);
                    searchView.clearFocus();
                } else
                {
                    tvShowsViewModel.getAllTVShows().observe(getViewLifecycleOwner(),
                        tvShowsListAdapter::setTVShowsList);
                    searchView.clearFocus();
                }
                return true;

            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                if (newText.length() != 0)
                {
                    tvShowsListAdapter.getFilter().filter(newText);
                } else
                {
                    tvShowsViewModel.getAllTVShows().observe(getViewLifecycleOwner(),
                        tvShowsListAdapter::setTVShowsList);
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

    private void hideTVShowsRecyclerView(AtomicInteger numberOfTVShows)
    {
        //METHOD TO HIDE OR SHOW RECYCLER VIEW, IMAGE VIEW, OR TEXT VIEW
        if (numberOfTVShows.get() <= 0)
        {
            tvShowsRecyclerView.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        } else if (numberOfTVShows.get() > 0)
        {
            tvShowsRecyclerView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPopupMenuClick(View view, int pos)
    {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater menuInflater = popup.getMenuInflater();
        menuInflater.inflate(R.menu.tv_shows_cards_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item ->
        {
            switch (item.getItemId())
            {
                // TO UPDATE SERIES
                case R.id.action_update_series:
                    TVShows currentTVShow = tvShowsListAdapter.getTVShowAtPosition(pos);
                    if (currentTVShow.getReleaseDate() != null)
                    {
                        // WEEKLY TV SHOW
                        String tvShowName = currentTVShow.getTvShowName();
                        int season = currentTVShow.getSeasonNumber();
                        int episode = currentTVShow.getEpisodeNumber();
                        // DISPLAY DIALOG FRAGMENT WITH TV SHOW TITLE, CURRENT
                        // SEASON AND CURRENT EPISODE ALREADY SET
                        EditContinuingTVShow.displayEditContinuingTVShow(getParentFragmentManager(), tvShowName, season, episode, currentTVShow);
                    } else
                    {
                        // BINGED TV Show
                        String tvShowName = currentTVShow.getTvShowName();
                        int season = currentTVShow.getSeasonNumber();
                        int episode = currentTVShow.getEpisodeNumber();
                        int noOfSeasons = currentTVShow.getNoOfSeasons();
                        // DISPLAY DIALOG FRAGMENT WITH TV SHOW TITLE, CURRENT
                        // SEASON, CURRENT EPISODE AND NUMBER OF SEASONS ALREADY SET
                        EditEndedTVShow.displayEditEndedTVShow(getParentFragmentManager(),
                            tvShowName, season, episode, noOfSeasons, currentTVShow);
                    }
                    break;
                case R.id.action_set_series_icon:
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
                        tvShowsImagesFolder = applicationContext.getDir("TVShowImages",
                            Context.MODE_PRIVATE);
                        if (!tvShowsImagesFolder.exists())
                        {
                            //noinspection ResultOfMethodCallIgnored
                            tvShowsImagesFolder.mkdirs();
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
    public void getTVShow(View view, int pos)
    {
        currentTVShow = tvShowsListAdapter.getTVShowAtPosition(pos);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults)
    {
        // If request is granted, the result arrays are not empty.
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
            tvShowsImagesFolder = applicationContext.getDir("TVShowImages", Context.MODE_PRIVATE);
            if (!tvShowsImagesFolder.exists())
            {
                //noinspection ResultOfMethodCallIgnored
                tvShowsImagesFolder.mkdirs();
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
                TVShows tvShow = currentTVShow;
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
                                Toast.LENGTH_LONG).show();
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
                                Toast.makeText(applicationContext, "Something went wrong",
                                    Toast.LENGTH_LONG).show();
                                ex.printStackTrace();
                            }
                            File mediaFile =
                                new File(tvShowsImagesFolder.getAbsolutePath() + File.separator + tvShow.getTvShowName() + ".jpg");
                            if (mediaFile.exists())
                            {
                                //noinspection ResultOfMethodCallIgnored
                                mediaFile.delete(); //DELETE existing file
                                mediaFile =
                                    new File(tvShowsImagesFolder.getAbsolutePath() + File.separator + tvShow.getTvShowName() + ".jpg");
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
                                    Toast.LENGTH_LONG).show();
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
                                    Toast.LENGTH_LONG).show();
                                ioException.printStackTrace();
                            }
                            tvShowsViewModel.addTVShowImageFilePath(tvShow.getTvShowID(),
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