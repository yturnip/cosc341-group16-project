package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FavoriteActivity extends AppCompatActivity implements ProductAdapter.OnFavoriteClickListener {

    private List<Product> favoriteProducts;
    private ProductAdapter productAdapter;
    private RecyclerView favoritesRecyclerView;
    private ProductRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite);
        // This listener handles padding for system bars (like status bar), which is good practice.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); // Set bottom padding to 0 to allow bottom nav to be flush
            return insets;
        });

        // 1. Get the single instance of the repository
        repository = ProductRepository.getInstance();

        // 2. Get the favorite products directly from the repository
        favoriteProducts = repository.getFavoriteProducts();

        // 3. Setup RecyclerView with the data
        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        productAdapter = new ProductAdapter(favoriteProducts, this); // Pass the list to the adapter
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoritesRecyclerView.setAdapter(productAdapter);

        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.filterToggleGroup);
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                applyStatusFilter(checkedId);
            }
        });

        BottomNavigationView bnv = findViewById(R.id.bottom_navigation);

        bnv.setSelectedItemId(R.id.nav_fav);
        bnv.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Navigate to MainActivity
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                // Optional: finish the current activity so the user can't go back to it
                finish();
                return true;

            } else if (itemId == R.id.nav_fav) {
                // We are already on this screen, so do nothing.
                return true;

            } else if (itemId == R.id.nav_sell) {
                // Navigate to SellActivity (assuming you have one)
                startActivity(new Intent(getApplicationContext(), SellActivity.class));
                finish();
                return true;

            } else if (itemId == R.id.nav_chat) {
                // Navigate to ChatActivity (assuming you have one)
                startActivity(new Intent(getApplicationContext(), ChatListActivity.class));
                finish();
                return true;

            /*} else if (itemId == R.id.nav_user) {
                // Navigate to ProfileActivity (assuming you have one)
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();
                return true;
            */} else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(getApplicationContext(), ProfilePage.class));
                return true;
            }

            return false;
        });
    }

    private void applyStatusFilter(int checkedId) {
        List<Product> baseList = repository.getFavoriteProducts(); // <-- Get the fresh list
        List<Product> filteredByStatus = new ArrayList<>();

        if(checkedId == R.id.btnAll) {
            filteredByStatus.addAll(baseList);
        } else {
            String status = "";
            if (checkedId == R.id.btnAvailable) {
                status = "Available";
            } else if (checkedId == R.id.btnPending) {
                status = "Pending";
            } else if (checkedId == R.id.btnSold) {
                status = "Sold";
            }

            String finalStatus = status;
            filteredByStatus = baseList.stream() // <-- Filter from the fresh list
                    .filter(p -> p.getStatus().equalsIgnoreCase(finalStatus))
                    .collect(Collectors.toList());
        }

        productAdapter.updateList(filteredByStatus);
    }
    // --- THIS IS THE METHOD THAT WILL BE CALLED WHEN THE HEART ICON IS CLICKED ---
    @Override
    public void onFavoriteClick(Product product) {
        // 1. Tell the repository to remove the favorite
        repository.getCurrentUser().removeFavorite(product.getId());

        // 2. Get the new, updated list of favorites
        List<Product> updatedFavorites = repository.getFavoriteProducts();

        // 3. Tell the adapter to update its data and refresh the screen
        productAdapter.updateList(updatedFavorites);

        // 4. Also update the local list used by the filter buttons
        this.favoriteProducts = updatedFavorites;
    }
}