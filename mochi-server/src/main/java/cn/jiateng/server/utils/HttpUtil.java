package cn.jiateng.server.utils;

import com.squareup.okhttp.*;

import java.io.IOException;
import java.util.Map;

public class HttpUtil {

    private static OkHttpClient client = new OkHttpClient();

    public static Response get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        return call.execute();
    }

    public static Response post(String url, Map<String, String> params) throws IOException {
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        for (Map.Entry<String, String> e : params.entrySet()) {
            formEncodingBuilder.add(e.getKey(), e.getValue());
        }
        RequestBody body = formEncodingBuilder.build();
        Request request = new Request.Builder()
                .url(url).post(body)
                .build();
        Call call = client.newCall(request);
        return call.execute();
    }

    public static Response post(String url, byte[] bytes) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), bytes);
        Request request = new Request.Builder()
                .url(url).post(body)
                .build();
        Call call = client.newCall(request);
        return call.execute();
    }
}
