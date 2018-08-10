package com.pablo.test_tmdb.Helpers;

import android.content.Context;
import android.util.Log;

import com.pablo.test_tmdb.R;
import com.pablo.test_tmdb.apis.TMDbApi;
import com.pablo.test_tmdb.models.Movie;
import com.pablo.test_tmdb.models.MoviePopular;
import com.pablo.test_tmdb.models.MoviePosition;
import com.pablo.test_tmdb.models.Video;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MoviesHelper {
    private static int currentMoviesPage = 0;
    public static final int MOVIES_PER_PAGE = 20;

    public static Single<List<Movie>> loadPopularMovies(Context context) {
        return loadPopularMovies(context,currentMoviesPage + 1);
    }

    public static Single<List<Movie>> loadPopularMovies(Context context, int page) {
        return loadPopularMovies(context, page, false);
    }

    public static Single<List<Movie>> loadPopularMovies(Context context, int page, boolean skipCache) {
        if (NetworkHelper.isNetworkAvailable(context) || skipCache) {
            Log.d("---x", "get data from internet: page:" + page);

            return TMDbApi.getPopularMovies(page)
                    .doOnSuccess(movies -> {
                        // persist movies on local database
                        LocalDataHelper.saveMovies(movies, context).subscribe(() -> {}, error -> {error.printStackTrace();});

                        // persist popular movies
                        Log.d("---x", "currentMoviesPage: " + currentMoviesPage);

                        if (page == 1) {
                            LocalDataHelper.clearPopularMovies(context);
                            List<MoviePopular> moviesPopular = new ArrayList<>();

                            for (int i = 0; i < movies.size(); i++) {
                                moviesPopular.add(new MoviePopular((page-1) * MOVIES_PER_PAGE + i + 1, movies.get(i).getId()));
                            }

                            LocalDataHelper.savePopularMovies(moviesPopular, context).subscribe(
                                    () -> {},
                                    error -> error.printStackTrace()
                            );
                        }
                        else {
                            List<MoviePopular> moviesPopular = new ArrayList<>();

                            for (int i = 0; i < movies.size(); i++) {
                                moviesPopular.add(new MoviePopular((page-1) * MOVIES_PER_PAGE + i + 1, movies.get(i).getId()));
                            }

                            LocalDataHelper.savePopularMovies(moviesPopular, context).subscribe(
                                    () -> {},
                                    error -> error.printStackTrace()
                            );
                        }

                        if (movies.size() > 0) {
                            currentMoviesPage = page;
                        }
                    });
        }
        else {
            Log.d("---x", "get data from cache: page:" + page);

            return LocalDataHelper.loadPopularMovies(page, context)
                    .doOnSuccess(
                            movies -> {
                                if (movies.size() > 0) {
                                    currentMoviesPage = page;
                                }
                            }
                    );
        }

    }

    public static int getCurrentMoviesPage() {
        return currentMoviesPage;
    }

    public static String getMovieUrl(String posterPath, Context context) {
        return String.format(context.getString(R.string.poster_path_format), posterPath);
    }

    private static List<MoviePosition> getMoviePositions(List<Movie> movies, int page) {
        List<MoviePosition> moviesPosition = new ArrayList<>();

        for (int i = 0; i < movies.size(); i++) {
            moviesPosition.add(
                    new MoviePosition((page - 1) * MOVIES_PER_PAGE + i + 1, movies.get(i).getId())
            );
        }

        return moviesPosition;
    }

    // Videos
    public static Single<Video> getMovieVideo(int videoId) {
        return TMDbApi.getMovieVideos(videoId)
                .flatMap(videos -> Single.just(videos.size() != 0 ? videos.get(0) : null));
    }

}
