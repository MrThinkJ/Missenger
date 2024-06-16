package com.mrthinkj.missenger.service;

import com.mrthinkj.missenger.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(User user);
    void disconnectUser(User user);
    List<User> getAllConnectedUser();
}
