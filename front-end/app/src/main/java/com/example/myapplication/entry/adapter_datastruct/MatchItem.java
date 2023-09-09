package com.example.myapplication.entry.adapter_datastruct;

public class MatchItem {
    private String senderUsername;
    private String receiverUsername;
    private int imageResource;
    private String textViewText;

    public MatchItem(int imageResource, String textViewText, String senderUsername, String receiverUsername) {
        this.imageResource = imageResource;
        this.textViewText = textViewText;
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getTextViewText() {
        return textViewText;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }
}

