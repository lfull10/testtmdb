package com.pablo.test_tmdb.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pablo.test_tmdb.models.Movie;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface MovieDao {
    @Query("select * from movies")
    Single<List<Movie>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Movie> movies);

    @Query("select count(id) as count from movies")
    Single<Integer> getCount();

    @Query("delete from moviespopular")
    void deletePopular();
}
