package org.youcancook.gobong.domain.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.youcancook.gobong.domain.authentication.service.RefreshTokenService;
import org.youcancook.gobong.domain.user.dto.SignupDto;
import org.youcancook.gobong.domain.user.dto.response.SignupResponse;
import org.youcancook.gobong.domain.user.entity.OAuthProvider;
import org.youcancook.gobong.domain.user.entity.User;
import org.youcancook.gobong.domain.user.exception.UserAlreadyExistsException;
import org.youcancook.gobong.domain.user.exception.DuplicationNicknameException;
import org.youcancook.gobong.domain.user.repository.UserRepository;
import org.youcancook.gobong.global.util.token.TokenDto;
import org.youcancook.gobong.global.util.token.TokenManager;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSignupServiceTest {

    @InjectMocks
    private UserSignupService userSignupService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private TokenManager tokenManager;

    @Test
    @DisplayName("회원가입 성공")
    void signupSuccess() {
        // given
        TokenDto tokenDto = createTestTokenDto();
        when(userRepository.existsByNickname("nickname"))
                .thenReturn(false);
        when(userRepository.existsByOAuthProviderAndOAuthId(any(OAuthProvider.class), any(String.class)))
                .thenReturn(false);
        when(tokenManager.createTokenDto(1L))
                .thenReturn(tokenDto);
        doNothing().when(refreshTokenService).saveRefreshToken(1L, tokenDto);
        when(userRepository.save(any(User.class)))
                .then(invocation -> {
                    User savedUser = invocation.getArgument(0);
                    ReflectionTestUtils.setField(savedUser, "id", 1L);
                    return savedUser;
                });

        // when
        SignupDto signupDto = SignupDto.builder()
                .oAuthProvider(OAuthProvider.KAKAO)
                .nickname("nickname")
                .oAuthId("123456789")
                .profileImageURL("profileImageURL")
                .build();
        SignupResponse result = userSignupService.signup(signupDto);

        // then
        assertThat(result.getGrantType()).isEqualTo(tokenDto.getGrantType());
        assertThat(result.getAccessToken()).isEqualTo(tokenDto.getAccessToken());
        assertThat(result.getRefreshToken()).isEqualTo(tokenDto.getRefreshToken());
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 닉네임")
    void signupFailByDuplicatedNickname() {
        // given
        when(userRepository.existsByNickname(any(String.class)))
                .thenReturn(true);

        // when
        SignupDto signupDto = SignupDto.builder()
                .oAuthProvider(OAuthProvider.KAKAO)
                .nickname("nickname")
                .oAuthId("123456789")
                .profileImageURL("profileImageURL")
                .build();
        assertThrows(DuplicationNicknameException.class,
                () -> userSignupService.signup(signupDto));
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 유저")
    void signupFailByExitUser() {
        // given
        when(userRepository.existsByNickname(any(String.class)))
                .thenReturn(false);
        when(userRepository.existsByOAuthProviderAndOAuthId(any(OAuthProvider.class), any(String.class)))
                .thenReturn(true);

        // when
        SignupDto signupDto = SignupDto.builder()
                .oAuthProvider(OAuthProvider.KAKAO)
                .nickname("nickname")
                .oAuthId("123456789")
                .profileImageURL("profileImageURL")
                .build();
        assertThrows(UserAlreadyExistsException.class,
                () -> userSignupService.signup(signupDto));
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