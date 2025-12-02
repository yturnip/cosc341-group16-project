package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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
    private ImageView searchBackButton;

    private User currentUser;
    private User friendUser;
    private List<Message> chatMessages;
    private Handler handler = new Handler(); // Handler for delayed reply
    private Product product;

    private String conversationId;
    private Conversation conversation;
    private Button buttonLeaveReview;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        // --- 1. Initialize UI Views ---
        recyclerView = findViewById(R.id.favoritesRecyclerView);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        searchBackButton = findViewById(R.id.searchBackButton3);
        TextView headerText = findViewById(R.id.favListingText);
        TextView textUserListingName = findViewById(R.id.textUserListingName);
        TextView textListingPrice = findViewById(R.id.textListingPrice);
        ImageView productImageView = findViewById(R.id.productImageView);
        Button buttonViewListings = findViewById(R.id.buttonViewListings);
        buttonLeaveReview = findViewById(R.id.buttonLeaveReview);

        // --- 2. Load Core Data (Users and Product) ---
        // Always get the single, true current user from the repository.
        currentUser = ProductRepository.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Can't continue without a current user
            finish();
            return;
        }

        conversationId = getIntent().getStringExtra("conversationId");
        if (conversationId == null) {
            finish(); // Can't work without a conversation ID
            return;
        }

        conversation = ChatManager.getInstance().getConversationById(conversationId);
        if (conversation == null) {
            // No conversation found with that id — we can't chat
            finish();
            return;
        }

        product = ProductRepository.getInstance().getProductById(conversation.getProductId());

        // Determine the friendUser (the other party in the conversation)
        String buyerId = conversation.getBuyerId();
        String sellerId = conversation.getSellerId();

        if (currentUser.getUserId().equals(buyerId)) {
            friendUser = ProductRepository.getInstance().getUserById(sellerId);
        } else if (currentUser.getUserId().equals(sellerId)) {
            friendUser = ProductRepository.getInstance().getUserById(buyerId);
        } else {
            // currentUser is not part of this conversation; choose seller as friend by default
            friendUser = ProductRepository.getInstance().getUserById(sellerId);
        }

        // --- 3. Setup UI based on Data ---
        headerText.setText(friendUser != null && friendUser.getName() != null ? friendUser.getName() : "Chat");

        if (product != null) {
            // A product is associated with this chat, so display its info in the banner.
            textUserListingName.setText(product.getName());
            textListingPrice.setText(String.format("$%.2f", product.getPrice()));

            Glide.with(this)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(productImageView);

            if ("Sold".equalsIgnoreCase(product.getStatus())) {
                // --- Product is SOLD ---
                // 1. Disable chat input
                editTextMessage.setEnabled(false);
                editTextMessage.setHint("This item has been sold.");
                buttonSend.setEnabled(false);
                buttonSend.setAlpha(0.5f); // Visually indicate it's disabled

                // 2. Swap the buttons
                buttonViewListings.setVisibility(View.GONE);
                buttonLeaveReview.setVisibility(View.VISIBLE);

            } else {
                // --- Product is Available or Pending ---
                // 1. Ensure chat is enabled
                editTextMessage.setEnabled(true);
                editTextMessage.setHint("Type a message");
                buttonSend.setEnabled(true);
                buttonSend.setAlpha(1.0f);

                // 2. Show the "View Item" button
                buttonViewListings.setVisibility(View.VISIBLE);
                buttonLeaveReview.setVisibility(View.GONE);
            }

        } else {
                // No product context for this chat. Show a "deal done" or disabled state.
                editTextMessage.setEnabled(false);
                buttonSend.setEnabled(false);
                buttonViewListings.setVisibility(View.GONE);
                buttonLeaveReview.setVisibility(View.GONE);
                textUserListingName.setText("General Chat");
                textListingPrice.setText("");
                // You could set a generic "deal done" icon here if you have one.
                productImageView.setImageResource(R.drawable.ic_launcher_background);

                buttonViewListings.setEnabled(false);
                buttonViewListings.setText("No Product");
        }

        // --- 4. Load Chat History ---
        chatMessages = conversation.getMessages();
        if (chatMessages == null) {
            chatMessages = new ArrayList<>();
        }

        // --- 5. Setup Interactive Components ---
        // The RecyclerView and Adapter
        chatAdapter = new ChattingAdapter(chatMessages, currentUser.getUserId());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);
        if (!chatMessages.isEmpty()) {
            recyclerView.scrollToPosition(chatMessages.size() - 1);
        }

        // Click Listeners
        searchBackButton.setOnClickListener(v -> finish());

        buttonViewListings.setOnClickListener(v -> {
            // This single listener now correctly handles the "View" button.
            if (product != null) {
                Intent intent = new Intent(ChattingActivity.this, BuyItemActivity.class);
                intent.putExtra("product", product);
                startActivity(intent);
            }
        });

        // TO BE ADDED: Review button listener
        buttonLeaveReview.setOnClickListener(v -> {
            Intent intent = new Intent(ChattingActivity.this, ReviewActivity.class);
            // Pass the seller's ID so the review activity knows who is being reviewed
            intent.putExtra("sellerId", friendUser.getUserId());
            startActivity(intent);
        });

        buttonSend.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                Message myMessage = new Message(currentUser.getUserId(), messageText, System.currentTimeMillis());
                // Save the message to the central manager
                ChatManager.getInstance().addMessageToConversation(conversationId, myMessage);

                chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                recyclerView.scrollToPosition(chatMessages.size() - 1);
                editTextMessage.setText("");

                // Generate a fake reply
                generateFakeReply(messageText);
            }
        });
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
            ChatManager.getInstance().addMessageToConversation(conversationId, replyMessage);
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
            recyclerView.scrollToPosition(chatMessages.size() - 1);

        }, 1500); // 1.5-second delay
    }
}
