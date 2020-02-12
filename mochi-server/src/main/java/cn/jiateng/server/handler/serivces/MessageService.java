package cn.jiateng.server.handler.serivces;
import java.util.List;

public interface MessageService {

    void sendMessage(String fromUserId, String toUserId, String message);

    void sendMessages(String fromUserId, List<String> toUserId, String message);

}
