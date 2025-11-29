package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Singleton class to hold a shared list of products and user data locally.
 * This acts as our in-memory "database".
 */
public class ProductRepository {

    // 1. The single, static instance of this class
    private static ProductRepository instance;

    private final List<Product> allProducts;
    private User currentUser; // You can also store the current user here

    // 2. Private constructor to prevent anyone else from creating an instance
    private ProductRepository() {
        allProducts = new ArrayList<>();
        // Load initial sample data when the repository is first created
        loadSampleData();
    }

    // 3. The public, static method to get the single instance
    public static synchronized ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }

    // --- Public methods to interact with the data ---

    public List<Product> getAllProducts() {
        return allProducts;
    }

    public void addProduct(Product product) {
        allProducts.add(product);
        // In a real app, you would save this to a file or database
    }

    public List<Product> getFavoriteProducts() {
        if (currentUser == null) {
            return new ArrayList<>(); // Return empty list if no user
        }
        List<String> favoriteIds = currentUser.getFavoriteProductIds();
        return allProducts.stream()
                .filter(product -> favoriteIds.contains(product.getId()))
                .collect(Collectors.toList());
    }

    // --- User Management ---

    public User getCurrentUser() {
        return currentUser;
    }

    // --- Sample Data Loader ---

    private void loadSampleData() {
        // Create a sample user
        currentUser = new User("user123", "Alex", "url_to_profile_pic");

        // Create sample products
        allProducts.add(new Product("prod1", "Used Textbook", 25.00, "url", "Available", "sellerA", "Used", "School Stuff", "Good", "Kelowna"));
        allProducts.add(new Product("prod2", "Desk Lamp", 15.00, "url", "Sold", "sellerB", "Used", "School Stuff", "Good", "Kelowna"));
        allProducts.add(new Product("prod3", "Mini Fridge", 50.00, "url", "Available", "sellerC", "Used", "School Stuff", "Good", "Kelowna"));
        allProducts.add(new Product("prod4", "Gaming Chair", 120.00, "url", "Pending", "sellerA", "Used", "School Stuff", "Good", "Kelowna"));

        // Make the user like some of the items
        currentUser.addFavorite("prod2"); // User likes the sold desk lamp
        currentUser.addFavorite("prod3"); // User likes the available mini fridge
        currentUser.addFavorite("prod4"); // User likes the gaming chair
    }
}

