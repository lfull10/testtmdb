package com.pablo.test_tmdb.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "moviespopular",
        foreignKeys = @ForeignKey(entity = Movie.class, parentColumns="id", childColumns="movieId", onDelete=CASCADE))
public class MoviePopular extends MoviePosition {

    public MoviePopular(int position, int movieId) {
        super(position, movieId);
    }
}
