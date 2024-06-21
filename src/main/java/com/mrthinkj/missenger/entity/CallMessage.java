package com.mrthinkj.missenger.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CallMessage {
    private String sender;
    private String receiver;
    private String status;
}
