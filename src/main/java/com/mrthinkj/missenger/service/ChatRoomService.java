package com.mrthinkj.missenger.service;

import java.util.Optional;

public interface ChatRoomService {
    Optional<String> getChatRoomId(String user_1, String user_2, boolean createNewRoomIfNotExist);
}
