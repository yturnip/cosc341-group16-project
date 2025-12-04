package com.example.myapplication;
import com.example.myapplication.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatManager {

    private static ChatManager instance;

    private final Map<String, Conversation> conversations;

    private ChatManager() {
        conversations = new HashMap<>();
        // Load the historical conversations when the manager is first created.
        loadPresetConversations();
    }

    public static synchronized ChatManager getInstance() {
        if (instance == null) {
            instance = new ChatManager();
        }
        return instance;
    }

    // Get or create a conversation
    public Conversation getOrCreateConversation(String buyerId, String sellerId, String productId) {
        String conversationId = buyerId + "_" + sellerId + "_" + productId;
        if (!conversations.containsKey(conversationId)) {
            conversations.put(conversationId, new Conversation(conversationId, buyerId, sellerId, productId));
        }
        return conversations.get(conversationId);
    }

    // Get a conversation by its ID
    public Conversation getConversationById(String conversationId) {
        return conversations.get(conversationId);
    }

    // Get all conversations
    public List<Conversation> getAllConversations() {
        return new ArrayList<>(conversations.values());
    }

    public void addMessageToConversation(String conversationId, Message message) {
        Conversation conversation = conversations.get(conversationId);

        if (conversation == null) {
            // Try to parse the conversationId to create a placeholder conversation
            if (conversationId != null) {
                String[] parts = conversationId.split("_", 3);
                if (parts.length == 3) {
                    String buyerId = parts[0];
                    String sellerId = parts[1];
                    String productId = parts[2];
                    conversation = new Conversation(conversationId, buyerId, sellerId, productId);
                    conversations.put(conversationId, conversation);
                }
            }
        }

        if (conversation != null) {
            conversation.addMessage(message);
        }
    }

    private void loadPresetConversations() {
        // We need the current user's ID to create the conversation ID correctly.
        String currentUserId = ProductRepository.getInstance().getCurrentUser().getUserId();

        // --- Conversation with Alice ---
        String aliceConvoId = currentUserId + "_user_alice_prod_sold_mic";
        Conversation aliceConvo = new Conversation(aliceConvoId, currentUserId, "user_alice", "prod_sold_mic");
        aliceConvo.addMessage(new Message("user_alice", "Hello! How are you?", System.currentTimeMillis() - 60000));
        aliceConvo.addMessage(new Message(currentUserId, "Hi Alice! I'm good, thanks.", System.currentTimeMillis() - 55000));
        aliceConvo.addMessage(new Message("user_alice", "Want to meet tomorrow?", System.currentTimeMillis() - 50000));
        aliceConvo.addMessage(new Message(currentUserId, "I'm down", System.currentTimeMillis() - 45000));
        conversations.put(aliceConvoId, aliceConvo);

        // --- Conversation with Bob ---
        String bobConvoId = currentUserId + "_user_bob_prod_sold_poster";
        Conversation bobConvo = new Conversation(bobConvoId, currentUserId, "user_bob", "prod_sold_poster");
        bobConvo.addMessage(new Message("user_bob", "Hey! Are you still interested in the deal?", System.currentTimeMillis() - 60000));
        bobConvo.addMessage(new Message(currentUserId, "Yes, do you want to meet in Pritchard at 8 am?", System.currentTimeMillis() - 55000));
        bobConvo.addMessage(new Message("user_bob", "Alrighty", System.currentTimeMillis() - 50000));
        conversations.put(bobConvoId, bobConvo);

        // --- Conversation with Charlie ---
        String charlieConvoId = currentUserId + "_user_charlie_prod_sold_lamp";
        Conversation charlieConvo = new Conversation(charlieConvoId, currentUserId, "user_charlie", "prod_sold_lamp");
        charlieConvo.addMessage(new Message("user_charlie", "Hi!", System.currentTimeMillis() - 60000));
        charlieConvo.addMessage(new Message(currentUserId, "Hi Charlie, How are you?", System.currentTimeMillis() - 55000));
        charlieConvo.addMessage(new Message("user_charlie", "Where do you wanna meet?", System.currentTimeMillis() - 50000));
        charlieConvo.addMessage(new Message(currentUserId, "Library, 10 pm?", System.currentTimeMillis() - 45000));
        charlieConvo.addMessage(new Message("user_charlie", "For sure", System.currentTimeMillis() - 40000));
        conversations.put(charlieConvoId, charlieConvo);
    }

}
