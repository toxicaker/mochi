package cn.jiateng.server.handler;

import cn.jiateng.server.common.*;
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

import java.util.Map;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = Logger.getLogger(WebSocketServerHandler.class);

    private WebSocketServerHandshaker handshaker;

    private SessionManager sessionManager;

    private Gson gson;

    private String wsUrl;


    public WebSocketServerHandler(SessionManager sessionManager, Gson gson) {
        this.sessionManager = sessionManager;
        this.gson = gson;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws ServiceException {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketRequest(ctx, (WebSocketFrame) msg);
        } else {
            logger.error("wrong request type, client = " + ctx.channel().remoteAddress().toString());
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws ServiceException {
        // handle request
        final String url = req.uri();
        logger.info("recevied http request " + url + " from client " + ctx.channel().remoteAddress().toString());
        String[] paths = url.split("\\?");
        if (!"/mochi/ws".equals(paths[0])) {
            throw new ServiceException("wrong websocket request, wrong url");
        }
        if (!paths[1].startsWith("userId")) {
            throw new ServiceException("wrong websocket request, wrong url. userId is required");
        }

        // establish websocket
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                wsUrl, null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory
                    .sendUnsupportedVersionResponse(ctx.channel());
        } else {
            ChannelFuture cf = handshaker.handshake(ctx.channel(), req);
            cf.addListener((ChannelFutureListener) future -> {
                logger.info("established websocket channel for client " + ctx.channel().remoteAddress().toString());
                Map<String, Object> params = UrlParser.getParameters(url);
                String userId = (String) params.get("userId");
                sessionManager.addSession(new Session(userId, ctx.channel()));
                logger.info("user " + userId + " now is online");
            });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Session session = sessionManager.removeSessionBySessionId(ctx.channel().id().asLongText());
        if (session != null) {
            logger.info("user " + session.userId + " now is offline");
        }
    }

    private void handleWebSocketRequest(ChannelHandlerContext ctx, WebSocketFrame frame) {
        logger.info("recevied websocket request from client " + ctx.channel().remoteAddress().toString());
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

        String request = ((TextWebSocketFrame) frame).text();
        ctx.channel().write(new TextWebSocketFrame("received websocket request: " + request));
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
