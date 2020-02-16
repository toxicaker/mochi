package cn.jiateng.server.handler.serivces.impl;

import cn.jiateng.api.common.JsonResp;
import cn.jiateng.api.common.MyConst;
import cn.jiateng.server.MochiMsgServer;
import cn.jiateng.server.common.ServiceException;
import cn.jiateng.server.common.Session;
import cn.jiateng.server.common.SessionManager;
import cn.jiateng.server.handler.serivces.UserService;
import cn.jiateng.server.utils.HttpUtil;
import cn.jiateng.server.utils.RedisUtil;
import cn.jiateng.zookeeper.ServiceManager;
import com.google.gson.Gson;
import com.squareup.okhttp.Response;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {

    private static final Logger logger = Logger.getLogger(UserService.class);

    private SessionManager sessionManager;

    private Gson gson = new Gson();

    public UserServiceImpl(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void login(String userId, ChannelHandlerContext ctx) throws ServiceException {
        if (!RedisUtil.jedis.exists(MyConst.redisKeySession(userId))) {
            ctx.channel().close();
            throw new ServiceException("user " + userId + " does not have session! login failed");
        } else {
            sessionManager.addSession(new Session(userId, ctx.channel(), MochiMsgServer.serviceName));
            logger.info("user " + userId + " now is online");
            RedisUtil.jedis.hset(MyConst.redisKeySession(userId), "sessionId", ctx.channel().id().asLongText());
            RedisUtil.jedis.hset(MyConst.redisKeySession(userId), "msgServer", MochiMsgServer.serviceName);
        }
    }

    @Override
    public void logout(ChannelHandlerContext ctx) {
        Session session = sessionManager.removeSessionBySessionId(ctx.channel().id().asLongText());
        if (session != null) {
            logger.info("user " + session.userId + " now is offline");
            RedisUtil.jedis.del(MyConst.redisKeySession(session.userId));
        }
    }

    @Override
    public List<String> getUserIdsByGroupId(String groupId) throws IOException, ServiceException {
        List<String> res = new ArrayList<>();
        Response response = HttpUtil.get("http://localhost:10086/api/groups/members/" + groupId);
        String jsonStr = response.body().string();
        JsonResp jsonResp = gson.fromJson(jsonStr, JsonResp.class);
        if (jsonResp.getCode() != 1) {
            throw new ServiceException(jsonResp.getMessage());
        }
        List<Map<String, Object>> users = (List<Map<String, Object>>) jsonResp.getData();
        for (Map<String, Object> user : users) {
            res.add((String) user.get("id"));
        }
        return res;
    }
}
