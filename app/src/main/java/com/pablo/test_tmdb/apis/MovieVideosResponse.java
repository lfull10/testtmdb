package com.pablo.test_tmdb.apis;

import com.google.gson.annotations.SerializedName;
import com.pablo.test_tmdb.models.Video;

import java.util.List;

public class MovieVideosResponse {
    @SerializedName("id")
    private String movieId;

    @SerializedName("results")
    private List<Video> videos;

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }
}
