package com.hackaton.website.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a shop associated with a user.
 * 
 * @author Gabriel Proença
 */
@Entity
@Table(name = "shops")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "shop_id") // Foreign key in the products table
    private List<Product> products;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "shop_id") // Foreign key in the movies table
    private List<Movies> movies;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Default constructor.
     * Initializes the creation date to the current time.
     */
    public Shop() {
        this.creationDate = LocalDateTime.now();
    }

    /**
     * Constructor for initialization.
     *
     * @param products the list of products in the shop
     */
    public Shop(List<Product> products) {
        this.creationDate = LocalDateTime.now();
        this.products = products;
    }

    /**
     * Gets the ID of the shop.
     *
     * @return the shop ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the creation date of the shop.
     *
     * @return the creation date
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Gets the list of products in the shop.
     *
     * @return the list of products
     */
    public List<Product> getProducts() {
        return products;
    }

    /**
     * Sets the list of products in the shop.
     *
     * @param products the list of products to set
     */
    public void setProducts(List<Product> products) {
        this.products = products;
    }

    /**
     * Gets the list of movies in the shop.
     *
     * @return the list of movies
     */
    public List<Movies> getMovies() {
        return movies;
    }

    /**
     * Sets the list of movies in the shop.
     *
     * @param movies the list of movies to set
     */
    public void setMovies(List<Movies> movies) {
        this.movies = movies;
    }

    /**
     * Gets the user associated with the shop.
     *
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user associated with the shop.
     *
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }
}
