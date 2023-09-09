package com.example.myapplication.chat.data_struct;

public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;

    private String content;
    private int type;
    private String sender;
    private int senderIconResId;   //发送者头像资源 ID 字段
    private int receiverIconResId; //接收者头像资源 ID 字段

    public Msg(String content, int type, String sender, int senderIconResId, int receiverIconResId) {
        this.content = content;
        this.type = type;
        this.sender = sender;
        this.senderIconResId = senderIconResId;
        this.receiverIconResId = receiverIconResId;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public int getSenderIconResId() {
        return senderIconResId;
    }

    public int getReceiverIconResId() {
        return receiverIconResId;
    }
}
