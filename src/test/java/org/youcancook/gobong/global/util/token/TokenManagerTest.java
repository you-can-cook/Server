package org.youcancook.gobong.global.util.token;

import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.youcancook.gobong.global.util.clock.ClockService;
import org.youcancook.gobong.global.util.token.exception.InvalidTokenException;
import org.youcancook.gobong.global.util.token.exception.InvalidTokenTypeException;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenManagerTest {

    @InjectMocks
    private TokenManager tokenManager;

    @Mock
    private ClockService clockService;

    private final String secretKey = "testSecretKey";
    private final Long accessTokenExpirationSeconds = 7200L;
    private final Long refreshTokenExpirationSeconds = 1209600L;

    @BeforeEach
    void setUp() throws ParseException {
        String dateTimeOnTestEnv = "2023-08-25 10:15:30";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse(dateTimeOnTestEnv);
        when(clockService.getCurrentDate()).thenReturn(date);

        ReflectionTestUtils.setField(tokenManager, "tokenSecret", secretKey);
        ReflectionTestUtils.setField(tokenManager, "accessTokenExpirationSeconds", accessTokenExpirationSeconds);
        ReflectionTestUtils.setField(tokenManager, "refreshTokenExpirationSeconds", refreshTokenExpirationSeconds);
    }

    @Test
    @DisplayName("토큰 생성")
    void createTokenDto() {
        // when
        TokenDto tokenDto = tokenManager.createTokenDto(1L);

        // then
        assertThat(tokenDto.getGrantType()).isEqualTo("Bearer");
        testAccessToken(tokenDto.getAccessToken());
        testRefreshToken(tokenDto.getRefreshToken());
    }

    private void testAccessToken(String accessToken) {
        Jws<Claims> claimsJws = Jwts.parser()
                .setClock(() -> clockService.getCurrentDate())
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(accessToken);

        Claims claims = claimsJws.getBody();
        assertThat(claims.getSubject()).isEqualTo(TokenType.ACCESS.name());
        assertThat(claims.get("userId")).isEqualTo(1);
        assertThat(claims.getIssuer()).isEqualTo("gobong.youcancook.org");

        Date expectedExpiredAt = new Date(clockService.getCurrentDate().getTime() + accessTokenExpirationSeconds * 1000);
        assertThat(claims.getExpiration()).isEqualTo(expectedExpiredAt);

        JwsHeader header = claimsJws.getHeader();
        assertThat(header.getType()).isEqualTo(Header.JWT_TYPE);
    }

    private void testRefreshToken(String refreshToken) {
        Jws<Claims> claimsJws = Jwts.parser()
                .setClock(() -> clockService.getCurrentDate())
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(refreshToken);

        Claims claims = claimsJws.getBody();
        assertThat(claims.getSubject()).isEqualTo(TokenType.REFRESH.name());
        assertThat(claims.get("userId")).isEqualTo(1);
        assertThat(claims.getIssuer()).isEqualTo("gobong.youcancook.org");

        Date expectedExpiredAt = new Date(clockService.getCurrentDate().getTime() + refreshTokenExpirationSeconds * 1000);
        assertThat(claims.getExpiration()).isEqualTo(expectedExpiredAt);

        JwsHeader header = claimsJws.getHeader();
        assertThat(header.getType()).isEqualTo(Header.JWT_TYPE);
    }

    @Test
    @DisplayName("AccessToken에서 userId 추출 성공")
    void getUserIdFromAccessTokenSuccess() {
        // given
        TokenDto tokenDto = tokenManager.createTokenDto(1L);

        // when
        Long userId = tokenManager.getUserIdFromAccessToken(tokenDto.getAccessToken());

        // then
        assertThat(userId).isEqualTo(1L);
    }

    @Test
    @DisplayName("AccessToken에서 userId 추출 실패 - 잘못된 토큰 타입")
    void getUserIdFromAccessTokenFailByWrongTokenType() {
        // given
        TokenDto tokenDto = tokenManager.createTokenDto(1L);

        // expect
        assertThrows(InvalidTokenTypeException.class,
                () -> tokenManager.getUserIdFromAccessToken(tokenDto.getRefreshToken()));
    }

    @Test
    @DisplayName("AccessToken에서 userId 추출 실패 - 잘못된 토큰")
    void getUserIdFromAccessTokenFailByWrongToken() {
        // given
        TokenDto tokenDto = tokenManager.createTokenDto(1L);
        String wrongAccessToken = tokenDto.getAccessToken() + "wrong";

        // expect
        assertThrows(InvalidTokenException.class,
                () -> tokenManager.getUserIdFromAccessToken(wrongAccessToken));
    }

    @Test
    @DisplayName("RefreshToken에서 userId 추출 성공")
    void getUserIdFromRefreshTokenSuccess() {
        // given
        TokenDto tokenDto = tokenManager.createTokenDto(1L);

        // when
        Long userId = tokenManager.getUserIdFromRefreshToken(tokenDto.getRefreshToken());

        // then
        assertThat(userId).isEqualTo(1L);
    }

    @Test
    @DisplayName("RefreshToken에서 userId 추출 실패 - 잘못된 토큰 타입")
    void getUserIdFromRefreshTokenFailByWrongTokenType() {
        // given
        TokenDto tokenDto = tokenManager.createTokenDto(1L);

        // expect
        assertThrows(InvalidTokenTypeException.class,
                () -> tokenManager.getUserIdFromRefreshToken(tokenDto.getAccessToken()));
    }

    @Test
    @DisplayName("RefreshToken에서 userId 추출 실패 - 잘못된 토큰")
    void getUserIdFromRefreshTokenFailByWrongToken() {
        // given
        TokenDto tokenDto = tokenManager.createTokenDto(1L);
        String wrongRefreshToken = tokenDto.getRefreshToken() + "wrong";

        // expect
        assertThrows(InvalidTokenException.class,
                () -> tokenManager.getUserIdFromRefreshToken(wrongRefreshToken));
    }
}