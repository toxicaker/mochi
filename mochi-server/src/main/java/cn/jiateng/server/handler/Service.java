package cn.jiateng.server.handler;

import cn.jiateng.api.common.MyConst;
import cn.jiateng.server.common.ServiceException;
import cn.jiateng.server.common.Session;
import cn.jiateng.server.common.SessionManager;
import cn.jiateng.server.common.WSMsg;
import cn.jiateng.server.utils.RedisUtil;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class Service {

    private SessionManager sessionManager;

    private Gson gson;

    private static final Logger logger = Logger.getLogger(Service.class);

    public Service(SessionManager sessionManager, Gson gson) {
        this.sessionManager = sessionManager;
        this.gson = gson;
    }

    public void login(String userId, ChannelHandlerContext ctx) {
        if (!RedisUtil.jedis.exists(MyConst.redisKeySession(userId))) {
            logger.info("user " + userId + " does not have session! login failed");
            ctx.channel().close();
        } else {
            sessionManager.addSession(new Session(userId, ctx.channel()));
            RedisUtil.jedis.hset(MyConst.redisKeySession(userId), "sessionId", ctx.channel().id().asLongText());
        }
    }

    public void logout(ChannelHandlerContext ctx) {
        Session session = sessionManager.removeSessionBySessionId(ctx.channel().id().asLongText());
        if (session != null) {
            logger.info("user " + session.userId + " now is offline");
            RedisUtil.jedis.del(MyConst.redisKeySession(session.userId));
        }
    }

    public void privateMessage(WSMsg msg, ChannelHandlerContext ctx) throws ServiceException {
        if (msg.getType() != WSMsg.MsgType.PRIVATE) {
            throw new ServiceException("wrong message type. it should be private chat");
        }
        String targetUserId = msg.getTargetId();
        Session session = sessionManager.getSession(targetUserId);
        // Todo: target session is offline, save message to redis
        if (session == null) {

        } else {
            String message = gson.toJson(msg);
            sendMessage(session.channel, message, 0);
        }
    }

    public void groupMessage(WSMsg msg, ChannelHandlerContext ctx) throws ServiceException {
        if (msg.getType() != WSMsg.MsgType.GROUP) {
            throw new ServiceException("wrong message type. it should be group chat");
        }
    }

    private void sendMessage(Channel targetChannel, String message, int times) {
        if (times < 3) {
            ChannelFuture cf = targetChannel.writeAndFlush(new TextWebSocketFrame(message));
            cf.addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    logger.warn("failed to send message to " + targetChannel.id().asShortText() + ". retrying...");
                    sendMessage(targetChannel, message, times + 1);
                } else {
                    logger.debug("sent message to " + targetChannel.id().asShortText() + ". content: " + message);
                }
            });
        } else {
            // Todo: save message to redis
        }
    }
}
