package com.hackaton.website.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "shops")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @ElementCollection
    @CollectionTable(name = "shop_products", joinColumns = @JoinColumn(name = "shop_id"))
    private List<Product> products;

    public Shop() {
        this.creationDate = LocalDateTime.now();
    }

    public Shop(List<Product> products) {
        this.creationDate = LocalDateTime.now();
        this.products = products;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
