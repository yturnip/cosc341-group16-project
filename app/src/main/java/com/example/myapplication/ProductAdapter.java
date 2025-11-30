package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;
    private final OnFavoriteClickListener listener;
    private final User currentUser;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Product product);
    }
    // Constructor to initialize the list
    public ProductAdapter(List<Product> productList, OnFavoriteClickListener listener) {
        this.productList = productList;
        this.listener = listener;
        this.currentUser = ProductRepository.getInstance().getCurrentUser();
    }

    // This method is called when the RecyclerView needs a new ViewHolder.
    // It inflates the item_product.xml layout.
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Get the context from the parent viewgroup
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view, listener);
    }

    // This method binds the data from your productList to the views in the ViewHolder.
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product currentProduct = productList.get(position);
        holder.bind(currentProduct, context, currentUser);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BuyItemActivity.class);
            intent.putExtra("product", currentProduct); // use the correct variable
            context.startActivity(intent);
        });
    }

    // Returns the total number of items in the list.
    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Method to update the list of products and notify the adapter of the change
    public void updateList(List<Product> newList) {
        productList = newList;
        notifyDataSetChanged(); // This refreshes the RecyclerView
    }

    /**
     * The ViewHolder class holds the views for a single item in the list.
     * This is where you find your TextViews, ImageView, etc. by their ID.
     */
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImageView;
        private final TextView productNameTextView;
        private final TextView productPriceTextView;
        private final TextView productStatusTextView;
        private final ImageButton favoriteButton;
        private final OnFavoriteClickListener listener;


        public ProductViewHolder(@NonNull View itemView, final OnFavoriteClickListener listener) {
            super(itemView);
            this.listener = listener;
            productImageView = itemView.findViewById(R.id.productImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            productStatusTextView = itemView.findViewById(R.id.productStatusTextView);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
        }

        // Helper method to set the data on the views
        public void bind(Product product, Context context, User user) {
            productNameTextView.setText(product.getName());
            // Format the price to show as currency
            productPriceTextView.setText(String.format(Locale.US, "$%.2f", product.getPrice()));
            productStatusTextView.setText(product.getStatus());

            // Check if the current user has this product in their favorites
            if (user != null && user.isFavorite(product.getId())) {
                // If it IS a favorite, tint the icon purple
                ImageViewCompat.setImageTintList(favoriteButton, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple_500)));
            } else {
                // If it is NOT a favorite, tint the icon white (or a light gray to be visible on a white background)
                // Let's use a light gray color for the non-favorite state.
                ImageViewCompat.setImageTintList(favoriteButton, ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.darker_gray)));
            }

            GradientDrawable statusBackground = (GradientDrawable) productStatusTextView.getBackground();

            String status = product.getStatus();
            if (status != null) {
                if (status.equalsIgnoreCase("Available")) {
                    // Use ContextCompat to get the color safely
                    statusBackground.setColor(ContextCompat.getColor(context, R.color.status_available));
                } else if (status.equalsIgnoreCase("Pending")) {
                    statusBackground.setColor(ContextCompat.getColor(context, R.color.status_pending));
                } else if (status.equalsIgnoreCase("Sold")) {
                    statusBackground.setColor(ContextCompat.getColor(context, R.color.status_sold));
                } else {
                    // Default color if status is something else
                    statusBackground.setColor(ContextCompat.getColor(context, R.color.darker_gray));
                }
            }
            favoriteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFavoriteClick(product);
                }
            });

            // Here you would use a library like Glide or Picasso to load the image from a URL
            // For now, we'll just set a placeholder.
            Glide.with(itemView.getContext()).load(product.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(productImageView);
        }
    }
}

