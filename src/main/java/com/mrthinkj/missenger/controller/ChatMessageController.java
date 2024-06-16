package com.mrthinkj.missenger.controller;

import com.mrthinkj.missenger.entity.ChatMessage;
import com.mrthinkj.missenger.service.ChatMessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@AllArgsConstructor
public class ChatMessageController {
    ChatMessageService chatMessageService;
    SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void sendMessage(@Payload ChatMessage message){
        chatMessageService.saveChatMessage(message);
        messagingTemplate.convertAndSendToUser(message.getRecipientId(), "/queue/messages",
                message);
    }

    @GetMapping("/message/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findMessageBySenderIdAndRecipientId(
            @PathVariable String senderId,
            @PathVariable String recipientId
    ){
        return ResponseEntity.ok(chatMessageService.findChatMessage(senderId, recipientId));
    }
}
