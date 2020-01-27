package cn.jiateng.server;

import cn.jiateng.server.common.SessionManager;
import cn.jiateng.server.handler.WebSocketServerHandler;
import com.google.gson.Gson;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.ImmediateEventExecutor;


public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        Gson gson = new Gson();
        SessionManager sessionManager = new SessionManager(new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE));
        ch.pipeline().addLast(new HttpServerCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(512 * 1024));
        ch.pipeline().addLast(new WebSocketServerHandler(sessionManager, gson));

    }
}
