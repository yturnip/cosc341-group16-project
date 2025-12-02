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
    private final List<User> allUsers;


    // 2. Private constructor to prevent anyone else from creating an instance
    private ProductRepository() {
        allProducts = new ArrayList<>();
        allUsers = new ArrayList<>();
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
        allUsers.add(currentUser);

        // The sellers
        User sellerA = new User("seller_jane_doe", "Jane Doe", "url_to_jane_pic");
        User sellerB = new User("seller_john_smith", "John Smith", "url_to_john_pic");
        User sellerC = new User("seller_emily_jones", "Emily Jones", "url_to_emily_pic");
        allUsers.add(sellerA);
        allUsers.add(sellerB);
        allUsers.add(sellerC);

        User presetUserAlice = new User("user_alice", "Alice", null);
        User presetUserBob = new User("user_bob", "Bob", null);
        User presetUserCharlie = new User("user_charlie", "Charlie", null);
        allUsers.add(presetUserAlice);
        allUsers.add(presetUserBob);
        allUsers.add(presetUserCharlie);

        String textbookUrl = getImageUrl("Used Textbook");
        String lampUrl = getImageUrl("Desk Lamp");
        String fridgeUrl = getImageUrl("Mini Fridge");
        String chairUrl = getImageUrl("Gaming Chair");

        // Create sample products
        allProducts.add(new Product("prod1", "Used Textbook", 25.00, textbookUrl, "Available", sellerA.getUserId(), "Used", "School Supplies", "Good", "Kelowna"));
        allProducts.add(new Product("prod2", "Desk Lamp", 15.00, lampUrl, "Sold", sellerB.getUserId(), "Used", "Electronics", "Good", "Kelowna"));
        allProducts.add(new Product("prod3", "Mini Fridge", 50.00, fridgeUrl, "Available", sellerC.getUserId(), "Used", "Electronics", "Good", "Kelowna"));
        allProducts.add(new Product("prod4", "Gaming Chair", 120.00, chairUrl, "Pending", sellerA.getUserId(), "Used", "Furniture", "Good", "Kelowna"));
        allProducts.add(new Product("prod_sold_mic", "Old Microphone", 10.00, getImageUrl("Old Microphone"), "Sold", presetUserAlice.getUserId(), "Used - Fair", "Electronics", "Fair", "Kelowna"));
        allProducts.add(new Product("prod_sold_poster", "Vintage Poster", 5.00, getImageUrl("Vintage Poster"), "Sold", presetUserBob.getUserId(), "Used - Good", "Room Decor", "Good", "Kelowna"));
        allProducts.add(new Product("prod_sold_lamp", "Desk Lamp", 12.00, getImageUrl("Desk Lamp"), "Sold", presetUserCharlie.getUserId(), "Used - Good", "Electronics", "Good", "Kelowna"));

        // --- 8 NEW AVAILABLE PRODUCTS ---
        allProducts.add(new Product("prod5", "Wireless Mouse", 20.00, getImageUrl("Wireless Mouse"), "Available", sellerB.getUserId(), "Used - Like New", "Electronics", "Like New", "Vancouver"));
        allProducts.add(new Product("prod6", "History Textbook", 45.00, getImageUrl("History Textbook"), "Available", sellerC.getUserId(), "Used - Good", "School Supplies", "Good", "Kelowna"));
        allProducts.add(new Product("prod7", "Winter Jacket", 75.00, getImageUrl("Winter Jacket"), "Available", sellerA.getUserId(), "Used - Good", "Clothing", "Good", "Kelowna"));
        allProducts.add(new Product("prod8", "Bookshelf", 40.00, getImageUrl("Bookshelf"), "Available", sellerB.getUserId(), "Used - Fair", "Furniture", "Fair", "Vernon"));
        allProducts.add(new Product("prod9", "Blender", 30.00, getImageUrl("Blender"), "Available", sellerC.getUserId(), "Used - Like New", "Kitchenware", "Like New", "Kelowna"));
        allProducts.add(new Product("prod10", "Acoustic Guitar", 150.00, getImageUrl("Acoustic Guitar"), "Available", sellerA.getUserId(), "Used - Good", "Hobbies", "Good", "Vancouver"));
        allProducts.add(new Product("prod11", "Yoga Mat", 10.00, getImageUrl("Yoga Mat"), "Available", sellerB.getUserId(), "New", "Sports", "New", "Kelowna"));
        allProducts.add(new Product("prod12", "Monitor", 180.00, getImageUrl("Monitor"), "Available", sellerC.getUserId(), "Used - Like New", "Electronics", "Like New", "Kelowna"));

        // Make the user like some of the items
        currentUser.addFavorite("prod2"); // User likes the sold desk lamp
        currentUser.addFavorite("prod3"); // User likes the available mini fridge
        currentUser.addFavorite("prod4"); // User likes the gaming chair
    }

    private String getImageUrl(String imageName) {
        // Correctly convert "Used Textbook" to "used_textbook"
        String resourceName = imageName.toLowerCase().replace(" ", "_");

        // The URI format Glide understands for local drawables
        return "android.resource://com.example.myapplication/drawable/" + resourceName;
    }

    public User getUserById(String userId) {
        if (userId == null) {
            return null;
        }
        // Stream through the list of all users and find the one with the matching ID
        return allUsers.stream()
                .filter(user -> userId.equals(user.getUserId()))
                .findFirst()
                .orElse(null); // Return null if no user is found
    }

    public Product getProductById(String productId) {
        if (productId == null) {
            return null;
        }
        // Stream through the list of all products and find the one with the matching ID
        return allProducts.stream()
                .filter(product -> productId.equals(product.getId()))
                .findFirst()
                .orElse(null); // Return null if no product is found
    }
}

