package cn.jiateng.server;

import cn.jiateng.server.common.SessionManager;
import cn.jiateng.server.handler.WebSocketServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;



public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {


    private SessionManager sessionManager;

    public MyChannelInitializer(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new HttpServerCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(512 * 1024));
        ch.pipeline().addLast(new WebSocketServerHandler(sessionManager));

    }
}
