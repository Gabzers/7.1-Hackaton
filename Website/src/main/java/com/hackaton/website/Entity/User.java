package com.hackaton.website.Entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true) // Permitir que o nome seja opcional
    private String name;

    @Column(nullable = true, unique = true) // Permitir que o email seja opcional
    private String email;

    @Column(nullable = true) // Permitir que a senha seja opcional
    private String password;

    @Column(nullable = true) // Permitir que os pontos sejam opcionais
    private Integer points; // Alterado para Integer para suportar valores nulos

    @Column(nullable = true) // Permitir que a barra de progresso seja opcional
    private Integer progressBar;

    @Column(nullable = true) // Permitir que o exp seja opcional
    private Integer exp; // Novo atributo para armazenar pontos de experiência

    @Column(nullable = true)
    private LocalDateTime lastDailyLogin; // Track the last daily login time

    @ElementCollection
    @CollectionTable(name = "user_movie_genres", joinColumns = @JoinColumn(name = "user_id"))
    @AttributeOverrides({
        @AttributeOverride(name = "genre", column = @Column(name = "genre", nullable = false)),
        @AttributeOverride(name = "score", column = @Column(name = "score", nullable = false))
    })
    private List<MovieGenre> movieGenres;

    @ElementCollection
    private Set<String> completedMissions;
    @ElementCollection
    private Set<String> redeemedOffers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Shop> shops;

    @ElementCollection
    @CollectionTable(name = "user_missions", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "mission", nullable = false)
    private List<String> missions; // New attribute for storing missions

    // Default constructor
    public User() {
        this.missions = List.of("rate a movie"); // Initialize with the "rate a movie" mission
        this.completedMissions = new HashSet<>(Set.of("RateAMovie", "Watch5Ads")); // Initialize as unavailable
    }
    
    // Constructor for initialization
    public User(String name, String email, String password, List<MovieGenre> movieGenres, Integer points, String redeemedOffers) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.movieGenres = movieGenres;
        this.points = points;
        this.missions = List.of("rate a movie"); // Initialize with the "rate a movie" mission
        this.redeemedOffers = Set.of(redeemedOffers);

    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public Integer getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(Integer progressBar) {
        this.progressBar = progressBar;
    }

    public Integer getExp() {
        return exp;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
    }

    public LocalDateTime getLastDailyLogin() {
        return lastDailyLogin;
    }

    public void setLastDailyLogin(LocalDateTime lastDailyLogin) {
        this.lastDailyLogin = lastDailyLogin;
    }

    public List<MovieGenre> getMovieGenres() {
        return movieGenres != null ? movieGenres : List.of();
    }
    public void setMovieGenres(List<MovieGenre> movieGenres) {
        System.out.println("Setting movie genres...");
        movieGenres.forEach(g -> System.out.println("Genre: " + g.getGenre() + ", Score: " + g.getScore()));
        this.movieGenres = movieGenres;
    }

    public Set<String> getCompletedMissions() {
        return completedMissions != null ? completedMissions : Set.of();
    }

    public void setCompletedMissions(Set<String> completedMissions) {
        this.completedMissions = completedMissions;
    }

    public List<Shop> getShops() {
        return shops;
    }

    public void setShops(List<Shop> shops) {
        this.shops = shops;
    }

    public List<String> getMissions() {
        return missions != null ? missions : List.of();
    }

    public void setMissions(List<String> missions) {
        this.missions = missions;
    }

    public Set<String> getRedeemedOffers() {
        return redeemedOffers != null ? redeemedOffers : Set.of();
    }

    public void setRedeemedOffers(Set<String> redeemedOffers) {
        this.redeemedOffers = redeemedOffers;
    }

    @Embeddable
    public static class MovieGenre {
        private String genre;
        private String score;

        public MovieGenre() {}

        public MovieGenre(String genre, String score) {
            this.genre = genre;
            this.score = score;
        }

        public String getGenre() { return genre; }
        public void setGenre(String genre) { this.genre = genre; }

        public String getScore() { return score; }
        public void setScore(String score) { this.score = score; }
    }
}