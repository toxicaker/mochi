package cn.jiateng.server.common;

import cn.jiateng.common.MyConst;
import cn.jiateng.server.utils.RedisUtil;
import io.netty.channel.group.ChannelGroup;

import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private ConcurrentHashMap<String, Session> userIdMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, Session> sessIdMap = new ConcurrentHashMap<>();

    private ChannelGroup channelGroup;

    public SessionManager(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }

    public synchronized void addSession(Session session) {
        if (userIdMap.containsKey(session.userId)) {
            return;
        }
        userIdMap.put(session.userId, session);
        sessIdMap.put(session.sessionId, session);
        channelGroup.add(session.channel);
    }

    public Session getSession(String userId) {
        if (!userIdMap.containsKey(userId)){
            return getSessionFromRedis(userId);
        }
        return userIdMap.get(userId);
    }

    public synchronized Session removeSessionBySessionId(String sessionId) {
        if (!sessIdMap.containsKey(sessionId)) {
            return null;
        }
        Session session = sessIdMap.get(sessionId);
        session.channel.close();
        userIdMap.remove(session.userId);
        sessIdMap.remove(sessionId);
        return session;
    }

    private Session getSessionFromRedis(String userId) {
        String key = MyConst.redisKeySession(userId);
        if (!RedisUtil.jedis.exists(key)) return null;
        String server = RedisUtil.jedis.hget(key, "msgServer");
        String sessionId = RedisUtil.jedis.hget(key, "sessionId");
        return new Session(userId, sessionId, server);
    }


    public synchronized void close() {
        userIdMap.clear();
        sessIdMap.clear();
        channelGroup.close();
    }
}
