package com.officeflow.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    // 确保字符串足够长，直接初始化为常量
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            "OfficeFlow_Secure_Secret_Key_For_JWT_HS256_32_Bytes".getBytes(StandardCharsets.UTF_8)
    );

    // Token 有效期：24 小时
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    /**
     * 生成 Token
     */
    public String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)                // 载荷 (userId, role 等)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)          // 直接使用静态常量 Key
                .compact();
    }

    /**
     * 解析并验证 Token
     */
    public Claims parseToken(String token) {
        // 非空判断，防止空指针
        if (!StringUtils.hasText(token)) {
            throw new RuntimeException("Token 不能为空");
        }

        // 处理前端可能传来的 Bearer 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)      // 解析
                .getPayload();                 // 获取数据
    }

    /**
     * 从 Token 中获取特定数据 (示例：用户名)
     * 注意：你在 AuthService 存的是 "username" 而不是标准 subject，所以用 get("username")
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        // 存的时候是 claims.put("username", ...), 那么这里就要 get("username")
        return claims.get("username", String.class);
    }
}