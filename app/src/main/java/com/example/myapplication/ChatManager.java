package com.example.myapplication;
import com.example.myapplication.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatManager {

    private static ChatManager instance;

    // List of chat users
    private final List<User> chatUsers;

    // Map of userId -> last product in chat
    private final Map<String, Product> lastProductMap;

    private ChatManager() {
        chatUsers = new ArrayList<>();
        lastProductMap = new HashMap<>();
    }

    public static ChatManager getInstance() {
        if (instance == null) {
            instance = new ChatManager();
        }
        return instance;
    }

    public List<User> getChatUsers() {
        return chatUsers;
    }

    public void addUser(User user, Product lastProduct) {
        if (!isUserInChatList(user.getUserId())) {
            chatUsers.add(user);
        }
        // Store the last product associated with this user
        lastProductMap.put(user.getUserId(), lastProduct);
    }

    public boolean isUserInChatList(String userId) {
        for (User u : chatUsers) {
            if (u.getUserId().equals(userId)) return true;
        }
        return false;
    }

    public Product getLastProductForUser(String userId) {
        return lastProductMap.get(userId);
    }
    private final Map<String, List<Message>> userMessagesMap = new HashMap<>();

    // Add a message to a user
    public void addMessageForUser(String userId, Message message) {
        userMessagesMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(message);
    }

    // Get previous messages for a user
    public List<Message> getMessagesForUser(String userId) {
        return userMessagesMap.getOrDefault(userId, new ArrayList<>());
    }
}
