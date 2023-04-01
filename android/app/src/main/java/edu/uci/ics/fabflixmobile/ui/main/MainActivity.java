package edu.uci.ics.fabflixmobile.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivityMainBinding;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import edu.uci.ics.fabflixmobile.ui.singlemovie.SingleMovieActivity;

public class MainActivity extends AppCompatActivity {
    private EditText movie_title;

    private final String host = "huaiwuli.com";
    private final String port = "8443";
    private final String domain = "cs122b-fall-team-58";
//    private final String host = "10.0.2.2";
//    private final String port = "8443";
//    private final String domain = "fabflix";
    private final String api = "/api/full-text-search";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain + api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);

        // Binding button
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());
        final Button movieListButton = binding.mainMovieList;

        movie_title = binding.searchMovieTitle;

        movieListButton.setOnClickListener(view -> {
            Intent MovieListPage = new Intent(MainActivity.this, MovieListActivity.class);
            // activate the list page.
            startActivity(MovieListPage);
        });

        final Button searchButton = binding.search;
        searchButton.setOnClickListener(view -> searchMovie());
    }
    @SuppressLint("SetTextI18n")
    public void searchMovie() {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest searchRequest = new StringRequest(
                Request.Method.POST,
                baseURL,
                response -> {
                    Log.i("main.search", response);
                    Intent movieListPage = new Intent(MainActivity.this, MovieListActivity.class);
                    movieListPage.putExtra("movies", response);
                    startActivity(movieListPage);
                },
                error -> {
                    // error
                    Log.d("main.error", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                params.put("query", movie_title.getText().toString());
                return params;
            }
        };
        // important: queue.add is where the login request is actually sent
        queue.add(searchRequest);
    }
}