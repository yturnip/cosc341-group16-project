package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class BuyItemActivity extends AppCompatActivity {

    private ImageView buyProductImage, searchBackButton;
    private TextView buyProductName, buyProductPrice, buyDescription, meetupProduct, sellerName;
    private Button buyMessageButton, buyFavoriteButton;

    private Product product;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_item);

        currentUser = ProductRepository.getInstance().getCurrentUser();

        buyProductImage = findViewById(R.id.buyProductImage);
        buyProductName = findViewById(R.id.buyProductName);
        buyProductPrice = findViewById(R.id.buyProductPrice);
        buyDescription = findViewById(R.id.buyDescription);
        buyMessageButton = findViewById(R.id.buyMessageButton);
        meetupProduct = findViewById(R.id.MeetupProduct);
        sellerName = findViewById(R.id.sellerName);
        searchBackButton = findViewById(R.id.searchBackButton2);

        searchBackButton.setOnClickListener(v -> finish());

        // Get product sent from Adapter click
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("product");

        showProductData();

        buyMessageButton.setOnClickListener(v -> {
            User sellerUser = new User(product.getSellerId(), product.getSellerId(), null); // Or get actual name
            ChatManager.getInstance().addUser(sellerUser, product);

            Intent chatIntent = new Intent(BuyItemActivity.this, ChattingActivity.class);
            chatIntent.putExtra("currentUserId", currentUser.getUserId());
            chatIntent.putExtra("friendUserId", product.getSellerId());
            chatIntent.putExtra("product", product);
            startActivity(chatIntent);
        });

        ImageButton buyFavoriteButton = findViewById(R.id.buyFavoriteButton);

        if (currentUser.isFavorite(product.getId())) {
            buyFavoriteButton.setImageResource(R.drawable.ic_heart_on);
        } else {
            buyFavoriteButton.setImageResource(R.drawable.love_icon);
        }

        buyFavoriteButton.setOnClickListener(v -> {
            boolean isFav = currentUser.isFavorite(product.getId());

            if (isFav) {
                currentUser.removeFavorite(product.getId());
                buyFavoriteButton.setImageResource(R.drawable.love_icon);
            } else {
                currentUser.addFavorite(product.getId());
                buyFavoriteButton.setImageResource(R.drawable.ic_heart_on);
            }
        });

    }

    private void showProductData() {
        buyProductName.setText(product.getName());
        buyProductPrice.setText(String.format("$%.2f", product.getPrice()));
        buyDescription.setText(product.getDescription());
        meetupProduct.setText(product.getLocation());
        sellerName.setText(product.getSellerId());


        Glide.with(this)
                .load(product.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(buyProductImage);
    }
}
