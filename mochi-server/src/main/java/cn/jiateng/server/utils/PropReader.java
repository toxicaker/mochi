package cn.jiateng.server.utils;

import cn.jiateng.server.MochiMsgServer;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class PropReader {

    private static Properties prop = new Properties();

    static {
        try {
            if ("dev".equals(MochiMsgServer.env)) {
                prop.load(Objects.requireNonNull(MochiMsgServer.class.getClassLoader().getResourceAsStream("application.properties")));
            } else {
                prop.load(Objects.requireNonNull(MochiMsgServer.class.getClassLoader().getResourceAsStream("application-prod.properties")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String read(String key) {
        return prop.getProperty(key);
    }
}
