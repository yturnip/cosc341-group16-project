package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatUserAdapter chatUserAdapter;
    private List<User> users;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Example current user
        currentUser = new User("user1", "Me", null);

        // Example friend users
        users = ChatManager.getInstance().getChatUsers();

        ChatManager.getInstance().addUser(new User("user22", "Alice", null), null);
        ChatManager.getInstance().addUser(new User("user33", "Bob", null), null);
        ChatManager.getInstance().addUser(new User("user44", "Charlie", null), null);


        // Initialize adapter
        chatUserAdapter = new ChatUserAdapter(users, user -> {
            // On user click, go to ChattingActivity
            Intent intent = new Intent(ChatListActivity.this, ChattingActivity.class);
            intent.putExtra("currentUserId", currentUser.getUserId());
            intent.putExtra("friendUserId", user.getUserId());
            intent.putExtra("friendUserName", user.getName());
            startActivity(intent);
        });

        recyclerView.setAdapter(chatUserAdapter);
        setupBottomNavigation();
    }

    private void addNewUser(String userId, String name) {
        User newUser = new User(userId, name, null);
        users.add(newUser);
        chatUserAdapter.notifyItemInserted(users.size() - 1);
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
            }

            return false;
        });
    }
}
