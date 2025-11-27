package com.example.myapplication;

import android.os.Bundle;

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

public class FavoriteActivity extends AppCompatActivity {

    private List<Product> allProducts;
    private List<Product> favoriteProducts;
    private ProductAdapter productAdapter;
    private RecyclerView favoritesRecyclerView;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadSampleData();

        filterFavorites();

        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        productAdapter = new ProductAdapter(favoriteProducts);
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
    }

    private void filterFavorites() {
        if (currentUser == null || allProducts == null) {
            favoriteProducts = new ArrayList<>();
            return;
        }

        List<String> favoriteIds = currentUser.getFavoriteProductIds();

        favoriteProducts = allProducts.stream()
                .filter(product -> favoriteIds.contains(product.getId()))
                .collect(Collectors.toList());
    }

    private void applyStatusFilter(int checkedId) {
        List<Product> filteredByStatus = new ArrayList<>();

        if(checkedId == R.id.btnAll) {
            filteredByStatus.addAll(favoriteProducts);
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
            filteredByStatus = favoriteProducts.stream()
                    .filter(p -> p.getStatus().equalsIgnoreCase(finalStatus))
                    .collect(Collectors.toList());
        }

        productAdapter.updateList(filteredByStatus);
    }

    private void loadSampleData() {
        // Sample User
        currentUser = new User("user123", "Alex", "url_to_profile_pic");

        // Sample Products
        allProducts = new ArrayList<>();
        allProducts.add(new Product("prod1", "Textbook", 25.00, "url", "Available", "sellerA", "Used", "School Stuff", "Good", "Kelowna"));
        allProducts.add(new Product("prod2", "Desk Lamp", 15.00, "url", "Sold", "sellerB", "Used", "School Stuff", "Good", "Kelowna"));
        allProducts.add(new Product("prod3", "Mini Fridge", 50.00, "url", "Available", "sellerC", "Used", "School Stuff", "Good", "Kelowna"));
        allProducts.add(new Product("prod4", "Chair", 20.00, "url", "Pending", "sellerA", "Used", "School Stuff", "Good", "Kelowna"));

        // Simulate the user favoriting some items
        currentUser.addFavorite("prod2"); // User likes the sold desk lamp
        currentUser.addFavorite("prod3"); // User likes the available mini fridge
    }
}