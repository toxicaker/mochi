package cn.jiateng.server.common;


import cn.jiateng.server.MochiMsgServer;
import cn.jiateng.common.ServiceManager;
import io.netty.channel.Channel;

public class Session {

    public String userId;

    public String sessionId;

    public Channel channel;

    public String server;

    private String serverAddress;

    public Session(String userId, String sessionId, String server) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.server = server;
    }

    public Session(String userId, Channel channel) {
        this.userId = userId;
        this.sessionId = channel.id().asLongText();
        this.channel = channel;
    }

    public Session(String userId, Channel channel, String server) {
        this.userId = userId;
        this.sessionId = channel.id().asLongText();
        this.channel = channel;
        this.server = server;
    }

    public boolean isCurrentHost() {
        return server.equals(MochiMsgServer.serviceName);
    }

    public String getServerAddress() {
        if(serverAddress == null){
            serverAddress = ServiceManager.getServiceAddress(server);
        }
        return serverAddress;
    }
}
