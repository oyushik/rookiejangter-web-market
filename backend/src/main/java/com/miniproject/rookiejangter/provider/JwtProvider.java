package com.miniproject.rookiejangter.provider;

import com.miniproject.rookiejangter.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component("jwtProvider") // 빈 이름을 "jwtProvider"로 명시적으로 지정
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

    // AccessToken 생성 (User 객체를 받는 public 메서드) - 권한 정보 포함
    public String createAccessToken(User user) {
        String role = Boolean.TRUE.equals(user.getIsAdmin()) ? "ADMIN" : "USER";
        Map<String, Object> claims = Map.of(
                "userName", user.getUserName(),
                "role", role  // isAdmin 필드를 기반으로 권한 정보 추가 (여기서는 "role" 클레임 사용)
        );
        return createAccessToken(String.valueOf(user.getUserId()), claims);
    }

    // RefreshToken 생성 (User 객체를 받는 public 메서드)
    public String createRefreshToken(User user) {
        return createRefreshToken(String.valueOf(user.getUserId()));
    }

    // AccessToken 생성 (실제 구현 - private)
    private String createAccessToken(String subject, Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                .signWith(key, Jwts.SIG.HS256) // Jwts.SIG.HS256 사용
                .compact();
    }

    // RefreshToken 생성 (실제 구현 - private)
    private String createRefreshToken(String subject) {
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .signWith(key, Jwts.SIG.HS256) // Jwts.SIG.HS256 사용
                .compact();
    }

    // 토큰에서 사용자 ID 추출
    public String getUserIdFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // 토큰에서 사용자 권한 추출
    public String getRoleFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("role", String.class));
    }

    // 토큰에서 사용자 이름 추출
    public String getUserNameFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("userName", String.class));
    }

    // 토큰에서 원하는 Claim 추출
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // 모든 Claims 추출
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key) // verifyWith 사용
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 토큰 만료 여부 확인
    public boolean isTokenExpired(String token) {
        final Date expiration = getClaimFromToken(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token); // verifyWith 사용
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException | ExpiredJwtException |
                 UnsupportedJwtException | IllegalArgumentException e) {
            log.warn("JWT 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    // RefreshToken 만료 시간 반환 (Redis 저장시 사용)
    public long getRefreshTokenExpireTime() {
        return REFRESH_TOKEN_VALIDITY;
    }

    /**
     * JWT 토큰에서 인증 정보(Authentication)를 조회합니다.
     * StompHandler에서 사용될 핵심 메서드.
     *
     * @param accessToken JWT Access Token
     * @return Authentication 객체
     */
    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        // JWT 생성 시 "role" 클레임에 권한을 저장하므로 "role" 클레임을 확인
        String roleString = claims.get("role", String.class); // "role" 클레임에서 String 타입으로 권한 추출

        if (roleString == null || roleString.isEmpty()) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다. (클레임: role)");
        }

        // 단일 역할을 SimpleGrantedAuthority로 변환
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleString));

        // Spring Security User 객체를 생성 (subject는 userId)
        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities);

        // UsernamePasswordAuthenticationToken을 반환하여 SecurityContext에 저장할 Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // JWT 토큰을 복호화하여 클레임(claims)을 추출하는 헬퍼 메서드
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser()
                    .verifyWith(key) // verifyWith 사용
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰에서도 클레임은 추출 가능 (리프레시 토큰 로직에 사용될 수 있음)
            log.warn("Expired JWT token: {}", e.getMessage());
            return e.getClaims();
        } catch (Exception e) {
            log.error("Failed to parse JWT claims: {}", e.getMessage());
            throw new JwtException("Failed to parse JWT claims", e);
        }
    }
}