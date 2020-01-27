package cn.jiateng.server.common;

// websocket message protocol
public class WSMsg {

    private String sourceId = "";

    private String targetId = "";

    private String message = "";

    private Integer type = MsgType.PRIVATE;

    private Long createTime;

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public class MsgType {

        public static final int PRIVATE = 1;

        public static final int GROUP = 2;

    }
}
