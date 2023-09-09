package com.example.myapplication.entry.adapter_datastruct;

public class MatchListItemNew {
    private String matchPartner;   //匹配对象
    private int iconResource;

    public MatchListItemNew(String username, String matchPartner, int iconResource) {
        this.matchPartner = matchPartner;
        this.iconResource = iconResource;
    }

    public String getMatchPartner() {
        return matchPartner;
    }

    public int getIconResource() {
        return iconResource;
    }


    private int imageResource;   //用于存储图像资源的ID
    private String textViewText; //用于存储TextView的文本

    public MatchListItemNew(int imageResource, String textViewText, String matchPartner) {
        this.matchPartner = matchPartner;
        this.imageResource = imageResource;
        this.textViewText = textViewText;
    }



    public int getImageResource() {
        return imageResource;
    }

    public String getTextViewText() {
        return textViewText;
    }
}
