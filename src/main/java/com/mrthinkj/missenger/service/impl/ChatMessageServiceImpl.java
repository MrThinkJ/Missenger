package com.mrthinkj.missenger.service.impl;

import com.mrthinkj.missenger.entity.ChatMessage;
import com.mrthinkj.missenger.entity.ChatMessageToAdd;
import com.mrthinkj.missenger.repository.ChatMessageRepository;
import com.mrthinkj.missenger.service.ChatMessageService;
import com.mrthinkj.missenger.service.ChatRoomService;
import com.mrthinkj.missenger.service.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    ChatRoomService chatRoomService;
    StorageService storageService;
    ChatMessageRepository chatMessageRepository;
    @Override
    public ChatMessage saveChatMessage(ChatMessageToAdd chatMessageToAdd) {
        String user_1 = chatMessageToAdd.getSenderId();
        String user_2 = chatMessageToAdd.getRecipientId();
        var chatId = chatRoomService.getChatRoomId(user_1, user_2, true)
                .orElseThrow(()-> new RuntimeException("Can not find or create chatroom for this two user"));
        ChatMessage chatMessage = ChatMessage.builder()
                .senderId(chatMessageToAdd.getSenderId())
                .recipientId(chatMessageToAdd.getRecipientId())
                .content(chatMessageToAdd.getContent())
                .timestamp(new Date(Long.parseLong(chatMessageToAdd.getTimestamp())))
                .chatId(chatId)
                .build();
        if (chatMessageToAdd.getImage()!= null){
            String imageName = storageService.save(chatMessageToAdd.getImage());
            chatMessage.setImage(imageName);
        }
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    @Override
    public List<ChatMessage> findChatMessage(String user_1, String user_2) {
        var chatId = chatRoomService.getChatRoomId(user_1, user_2, false);
        return chatId.map(chatMessageRepository::findByChatId).orElse(new ArrayList<>());
    }
}
