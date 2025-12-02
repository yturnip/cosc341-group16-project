package com.example.myapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Conversation implements Serializable {
    private final String conversationId;
    private final String buyerId;
    private final String sellerId;
    private final String productId;
    private final List<Message> messages;

    public Conversation(String conversationId, String buyerId, String sellerId, String productId) {
        this.conversationId = conversationId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.productId = productId;
        this.messages = new ArrayList<>();
    }

    // --- Getters ---
    public String getConversationId() { return conversationId; }
    public String getProductId() { return productId; }
    public String getBuyerId() { return buyerId; }
    public String getSellerId() { return sellerId; }
    public List<Message> getMessages() { return messages; }
    public Message getLastMessage() {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }

    // --- Modifier ---
    public void addMessage(Message message) {
        this.messages.add(message);
    }
}
