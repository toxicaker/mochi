package cn.jiateng.server;

import cn.jiateng.server.common.SessionManager;
import cn.jiateng.server.utils.RedisUtil;
import com.google.gson.Gson;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.apache.log4j.Logger;


public class MochiMsgServer {

    final static Logger logger = Logger.getLogger(MochiMsgServer.class);

    final static Gson gson = new Gson();

    final static SessionManager sessionManager = new SessionManager(new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE));

    public static void main(String[] args) throws Exception {
        int port = 12306;
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).
                    channel(NioServerSocketChannel.class).
                    option(ChannelOption.SO_BACKLOG, 128).
                    childOption(ChannelOption.SO_KEEPALIVE, true).
                    childHandler(new MyChannelInitializer(sessionManager, gson));
            ChannelFuture cf = bootstrap.bind(port).sync();
            logger.info("Mochi message server started at localhost:" + port);
            cf.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            RedisUtil.close();
        }
    }

}
