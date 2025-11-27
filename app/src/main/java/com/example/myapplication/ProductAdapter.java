package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;

    // Constructor to initialize the list
    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    // This method is called when the RecyclerView needs a new ViewHolder.
    // It inflates the item_product.xml layout.
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    // This method binds the data from your productList to the views in the ViewHolder.
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product currentProduct = productList.get(position);
        holder.bind(currentProduct);
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

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            productStatusTextView = itemView.findViewById(R.id.productStatusTextView);
        }

        // Helper method to set the data on the views
        public void bind(Product product) {
            productNameTextView.setText(product.getName());
            // Format the price to show as currency
            productPriceTextView.setText(String.format(Locale.US, "$%.2f", product.getPrice()));
            productStatusTextView.setText(product.getStatus());

            // Here you would use a library like Glide or Picasso to load the image from a URL
            // For now, we'll just set a placeholder.
            // Glide.with(itemView.getContext()).load(product.getImageUrl()).into(productImageView);
        }
    }
}

