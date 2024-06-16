package com.mrthinkj.missenger.controller;

import com.mrthinkj.missenger.entity.User;
import com.mrthinkj.missenger.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class UserController {
    UserService userService;

    @MessageMapping("/user.addUser")
    @SendTo("/topic/public")
    public User addUser(@Payload User user){
        userService.saveUser(user);
        return user;
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/topic/public")
    public User disconnectUser(@Payload User user){
        userService.disconnectUser(user);
        return user;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllConnectedUser(){
        return ResponseEntity.ok(userService.getAllConnectedUser());
    }
}
