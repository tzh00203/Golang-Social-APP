package com.example.myapplication.entry.adapter_datastruct;

public class ChatListItem {
    private String chatPartner;   //聊天对象
    private int iconResource;

    public ChatListItem(String username, String chatPartner, int iconResource) {
        this.chatPartner = chatPartner;
        this.iconResource = iconResource;
    }

    public String getChatPartner() {
        return chatPartner;
    }

    public int getIconResource() {
        return iconResource;
    }
}
