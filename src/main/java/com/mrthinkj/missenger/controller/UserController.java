package com.mrthinkj.missenger.controller;

import com.mrthinkj.missenger.entity.User;
import com.mrthinkj.missenger.payload.UserDTO;
import com.mrthinkj.missenger.service.AuthService;
import com.mrthinkj.missenger.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    UserService userService;
    AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User registerUser){
        authService.register(registerUser);
        return new ResponseEntity<>(registerUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public UserDTO login(@RequestBody UserDTO user){
        authService.login(user);
        return user;
    }

    @MessageMapping("/user.addUser")
    @SendTo("/topic/public")
    public User addUser(@Payload User user){
        userService.activeUser(user);
        return user;
    }

    @MessageMapping("/user.logout")
    @SendTo("/topic/public")
    public UserDTO disconnectUser(@Payload UserDTO user){
        authService.logout(user);
        return user;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllConnectedUser(){
        return ResponseEntity.ok(userService.getAllConnectedUser());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e){
        e.printStackTrace();
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
