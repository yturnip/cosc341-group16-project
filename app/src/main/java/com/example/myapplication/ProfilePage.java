package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;
import java.util.stream.Collectors;

public class ProfilePage extends AppCompatActivity {
    private List<Product> allUserListings;
    private UserListingAdapter adapter;
    private RecyclerView listingsRecyclerView;
    private TextView seeAllButton;
    private TextView profileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        // Handle status bar padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); // Important: only top padding
            return insets;
        });

        // Get data
        ProductRepository repository = ProductRepository.getInstance();
        User currentUser = repository.getCurrentUser();

        // Bind user info
        profileName = findViewById(R.id.profile_name);
        if (currentUser != null) {
            profileName.setText(currentUser.getName());
        }

        // --- Listings Preview ---
        listingsRecyclerView = findViewById(R.id.listingsRecyclerView);
        allUserListings = repository.getUserListings();

        // Show only a preview (e.g., max 3 items)
        List<Product> previewListings = allUserListings.stream().limit(3).collect(Collectors.toList());

        // Use the new UserListingAdapter
        adapter = new UserListingAdapter(this, previewListings);
        listingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        listingsRecyclerView.setAdapter(adapter);

        // --- "See All" button ---
        seeAllButton = findViewById(R.id.see_all_button);
        seeAllButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilePage.this, UserListingsActivity.class);
            startActivity(intent);
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bnv = findViewById(R.id.bottom_navigation);
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
                return true;
            } else if (itemId == R.id.nav_chat) {
                startActivity(new Intent(getApplicationContext(), ChatListActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true; // Already here
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 1. Get the potentially updated list from the repository.
        List<Product> updatedListings = ProductRepository.getInstance().getUserListings();

        // 2. Update the adapter's list.
        // It's good practice to clear the old list and add the new one.
        allUserListings.clear();
        allUserListings.addAll(updatedListings);

        // 3. Notify the adapter that the entire dataset has changed.
        adapter.notifyDataSetChanged();
    }
}
