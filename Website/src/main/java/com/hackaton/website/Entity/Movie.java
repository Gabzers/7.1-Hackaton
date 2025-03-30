package com.hackaton.website.Entity;

public class Movie {
    private Long id;
    private String title;
    private String type;
    private String genres;
    private Double averageRating;
    private Integer numVotes;
    private Integer releaseYear;
    private Double weightedRating;

    // Constructor
    public Movie(Long id, String title, String type, String genres, Double averageRating, Integer numVotes, Integer releaseYear, Double weightedRating) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.genres = genres;
        this.averageRating = averageRating;
        this.numVotes = numVotes;
        this.releaseYear = releaseYear;
        this.weightedRating = weightedRating;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getNumVotes() {
        return numVotes;
    }

    public void setNumVotes(Integer numVotes) {
        this.numVotes = numVotes;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Double getWeightedRating() {
        return weightedRating;
    }

    public void setWeightedRating(Double weightedRating) {
        this.weightedRating = weightedRating;
    }
}
