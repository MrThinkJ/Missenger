package com.mrthinkj.missenger.repository;

import com.mrthinkj.missenger.entity.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    Optional<ChatRoom> findByUser1AndUser2OrUser2AndUser1(String senderId_1, String recipientId_1,
                                                String senderId_2, String recipientId_2);
}
