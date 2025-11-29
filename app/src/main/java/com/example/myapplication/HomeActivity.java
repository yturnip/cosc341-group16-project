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

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements ProductAdapter.OnFavoriteClickListener{
    private RecyclerView categoriesRecycler;
    private RecyclerView productsRecycler;

    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home){
                return true;
            } else if (id == R.id.nav_fav) {
                startActivity(new Intent(HomeActivity.this, FavoriteActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_sell) {
                startActivity(new Intent(HomeActivity.this, SellActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });

        setupCategoriesRecycler();
        setupProductsRecycler();
    }

    @Override
    public void onFavoriteClick(Product product){

    }

    private void setupCategoriesRecycler(){
        categoriesRecycler = findViewById(R.id.categoriesRecycler);

        LinearLayoutManager horizontalManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        categoriesRecycler.setLayoutManager(horizontalManager);
        categoriesRecycler.setNestedScrollingEnabled(false);

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Electronics", R.drawable.ic_launcher_background));
        categoryList.add(new Category("Clothing", R.drawable.ic_launcher_background));
        categoryList.add(new Category("Furniture", R.drawable.ic_launcher_background));
        categoryList.add(new Category("Kitchenware", R.drawable.ic_launcher_background));
        categoryList.add(new Category("Room Decor", R.drawable.ic_launcher_background));
        categoryList.add(new Category("Books", R.drawable.ic_launcher_background));

        categoryAdapter = new CategoryAdapter(categoryList);
        categoriesRecycler.setAdapter(categoryAdapter);
    }

    private void setupProductsRecycler(){
        productsRecycler = findViewById(R.id.productsRecyclerView);

        LinearLayoutManager verticalManager = new LinearLayoutManager(this);
        productsRecycler.setLayoutManager(verticalManager);
        productsRecycler.setNestedScrollingEnabled(false);

        List<Product> productList = new ArrayList<>();
        productList.add(new Product("1", "Desk Lamp", 17.00, "", "Available", "user1", "New", "Category", "Desc", "Kelowna"));
        productList.add(new Product("2", "Mini Fridge", 50.00, "", "Available", "user1", "New", "Category", "Desc", "Kelowna"));
        productList.add(new Product("3", "Chair", 20.00, "", "Available", "user1", "New", "Category", "Desc", "Kelowna"));
        productList.add(new Product("4", "Headphone", 32.00, "", "Available", "user1", "New", "Category", "Desc", "Kelowna"));
        productList.add(new Product("5", "Camera", 100.00, "", "Available", "user1", "New", "Category", "Desc", "Kelowna"));

        productAdapter = new ProductAdapter(productList, this);
        productsRecycler.setAdapter(productAdapter);
    }

}