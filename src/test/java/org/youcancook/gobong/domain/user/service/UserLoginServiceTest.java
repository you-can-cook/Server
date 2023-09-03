package org.youcancook.gobong.domain.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.youcancook.gobong.domain.authentication.service.RefreshTokenService;
import org.youcancook.gobong.domain.user.dto.response.LoginResponse;
import org.youcancook.gobong.domain.user.entity.OAuthProvider;
import org.youcancook.gobong.domain.user.entity.User;
import org.youcancook.gobong.domain.user.exception.OAuthProviderNotFoundException;
import org.youcancook.gobong.domain.user.exception.UserNotFoundException;
import org.youcancook.gobong.domain.user.repository.UserRepository;
import org.youcancook.gobong.global.util.token.TokenDto;
import org.youcancook.gobong.global.util.token.TokenManager;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserLoginServiceTest {

    @InjectMocks
    private UserLoginService userLoginService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenManager tokenManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() {
        // given
        String oAuthId = "123456789";
        User user = createTestUser(oAuthId);
        TokenDto tokenDto = createTestTokenDto();
        when(userRepository.findByOAuthProviderAndOAuthId(OAuthProvider.KAKAO, oAuthId))
                .thenReturn(Optional.of(user));
        when(tokenManager.createTokenDto(1L))
                .thenReturn(tokenDto);
        doNothing().when(refreshTokenService).saveRefreshToken(1L, tokenDto);

        // when
        LoginResponse result = userLoginService.login("kakao", oAuthId);

        // then
        assertThat(result.getGrantType()).isEqualTo(tokenDto.getGrantType());
        assertThat(result.getAccessToken()).isEqualTo(tokenDto.getAccessToken());
        assertThat(result.getRefreshToken()).isEqualTo(tokenDto.getRefreshToken());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 provider")
    void loginFailByWrongProvider() {
        assertThrows(OAuthProviderNotFoundException.class,
                () -> userLoginService.login("wrong", "123456789"));
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 유저")
    void loginFailByNotFoundUser() {
        String oAuthId = "123456789";
        when(userRepository.findByOAuthProviderAndOAuthId(OAuthProvider.KAKAO, oAuthId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userLoginService.login("KAKAO", oAuthId));
    }

    private User createTestUser(String oAuthId) {
        User user = User.builder()
                .nickname("nickname")
                .oAuthProvider(OAuthProvider.KAKAO)
                .oAuthId(oAuthId)
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    private TokenDto createTestTokenDto() {
        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .accessTokenExpiredAt(new Date())
                .refreshTokenExpiredAt(new Date())
                .build();
    }
}