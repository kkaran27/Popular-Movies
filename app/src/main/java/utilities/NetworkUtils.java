package utilities;

import android.net.Uri;

import com.example.karan.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Karan on 2/8/2017.
 */

//https://api.themoviedb.org/3/movie/popular?api_key=c678accbb321fda3e70ff8892c240dbb&language=en-US&page=1

public class NetworkUtils {
    final static String MOVIEDB_BASE_URL_POPULAR =
            "https://api.themoviedb.org/3/movie/popular";

    final static String MOVIEDB_BASE_URL_TOP_RATED =
            "https://api.themoviedb.org/3/movie/top_rated";

    final static String API_KEY = "api_key";
    final static String API_KEY_PARAM = "c678accbb321fda3e70ff8892c240dbb";

    /*
     * The sort field. One of stars, forks, or updated.
     * Default: results are sorted by best match if no field is specified.
     */



    /**
     * Builds the URL used to query Github.
     *
     *
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrl(int id) {
        URL url = null;
        if(id == R.id.action_sort_popular) {
            Uri builtUri = Uri.parse(MOVIEDB_BASE_URL_POPULAR).buildUpon()
                    .appendQueryParameter(API_KEY, API_KEY_PARAM)
                    .build();
            try {
                url = new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else if (id == R.id.action_sort_top_rated){
            Uri builtUri = Uri.parse(MOVIEDB_BASE_URL_TOP_RATED).buildUpon()
                    .appendQueryParameter(API_KEY, API_KEY_PARAM)
                    .build();
            try {
                url = new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
