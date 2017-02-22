package com.example.karan.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

import utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, Serializable {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView displayUrlTextView;
    public ArrayList<MovieDetails> movies;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private ProgressBar progressBar;
    private Toast toast;
    private MovieDetails movieDetails;
    private GridLayoutManager gridLayoutManager;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.loading_indicator);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(this, 2);
        }
        else{
            gridLayoutManager = new GridLayoutManager(this, 3);
        }
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        movieAdapter = new MovieAdapter(this);
        recyclerView.setAdapter(movieAdapter);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = gridLayoutManager.getChildCount();
                    Log.i("##########", Integer.valueOf(visibleItemCount).toString());
                    totalItemCount = gridLayoutManager.getItemCount();
                    pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            Log.i("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
                        }
                    }
                }
            }
        });

        displayUrlTextView = (TextView) findViewById(R.id.error_message);
        if(savedInstanceState == null) {
            final URL movieDbUrl = NetworkUtils.buildUrl(R.id.action_sort_top_rated);
            new MovieDbQueryTask().execute(movieDbUrl);
        }



    }

    @Override
    public void onClick(MovieDetails movieDetails) {
        Context context = this;
        Class destinationActivity = Details.class;
        Intent intent = new Intent(MainActivity.this, destinationActivity);
        intent.putExtra("TITLE", movieDetails.getTitle());
        intent.putExtra("ADULT", movieDetails.isAdult());
        intent.putExtra("POSTER_PATH", movieDetails.getPosterPath());
        intent.putExtra("POPULARITY", movieDetails.getPopularity());
        intent.putExtra("AVERAGE_VOTE", movieDetails.getAverageVote());
        intent.putExtra("ID", movieDetails.getId());
        intent.putExtra("OVERVIEW", movieDetails.getOverview());
        intent.putExtra("RELEASE_DATE",movieDetails.getReleaseDate());
        intent.putExtra("VOTE_COUNT", movieDetails.getVoteCount());
        startActivity(intent);
        /*if(toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, title, Toast.LENGTH_LONG);
        toast.show();*/
    }

    public class MovieDbQueryTask extends AsyncTask<URL, Void, ArrayList<MovieDetails>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected ArrayList<MovieDetails> doInBackground(URL... params) {
            URL searchUrl = params[0];
            String movieDbSearchResults = null;
            movies = new ArrayList<MovieDetails>();
            try {
                movieDbSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                Log.i(TAG, movieDbSearchResults);
                JSONObject obj = null;

                    obj = new JSONObject(movieDbSearchResults);
                    if(obj != null) {
                        JSONArray result = obj.getJSONArray("results");
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject js = result.getJSONObject(i);
                            String title = js.getString("title");
                            boolean adult = js.getBoolean("adult");
                            String releaseDate = js.getString("release_date");
                            int id = js.getInt("id");
                            String posterPath = js.getString("poster_path");
                            String overview = js.getString("overview");
                            double popularity = js.getDouble("popularity");
                            int voteCount = js.getInt("vote_count");
                            double averageVote = js.getDouble("vote_average");

                            movieDetails = new MovieDetails(posterPath, adult, overview, releaseDate, id, title, popularity, voteCount, averageVote);
                            movies.add(movieDetails);


                        }
                    }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return movies;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieDetails> movies) {
            progressBar.setVisibility(View.INVISIBLE);
            if(movies != null) {
                movieAdapter.setMovieDetails(movies);
            }
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("MovieList", movies);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        movies = (ArrayList<MovieDetails>) savedInstanceState.getSerializable("MovieList");
        movieAdapter.setMovieDetails(movies);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_sort_popular){
            final URL movieDbUrl = NetworkUtils.buildUrl(id);
            new MovieDbQueryTask().execute(movieDbUrl);
        }
        else if(id == R.id.action_sort_top_rated){
            final URL movieDbUrl = NetworkUtils.buildUrl(id);
            new MovieDbQueryTask().execute(movieDbUrl);
        }
        else if(id == R.id.action_refresh){

        }
        return super.onOptionsItemSelected(item);
    }
}
