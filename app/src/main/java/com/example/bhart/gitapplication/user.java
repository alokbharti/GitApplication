package com.example.bhart.gitapplication;

/**
 * Created by bhart on 4/28/2018.
 */

public class user {
    public String name;
    public String score;

    public user(String name, String score) {
        this.name=name;
        this.score=score;
    }

    public String getName() {

        return name;
    }

    public String getScore() {
        return score;
    }
}
