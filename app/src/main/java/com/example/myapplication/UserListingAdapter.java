package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

public class UserListingAdapter extends RecyclerView.Adapter<UserListingAdapter.UserListingViewHolder> {

    private List<Product> productList;
    private Context context;

    public UserListingAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public UserListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_listing, parent, false);
        return new UserListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListingViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class UserListingViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView, productPriceTextView, productStatusTextView;
        Button editButton, statusButton;
        ImageButton deleteButton;
        ProductRepository repository = ProductRepository.getInstance();


        public UserListingViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            productStatusTextView = itemView.findViewById(R.id.productStatusTextView);
            editButton = itemView.findViewById(R.id.editButton);
            statusButton = itemView.findViewById(R.id.statusButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(Product product) {
            productNameTextView.setText(product.getName());
            productPriceTextView.setText(String.format(Locale.US, "$%.2f", product.getPrice()));
            productStatusTextView.setText(product.getStatus());

            Glide.with(context)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(productImageView);

            // Set status color
            GradientDrawable statusBackground = (GradientDrawable) productStatusTextView.getBackground();
            String status = product.getStatus();
            int color;
            if ("Available".equalsIgnoreCase(status)) {
                color = ContextCompat.getColor(context, R.color.status_available);
                statusButton.setText("Mark as Pending");
            } else if ("Pending".equalsIgnoreCase(status)) {
                color = ContextCompat.getColor(context, R.color.status_pending);
                statusButton.setText("Mark as Sold");
            } else { // Sold
                color = ContextCompat.getColor(context, R.color.status_sold);
                statusButton.setText("Sold");
                statusButton.setEnabled(false);
            }
            statusBackground.setColor(color);

            if ("Sold".equalsIgnoreCase(status)) {
                editButton.setEnabled(false);
                statusButton.setEnabled(false);
                statusButton.setText("Sold");
            } else {
                editButton.setEnabled(true);
                statusButton.setEnabled(true);
                statusButton.setText("Change Status");
            }

            // Button listeners (implement actual logic later)
            editButton.setOnClickListener(v -> {
                Intent intent= new Intent(context, EditListingActivity.class);
                intent.putExtra("productId", product.getId());
                context.startActivity(intent);
            });
            deleteButton.setOnClickListener(v -> {
                // It's good practice to show a confirmation dialog before deleting
                new AlertDialog.Builder(context)
                        .setTitle("Delete Listing")
                        .setMessage("Are you sure you want to delete '" + product.getName() + "'?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                           productList.remove(product);
                           notifyItemRemoved(getAdapterPosition());
                           notifyItemRangeChanged(getAdapterPosition(), productList.size());
                           repository.deleteProduct(product.getId());
                           Toast.makeText(context, "Deleted " + product.getName(), Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
            statusButton.setOnClickListener(v -> {
                // Create a PopupMenu
                PopupMenu popup = new PopupMenu(context, statusButton);

                // Inflate the menu from a new menu resource
                popup.getMenuInflater().inflate(R.menu.menu_status_options, popup.getMenu());

                // Show/hide options based on the current status
                if ("Available".equalsIgnoreCase(product.getStatus())) {
                    popup.getMenu().findItem(R.id.action_mark_available).setVisible(false);
                } else if ("Pending".equalsIgnoreCase(product.getStatus())) {
                    popup.getMenu().findItem(R.id.action_mark_pending).setVisible(false);
                }

                // Set a listener for menu item clicks
                popup.setOnMenuItemClickListener(item -> {
                    int itemId = item.getItemId();
                    String newStatus = null;
                    if (itemId == R.id.action_mark_available) {
                       newStatus = "Available";
                    } else if (itemId == R.id.action_mark_pending) {
                       newStatus = "Pending";
                    } else if (itemId == R.id.action_mark_sold) {
                       newStatus = "Sold";
                    }
                    if (newStatus != null) {
                        repository.updateProductStatus(product.getId(), newStatus);
                        Toast.makeText(context, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                        notifyItemChanged(getAdapterPosition());
                        return true;
                    }
                    return false;
                });

                popup.show(); // Show the popup menu
            });
        }
    }
}
