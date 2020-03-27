package cn.toxicaker.common.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


public final class TokenUtil {

    private static final String SALT = "mochi-Bbbkqosfknmxcziewnczscauihvbjsbvemkjweighjkqb";

    public static final int TIME = 8 * 60 * 60 * 1000; // 8 hours

    public static String createToken(String userId) {
        Date expiration = new Date(System.currentTimeMillis() + TIME);
        return Jwts.builder().setId(userId).setIssuedAt(new Date()).setExpiration(expiration).signWith(SignatureAlgorithm.HS256, SALT).compact();
    }

    public static boolean isExpired(String token) {
        Date expiration = Jwts.parser().setSigningKey(SALT).parseClaimsJws(token).getBody().getExpiration();
        Date curDate = new Date();
        return !curDate.after(expiration);
    }

    public static String getUserIdByToken(String token) {
        return Jwts.parser().setSigningKey(SALT).parseClaimsJws(token).getBody().getId();
    }

    public static String getMd5(String input) {
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
}
