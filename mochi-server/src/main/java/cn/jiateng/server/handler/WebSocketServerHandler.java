package cn.jiateng.server.handler;

import cn.jiateng.server.common.*;
import cn.jiateng.server.utils.UrlParser;
import com.google.gson.Gson;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.*;
import org.apache.log4j.Logger;

import java.rmi.ServerException;
import java.util.Map;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = Logger.getLogger(WebSocketServerHandler.class);

    private WebSocketServerHandshaker handshaker;

    private Gson gson;

    private Service service;


    public WebSocketServerHandler(SessionManager sessionManager, Gson gson) {
        this.gson = gson;
        this.service = new Service(sessionManager, gson);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws ServiceException, InterruptedException {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketRequest(ctx, (WebSocketFrame) msg);
        } else {
            throw new ServiceException("wrong request type");
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws ServiceException {
        // handle request
        final String url = req.uri();
        String[] paths = url.split("\\?");
        if (!"/mochi/ws".equals(paths[0])) {
            throw new ServiceException("wrong websocket request, wrong url");
        }
        if (!paths[1].startsWith("userId")) {
            throw new ServiceException("wrong websocket request, wrong url. userId is required");
        }

        // establish websocket
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "", null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory
                    .sendUnsupportedVersionResponse(ctx.channel());
        } else {
            ChannelFuture cf = handshaker.handshake(ctx.channel(), req);
            // websocket has established
            cf.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    logger.info("established websocket channel for client " + ctx.channel().remoteAddress().toString());
                    Map<String, Object> params = UrlParser.getParameters(url);
                    String userId = (String) params.get("userId");
                    service.login(userId, ctx);
                } else {
                    throw new ServerException("cannot establish websocket for client " + ctx.channel().remoteAddress().toString());
                }
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        service.logout(ctx);
    }

    private void handleWebSocketRequest(ChannelHandlerContext ctx, WebSocketFrame frame) throws ServiceException, InterruptedException {
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(),
                    (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(
                    new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format(
                    "%s frame types not supported", frame.getClass().getName()));
        }

        String message = ((TextWebSocketFrame) frame).text();
        WSMsg wsMsg;
        try {
            wsMsg = gson.fromJson(message, WSMsg.class);
        } catch (Exception e) {
            throw new ServiceException("wrong message format: " + message);
        }

        //send an ack
        wsMsg.setCreateTime(System.currentTimeMillis());

        ctx.channel().writeAndFlush(new TextWebSocketFrame("success-" + wsMsg.getCreateTime()));

        // message handling
        switch (wsMsg.getType()) {
            case WSMsg.MsgType.PRIVATE:
                service.privateMessage(wsMsg, ctx);
                break;
            case WSMsg.MsgType.GROUP:
                service.groupMessage(wsMsg, ctx);
                break;
            default:
                break;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof ServiceException) {
            logger.warn("bad request from client: " + ctx.channel().remoteAddress().toString(), cause);
            HttpResponseBuilder builder = new HttpResponseBuilder();
            FullHttpResponse resp = builder.setResponseStatus(HttpResponseStatus.BAD_REQUEST).setResponseMsg(cause.getMessage()).build();
            ctx.channel().writeAndFlush(resp);
        } else {
            logger.error("exception in handling request for client: " + ctx.channel().remoteAddress().toString(), cause);
            HttpResponseBuilder builder = new HttpResponseBuilder();
            FullHttpResponse resp = builder.setResponseStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR).setResponseMsg(cause.getMessage()).build();
            ctx.channel().writeAndFlush(resp);
        }
    }
}
