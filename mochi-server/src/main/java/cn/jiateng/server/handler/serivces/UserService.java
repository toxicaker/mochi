package cn.jiateng.server.handler.serivces;

import cn.jiateng.server.common.ServiceException;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.List;

public interface UserService {

    void login(String userId, ChannelHandlerContext ctx) throws ServiceException;

    void logout(ChannelHandlerContext ctx);

    List<String> getUserIdsByGroupId(String groupId) throws IOException, ServiceException;

}
