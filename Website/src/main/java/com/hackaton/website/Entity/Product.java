package com.hackaton.website.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the product

    private String productName;
    private String category;
    private String cost;
    private Boolean isRedeemed = false; // Explicitly initialized to false

    public Product() {
        this.isRedeemed = false; // Ensure initialized to false
    }

    public Product(String productName, String category, String cost) {
        this.productName = productName;
        this.category = category;
        this.cost = cost;
        this.isRedeemed = false; // Ensure initialized to false
    }

    public Long getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public Boolean getIsRedeemed() {
        return isRedeemed;
    }

    public void setIsRedeemed(Boolean isRedeemed) {
        this.isRedeemed = isRedeemed;
    }

    @Override
    public String toString() {
        return String.format("Product ID: %s, Name: %s, Category: %s, Cost: %s points, Redeemed: %s",
                id, productName, category, cost, isRedeemed != null ? isRedeemed : "null");
    }
}
