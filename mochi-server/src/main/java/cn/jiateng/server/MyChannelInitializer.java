package cn.jiateng.server;

import cn.jiateng.server.common.SessionManager;
import cn.jiateng.server.handler.WebSocketServerHandler;
import com.google.gson.Gson;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;



public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {


    private SessionManager sessionManager;

    private Gson gson;

    public MyChannelInitializer(SessionManager sessionManager, Gson gson) {
        this.sessionManager = sessionManager;
        this.gson = gson;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new HttpServerCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(512 * 1024));
        ch.pipeline().addLast(new WebSocketServerHandler(sessionManager, gson));

    }
}
