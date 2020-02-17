package cn.jiateng.common;


import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperManager {

    private ZooKeeper zooKeeper;
    private CountDownLatch latch = new CountDownLatch(1);

    public ZookeeperManager(String host, int port) {
        try {
            zooKeeper = connect(host, port);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private ZooKeeper connect(String host, int port) throws InterruptedException, IOException {
        zooKeeper = new ZooKeeper(host + ":" + port, 5000, event -> {
            if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                latch.countDown();
            }
        });
        latch.await();
        return zooKeeper;
    }

    public String createNode(String path, String data, CreateMode mode) throws KeeperException, InterruptedException {
        return zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
    }

    public String getData(String path) throws KeeperException, InterruptedException {
        return new String(zooKeeper.getData(path, false, null));
    }

    public void updateNode(String path, String data) throws KeeperException, InterruptedException {
        zooKeeper.setData(path, data.getBytes(), zooKeeper.exists(path, true).getVersion());
    }

    public boolean exist(String path) throws KeeperException, InterruptedException {
        Stat exists = zooKeeper.exists(path, false);
        return exists != null;
    }

    public void deleteNode(String path) throws KeeperException, InterruptedException {
        zooKeeper.delete(path, zooKeeper.exists(path, true).getVersion());
    }

    public void close() throws InterruptedException {
        zooKeeper.close();
    }
}
