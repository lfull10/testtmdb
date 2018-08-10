package com.pablo.test_tmdb.apis;

import com.google.gson.annotations.SerializedName;
import com.pablo.test_tmdb.models.Movie;

import java.util.List;

public class MoviesResponse {
    private int page;
    @SerializedName("total_results")
    private int totalResults;
    @SerializedName("total_pages")
    private int totalPages;
    @SerializedName("results")
    private List<Movie> movies;

    public int getPage() {
        return page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<Movie> getMovies() {
        return movies;
    }
}
