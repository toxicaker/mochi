package cn.jiateng.api.common;

public class MyConst {

    public static String redisKeyFriends(String userId) {
        return "mochi-userService-friends-" + userId;
    }

}
