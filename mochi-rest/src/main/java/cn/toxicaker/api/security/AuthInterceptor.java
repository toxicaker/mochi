package cn.toxicaker.api.security;

import cn.jiateng.common.JsonResp;
import cn.toxicaker.api.MochiRestServer;
import cn.toxicaker.api.service.AuthService;
import cn.toxicaker.common.util.TokenUtil;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {


    private Gson gson = new Gson();

    private AuthService authService;

    @Autowired
    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
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
        String token = request.getHeader("mochi-token");
        if (token == null || "".equals(token)) {
            JsonResp jsonResp = new JsonResp(JsonResp.FAIL, "no token found!", null);
            response.setStatus(401);
            response.getWriter().write(gson.toJson(jsonResp));
            return false;
        }
        String userId = TokenUtil.getUserIdByToken(token);
        if (userId != null && !TokenUtil.isExpired(token) && authService.checkAndUpdateSession(userId)) {
            MochiRestServer.setUserId(userId);
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
        MochiRestServer.clearUserId();
    }
}
