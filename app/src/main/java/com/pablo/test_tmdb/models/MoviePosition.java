package com.pablo.test_tmdb.models;

import android.arch.persistence.room.PrimaryKey;

public class MoviePosition {
    @PrimaryKey
    private int position;

    private int movieId;

    public MoviePosition(int position, int movieId) {
        this.position = position;
        this.movieId = movieId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }
}
