package com.example.myapplication;

import java.io.Serializable;

// Create this file in its own Product.java file
public class Product implements Serializable {
    private String id;
    private String name;
    private double price;
    private String imageUrl; // URL to the product image
    private String status; // "Available", "Pending", "Sold"
    private String sellerId; // To know who is selling it
    private String condition;
    private String category;
    private String description;
    private String location;

    // Constructor
    public Product(String id, String name, double price, String imageUrl, String status, String sellerId, String condition, String category, String description, String location) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.status = status;
        this.sellerId = sellerId;
        this.condition = condition;
        this.category = category;
        this.description = description;
        this.location = location;
    }

    // Getters (and maybe Setters if you need to change data later)
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getCategory() { return category; }

    public String getCondition() { return condition; }

    public String getDescription() { return description; }

    public String getLocation() { return location; }

    public void setStatus(String status) {this.status = status;}
    public void setName(String name) {this.name = name;}
    public void setPrice(double price) {this.price = price;}
    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}
    public void setSellerId(String sellerId) {this.sellerId = sellerId;}
    public void setCondition(String condition) { this.condition = condition; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) {this.description = description;}
    public void setLocation(String location) {this.location = location;}
}