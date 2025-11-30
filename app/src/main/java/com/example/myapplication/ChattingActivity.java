package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        if (currentUserId == null) currentUserId = "user1";
        if (friendUserId == null) friendUserId = "user2";
        if (friendUserName == null) friendUserName = "Alice";

        currentUser = new User(currentUserId, "Me", null);
        friendUser = new User(friendUserId, friendUserName, null);

        // Set header to the name of the user
        headerText.setText(friendUser.getName());

        TextView textUserListingName = findViewById(R.id.textUserListingName);
        TextView textListingPrice = findViewById(R.id.textListingPrice);

        if (friendUser.getName().equalsIgnoreCase("Alice")) {
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

                // 2️⃣ Only Charlie replies automatically once
                if (friendUser.getName().equalsIgnoreCase("Charlie") && !charlieHasReplied.get()) {
                    charlieHasReplied.set(true); // mark that Charlie started replying
                    String[] replies = {"See you there"};
                    sendCharlieRepliesSequentially(replies, 0);
                }
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
}
