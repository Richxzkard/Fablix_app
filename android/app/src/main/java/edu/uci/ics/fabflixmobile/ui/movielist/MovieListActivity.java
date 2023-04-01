package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;

import org.json.*;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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
import edu.uci.ics.fabflixmobile.databinding.ActivityMovielistBinding;
import edu.uci.ics.fabflixmobile.databinding.MovielistRowBinding;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.main.MainActivity;
import edu.uci.ics.fabflixmobile.ui.singlemovie.SingleMovieActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieListActivity extends AppCompatActivity {
    private final String host = "huaiwuli.com";
    private final String port = "8443";
    private final String domain = "cs122b-fall-team-58";

    //    private final String host = "10.0.2.2";
//    private final String port = "8443";
//    private final String domain = "fabflix";
    private final String api = "/api/movies";
    private final String prevParams = "?pageChange=1&NChange=&rating=&title=&sort=&type=";
    private final String nextParams = "?pageChange=2&NChange=&rating=&title=&sort=&type=";

    private final String baseURL = "https://" + host + ":" + port + "/" + domain + api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);

        // Binding button
        ActivityMovielistBinding binding = ActivityMovielistBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());
        final Button previousButton = binding.previousBtn;
        final Button nextButton = binding.nextBtn;
        final Button mainButton = binding.mainPage;
        previousButton.setOnClickListener(view -> previousMovies());
        nextButton.setOnClickListener(view -> nextMovies());
        mainButton.setOnClickListener(view -> toMainPage());

        Intent currentIntent = getIntent();
        String moviesArray = currentIntent.getStringExtra("movies");
        if (moviesArray == null || moviesArray.isEmpty()) {
            final RequestQueue queue = NetworkManager.sharedManager(this).queue;
            queue.add(createMovieListRequest(baseURL));
        } else {
            renderMovieList(moviesArray);
        }
    }

    @SuppressLint("SetTextI18n")
    private void renderMovieList(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            final ArrayList<Movie> movies = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject m = jsonArray.getJSONObject(i);
                movies.add(new Movie(m.getString("movie_id"), m.getString("movie_title"), (short) Integer.parseInt(m.getString("movie_year")), m.getString("movie_director"), m.getString("movie_genre"), m.getString("movie_star_name")));
            }
            MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
            ListView listView = findViewById(R.id.list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Movie movie = movies.get(position);
                Intent SingleMoviePage = new Intent(MovieListActivity.this, SingleMovieActivity.class);
                SingleMoviePage.putExtra("movie_id", movie.getID());
                startActivity(SingleMoviePage);
            });
        } catch (Exception e) {
            Log.e("login.error", e.toString());
        }
    }

    @SuppressLint("SetTextI18n")
    private StringRequest createMovieListRequest(String url) {
        return new StringRequest(Request.Method.GET, url, response -> {
            renderMovieList(response);
        }, error -> {
            // error
            Log.d("movies", error.toString());
        }) {
        };
    }

    @SuppressLint("SetTextI18n")
    public void previousMovies() {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        queue.add(createMovieListRequest(baseURL + prevParams));
    }

    @SuppressLint("SetTextI18n")
    public void nextMovies() {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        queue.add(createMovieListRequest(baseURL + nextParams));
    }

    @SuppressLint("SetTextI18n")
    public void toMainPage() {
        Intent mainPage = new Intent(MovieListActivity.this, MainActivity.class);
        // activate the list page.
        startActivity(mainPage);
    }
}