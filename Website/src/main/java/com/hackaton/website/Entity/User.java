package com.hackaton.website.Entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ElementCollection
    @Column(name = "movie_genre")
    private List<String[]> movieGenres; // Alterado para armazenar pares [gênero, pontuação]

    @Column(nullable = false)
    private int points = 0; // Novo atributo para armazenar os pontos do utilizador

    // Default constructor
    public User() {}

    // Constructor for initialization
    public User(String name, String email, String password, List<String[]> movieGenres) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.movieGenres = movieGenres;
        this.points = 0; // Inicializa os pontos com 0
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

    public List<String[]> getMovieGenres() { return movieGenres; } // Getter atualizado
    public void setMovieGenres(List<String[]> movieGenres) { this.movieGenres = movieGenres; } // Setter atualizado

    public int getPoints() { return points; } // Getter para pontos
    public void setPoints(int points) { this.points = points; } // Setter para pontos
}