package com.hackaton.website.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity representing a movie in the shop.
 * 
 * @author Gabriel Proença
 */
@Entity
@Table(name = "movies")
public class Movies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the movie

    private String title;
    private String genres;
    private String averageRating;
    private String releaseYear;
    private Boolean isRedeemed = false; // Explicitly initialized to false

    /**
     * Default constructor.
     * Ensures the movie is not redeemed by default.
     */
    public Movies() {
        this.isRedeemed = false;
    }

    /**
     * Constructor for initialization.
     *
     * @param title         the title of the movie
     * @param genres        the genres of the movie
     * @param averageRating the average rating of the movie
     * @param releaseYear   the release year of the movie
     */
    public Movies(String title, String genres, String averageRating, String releaseYear) {
        this.title = title;
        this.genres = genres;
        this.averageRating = averageRating;
        this.releaseYear = releaseYear;
        this.isRedeemed = false;
    }

    /**
     * Gets the unique identifier for the movie.
     *
     * @return the movie ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the title of the movie.
     *
     * @return the movie title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the movie.
     *
     * @param title the movie title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the genres of the movie.
     *
     * @return the movie genres
     */
    public String getGenres() {
        return genres;
    }

    /**
     * Sets the genres of the movie.
     *
     * @param genres the movie genres
     */
    public void setGenres(String genres) {
        this.genres = genres;
    }

    /**
     * Gets the average rating of the movie.
     *
     * @return the average rating
     */
    public String getAverageRating() {
        return averageRating;
    }

    /**
     * Sets the average rating of the movie.
     *
     * @param averageRating the average rating
     */
    public void setAverageRating(String averageRating) {
        this.averageRating = averageRating;
    }

    /**
     * Gets the release year of the movie.
     *
     * @return the release year
     */
    public String getReleaseYear() {
        return releaseYear;
    }

    /**
     * Sets the release year of the movie.
     *
     * @param releaseYear the release year
     */
    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    /**
     * Gets the redemption status of the movie.
     *
     * @return true if redeemed, false otherwise
     */
    public Boolean getIsRedeemed() {
        return isRedeemed;
    }

    /**
     * Sets the redemption status of the movie.
     *
     * @param isRedeemed true if redeemed, false otherwise
     */
    public void setIsRedeemed(Boolean isRedeemed) {
        this.isRedeemed = isRedeemed;
    }

    /**
     * Returns a string representation of the movie.
     *
     * @return a formatted string with movie details
     */
    @Override
    public String toString() {
        return String.format("Movie ID: %s, Title: %s, Genres: %s, Rating: %s, Release Year: %s, Redeemed: %s",
                id, title, genres, averageRating, releaseYear, isRedeemed != null ? isRedeemed : "null");
    }
}
