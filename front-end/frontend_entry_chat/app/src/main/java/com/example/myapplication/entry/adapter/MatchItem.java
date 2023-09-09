package com.example.myapplication.entry.adapter;

public class MatchItem {
    private String buttonText;
    private int imageResource; // 用于存储图像资源的ID，你可以根据需要更改数据类型
    private String textViewText; // 用于存储 TextView 的文本

    public MatchItem(String buttonText, int imageResource, String textViewText) {
        this.buttonText = buttonText;
        this.imageResource = imageResource;
        this.textViewText = textViewText;
    }

    public String getButtonText() {
        return buttonText;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getTextViewText() {
        return textViewText;
    }
}
