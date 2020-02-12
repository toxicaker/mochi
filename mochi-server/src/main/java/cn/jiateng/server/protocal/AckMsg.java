package cn.jiateng.server.protocal;

public class AckMsg {

    private long id;

    private String fromId;

    private String destId;

    private int msgType;  // 1 sent, 2 delivered, 3 read

    public class MsgType {

        public static final int SENT = 1;

        public static final int DELIVERED = 2;

        public static final int READ = 3;

    }
}
