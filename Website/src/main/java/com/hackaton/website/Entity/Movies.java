package com.hackaton.website.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    public Movies() {
        this.isRedeemed = false; // Ensure initialized to false
    }

    public Movies(String title, String genres, String averageRating, String releaseYear) {
        this.title = title;
        this.genres = genres;
        this.averageRating = averageRating;
        this.releaseYear = releaseYear;
        this.isRedeemed = false; // Ensure initialized to false
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(String averageRating) {
        this.averageRating = averageRating;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Boolean getIsRedeemed() {
        return isRedeemed;
    }

    public void setIsRedeemed(Boolean isRedeemed) {
        this.isRedeemed = isRedeemed;
    }

    @Override
    public String toString() {
        return String.format("Movie ID: %s, Title: %s, Genres: %s, Rating: %s, Release Year: %s, Redeemed: %s",
                id, title, genres, averageRating, releaseYear, isRedeemed != null ? isRedeemed : "null");
    }
}
