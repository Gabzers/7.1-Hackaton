package com.hackaton.website.Entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class Product {

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
        return String.format("Product Name: %s, Category: %s, Cost: %s points, Redeemed: %s",
                productName, category, cost, isRedeemed != null ? isRedeemed : "null");
    }
}
