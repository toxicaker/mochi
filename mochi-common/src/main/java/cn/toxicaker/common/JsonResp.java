package cn.toxicaker.common;

public class JsonResp {

    public static final int SUCCESS = 1;
    public static final int FAILURE = -1;

    private int code = SUCCESS;
    private String message = "success";
    private Object data;

    public JsonResp(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public JsonResp(Object data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
