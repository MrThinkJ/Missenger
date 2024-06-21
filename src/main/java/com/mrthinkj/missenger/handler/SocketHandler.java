package com.mrthinkj.missenger.handler;

import com.mrthinkj.missenger.entity.SignalMessage;
import com.mrthinkj.missenger.utils.SignalMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

public class SocketHandler extends TextWebSocketHandler {
    private static final String INIT = "init";
    private static final String LOGOUT = "logout";
    Map<String, WebSocketSession> connectedSession = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(SocketHandler.class);
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        LOG.info(String.format("[%s] Connection established: %s", session.getId(), session.getId()));
        SignalMessage signalMessage = SignalMessage.builder()
                .sender(session.getId())
                .type(INIT)
                .build();
        try{
            session.sendMessage(new TextMessage(SignalMessageUtil.getString(signalMessage)));
        } catch (Exception e){
            LOG.error(e.getMessage());
        }
        connectedSession.put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    }
}
