package com.mrthinkj.missenger.service.impl;

import com.mrthinkj.missenger.entity.User;
import com.mrthinkj.missenger.payload.Status;
import com.mrthinkj.missenger.payload.UserDTO;
import com.mrthinkj.missenger.repository.UserRepository;
import com.mrthinkj.missenger.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    UserRepository userRepository;
    @Override
    public void register(User registerUser) {
        User user = userRepository.findByNickname(registerUser.getNickname());
        if (user != null)
            throw new RuntimeException(String.format("User with nickname %s already exist", registerUser.getNickname()));
        registerUser.setStatus(Status.OFFLINE);
        userRepository.save(registerUser);
    }

    @Override
    public void login(UserDTO userDTO) {
        User user = userRepository.findByNickname(userDTO.getNickname());
        if (user == null)
            throw new RuntimeException(String.format("User with nickname %s does not exist", userDTO.getNickname()));
        if (!user.getPassword().equals(userDTO.getPassword()))
            throw new RuntimeException("Password incorrect");
    }

    @Override
    public void logout(UserDTO userDTO) {
        User user = userRepository.findByNickname(userDTO.getNickname());
        if (user == null)
            throw new RuntimeException(String.format("User with nickname %s does not exist", userDTO.getNickname()));
        if (!user.getPassword().equals(userDTO.getPassword()))
            throw new RuntimeException("Password incorrect");
        user.setStatus(Status.OFFLINE);
        userRepository.save(user);
    }
}
