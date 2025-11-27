package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

// Create this file in its own User.java file
public class User {
    private String userId;
    private String name;
    private String profileImageUrl;
    private List<String> favoriteProductIds;
    // Add other details like rating, etc.

    // Constructor
    public User(String userId, String name, String profileImageUrl) {
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.favoriteProductIds = new ArrayList<>();
    }

    // Getters
    public String getUserId() {
        return userId;
    }
    public String getName() {
        return name;
    }
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public List<String> getFavoriteProductIds() { return favoriteProductIds; }

    public void addFavorite(String productId) {
        if(!favoriteProductIds.contains(productId)) {
            favoriteProductIds.add(productId);
        }
    }

    public void removeFavorite(String productId) {
        favoriteProductIds.remove(productId);
    }

    public boolean isFavorite(String productId) {
        return favoriteProductIds.contains(productId);
    }
}