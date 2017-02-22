package com.example.karan.popularmovies;

/**
 * Created by Karan on 2/18/2017.
 */

public class Trailer {
    public Trailer(String key, String type) {
        this.key = key;
        this.type = type;
    }

    private String key;
    private String type;

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
