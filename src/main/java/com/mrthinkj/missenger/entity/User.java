package com.mrthinkj.missenger.entity;

import com.mrthinkj.missenger.payload.Status;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class User {
    @Id
    private String id;
    private String fullName;
    private String nickname;
    private Status status;
}
