package org.youcancook.gobong.domain.authentication.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.youcancook.gobong.domain.authentication.dto.request.ReissueTokenRequest;
import org.youcancook.gobong.domain.authentication.dto.response.ReissueTokenResponse;
import org.youcancook.gobong.domain.authentication.entity.RefreshToken;
import org.youcancook.gobong.domain.authentication.exception.InvalidRefreshTokenException;
import org.youcancook.gobong.domain.authentication.repository.RefreshTokenRepository;
import org.youcancook.gobong.global.util.clock.ClockService;
import org.youcancook.gobong.global.util.token.TokenDto;
import org.youcancook.gobong.global.util.token.TokenManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TokenManager tokenManager;

    @Mock
    private ClockService clockService;

    @Test
    @DisplayName("리프레시 토큰 저장")
    void saveRefreshToken() {
        // given
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .then(invocation -> invocation.getArgument(0));

        // when
        TokenDto tokenDto = createTestTokenDto(new Date());
        refreshTokenService.saveRefreshToken(1L, tokenDto);

        // then
        ArgumentCaptor<RefreshToken> argument = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository, times(1)).save(argument.capture());

        RefreshToken savedRefreshToken = argument.getValue();
        assertThat(savedRefreshToken.getUserId()).isEqualTo(1L);
        assertThat(savedRefreshToken.getRefreshToken()).isEqualTo(tokenDto.getRefreshToken());
        assertThat(savedRefreshToken.getExpiredAt()).isEqualTo(tokenDto.getRefreshTokenExpiredAt());
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    void reissueTokenSuccess() throws ParseException {
        // given
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        when(tokenManager.getUserIdFromRefreshToken(any(String.class)))
                .thenReturn(1L);
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .then(invocation -> invocation.getArgument(0));
        when(clockService.getCurrentDate())
                .thenReturn(simpleDateFormat.parse("2023-08-25 08:10:55"));

        Date date = simpleDateFormat.parse("2023-08-25 08:15:55");
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken("refreshToken")
                .expiredAt(date)
                .userId(1L)
                .build();
        when(refreshTokenRepository.findByUserId(1L))
                .thenReturn(Optional.of(refreshToken));

        TokenDto tokenDto = createTestTokenDto(new Date(date.getTime() + 100000));
        when(tokenManager.createTokenDto(1L))
                .thenReturn(tokenDto);

        // when
        ReissueTokenRequest reissueTokenRequest = new ReissueTokenRequest(refreshToken.getRefreshToken());
        ReissueTokenResponse reissueTokenResponse = refreshTokenService.reissueToken(reissueTokenRequest);

        // then
        assertThat(reissueTokenResponse.getAccessToken()).isEqualTo(tokenDto.getAccessToken());
        assertThat(reissueTokenResponse.getRefreshToken()).isEqualTo(tokenDto.getRefreshToken());
        assertThat(reissueTokenResponse.getGrantType()).isEqualTo(tokenDto.getGrantType());
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 만료시간이 지난 refreshToken")
    void reissueTokenFailByExpiredToken() throws ParseException {
        // given
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        when(tokenManager.getUserIdFromRefreshToken(any(String.class)))
                .thenReturn(1L);
        when(clockService.getCurrentDate())
                .thenReturn(simpleDateFormat.parse("2023-08-25 08:10:55"));

        Date date = simpleDateFormat.parse("2023-08-25 08:05:55");
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken("refreshToken")
                .expiredAt(date)
                .userId(1L)
                .build();
        when(refreshTokenRepository.findByUserId(1L))
                .thenReturn(Optional.of(refreshToken));

        // expect
        ReissueTokenRequest reissueTokenRequest = new ReissueTokenRequest(refreshToken.getRefreshToken());
        assertThrows(InvalidRefreshTokenException.class,
                () -> refreshTokenService.reissueToken(reissueTokenRequest));
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 일치하지 않는 refreshToken")
    void reissueTokenFailByNotEqualToken() throws ParseException {
        // given
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        when(tokenManager.getUserIdFromRefreshToken(any(String.class)))
                .thenReturn(1L);
        when(clockService.getCurrentDate())
                .thenReturn(simpleDateFormat.parse("2023-08-25 08:10:55"));

        Date date = simpleDateFormat.parse("2023-08-25 08:15:55");
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken("refreshToken")
                .expiredAt(date)
                .userId(1L)
                .build();
        when(refreshTokenRepository.findByUserId(1L))
                .thenReturn(Optional.of(refreshToken));

        // expect
        ReissueTokenRequest reissueTokenRequest = new ReissueTokenRequest(refreshToken.getRefreshToken() + "wrong");
        assertThrows(InvalidRefreshTokenException.class,
                () -> refreshTokenService.reissueToken(reissueTokenRequest));
    }

    private TokenDto createTestTokenDto(Date expiredAt) {
        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .accessTokenExpiredAt(expiredAt)
                .refreshTokenExpiredAt(expiredAt)
                .build();
    }
}