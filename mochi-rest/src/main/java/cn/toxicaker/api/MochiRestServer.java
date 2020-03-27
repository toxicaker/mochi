package cn.toxicaker.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class MochiRestServer {

    private static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void main(String[] args) {
        SpringApplication.run(MochiRestServer.class, args);
    }

    public static void setUserId(String userId) {
        threadLocal.set(userId);
    }

    public String getUserId() {
        return threadLocal.get();
    }

    public static void clearUserId() {
        threadLocal.remove();
    }
}
