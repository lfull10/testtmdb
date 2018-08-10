package com.pablo.test_tmdb;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.pablo.test_tmdb.Helpers.MoviesHelper;
import com.pablo.test_tmdb.Helpers.NetworkHelper;
import com.pablo.test_tmdb.Listeners.PaginationScrollListener;
import com.pablo.test_tmdb.adapters.MoviesListAdapter;
import com.pablo.test_tmdb.models.Movie;
import com.pablo.test_tmdb.room.AppDatabase;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements MoviesListAdapter.MovieClickedListener {
    RecyclerView moviesList;
    MoviesListAdapter moviesListAdapter;
    LinearLayoutManager moviesListLayoutManager;
    TextView offlineMessage;
    private SwipeRefreshLayout moviesRefresh;

    boolean waitUntilNetwork = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        moviesListLayoutManager = new LinearLayoutManager(MainActivity.this);
        moviesList.setLayoutManager(moviesListLayoutManager);
        moviesListAdapter = new MoviesListAdapter(this);
        moviesList.setAdapter(moviesListAdapter);

        loadMovies(1);

        initEvents();
    }

    private void loadMovies() {
        loadMovies(MoviesHelper.getCurrentMoviesPage() + 1);
    }

    private void loadMovies(int page) {
        Log.d("---x", "loadMovies");

        boolean isNetworkAvailable = NetworkHelper.isNetworkAvailable(MainActivity.this);

        offlineMessage.setVisibility(isNetworkAvailable ? View.GONE : View.VISIBLE);

        if (isNetworkAvailable) {
            waitUntilNetwork = false;
        }

        if (!waitUntilNetwork && !moviesListAdapter.isLoadingMovies()) {
            moviesListAdapter.showLoading(true);

            MoviesHelper.loadPopularMovies(getApplicationContext(), page)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            movies -> {
                                if (movies.size() == 0 && !isNetworkAvailable) {
                                    waitUntilNetwork = true;

                                    moviesListAdapter.showLoading(false);

                                    Log.d("---x", "waitUntilNetwork");
                                }
                                else {
                                    waitUntilNetwork = false;

                                    if (MoviesHelper.getCurrentMoviesPage() == 1) {
                                        moviesListAdapter.setMovies(movies);
                                    }
                                    else {
                                        moviesListAdapter.addMovies(movies);
                                    }

                                    AppDatabase.getInstance(getApplicationContext()).movieDao().getCount()
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    count -> {
                                                        Log.d("---x", "count: " + count);
                                                    },
                                                    error -> {
                                                        error.printStackTrace();
                                                    }
                                            );
                                }

                                moviesRefresh.setRefreshing(false);
                            },
                            error -> {
                                error.printStackTrace();

                                moviesRefresh.setRefreshing(false);
                            }
                    );
        }
        else {
            moviesRefresh.setRefreshing(false);
        }
    }

    private void initViews() {
        moviesList = findViewById(R.id.movies_list);
        offlineMessage = findViewById(R.id.offline_message);
        moviesRefresh = findViewById(R.id.moviesRefresh);
    }

    private void initEvents() {
        moviesList.addOnScrollListener(new PaginationScrollListener(moviesListLayoutManager) {
            @Override
            protected void loadMoreItems() {
                moviesList.post(() -> loadMovies());
            }

            @Override
            public int getTotalPageCount() {
                return 0;
            }

            @Override
            public boolean isLastPage() {
                return false;
            }

            @Override
            public boolean isLoading() {
                return false;
            }
        });

        moviesRefresh.setOnRefreshListener(() -> {
            loadMovies(1);
        });
    }

    @Override
    public void onMovieClicked(Movie movie) {
        MovieActivity.start(movie, MainActivity.this);
    }
}
