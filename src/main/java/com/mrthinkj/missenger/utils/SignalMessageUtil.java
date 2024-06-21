package com.mrthinkj.missenger.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrthinkj.missenger.entity.SignalMessage;

public class SignalMessageUtil {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static SignalMessage getObject(String message) throws JsonProcessingException {
        return mapper.readValue(message, SignalMessage.class);
    }

    public static String getString(SignalMessage message) throws JsonProcessingException {
        return mapper.writeValueAsString(message);
    }
}
