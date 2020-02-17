package cn.jiateng.common;

public class MyConst {

    public static String redisKeyFriends(String userId) {
        return "mochi-userService-friends-" + userId;
    }

    public static String redisKeySession(String userId) {
        return "mochi-authService-session-" + userId;
    }

    public static String redisKeyGroupMembers(String groupId) {
        return "mochi-groupService-members-" + groupId;
    }

    public static String redisKeyGroups(String userId) {
        return "mochi-groupService-groups-" + userId;
    }
}
