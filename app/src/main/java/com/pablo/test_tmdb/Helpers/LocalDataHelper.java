package com.pablo.test_tmdb.Helpers;

import android.content.Context;
import android.util.Log;

import com.pablo.test_tmdb.models.Movie;
import com.pablo.test_tmdb.models.MoviePopular;
import com.pablo.test_tmdb.models.MoviePosition;
import com.pablo.test_tmdb.room.AppDatabase;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LocalDataHelper {
    public static Completable saveMovies(List<Movie> movies, Context context) {
        return Completable.create(emitter -> {
            try {
                AppDatabase.getInstance(context).movieDao().insertAll(movies);

                emitter.onComplete();
            }
            catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    public static void clearPopularMovies(Context context) {
//        return Completable.create(emitter -> {
//            try {
                AppDatabase.getInstance(context).movieDao().deletePopular();

//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                                deletedCount -> emitter.onComplete(),
//                                error -> emitter.onError(error)
//                        );

//                emitter.onComplete();
//            }
//            catch (Exception e) {
//                emitter.onError(e);
//            }
//        });
    }

    public static Completable savePopularMovies(List<MoviePopular> moviesPopular, Context context) {
        return Completable.create(emitter -> {
            try {
                AppDatabase.getInstance(context).moviePopularDao().insertAll(moviesPopular);
            }
            catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    public static Single<List<Movie>> loadPopularMovies(int page, Context context) {
        Log.d("---x", "page: " + page + " offset: " + ((page -1) * MoviesHelper.MOVIES_PER_PAGE));

        return AppDatabase.getInstance(context).moviePopularDao().loadMovies((page - 1) * MoviesHelper.MOVIES_PER_PAGE, MoviesHelper.MOVIES_PER_PAGE);
    }
}
