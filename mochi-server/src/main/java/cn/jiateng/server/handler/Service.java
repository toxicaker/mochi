package cn.jiateng.server.handler;

import cn.jiateng.server.common.ServiceException;
import cn.jiateng.server.common.Session;
import cn.jiateng.server.common.SessionManager;
import cn.jiateng.server.protocal.WSMsg;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.log4j.Logger;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Service {

    private SessionManager sessionManager;

    private Gson gson;

    private static final Logger logger = Logger.getLogger(Service.class);

    public Service(SessionManager sessionManager, Gson gson) {
        this.sessionManager = sessionManager;
        this.gson = gson;
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
        ctx.channel().eventLoop().schedule(() -> {
            try {
                List<String> userIds = getUserIdByGroupId(msg.getTargetId());
                for (String userId : userIds) {
                    Session session = sessionManager.getSession(userId);
                    if (session == null) {
                        // Todo offline message
                    } else {
                        String message = gson.toJson(msg);
                        sendMessage(session.channel, message, 0);
                    }
                }
            } catch (Exception e) {
                logger.error("failed to handle group message " + msg, e);
            }
        }, 0, TimeUnit.MILLISECONDS);

    }

    private List<String> getUserIdByGroupId(String groupId) throws ServiceException {

        try {

        } catch (Exception e) {
            throw new ServiceException("failed to get group " + groupId + " member");
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
