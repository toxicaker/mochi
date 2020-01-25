package cn.jiateng.server.common;
import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

public class HttpResponseBuilder {

    private HttpVersion httpVersion = HttpVersion.HTTP_1_1;

    private HttpResponseStatus responseStatus = HttpResponseStatus.OK;

    private HttpHeaders headers;

    private JsonObj jsonObj;

    private class JsonObj {
        int code;
        String responseMsg;
        Object data;

        JsonObj(int code, String responseMsg, Object data) {
            this.code = code;
            this.responseMsg = responseMsg;
            this.data = data;
        }
    }

    public HttpResponseBuilder() {
        headers = new DefaultHttpHeaders();
        headers.set(HttpHeaders.Names.CONTENT_TYPE,
                "text/plain");
        jsonObj = new JsonObj(1, "success", null);
    }


    public HttpResponseBuilder setHttpVersion(HttpVersion version) {
        httpVersion = version;
        return this;
    }

    public HttpResponseBuilder setResponseMsg(String msg) {
        jsonObj.responseMsg = msg;
        return this;
    }

    public HttpResponseBuilder setData(Object data) {
        jsonObj.data = data;
        return this;
    }

    public HttpResponseBuilder setResponseStatus(HttpResponseStatus status) {
        responseStatus = status;
        return this;
    }

    public HttpResponseBuilder addHeader(String key, Object val) {
        headers.add(key, val);
        return this;
    }

    public FullHttpResponse build() {
        Gson gson = new Gson();
        jsonObj.code = this.responseStatus == HttpResponseStatus.OK ? 1 : -1;
        if(jsonObj.code == -1 && jsonObj.responseMsg.equals("success")){
            jsonObj.responseMsg = "failure";
        }
        String jsonStr = gson.toJson(jsonObj, JsonObj.class);
        FullHttpResponse response = new DefaultFullHttpResponse(
                httpVersion,
                responseStatus,
                Unpooled.copiedBuffer(jsonStr.getBytes())
        );
        headers.set(HttpHeaders.Names.CONTENT_LENGTH, jsonStr.length());
        response.headers().add(headers);
        return response;
    }
}
