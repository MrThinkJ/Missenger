package com.mrthinkj.missenger.controller;

import com.mrthinkj.missenger.entity.ChatMessage;
import com.mrthinkj.missenger.entity.ChatMessageToAdd;
import com.mrthinkj.missenger.service.ChatMessageService;
import com.mrthinkj.missenger.service.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
public class ChatMessageController {
    ChatMessageService chatMessageService;
    StorageService storageService;
    SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void sendMessageToQueue(@Payload ChatMessage message){
        messagingTemplate.convertAndSendToUser(message.getRecipientId(), "/queue/messages",
                message);
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatMessage> sendMessage(@ModelAttribute ChatMessageToAdd chatMessageToAdd){
        ChatMessage chatMessage = chatMessageService.saveChatMessage(chatMessageToAdd);
        messagingTemplate.convertAndSendToUser(chatMessage.getRecipientId(), "/queue/messages",
                chatMessage);
        return new ResponseEntity<>(chatMessage, HttpStatus.CREATED);
    }

    @GetMapping("/message/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findMessageBySenderIdAndRecipientId(
            @PathVariable String senderId,
            @PathVariable String recipientId
    ){
        return ResponseEntity.ok(chatMessageService.findChatMessage(senderId, recipientId));
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename){
        Resource image = storageService.get(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(image);
    }
}
