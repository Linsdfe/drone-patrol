package com.drone.patrol.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT令牌工具类
 *
 * 功能说明：
 * - 生成JWT格式的认证令牌
 * - 解析和验证JWT令牌
 * - 从令牌中提取用户信息
 *
 * 配置说明：
 * - jwt.secret: JWT签名密钥（至少32字符）
 * - jwt.expiration: 令牌过期时间（毫秒）
 *
 * 使用示例：
 * - 生成令牌：generateToken(userId, username, role)
 * - 验证令牌：validateToken(token)
 * - 获取用户ID：getUserIdFromToken(token)
 *
 * @author Drone Patrol Team
 */
@Component
public class JwtUtil {

    /** JWT签名密钥 */
    @Value("${jwt.secret}")
    private String secret;

    /** 令牌过期时间（毫秒） */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 获取签名密钥
     *
     * @return HMAC-SHA密钥对象
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成JWT令牌
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param role     用户角色
     * @return JWT令牌字符串
     *
     * @example
     * String token = jwtUtil.generateToken(1L, "admin", "ADMIN");
     */
    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        return generateToken(claims);
    }

    /**
     * 使用声明信息生成JWT令牌
     *
     * @param claims 声明信息映射
     * @return JWT令牌字符串
     */
    private String generateToken(Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从令牌中解析声明信息
     *
     * @param token JWT令牌
     * @return 声明信息对象
     *
     * @example
     * Claims claims = jwtUtil.getClaimsFromToken(token);
     * Long userId = claims.get("userId", Long.class);
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从令牌中获取用户ID
     *
     * @param token JWT令牌
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.valueOf(claims.get("userId").toString());
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("username").toString();
    }

    /**
     * 从令牌中获取用户角色
     *
     * @param token JWT令牌
     * @return 用户角色（如：ADMIN, USER）
     */
    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role").toString();
    }

    /**
     * 检查令牌是否已过期
     *
     * @param token JWT令牌
     * @return true表示已过期，false表示未过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 验证令牌是否有效
     *
     * @param token JWT令牌
     * @return true表示有效，false表示无效
     */
    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
}
