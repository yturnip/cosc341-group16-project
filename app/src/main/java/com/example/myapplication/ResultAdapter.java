package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder>{
    private Context context;
    private List<Product> productList;
    private final ProductRepository repo = ProductRepository.getInstance();

    public ResultAdapter(Context context, List<Product> productList){
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.result_item, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        Product product = productList.get(position);
        User currentUser = repo.getCurrentUser();

        holder.name.setText(product.getName());
        holder.price.setText("$" + product.getPrice());
        holder.status.setText(product.getStatus());

        Glide.with(context).load(product.getImageUrl()). into(holder.image);

        //  Status
        String status = product.getStatus();

        if (status.equalsIgnoreCase("Available")){
            holder.status.setBackgroundTintList(context.getColorStateList(R.color.status_available));
        } else if (status.equalsIgnoreCase("Pending")) {
            holder.status.setBackgroundTintList(context.getColorStateList(R.color.status_pending));
        } else if (status.equalsIgnoreCase("Sold")) {
            holder.status.setBackgroundTintList(context.getColorStateList(R.color.status_sold));
        }

        //  Favorite icon
        boolean isFav = currentUser.isFavorite(product.getId());
        holder.favorite.setImageResource(isFav ? R.drawable.ic_heart_on : R.drawable.love_icon);

        //  Favorite toggle
        holder.favorite.setOnClickListener(v -> {
            if (currentUser.isFavorite(product.getId())){
                currentUser.removeFavorite(product.getId());
            } else {
                currentUser.addFavorite(product.getId());
            }
            notifyItemChanged(position);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BuyItemActivity.class);
            intent.putExtra("product", product);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name, price, status;
        ImageButton favorite;

        public ResultViewHolder(@NonNull View itemView){
            super(itemView);

            image = itemView.findViewById(R.id.productImageView);
            name = itemView.findViewById(R.id.productNameTextView);
            price = itemView.findViewById(R.id.productPriceTextView);
            status = itemView.findViewById(R.id.productStatusTextView);
            favorite = itemView.findViewById(R.id.favoriteButton);
        }
    }
}