package com.example.karan.popularmovies;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Karan on 2/6/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private ArrayList<MovieDetails> moviePosters;
    private static final String basePosterUrl = "http://image.tmdb.org/t/p/w185/";
    public ImageView moviePoster;
    private final MovieAdapterOnClickHandler posterClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(MovieDetails movieDetails);
    }


    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        posterClickHandler = clickHandler;
    }


    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView moviePoster;
        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieDetails movieDetail = moviePosters.get(adapterPosition);
            posterClickHandler.onClick(movieDetail);
        }
    }


    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movies_list;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        String url = basePosterUrl + moviePosters.get(position).getPosterPath();
        Glide.with(holder.itemView.getContext()).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.moviePoster);
    }

    @Override
    public int getItemCount() {
        if(null == moviePosters){
            return 0;
        }
        return moviePosters.size();
    }

    public void setMovieDetails(ArrayList<MovieDetails> a){
        moviePosters = a;
        notifyDataSetChanged();
    }
}
