package cn.jiateng.server.common;

import com.google.gson.Gson;
import io.netty.channel.group.ChannelGroup;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private ConcurrentHashMap<String, Session> userIdMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, Session> sessIdMap = new ConcurrentHashMap<>();

    private ChannelGroup channelGroup;

    private Gson gson = new Gson();

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
        if (userId == null) return null;
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



    public synchronized void close() {
        userIdMap.clear();
        sessIdMap.clear();
        channelGroup.close();
    }
}
