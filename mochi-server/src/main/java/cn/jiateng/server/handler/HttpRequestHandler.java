package cn.jiateng.server.handler;

import cn.jiateng.server.common.HttpResponseBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.log4j.Logger;


public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    private static final String WS_URL = "/ws";

    final static Logger logger = Logger.getLogger(HttpRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // if its websocket, deliver it to the next handler
        if (WS_URL.equalsIgnoreCase(request.uri())) {
            ctx.fireChannelRead(request.retain());
        } else {
            FullHttpResponse response = new HttpResponseBuilder().setData("Hello Mochi~").build();
            ctx.writeAndFlush(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("exception in handling http request: " + cause.getCause().toString());
        FullHttpResponse response = new HttpResponseBuilder().setResponseStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR).build();
        ctx.writeAndFlush(response);
        ctx.channel().close();
    }
}
