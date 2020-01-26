package cn.jiateng.server;

import cn.jiateng.server.common.Config;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class MochiMsgServer {

    final static Logger logger = Logger.getLogger(MochiMsgServer.class);

    final static JedisPool pool = new JedisPool(new JedisPoolConfig(), Config.read("redis.host"));

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(Config.read("server.port"));
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).
                    channel(NioServerSocketChannel.class).
                    option(ChannelOption.SO_BACKLOG, 128).
                    childOption(ChannelOption.SO_KEEPALIVE, true).
                    childHandler(new MyChannelInitializer());
            ChannelFuture cf = bootstrap.bind(port).sync();
            logger.info("Mochi message server started at localhost:" + port);
            cf.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
