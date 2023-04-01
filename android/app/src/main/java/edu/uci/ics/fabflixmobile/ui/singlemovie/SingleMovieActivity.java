package edu.uci.ics.fabflixmobile.ui.singlemovie;

import android.annotation.SuppressLint;
import android.content.Intent;

import org.json.*;

import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.databinding.ActivityMainBinding;
import edu.uci.ics.fabflixmobile.databinding.ActivityMovielistBinding;
import edu.uci.ics.fabflixmobile.databinding.ActivitySingleMovieBinding;
import edu.uci.ics.fabflixmobile.databinding.MovielistRowBinding;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.main.MainActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingleMovieActivity extends AppCompatActivity {
    private final String host = "huaiwuli.com";
    private final String port = "8443";
    private final String domain = "cs122b-fall-team-58";

    //    private final String host = "10.0.2.2";
//    private final String port = "8443";
//    private final String domain = "fabflix";
    private final String api = "/api/single-movie";
    //    private final String params = "?id=";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain + api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("singlepage", "creating single page");
        setContentView(R.layout.activity_single_movie);

        // Binding button
        ActivitySingleMovieBinding binding = ActivitySingleMovieBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());
        final Button movieListButton = binding.movieList;

        movieListButton.setOnClickListener(view -> {
            Intent MovieListPage = new Intent(SingleMovieActivity.this, MovieListActivity.class);
            // activate the list page.
            startActivity(MovieListPage);
        });

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        Intent currentIntent = getIntent();
        String movie_id = currentIntent.getStringExtra("movie_id");
        queue.add(createSingleMovieRequest(baseURL + "?id="+movie_id));
    }

    @SuppressLint("SetTextI18n")
    private StringRequest createSingleMovieRequest(String url) {
        return new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                JSONObject m = jsonArray.getJSONObject(0);
                Movie movie = new Movie(m.getString("movie_id"), m.getString("movie_title"), (short) Integer.parseInt(m.getString("movie_year")), m.getString("movie_director"), m.getString("movie_genre"), m.getString("movie_star_name"));
                TextView view = findViewById(R.id.movie_info);
                view.setText(movie.getName() + "\nYear: " + movie.getYear() + "\nDirector" + movie.getDirector() + "\nGenres: " + movie.getGenres() + "\nStarts: " + movie.getStars());
            } catch (Exception e) {
                Log.e("login.error", e.toString());
            }
        }, error -> {
            // error
            Log.d("movies", error.toString());
        }) {
        };
    }
}