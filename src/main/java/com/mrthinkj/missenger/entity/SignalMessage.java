package com.mrthinkj.missenger.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignalMessage {
    private String type;
    private String sender;
    private String receiver;
    private Object data;
}
