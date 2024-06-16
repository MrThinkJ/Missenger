package com.mrthinkj.missenger.service.impl;

import com.mrthinkj.missenger.entity.ChatMessage;
import com.mrthinkj.missenger.repository.ChatMessageRepository;
import com.mrthinkj.missenger.service.ChatMessageService;
import com.mrthinkj.missenger.service.ChatRoomService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    ChatRoomService chatRoomService;
    ChatMessageRepository chatMessageRepository;
    @Override
    public ChatMessage saveChatMessage(ChatMessage chatMessage) {
        String user_1 = chatMessage.getSenderId();
        String user_2 = chatMessage.getRecipientId();
        var chatId = chatRoomService.getChatRoomId(user_1, user_2, true)
                .orElseThrow(()-> new RuntimeException("Can not find or create chatroom for this two user"));
        chatMessage.setChatId(chatId);
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    @Override
    public List<ChatMessage> findChatMessage(String user_1, String user_2) {
        var chatId = chatRoomService.getChatRoomId(user_1, user_2, false);
        return chatId.map(chatMessageRepository::findByChatId).orElse(new ArrayList<>());
    }
}
