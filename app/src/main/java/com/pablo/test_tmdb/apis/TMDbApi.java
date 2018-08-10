package com.pablo.test_tmdb.apis;

import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.pablo.test_tmdb.BuildConfig;
import com.pablo.test_tmdb.models.Movie;
import com.pablo.test_tmdb.models.Video;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TMDbApi {
    private static ITMDbApi itmdbApi;

    public static synchronized ITMDbApi getInstance() {
        if (itmdbApi == null) {
            itmdbApi =  new Retrofit.Builder()
                    .baseUrl(BuildConfig.TMDB_ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
                    .create(ITMDbApi.class);
        }

        return itmdbApi;
    }

    public static Single<List<Movie>> getPopularMovies(int page) {
        Log.d("---x", "loading page: " + page);

        return getInstance().getPopularMovies(page, BuildConfig.TMDB_API_KEY)
                .flatMap(moviesResponse -> Single.just(moviesResponse.getMovies()));
    }

    public static Single<List<Video>> getMovieVideos(int movieId) {
        return getInstance().getMovieVideos(movieId, BuildConfig.TMDB_API_KEY)
                .flatMap(movieVideosResponses -> Single.just(movieVideosResponses.getVideos()));
    }

}
