package cn.jiateng.server.handler.serivces.impl;


import cn.jiateng.server.common.Session;
import cn.jiateng.server.common.SessionManager;
import cn.jiateng.server.handler.serivces.MessageService;
import cn.jiateng.server.protocal.Msg;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MessageServiceImpl implements MessageService {

    private SessionManager sessionManager;


    public MessageServiceImpl(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void sendMessage(String fromUserId, String toUserId, String message) {
        Session session = sessionManager.getSession(toUserId);
        // Todo: target session is offline, save message to redis
        if (session == null) {

        } else {
            Msg.Message.Builder builder = Msg.Message.newBuilder();
            builder.setFromId(fromUserId);
            builder.setFromId(toUserId);
            builder.setContent(message);
            builder.setCreateTime(System.currentTimeMillis());
            session.channel.writeAndFlush(new TextWebSocketFrame(Unpooled.wrappedBuffer(builder.build().toByteArray())));
        }
    }

    @Override
    public void sendMessages(String fromUserId, List<String> toUserId, String message) {
        Session session = sessionManager.getSession(fromUserId);
        session.channel.eventLoop().schedule(() -> {
            for (String userId : toUserId) {
                Session sess = sessionManager.getSession(userId);
                if (sess == null) {
                    // Todo offline message
                } else {
                    Msg.Message.Builder builder = Msg.Message.newBuilder();
                    builder.setFromId(fromUserId);
                    builder.setFromId(userId);
                    builder.setContent(message);
                    builder.setCreateTime(System.currentTimeMillis());
                    session.channel.writeAndFlush(new TextWebSocketFrame(Unpooled.wrappedBuffer(builder.build().toByteArray())));
                }
            }
        }, 0, TimeUnit.MILLISECONDS);
    }
}
