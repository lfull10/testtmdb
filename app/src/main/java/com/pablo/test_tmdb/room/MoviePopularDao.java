package com.pablo.test_tmdb.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pablo.test_tmdb.models.Movie;
import com.pablo.test_tmdb.models.MoviePopular;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface MoviePopularDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MoviePopular> movies);

    @Query("select movies.* from movies inner join moviespopular on movies.id=moviespopular.movieId order by position limit :offset, :limit")
    Single<List<Movie>> loadMovies(int offset, int limit);
}
