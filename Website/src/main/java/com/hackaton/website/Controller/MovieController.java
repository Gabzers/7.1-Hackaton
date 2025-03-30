package com.hackaton.website.Controller;

import com.hackaton.website.Service.MovieService;
import com.hackaton.website.Entity.Movie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<Movie> listMovies() {
        List<Movie> movies = movieService.readMoviesFromCSV();
        logger.info("Returning {} movies from the service", movies.size());
        return movies;
    }
}
