package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.stream.Collectors;

public class ResultActivity extends AppCompatActivity {

    private RecyclerView resultRecycler;
    private TextView resultTitle;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultRecycler = findViewById(R.id.resultRecycler);
        resultTitle = findViewById(R.id.resultTitle);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        User currentUser = ProductRepository.getInstance().getCurrentUser();

        //  Get data from Search or Filter
        String keyword = getIntent().getStringExtra("keyword");
        String category = getIntent().getStringExtra("category");
        String query = getIntent().getStringExtra("query");

        if ((keyword == null || keyword.isEmpty()) && query != null){
            keyword = query;
        }

        float minPrice = getIntent().getFloatExtra("minPrice", 0f);
        float maxPrice = getIntent().getFloatExtra("maxPrice", Float.MAX_VALUE);

        List<Product> allProducts = ProductRepository.getInstance().getAllProducts();

        final String finalKeyword = keyword;
        final String finalCategory = category;

        //   Apply search and filter
        List<Product> filteredList = allProducts.stream()
                .filter(p -> p.getStatus().equalsIgnoreCase("Available"))
                .filter(p -> finalKeyword == null || finalKeyword.isEmpty() ||
                        p.getName().toLowerCase().contains(finalKeyword.toLowerCase()))
                .filter(p -> finalCategory == null || finalCategory.isEmpty() ||
                        p.getCategory().equalsIgnoreCase(finalCategory))
                .filter(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice)
                .collect(Collectors.toList());

        if (keyword != null && !keyword.isEmpty())
            resultTitle.setText("Results for " + keyword);
        else if (category != null && !category.isEmpty())
            resultTitle.setText(category + " Items");
        else
            resultTitle.setText("Filtered Results");

        //   Set recycler
        resultRecycler.setLayoutManager(new LinearLayoutManager(this));
        resultRecycler.setAdapter(new ResultAdapter(this, filteredList));
    }
}