package com.pablo.test_tmdb.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pablo.test_tmdb.Helpers.GlideApp;
import com.pablo.test_tmdb.R;
import com.pablo.test_tmdb.models.Movie;

import java.util.ArrayList;
import java.util.List;

public class MoviesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_HOLDER_MOVIE = 1;
    public static final int VIEW_HOLDER_LOADING = 2;
    public static final int VIEW_HOLDER_NO_MOVIES = 4;

    private List<Movie> movies;
    private boolean isShowingLoading = false;
    private MovieClickedListener movieClickedListener;
    private boolean isShowingNoMovies = false;

    public MoviesListAdapter(MovieClickedListener movieClickedListener) {
        this(movieClickedListener, new ArrayList<>());
    }

    public MoviesListAdapter(MovieClickedListener movieClickedListener, List<Movie> movies) {
        this.movieClickedListener = movieClickedListener;

        setMovies(movies);
    }

    public void setMovies(List<Movie> movies) {
        this.movies = new ArrayList<>();

        if (movies.size() == 0) {
            this.movies.add(new Movie());

            isShowingNoMovies = true;
        }
        else {
            this.movies.addAll(movies);

            isShowingNoMovies = false;
        }

        isShowingLoading = false;

        notifyDataSetChanged();
    }

    public void addMovies(List<Movie> movies) {
        if (isShowingLoading) {
            isShowingLoading = false;

            this.movies.remove(this.movies.size()-1);
        }

        this.movies.addAll(movies);

        notifyDataSetChanged();
    }

    public void showLoading(boolean showLoading) {
        if (showLoading) {
            if (isShowingNoMovies) {
                isShowingNoMovies = false;
            }
            else if (!this.isShowingLoading) {
                movies.add(new Movie());
            }

        }
        else if (!showLoading && this.isShowingLoading) {
            movies.remove(movies.size()-1);
        }

        this.isShowingLoading = showLoading;

        notifyDataSetChanged();
    }

    public boolean isLoadingMovies() {
        return isShowingLoading;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_HOLDER_MOVIE:
                return new MovieViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_movie, parent, false));

            case VIEW_HOLDER_LOADING:
                return new LoadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_loading, parent, false));

            case VIEW_HOLDER_NO_MOVIES:
                return new NoMoviesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_no_movies, parent, false));

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MovieViewHolder) {
            bindViewHolderMovie((MovieViewHolder) holder, position);
        }
        else if (holder instanceof LoadingViewHolder) {
            bindViewHolderLoading((LoadingViewHolder) holder);
        }
        else if (holder instanceof NoMoviesViewHolder) {
            bindNoMoviesViewHolder((NoMoviesViewHolder) holder);
        }
    }

    private void bindViewHolderMovie(MovieViewHolder holder, int position) {
        Movie movie = (Movie) movies.get(position);

        Context context = holder.poster.getContext();

        holder.movieContainer.setTag(movie.getId());
        GlideApp.with(context).load(String.format(context.getString(R.string.poster_path_format), movie.getPosterPath())).placeholder(R.drawable.movie_placeholder).error(R.drawable.movie_placeholder).into(holder.poster);
        holder.title.setText(movie.getTitle());
        holder.overview.setText(movie.getOverview());
        holder.releaseDate.setText(String.format(context.getString(R.string.movie_released), movie.getReleaseDate()));
        holder.rating.setText(String.valueOf(movie.getVoteAverage()));
    }

    private void bindViewHolderLoading(LoadingViewHolder holder) {

    }

    private void bindNoMoviesViewHolder(NoMoviesViewHolder holder) {

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowingLoading && position == movies.size() - 1) {
            return VIEW_HOLDER_LOADING;
        }
        else {
            if (isShowingNoMovies) {
                return VIEW_HOLDER_NO_MOVIES;
            }
            else {
                return VIEW_HOLDER_MOVIE;
            }
        }
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView movieContainer;
        ImageView poster;
        TextView title, overview, releaseDate, rating;

        public MovieViewHolder(View view) {
            super(view);

            movieContainer = view.findViewById(R.id.movieContainer);
            poster = view.findViewById(R.id.poster);
            title = view.findViewById(R.id.title);
            overview = view.findViewById(R.id.overview);
            releaseDate = view.findViewById(R.id.releaseDate);
            rating = view.findViewById(R.id.voteAverage);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("---x", "clicked");

            if (movieClickedListener != null) {
                movieClickedListener.onMovieClicked(getMovieById((int) view.getTag()));
            }
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class NoMoviesViewHolder extends RecyclerView.ViewHolder {
        public NoMoviesViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface MovieClickedListener {
        void onMovieClicked(Movie movie);
    }

    private Movie getMovieById(int movieId) {
        for (Movie movie : movies) {
            if (movie.getId() == movieId) {
                return movie;
            }
        }

        return null;
    }
}
