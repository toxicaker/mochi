package cn.jiateng.api.security;

import cn.jiateng.common.JsonResp;
import cn.jiateng.api.utils.AuthUtil;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    private final AuthUtil authUtil;

    private Gson gson = new Gson();

    public AuthInterceptor(AuthUtil authUtil) {
        this.authUtil = authUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        if (method.getAnnotation(SkipAuth.class) != null) {
            return true;
        }
        String authorization = request.getHeader("mochi-token");
        if (authorization == null || "".equals(authorization)) {
            JsonResp jsonResp = new JsonResp(JsonResp.FAIL, "no token found!", null);
            response.setStatus(401);
            response.getWriter().write(gson.toJson(jsonResp));
            return false;
        }
        String userId = authUtil.checkToken(authorization);
        if (userId != null) {
            authUtil.setUserId(userId);
            return true;
        } else {
            JsonResp jsonResp = new JsonResp(JsonResp.FAIL, "token is invalid", null);
            response.setStatus(401);
            response.getWriter().write(gson.toJson(jsonResp));
            return false;
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        authUtil.clearUserId();
    }
}
