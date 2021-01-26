package com.example.whatsappclone.Models;

public class MessageModel {

    String uId, userMessage, messageId;
    Long textTime;

    public MessageModel(String uId, String userMessage, Long textTime) {
        this.uId = uId;
        this.userMessage = userMessage;
        this.textTime = textTime;
    }

    public MessageModel(String uId, String userMessage) {
        this.uId = uId;
        this.userMessage = userMessage;
    }

    public MessageModel(){}

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public Long getTextTime() {
        return textTime;
    }

    public void setTextTime(Long textTime) {
        this.textTime = textTime;
    }
}
