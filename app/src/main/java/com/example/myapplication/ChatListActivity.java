package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    // The adapter is now a ConversationListAdapter
    private ConversationListAdapter conversationAdapter;
    private List<Conversation> conversations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get all conversations from the manager.
        conversations = ChatManager.getInstance().getAllConversations();

        // Initialize the new adapter with the list of conversations.
        conversationAdapter = new ConversationListAdapter(conversations, conversation -> {
            // This is the click listener lambda.
            // When a conversation is clicked, open ChattingActivity.
            Intent intent = new Intent(ChatListActivity.this, ChattingActivity.class);
            // Pass the unique ID of the conversation that was clicked.
            intent.putExtra("conversationId", conversation.getConversationId());
            startActivity(intent);
        });

        // Set the new adapter on the RecyclerView.
        recyclerView.setAdapter(conversationAdapter);
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bnv = findViewById(R.id.bottom_navigation);

        // Set the "Chat" item as the selected one since we are on the Chat screen
        bnv.setSelectedItemId(R.id.nav_chat);

        bnv.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_fav) {
                startActivity(new Intent(getApplicationContext(), FavoriteActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_sell) {
                startActivity(new Intent(getApplicationContext(), SellActivity.class));
                return true;
            } else if (itemId == R.id.nav_chat) {
                // Already on chat, do nothing
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(getApplicationContext(), ProfilePage.class));
                return true;
            }

            return false;
        });
    }
}
