package cn.toxicaker.common;

public class MyConst {

    public static String redisKeySession(String userId) {
        return "mochi-authService-session-" + userId;
    }

    public static String REST_HOST = "http://localhost:10086";

}
