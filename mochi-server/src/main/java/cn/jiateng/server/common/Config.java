package cn.jiateng.server.common;

import java.io.*;
import java.util.Properties;

public class Config {

    private static Properties prop = new Properties();

    static {
        try (InputStream is = new FileInputStream("./mochi-server/src/main/resources/server.properties")) {
            prop.load(is);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public static String read(String key) {
        return prop.getProperty(key);
    }
}
