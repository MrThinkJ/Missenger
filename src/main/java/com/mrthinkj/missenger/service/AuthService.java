package com.mrthinkj.missenger.service;

import com.mrthinkj.missenger.entity.User;
import com.mrthinkj.missenger.payload.UserDTO;

public interface AuthService {
    void register(User registerUser);
    void login(UserDTO userDTO);
    void logout(UserDTO userDTO);
}
