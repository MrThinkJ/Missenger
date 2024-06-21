package com.mrthinkj.missenger.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mrthinkj.missenger.entity.CallMessage;
import com.mrthinkj.missenger.entity.SignalMessage;
import com.mrthinkj.missenger.service.UserService;
import com.mrthinkj.missenger.utils.SignalMessageUtil;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class CallController {
    SimpMessagingTemplate messagingTemplate;
    UserService userService;

    @MessageMapping("/call")
    public void sendMessage(@Payload CallMessage callMessage){
        boolean isOnline = userService.isUserOnline(callMessage.getReceiver());
        if (!isOnline){
            callMessage.setStatus("deny");
            messagingTemplate.convertAndSendToUser(callMessage.getSender(), "/queue/callRequest", callMessage);
            return;
        }
        messagingTemplate.convertAndSendToUser(callMessage.getReceiver(), "/queue/callRequest", callMessage);
    }

    @MessageMapping("/callConnect")
    public void sendCallMessage(@Payload String message) throws JsonProcessingException {
        SignalMessage signalMessage = SignalMessageUtil.getObject(message);
        String receiver = signalMessage.getReceiver();
        messagingTemplate.convertAndSendToUser(receiver, "/queue/call", message);
    }
}
