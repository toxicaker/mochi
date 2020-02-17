package cn.jiateng.common;


import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

/**
 * Service registration and discovery
 */
public class ServiceManager {

//    private static ZookeeperManager zk = new ZookeeperManager("localhost", 2181);

    private static ZookeeperManager zk = new ZookeeperManager("10.138.15.193", 2181);

    public static String registerService(String serviceName, String host, String port) {
        try {
            boolean exist = zk.exist(serviceName);
            if (!exist) {
                zk.createNode(serviceName, "", CreateMode.PERSISTENT);
            }
            String addr = host + ":" + port;
            return zk.createNode(serviceName + "/child", addr, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                zk.close();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static String getServiceAddress(String path){
        try {
            return zk.getData(path);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
