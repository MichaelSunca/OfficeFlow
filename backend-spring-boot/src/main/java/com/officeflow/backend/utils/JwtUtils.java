package com.officeflow.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {

    // 实际项目中应放在 application.yml 中，且长度至少 32 位
    private static final String SECRET_KEY = "OfficeFlow_Secret_Key_For_Test_Temp";
    // Token 有效期：24 小时
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 Token
     * @param username 用户名
     * @return JWT Token 字符串
     */
    public String createToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        /*
         * · 标准声明 (Reserved Claims)：这是 JWT 协议预留的字段。
         *   · sub (Subject)：通常放用户名或用户 ID（对应代码里的 .subject(username)）。
         *   · iat (Issued At)：签发时间。
         *   · exp (Expiration)：过期时间。
         * · 自定义声明 (Custom Claims)：你可以根据业务需要往里塞任何信息。
         * 例如：.claim("role", "ADMIN")。这样你解析 Token 时，不需要查数据库就能立刻知道这个人的角色。
         */

        return Jwts.builder()
                .subject(username)                 // 将用户名存入载荷 (Subject)
                .issuedAt(now)                     // 签发时间
                .expiration(expiryDate)            // 过期时间
                .signWith(getSigningKey())         // 签名算法
                .compact();
    }

    /**
     * 解析并验证 Token
     * @param token 前端传来的 Token
     * @return 包含用户信息的 Claims
     */
    public Claims parseToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // 截取 "Bearer " 之后的真正 JWT 内容
        }

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }
}