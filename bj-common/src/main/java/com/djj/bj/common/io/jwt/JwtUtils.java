package com.djj.bj.common.io.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import java.util.Date;

/**
 * JWT工具类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.common.io.jwt
 * @className JwtUtils
 * @date 2025/6/18 21:21
 */
public class JwtUtils {

    /**
     * 生成jwt字符串 JWT(json web token)
     *
     * @param userId 用户id
     * @param info 用户信息
     * @param expireIn 过期时间
     * @param secret 密钥
     * @return token
     */
    public static String sign(Long userId, String info, long expireIn, String secret) {
        try {
            Date date = new Date(System.currentTimeMillis() + expireIn * 1000);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withAudience(userId.toString()) // 将userId保存到token中
                    .withClaim("info", info) // 存放自定义数据
                    .withExpiresAt(date) // 设置过期时间
                    .sign(algorithm); // token密钥
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据token获取userId
     *
     * @param token 登录token
     * @return 用户id
     */
    public static Long getUserId(String token) {
        try {
            String userId = JWT.decode(token).getAudience().get(0);
            return Long.parseLong(userId);
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 获取token中的用户信息
     *
     * @param token 登录token
     * @return 用户信息
     */
    public static String getInfo(String token){
        try {
            return JWT.decode(token).getClaim("info").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 校验token
     *
     * @param token 用户登录token
     * @param secret 密钥
     * @return 是否验证通过
     */
    public static Boolean checkSign(String token, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWT.require(algorithm).build().verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }
}
