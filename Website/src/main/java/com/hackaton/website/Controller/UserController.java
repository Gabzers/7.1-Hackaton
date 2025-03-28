package com.hackaton.website.Controller;

import com.hackaton.website.Entity.User;

import java.util.ArrayList;
import java.util.List;

public class UserController {

    // Method to add default movie genres to a user with scores initialized to 0
    public void addDefaultMovieGenres(User user) {
        // List of default genres
        String[] defaultGenres = {
            "Action", "Adult", "Adventure", "Animation", "Biography", "Comedy", "Crime",
            "Documentary", "Drama", "Family", "Fantasy", "Film Noir", "Game Show", "History",
            "Horror", "Musical", "Music", "Mystery", "News", "Reality-TV", "Romance", "Sci-Fi",
            "Short", "Sport", "Talk-Show", "Thriller", "War", "Western"
        };

        // Initialize movieGenres if null
        if (user.getMovieGenres() == null) {
            user.setMovieGenres(new ArrayList<>());
        }

        // Add each genre with a score of 0
        List<String[]> movieGenres = user.getMovieGenres();
        for (String genre : defaultGenres) {
            movieGenres.add(new String[]{genre, "0"}); // Add genre with score 0
        }
    }
}