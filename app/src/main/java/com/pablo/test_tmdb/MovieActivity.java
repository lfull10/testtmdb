package com.pablo.test_tmdb;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pablo.test_tmdb.Helpers.GlideApp;
import com.pablo.test_tmdb.Helpers.MoviesHelper;
import com.pablo.test_tmdb.Helpers.NetworkHelper;
import com.pablo.test_tmdb.models.Movie;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MovieActivity extends AppCompatActivity {
    private static final String EXTRA_MOVIE = "extra_movie";

    private ImageView poster;
    private TextView title, overview, releaseDate, voteAverage;
    private WebView videoPlayer;
    private ConstraintLayout screenContainer;

    private Movie movie;

    public static void start(Movie movie, Context context) {
        Intent intent = new Intent(context, MovieActivity.class);
        intent.putExtra(EXTRA_MOVIE, movie);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        initViews();

        if (getIntent() != null) {
            movie = getIntent().getParcelableExtra(EXTRA_MOVIE);
        }

        if (movie != null) {
            showMovieInfo(movie);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        stopVideoPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void showMovieInfo(Movie movie) {
        GlideApp.with(this).load(MoviesHelper.getMovieUrl(movie.getPosterPath(), this)).placeholder(R.drawable.movie_placeholder).error(R.drawable.movie_placeholder).into(poster);
        title.setText(movie.getTitle());
        overview.setText(movie.getOverview());
        releaseDate.setText(String.format(getString(R.string.movie_released), movie.getReleaseDate()));
        voteAverage.setText(String.valueOf(movie.getVoteAverage()));

        if (NetworkHelper.isNetworkAvailable(MovieActivity.this)) {
            MoviesHelper.getMovieVideo(movie.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            videoInfo -> {
                                if (videoInfo != null) {
                                    showVideo(videoInfo.getKey());
                                }
                                else {
                                    videoPlayer.setVisibility(View.GONE);
                                }
                            },
                            error -> {
                                videoPlayer.setVisibility(View.GONE);

                                error.printStackTrace();
                            }
                    );
        }
    }

    private void showVideo(String key) {
        videoPlayer.getSettings().setJavaScriptEnabled(true);
        videoPlayer.setWebChromeClient(new WebChromeClient() {});
        videoPlayer.loadDataWithBaseURL("", getHTML(key), "text/html", "UTF-8", "");

        ConstraintLayout.LayoutParams videoPlayerParams = (ConstraintLayout.LayoutParams) videoPlayer.getLayoutParams();
        videoPlayerParams.height = (int) screenContainer.getWidth() * 2 / 3;
        videoPlayer.setLayoutParams(videoPlayerParams);
        videoPlayer.setVisibility(View.VISIBLE);
    }

    private void stopVideoPlayer() {
        try {
            videoPlayer.loadUrl("about:blank");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        poster = findViewById(R.id.poster);
        title = findViewById(R.id.title);
        overview = findViewById(R.id.overview);
        releaseDate = findViewById(R.id.releaseDate);
        voteAverage = findViewById(R.id.voteAverage);
        videoPlayer = findViewById(R.id.videoPlayer);
        screenContainer = findViewById(R.id.screen_container);
    }

    public String getHTML(String key) {
        String html = "<iframe class=\"youtube-player\" style=\"border: 0; width: 100%; height: 95%; padding:0px; margin:0px\" id=\"ytplayer\" type=\"text/html\" src=\"http://www.youtube.com/embed/"
                + key
                + "?fs=0\" frameborder=\"0\">\n"
                + "</iframe>\n";

        return html;
    }
}
