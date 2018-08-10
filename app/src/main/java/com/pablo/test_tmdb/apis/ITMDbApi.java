package com.pablo.test_tmdb.apis;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ITMDbApi {
    @GET("movie/popular")
    Single<MoviesResponse> getPopularMovies(@Query("page") int page, @Query("api_key") String apiKey);

    @GET("movie/{videoId}/videos")
    Single<MovieVideosResponse> getMovieVideos(@Path("videoId") int videoId, @Query("api_key") String apiKey);
}
