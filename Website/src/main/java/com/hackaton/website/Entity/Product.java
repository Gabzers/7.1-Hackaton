package com.hackaton.website.Entity;

public class Product {
    private String name;
    private int points;
    private int cost; // Change cost to integer

    public Product(String name, int points, int cost) {
        this.name = name;
        this.points = points;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getFormattedCost() {
        return String.valueOf(cost); // Return cost as an integer string
    }
    
}