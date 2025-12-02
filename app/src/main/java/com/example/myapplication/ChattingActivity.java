package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChattingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChattingAdapter chatAdapter;
    private EditText editTextMessage;
    private ImageButton buttonSend;

    private User currentUser;
    private User friendUser;
    private List<Message> chatMessages;
    private Handler handler = new Handler(); // Handler for delayed reply
    private Product product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        // UI references
        recyclerView = findViewById(R.id.favoritesRecyclerView);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        TextView headerText = findViewById(R.id.favListingText);

        // Get users from intent (or default for testing)
        String currentUserId = getIntent().getStringExtra("currentUserId");
        String friendUserId = getIntent().getStringExtra("friendUserId");
        String friendUserName = getIntent().getStringExtra("friendUserName");
        product = (Product) getIntent().getSerializableExtra("product");

        if (currentUserId == null) currentUserId = "user1";
        if (friendUserId == null) friendUserId = "user2";
        if (friendUserName == null) friendUserName = "Alice";

        currentUser = new User(currentUserId, "Me", null);
        friendUser = new User(friendUserId, friendUserName, null);

        // Set header to the name of the user
        headerText.setText(friendUser.getName());

        TextView textUserListingName = findViewById(R.id.textUserListingName);
        TextView textListingPrice = findViewById(R.id.textListingPrice);
        ImageView productImageView = findViewById(R.id.productImageView);

        /*if (friendUser.getName().equalsIgnoreCase("Alice")) {
            friendUser.addListing(new Product("p1", "Book", 10, "url1", "Available", friendUser.getUserId(), "Used", "Books", "Good book", "City"));
        } else if (friendUser.getName().equalsIgnoreCase("Bob")) {
            friendUser.addListing(new Product("p3", "Notebook", 5, "url3", "Available", friendUser.getUserId(), "Used", "Stationery", "Notebook 100 pages", "City"));
        } else if (friendUser.getName().equalsIgnoreCase("Charlie")) {
            friendUser.addListing(new Product("p4", "Hoodie", 55, "url4", "Available", friendUser.getUserId(), "Used", "Clothing", "Mint condition", "Campus"));
        }

        // Update the name dynamically based on the user
        if (friendUser.getListings().size() == 1) {
            textUserListingName.setText(friendUser.getListings().get(0).getName());
            textListingPrice.setText(String.valueOf(friendUser.getListings().get(0).getPrice()) + " $");
        } else {
            textUserListingName.setText("No Listings");
        }


        // Initialize message list
        chatMessages = new ArrayList<>();

        Button buttonViewListings = findViewById(R.id.buttonViewListings);
        buttonViewListings.setOnClickListener(v -> {
            List<Product> userListings = friendUser.getListings();

            if (!userListings.isEmpty()) {
                // Open the first product in BuyItemActivity
                Product firstProduct = userListings.get(0);

                Intent intent = new Intent(ChattingActivity.this, BuyItemActivity.class);
                intent.putExtra("product", firstProduct); // Product must implement Serializable
                startActivity(intent);
            }
        });




        // PRESET MESSAGES
        if (friendUser.getName().equalsIgnoreCase("Alice")) {
            chatMessages.add(new Message("user2", "Hello! How are you?", System.currentTimeMillis() - 60000));
            chatMessages.add(new Message(currentUser.getUserId(), "Hi Alice! I'm good, thanks.", System.currentTimeMillis() - 55000));
            chatMessages.add(new Message("user2", "Want to meet tomorrow?", System.currentTimeMillis() - 50000));
        } else if (friendUser.getName().equalsIgnoreCase("Bob")) {
            chatMessages.add(new Message("user3", "Hey! Did you finish the assignment?", System.currentTimeMillis() - 60000));
            chatMessages.add(new Message(currentUser.getUserId(), "Hi Bob, not yet. Working on it.", System.currentTimeMillis() - 55000));
            chatMessages.add(new Message("user3", "Alright, let's discuss later.", System.currentTimeMillis() - 50000));
        } else if (friendUser.getName().equalsIgnoreCase("Charlie")) {
            chatMessages.add(new Message("user4", "Hi!", System.currentTimeMillis() - 60000));
            chatMessages.add(new Message(currentUser.getUserId(), "Hi Charlie, How are you?", System.currentTimeMillis() - 55000));
            chatMessages.add(new Message("user4", "Pretty good, thanks!", System.currentTimeMillis() - 50000));
            chatMessages.add(new Message("user4", "Where do you wanna meet?", System.currentTimeMillis() - 50000));
        }
        */

        // --- Now use the product data to populate the UI ---
        if (product != null) {
            // Find the seller's name from the ProductRepository or pass it via intent
            // For now, let's just use the ID. A better approach would be fetching the user's name.
            String sellerName = "Seller " + product.getSellerId();
            headerText.setText(sellerName); // Set the chat header to the seller's name/ID

            // Populate the product banner
            textUserListingName.setText(product.getName());
            textListingPrice.setText(String.format("$%.2f", product.getPrice()));

            Glide.with(this)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(productImageView);

        } else {
            // Handle case where product is not passed (optional)
            headerText.setText("Chat");
            textUserListingName.setText("No product specified");
            textListingPrice.setText("");
        }

        // The rest of your chat logic can remain largely the same.
        // You can remove all the hardcoded messages for "Alice", "Bob", and "Charlie".
        chatMessages = new ArrayList<>();

        Button buttonViewListings = findViewById(R.id.buttonViewListings);
        buttonViewListings.setOnClickListener(v -> {
            // Check if the product object exists before trying ProcessBuilder.Redirect.to use it.
            if (product != null) {
                // Create an intent to go back to the BuyItemActivity.
                Intent intent = new Intent(ChattingActivity.this, BuyItemActivity.class);

                // Pass the *same product* back to the activity.
                intent.putExtra("product", product);

                // Start the activity.
                startActivity(intent);
            }
        });

        // Setup RecyclerView
        chatAdapter = new ChattingAdapter(chatMessages, currentUser.getUserId());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);
        recyclerView.scrollToPosition(chatMessages.size() - 1);

        // Send button click
        AtomicBoolean charlieHasReplied = new AtomicBoolean(false);

        buttonSend.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                // 1️⃣ Add current user's message
                Message myMessage = new Message(currentUser.getUserId(), messageText, System.currentTimeMillis());
                chatMessages.add(myMessage);
                chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                recyclerView.scrollToPosition(chatMessages.size() - 1);
                editTextMessage.setText("");

                /*// 2️⃣ Only Charlie replies automatically once
                if (friendUser.getName().equalsIgnoreCase("Charlie") && !charlieHasReplied.get()) {
                    charlieHasReplied.set(true); // mark that Charlie started replying
                    String[] replies = {"See you there"};
                    sendCharlieRepliesSequentially(replies, 0);
                }*/

                generateFakeReply(messageText);
            }
        });
    }
    private void sendCharlieRepliesSequentially(String[] replies, int index) {
        if (index >= replies.length) return; // stop when all replies sent

        handler.postDelayed(() -> {
            Message reply = new Message(friendUser.getUserId(), replies[index], System.currentTimeMillis());
            chatMessages.add(reply);
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
            recyclerView.scrollToPosition(chatMessages.size() - 1);

            // Call the next reply
            sendCharlieRepliesSequentially(replies, index + 1);
        }, 2000); // 2-second delay for each message
    }

    private void generateFakeReply(String userMessage) {
        // Wait for a couple of seconds to make it feel real
        handler.postDelayed(() -> {
            String replyText;
            String lowerCaseMessage = userMessage.toLowerCase();

            // Simple keyword-based logic for the reply
            if (lowerCaseMessage.contains("available")) {
                replyText = "Yes, it's still available! Are you interested?";
            } else if (lowerCaseMessage.contains("meet") || lowerCaseMessage.contains("location")) {
                replyText = "I can meet on campus near the library. Does that work for you?";
            } else if (lowerCaseMessage.contains("price") || lowerCaseMessage.contains("negotiate")) {
                replyText = "The price is pretty firm, but I can do $2 off since you're a student.";
            } else if (lowerCaseMessage.contains("hi") || lowerCaseMessage.contains("hello")) {
                replyText = "Hello! Thanks for your interest in the " + product.getName() + ".";
            } else {
                replyText = "Got it. Let me check and get back to you shortly."; // A generic default reply
            }

            // Create and display the fake reply message
            Message replyMessage = new Message(friendUser.getUserId(), replyText, System.currentTimeMillis());
            chatMessages.add(replyMessage);
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
            recyclerView.scrollToPosition(chatMessages.size() - 1);

        }, 1500); // 1.5-second delay
    }
}
