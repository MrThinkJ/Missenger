package com.mrthinkj.missenger.service;

import com.mrthinkj.missenger.entity.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    ChatMessage saveChatMessage(ChatMessage chatMessage);
    List<ChatMessage> findChatMessage(String user_1, String user_2);
}
