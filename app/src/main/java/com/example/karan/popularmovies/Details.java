package com.example.karan.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import utilities.NetworkUtils;


public class Details extends AppCompatActivity  {


    private TextView displayTitle;
    private TextView displayReleaseDate;
    private ImageView displayPoster;
    private TextView displayPopularity;
    private TextView displayAverageVote;
    private static final String basePosterUrl = "http://image.tmdb.org/t/p/w185/";
    private static final String baseTrailerUrl = "http://api.themoviedb.org/3/movie/";
    private String posterUrl;
    private ArrayList<Trailer> trailersList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        displayTitle = (TextView) findViewById(R.id.details_movie_title);
        displayPoster = (ImageView) findViewById(R.id.details_poster);
        displayReleaseDate = (TextView) findViewById(R.id.display_release_date);
        displayPopularity = (TextView) findViewById(R.id.display_popularity);
        displayAverageVote = (TextView) findViewById(R.id.display_average_vote);
        Intent intent = getIntent();
        displayTitle.setText(intent.getStringExtra("TITLE"));
        displayReleaseDate.setText(intent.getStringExtra("RELEASE_DATE"));
        displayPopularity.setText(Double.toString(intent.getDoubleExtra("POPULARITY",0.0)));
        displayAverageVote.setText(Double.toString(intent.getDoubleExtra("AVERAGE_VOTE",0.0)));
        posterUrl = basePosterUrl + intent.getStringExtra("POSTER_PATH");
        Glide.with(displayPoster.getContext()).load(posterUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(displayPoster);

        int id = intent.getIntExtra("ID",0);
        String basePlusSearch = baseTrailerUrl + id + "/videos?api_key=2e0f941942c69eb418edab945a63a57d";
        Uri uri = Uri.parse(basePlusSearch);
        URL searchUrl = null;
        try {
            searchUrl = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new MovieTrailerQueryTask().execute(searchUrl);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        displayPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = null;
                for(int i=0; i < trailersList.size(); i++){
                    if(trailersList.get(i).getType().equals("Trailer")){
                        key = trailersList.get(i).getKey();
                        Log.i("*****************",key);
                        break;
                    }
                }
                if(key != null) {
                    openTrailer(Uri.parse("https://www.youtube.com/watch?v=" + key));
                }
            }
        });


        ExpandableTextView expTv1 = (ExpandableTextView) findViewById(R.id.details_overview)
                .findViewById(R.id.details_overview);

        expTv1.setText(intent.getStringExtra("OVERVIEW"));

    }

    public class MovieTrailerQueryTask extends AsyncTask<URL, Void, ArrayList<Trailer>>{
        @Override
        protected ArrayList<Trailer> doInBackground(URL... params) {
            URL searchUrl = params[0];
            String trailerSearchResults = null;
            trailersList = new ArrayList<Trailer>();
            try {
                trailerSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                JSONObject obj = null;
                obj = new JSONObject(trailerSearchResults);
                if(obj != null){
                    JSONArray result = obj.getJSONArray("results");
                    for(int i=0; i<result.length(); i++){
                        JSONObject js = result.getJSONObject(i);
                        String type = js.getString("type");
                        String key = js.getString("key");

                        Trailer trailer = new Trailer(key, type);
                        trailersList.add(trailer);
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return trailersList;
        }

        @Override
        protected void onPostExecute(ArrayList<Trailer> trailers) {
            super.onPostExecute(trailers);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }




    public void openTrailer(Uri uri){
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        if (i.resolveActivity(getPackageManager()) != null) {
            startActivity(i);
        }
    }
}
