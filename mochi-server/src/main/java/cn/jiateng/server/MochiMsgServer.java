package cn.jiateng.server;

import cn.jiateng.server.common.SessionManager;
import cn.jiateng.server.utils.PropReader;
import cn.jiateng.server.utils.RedisUtil;
import cn.jiateng.common.ServiceManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.apache.log4j.Logger;

import java.net.InetAddress;


public class MochiMsgServer {

    final static Logger logger = Logger.getLogger(MochiMsgServer.class);

    final static SessionManager sessionManager = new SessionManager(new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE));

    public static String serviceName;

    public static String address;

    public static String env;

    public static void main(String[] args) throws Exception {
        if (args.length >= 1 && args[1].equals("prod")) {
            env = "prod";
        } else {
            env = "dev";
        }
        int port = Integer.parseInt(PropReader.read("server.port"));
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        String ip = InetAddress.getLocalHost().getHostAddress();
        serviceName = ServiceManager.registerService("/mochi-server", ip, port + "");
        address = ip + ":" + port;
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).
                    channel(NioServerSocketChannel.class).
                    option(ChannelOption.SO_BACKLOG, 128).
                    childOption(ChannelOption.SO_KEEPALIVE, true).
                    childHandler(new MyChannelInitializer(sessionManager));
            ChannelFuture cf = bootstrap.bind(port).sync();
            logger.info("Mochi message server started at localhost:" + port);
            cf.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            RedisUtil.close();
            sessionManager.close();
        }
    }

}
