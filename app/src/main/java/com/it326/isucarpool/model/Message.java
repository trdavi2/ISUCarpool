package com.it326.isucarpool.model;

/**
 * Created by Cedomir Spalevic on 11/30/2016.
 */

public class Message
{
    private String messageText;
    private String messageUser;
    private long messageTime;

    public Message(String messageText, long messageTime, String messageUser) {
        this.setMessageText(messageText);
        this.setMessageTime(messageTime);
        this.setMessageUser(messageUser);
    }

    public Message()
    {
    }

    public String getMessageText() { return messageText; }

    public void setMessageText(String messageText) { this.messageText = messageText; }

    public String getMessageUser() { return messageUser; }

    public void setMessageUser(String messageUser) { this.messageUser = messageUser; }

    public long getMessageTime() { return messageTime; }

    public void setMessageTime(long messageTime) { this.messageTime = messageTime; }
}