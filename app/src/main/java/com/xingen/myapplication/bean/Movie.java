package com.xingen.myapplication.bean;

import com.google.gson.Gson;

/**
 * Author by {xinGen}
 * Date on 2018/8/3 11:37
 */
public class Movie {
    public String year;
    private String title;
    private String id;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
