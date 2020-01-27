package cn.jiateng.server.common;


import io.netty.channel.Channel;

public class Session {

    public String userId;

    public String sessionId;

    public Channel channel;

    public Session(String userId, Channel channel) {
        this.userId = userId;
        this.sessionId = channel.id().asLongText();
        this.channel = channel;
    }
}
