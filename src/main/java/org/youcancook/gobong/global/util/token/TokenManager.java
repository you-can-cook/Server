package org.youcancook.gobong.global.util.token;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.youcancook.gobong.global.util.clock.ClockService;
import org.youcancook.gobong.global.util.token.exception.ExpiredTokenException;
import org.youcancook.gobong.global.util.token.exception.InvalidTokenException;
import org.youcancook.gobong.global.util.token.exception.InvalidTokenTypeException;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenManager {

    @Value("${token.secret}")
    private String tokenSecret;

    @Value("${token.access-token-expiration-seconds}")
    private Long accessTokenExpirationSeconds;

    @Value("${token.refresh-token-expiration-seconds}")
    private Long refreshTokenExpirationSeconds;

    private final ClockService clockService;

    private final String USER_INFO_KEY = "userId";

    public TokenDto createTokenDto(Long userId) {
        Date accessTokenExpiredAt = createAccessTokenExpirationTime();
        Date refreshTokenExpiredAt = createRefreshTokenExpirationTime();

        String accessToken = createAccessToken(userId, accessTokenExpiredAt);
        String refreshToken = createRefreshToken(userId, refreshTokenExpiredAt);

        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiredAt(accessTokenExpiredAt)
                .refreshTokenExpiredAt(refreshTokenExpiredAt)
                .build();
    }

    private Date createAccessTokenExpirationTime() {
        Date now = clockService.getCurrentDate();
        return new Date(now.getTime() + accessTokenExpirationSeconds * 1000);
    }

    private Date createRefreshTokenExpirationTime() {
        Date now = clockService.getCurrentDate();
        return new Date(now.getTime() + refreshTokenExpirationSeconds * 1000);
    }

    private String createAccessToken(Long userId, Date expiredAt) {
        Claims claims = createClaims(userId, TokenType.ACCESS);
        return buildJwt(expiredAt, claims);
    }

    private String createRefreshToken(Long userId, Date expiredAt) {
        Claims claims = createClaims(userId, TokenType.REFRESH);
        return buildJwt(expiredAt, claims);
    }

    private Claims createClaims(Long userId, TokenType tokenType) {
        Claims claims = Jwts.claims();
        claims.put(USER_INFO_KEY, userId);
        claims.put(Claims.SUBJECT, tokenType.name());
        claims.put(Claims.ISSUER, "gobong.youcancook.org");
        return claims;
    }

    private String buildJwt(Date expiredAt, Claims claims) {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .setIssuedAt(clockService.getCurrentDate())
                .setExpiration(expiredAt)
                .signWith(SignatureAlgorithm.HS256, tokenSecret.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    public Long getUserIdFromAccessToken(String token) {
        Claims claims = getClaimsFromToken(token);
        validateAccessTokenType(claims);
        return getUserIdFromClaims(claims);
    }

    public Long getUserIdFromRefreshToken(String token) {
        Claims claims = getClaimsFromToken(token);
        validateRefreshTokenType(claims);
        return getUserIdFromClaims(claims);
    }

    private void validateAccessTokenType(Claims claims) {
        String tokenType = claims.getSubject();
        if (!TokenType.ACCESS.name().equals(tokenType)) {
            throw new InvalidTokenTypeException();
        }
    }

    private void validateRefreshTokenType(Claims claims) {
        String tokenType = claims.getSubject();
        if (!TokenType.REFRESH.name().equals(tokenType)) {
            throw new InvalidTokenTypeException();
        }
    }

    private Long getUserIdFromClaims(Claims claims) {
        Object userIdObject = claims.get(USER_INFO_KEY);
        String userIdString = String.valueOf(userIdObject);
        return Long.valueOf(userIdString);
    }

    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(tokenSecret.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("토큰 기한 만료", e);
            throw new ExpiredTokenException();
        } catch (JwtException e) {
            log.error("잘못된 jwt token", e);
            throw new InvalidTokenException();
        } catch (Exception e) {
            log.error("jwt token 검증 중 에러 발생", e);
            throw new InvalidTokenException();
        }
    }
}