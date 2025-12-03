package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.stream.Collectors;

public class ProfilePage extends AppCompatActivity implements ProductAdapter.OnFavoriteClickListener {

    private List<Product> userListings;
    private ProductAdapter productAdapter;
    private RecyclerView listingsRecyclerView;
    private ProductRepository repository;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Get the single instance of the repository
        repository = ProductRepository.getInstance();

        // 2. Get the favorite products directly from the repository
        userListings = repository.getUserListings();

        // 3. Setup RecyclerView with the data
        listingsRecyclerView = findViewById(R.id.listingsRecyclerView);
        productAdapter = new ProductAdapter(userListings, this); // Pass the list to the adapter
        listingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        listingsRecyclerView.setAdapter(productAdapter);

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bnv = findViewById(R.id.bottom_navigation);

        // Set the "Profile" item as the selected one since we are on the Chat screen
        bnv.setSelectedItemId(R.id.nav_profile);

        bnv.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                return true;
            } else if (itemId == R.id.nav_fav) {
                startActivity(new Intent(getApplicationContext(), FavoriteActivity.class));
                return true;
            } else if (itemId == R.id.nav_sell) {
                startActivity(new Intent(getApplicationContext(), SellActivity.class));
                // Don't finish HomeActivity, so the user can easily return
                return true;
            }  else if (itemId == R.id.nav_chat) {
                startActivity(new Intent(getApplicationContext(), ChatListActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                // We are already on the profile screen, so do nothing.
                return true;
            }

            // Add other navigation items (chat, profile) here in the future
            return false;
        });
    }

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