package cn.jiateng.api.utils;

import cn.jiateng.api.common.MyConst;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public final class AuthUtil {

    private final String SALT = "mochi-Bbbkqosfknmxcziewnczscauihvbjsbvemkjweighjkqb";

    public final int TIME = 8 * 60 * 60 * 1000; // 8 hours

    private final RedisUtil redisUtil;

    private ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public AuthUtil(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    public String createToken(String userId) {
        String token = Jwts.builder().setId(userId).setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, SALT).compact();
        Map<String, Object> session = new HashMap<>();
        session.put("userId", userId);
        session.put("token", token);
        redisUtil.mapAdd(MyConst.redisKeySession(userId), session);
        redisUtil.setExpire(MyConst.redisKeySession(userId), TIME);
        return token;
    }

    /**
     * check if token is valid
     *
     * @param token
     * @return null if false, userId if true;
     */
    public String checkToken(String token) {
        if (token == null || "".equals(token)) return null;
        String userId = Jwts.parser().setSigningKey(SALT).parseClaimsJws(token).getBody().getId();
        if (userId == null || "".equals(userId)) return null;
        if (!redisUtil.hasKey(MyConst.redisKeySession(userId))) return null;
        String existToken = (String) redisUtil.mapGet(MyConst.redisKeySession(userId), "token");
        if (token.equals(existToken)) {
            redisUtil.setExpire(MyConst.redisKeySession(userId), TIME);
            return userId;
        }
        return null;
    }

    public String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public void setUserId(String userId) {
        this.threadLocal.set(userId);
    }

    public String getUserId(String userId) {
        return this.threadLocal.get();
    }

    public void clearUserId() {
        this.threadLocal.remove();
    }
}
