package com.mrthinkj.missenger.service;

import com.mrthinkj.missenger.entity.User;

import java.util.List;

public interface UserService {
    void activeUser(User user);
    void unActiveUser(User user);
    List<User> getAllConnectedUser();
    boolean isUserOnline(String nickname);
}
