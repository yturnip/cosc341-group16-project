package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ConversationListAdapter extends RecyclerView.Adapter<ConversationListAdapter.ConversationViewHolder> {

    public interface OnConversationClickListener {
        void onConversationClick(Conversation conversation);
    }

    private final List<Conversation> conversations;
    private final OnConversationClickListener listener;
    private final ProductRepository repository;

    public ConversationListAdapter(List<Conversation> conversations, OnConversationClickListener listener) {
        this.conversations = conversations;
        this.listener = listener;
        this.repository = ProductRepository.getInstance();
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        Conversation conversation = conversations.get(position);
        holder.bind(conversation, listener, repository);
    }

    @Override
    public int getItemCount() {
        return conversations == null ? 0 : conversations.size();
    }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {
        // These are the views you need in your 'item_conversation.xml' layout.
        ImageView productImageView;
        TextView sellerNameTextView;
        TextView productNameTextView;
        TextView lastMessageTextView;

        ConversationViewHolder(View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageView); // Example ID
            sellerNameTextView = itemView.findViewById(R.id.sellerNameTextView); // Example ID
            productNameTextView = itemView.findViewById(R.id.productNameTextView); // Example ID
            lastMessageTextView = itemView.findViewById(R.id.lastMessageTextView); // Example ID
        }

        // The bind method populates the views with data from the Conversation object.
        public void bind(final Conversation conversation, final OnConversationClickListener listener, ProductRepository repository) {
            // Get the associated user (seller) and product from the repository.
            User seller = repository.getUserById(conversation.getSellerId());
            Product product = repository.getProductById(conversation.getProductId());

            if (seller != null) {
                sellerNameTextView.setText(seller.getName());
            } else {
                sellerNameTextView.setText("Unknown Seller");
            }

            if (product != null) {
                productNameTextView.setText(product.getName());
                Glide.with(itemView.getContext())
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(productImageView);
            } else {
                productNameTextView.setText("Product not found");
                productImageView.setImageResource(R.drawable.ic_launcher_background);
            }

            // Set the last message text
            Message lastMessage = conversation.getLastMessage();
            if (lastMessage != null) {
                lastMessageTextView.setText(lastMessage.getText());
            } else {
                lastMessageTextView.setText("No messages yet.");
            }

            // Set the click listener for the whole item.
            itemView.setOnClickListener(v -> listener.onConversationClick(conversation));
        }
    }
}
