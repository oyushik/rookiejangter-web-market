package com.miniproject.rookiejangter.provider;

import com.miniproject.rookiejangter.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JwtProvider {

    private static final long ACCESS_TOKEN_VALIDITY = 1000L * 60 * 30;  // 30분
    private static final long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 7;  // 7일

    @Value("${jwt.secret}")
    private String secret;
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // AccessToken 생성 (User 객체를 받는 public 메서드)
    public String createAccessToken(User user) {
        Map<String, Object> claims = Map.of("userName", user.getUserName());
        return createAccessToken(String.valueOf(user.getUserId()), claims);
    }

    // RefreshToken 생성 (User 객체를 받는 public 메서드)
    public String createRefreshToken(User user) {
        return createRefreshToken(String.valueOf(user.getUserId()));
    }

    // AccessToken 생성 (실제 구현 - private)
    private String createAccessToken(String subject, Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // RefreshToken 생성 (실제 구현 - private)
    private String createRefreshToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 사용자 ID 추출
    public String getUserIdFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // 토큰에서 원하는 Claim 추출
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // 모든 Claims 추출
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰 만료 여부 확인
    public boolean isTokenExpired(String token) {
        final Date expiration = getClaimFromToken(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | ExpiredJwtException |
                 UnsupportedJwtException | IllegalArgumentException e) {
            log.warn("JWT 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    // RefreshToken 만료 시간 반환 (Redis 저장시 사용)
    public long getRefreshTokenExpireTime() {
        return REFRESH_TOKEN_VALIDITY;
    }
}
