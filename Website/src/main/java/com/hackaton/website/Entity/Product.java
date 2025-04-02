package com.hackaton.website.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity representing a product in the shop.
 * 
 * @author Gabriel Proença
 */
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

    /**
     * Default constructor.
     * Ensures the product is not redeemed by default.
     */
    public Product() {
        this.isRedeemed = false;
    }

    /**
     * Constructor for initialization.
     *
     * @param productName the name of the product
     * @param category    the category of the product
     * @param cost        the cost of the product in points
     */
    public Product(String productName, String category, String cost) {
        this.productName = productName;
        this.category = category;
        this.cost = cost;
        this.isRedeemed = false;
    }

    /**
     * Gets the unique identifier of the product.
     *
     * @return the product ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the name of the product.
     *
     * @return the product name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets the name of the product.
     *
     * @param productName the product name
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Gets the category of the product.
     *
     * @return the product category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category of the product.
     *
     * @param category the product category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the cost of the product in points.
     *
     * @return the product cost
     */
    public String getCost() {
        return cost;
    }

    /**
     * Sets the cost of the product in points.
     *
     * @param cost the product cost
     */
    public void setCost(String cost) {
        this.cost = cost;
    }

    /**
     * Gets the redemption status of the product.
     *
     * @return true if the product is redeemed, false otherwise
     */
    public Boolean getIsRedeemed() {
        return isRedeemed;
    }

    /**
     * Sets the redemption status of the product.
     *
     * @param isRedeemed true if the product is redeemed, false otherwise
     */
    public void setIsRedeemed(Boolean isRedeemed) {
        this.isRedeemed = isRedeemed;
    }

    /**
     * Returns a string representation of the product.
     *
     * @return a formatted string containing product details
     */
    @Override
    public String toString() {
        return String.format("Product ID: %s, Name: %s, Category: %s, Cost: %s points, Redeemed: %s",
                id, productName, category, cost, isRedeemed != null ? isRedeemed : "null");
    }
}
