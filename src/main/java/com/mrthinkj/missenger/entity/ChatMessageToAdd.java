package com.mrthinkj.missenger.entity;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageToAdd {
    private String chatId;
    private String content;
    private String senderId;
    private String recipientId;
    private MultipartFile image;
    private Date timestamp;
}
