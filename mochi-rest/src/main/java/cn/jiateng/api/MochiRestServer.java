package cn.jiateng.api;
import cn.jiateng.common.ServiceManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class MochiRestServer {

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext ctx = SpringApplication.run(MochiRestServer.class, args);
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = ctx.getEnvironment().getProperty("server.port");
        ServiceManager.registerService("/mochi-rest", ip, port);
    }
}
