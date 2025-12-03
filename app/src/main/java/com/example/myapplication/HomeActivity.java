package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeActivity extends AppCompatActivity implements ProductAdapter.OnFavoriteClickListener {

    private RecyclerView categoriesRecycler;
    private RecyclerView productsRecycler;

    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;

    private ProductRepository repository;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this); // This can sometimes interfere with padding, let's keep it simple for now.
        setContentView(R.layout.activity_home);

        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.setFocusable(false);
        searchBar.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, SearchActivity.class));
        });


        // This listener handles padding for system bars (like status bar), which is good practice.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); // Set bottom padding to 0 to allow bottom nav to be flush
            return insets;
        });

        // 1. Initialize Repository and get data
        repository = ProductRepository.getInstance();
        currentUser = repository.getCurrentUser();

        // 2. Setup the main products RecyclerView
        // Your activity_home.xml has a RecyclerView with the ID 'productsRecyclerView'
        productsRecycler = findViewById(R.id.productsRecyclerView);

        // 3. Setup Bottom Navigation
        setupBottomNavigation();
        setupCategoriesRecycler();
        setupProductsRecycler();

        ImageView profileHomePage = findViewById(R.id.profileHomePage);

        profileHomePage.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfilePage.class);
            startActivity(intent);
        });
    }

    private void setupCategoriesRecycler(){
        categoriesRecycler = findViewById(R.id.categoriesRecycler);

        LinearLayoutManager horizontalManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        categoriesRecycler.setLayoutManager(horizontalManager);
        categoriesRecycler.setNestedScrollingEnabled(false);

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Electronics", R.drawable.electronics));
        categoryList.add(new Category("Clothing", R.drawable.clothing));
        categoryList.add(new Category("Furniture", R.drawable.furniture));
        categoryList.add(new Category("Kitchenware", R.drawable.kitchenware));
        categoryList.add(new Category("Room Decor", R.drawable.room_decor));
        categoryList.add(new Category("Books", R.drawable.books));

        categoryAdapter = new CategoryAdapter(categoryList, category -> {
            Intent intent = new Intent(HomeActivity.this, ResultActivity.class);
            intent.putExtra("category", category.getName());
            startActivity(intent);
        });
        categoriesRecycler.setAdapter(categoryAdapter);
    }

    private void setupProductsRecycler(){
        List<Product> productList = ProductRepository.getInstance().getAllProducts()
                .stream().filter(p -> p.getStatus().equals("Available"))
                .collect(Collectors.toList());

        if (productAdapter == null) {
            LinearLayoutManager verticalManager = new LinearLayoutManager(this);
            productsRecycler.setLayoutManager(verticalManager);
            productsRecycler.setNestedScrollingEnabled(false);
            productAdapter = new ProductAdapter(productList, this);
            productsRecycler.setAdapter(productAdapter);
        } else {
            productAdapter.updateList(productList);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load (or reload) the data here
        setupBottomNavigation();
        setupCategoriesRecycler();
        setupProductsRecycler();
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
            }  else if (itemId == R.id.nav_chat) {
                startActivity(new Intent(getApplicationContext(), ChatListActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(getApplicationContext(), ProfilePage.class));
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