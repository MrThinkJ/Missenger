package com.mrthinkj.missenger.service.impl;

import com.mrthinkj.missenger.entity.User;
import com.mrthinkj.missenger.payload.Status;
import com.mrthinkj.missenger.repository.UserRepository;
import com.mrthinkj.missenger.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    @Override
    public void saveUser(User user) {
        user.setStatus(Status.ONLINE);
        userRepository.save(user);
    }

    @Override
    public void disconnectUser(User user) {
        var optionalUser = userRepository.findById(user.getId())
                .orElse(null);
        if (optionalUser != null){
            optionalUser.setStatus(Status.OFFLINE);
            userRepository.save(optionalUser);
        }
    }

    @Override
    public List<User> getAllConnectedUser() {
        return userRepository.findAllByStatus(Status.ONLINE);
    }
}
