package com.xingen.myapplication.bean;

import com.google.gson.Gson;

import java.util.List;

/**
 * Author by {xinGen}
 * Date on 2018/8/3 11:38
 */
public class MovieList<T> {
    public List<T> getSubjects() {
        return subjects;
    }

    private List<T> subjects;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
