package com.example.myapplication.entry.adapter;

public class ChatListItem {
    private String username;
    private String chatPartner;   //聊天对象
    private int iconResource;

    public ChatListItem(String username, String chatPartner, int iconResource) {
        this.username = username;
        this.chatPartner = chatPartner;
        this.iconResource = iconResource;
    }

    public String getUsername() {
        return username;
    }

    public String getChatPartner() {
        return chatPartner;
    }

    public int getIconResource() {
        return iconResource;
    }
}
