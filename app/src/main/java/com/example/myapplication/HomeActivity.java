package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

// FIX 1: Implement the adapter's click listener interface
public class HomeActivity extends AppCompatActivity implements ProductAdapter.OnFavoriteClickListener {

    private ProductAdapter productAdapter;
    private ProductRepository repository;
    private User currentUser;
    private RecyclerView productsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this); // This can sometimes interfere with padding, let's keep it simple for now.
        setContentView(R.layout.activity_home);

        // This listener handles padding for system bars (like status bar), which is good practice.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); // Set bottom padding to 0 to allow bottom nav to be flush
            return insets;
        });

        // 1. Initialize Repository and get data
        repository = ProductRepository.getInstance();
        currentUser = repository.getCurrentUser();
        List<Product> allProducts = repository.getAllProducts();

        // 2. Setup the main products RecyclerView
        // Your activity_home.xml has a RecyclerView with the ID 'productsRecyclerView'
        productsRecyclerView = findViewById(R.id.productsRecyclerView);

        // The adapter needs 'this' as the listener because this activity implements OnFavoriteClickListener
        productAdapter = new ProductAdapter(allProducts, this);

        // Use a GridLayoutManager to show products in a 2-column grid, which is common for e-commerce apps
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productsRecyclerView.setAdapter(productAdapter);

        // 3. Setup Bottom Navigation
        setupBottomNavigation();

        // FIX 2: Remove the incorrect navigation to FavoriteActivity
        // Intent intent = new Intent(this, FavoriteActivity.class);
        // startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load (or reload) the data here
        setupBottomNavigation();
        loadAndDisplayProducts();
    }

    private void loadAndDisplayProducts() {
        // Get the latest list of products from the repository
        List<Product> allProducts = repository.getAllProducts();

        // If the adapter hasn't been created yet, create and set it
        if (productAdapter == null) {
            productAdapter = new ProductAdapter(allProducts, this);
            productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            productsRecyclerView.setAdapter(productAdapter);
        } else {
            // If the adapter already exists, just update its list
            productAdapter.updateList(allProducts);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bnv = findViewById(R.id.bottom_navigation);

        // Set the "Home" item as the selected one since we are on the Home screen
        bnv.setSelectedItemId(R.id.nav_home);

        bnv.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // We are already on the home screen, so do nothing.
                return true;
            } else if (itemId == R.id.nav_fav) {
                startActivity(new Intent(getApplicationContext(), FavoriteActivity.class));
                overridePendingTransition(0, 0); // Optional: removes the default animation
                finish(); // Close HomeActivity to prevent a stack of activities
                return true;
            } else if (itemId == R.id.nav_sell) {
                startActivity(new Intent(getApplicationContext(), SellActivity.class));
                // Don't finish HomeActivity, so the user can easily return
                return true;
            }
            // Add other navigation items (chat, profile) here in the future
            return false;
        });
    }

    // FIX 3: Implement the onFavoriteClick method required by the interface
    @Override
    public void onFavoriteClick(Product product) {
        // This method is called from the adapter whenever a heart icon is clicked.
        if (currentUser != null) {
            // Check if the product is already a favorite
            if (currentUser.isFavorite(product.getId())) {
                // If yes, remove it
                currentUser.removeFavorite(product.getId());
            } else {
                // If no, add it
                currentUser.addFavorite(product.getId());
            }
            // Notify the adapter that the underlying data has changed for a specific item.
            // This will trigger the adapter to re-bind the view and update the heart icon.
            // A more efficient way is to find the item's position and use notifyItemChanged(position).
            productAdapter.notifyDataSetChanged();
        }
    }
}
