package cn.jiateng.server.handler.serivces;
import java.io.IOException;
import java.util.List;

public interface MessageService {

    void sendMessage(String fromUserId, String toUserId, String message) throws IOException;

    void sendMessages(String fromUserId, List<String> toUserId, String message);

}
