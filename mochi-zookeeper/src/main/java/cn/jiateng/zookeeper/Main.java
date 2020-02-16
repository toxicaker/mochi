package cn.jiateng.zookeeper;

public class Main {
    public static void main(String[] args){
        String res = ServiceManager.registerService("/mochi-rest", "127.0.0.1", "10086");
        System.out.println(ServiceManager.getServiceAddress(res));
    }
}
