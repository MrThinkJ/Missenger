package com.mrthinkj.missenger.service.impl;

import com.mrthinkj.missenger.entity.ChatRoom;
import com.mrthinkj.missenger.repository.ChatRoomRepository;
import com.mrthinkj.missenger.service.ChatRoomService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    ChatRoomRepository chatRoomRepository;
    @Override
    public Optional<String> getChatRoomId(String user_1, String user_2, boolean createNewRoomIfNotExist) {
        return chatRoomRepository.findByUser1AndUser2OrUser2AndUser1(user_1, user_2, user_1, user_2)
                .map(ChatRoom::getChatId)
                .or(()->{
                    if (createNewRoomIfNotExist){
                        var chatId = createNewChatRoom(user_1, user_2);
                        return Optional.of(chatId);
                    }
                    return Optional.empty();
                });
    }

    private String createNewChatRoom(String user_1, String user_2) {
        var chatId = String.format("%s_%s", user_1, user_2);
        ChatRoom chatRoom = ChatRoom.builder()
                .chatId(chatId)
                .user1(user_1)
                .user2(user_2)
                .build();
        chatRoomRepository.save(chatRoom);
        return chatId;
    }


}
