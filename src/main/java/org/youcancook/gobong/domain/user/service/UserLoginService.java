package org.youcancook.gobong.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.youcancook.gobong.domain.authentication.service.RefreshTokenService;
import org.youcancook.gobong.domain.user.dto.response.LoginResponse;
import org.youcancook.gobong.domain.user.entity.OAuthProvider;
import org.youcancook.gobong.domain.user.entity.User;
import org.youcancook.gobong.domain.user.exception.UserNotFoundException;
import org.youcancook.gobong.domain.user.repository.UserRepository;
import org.youcancook.gobong.global.util.token.TokenDto;
import org.youcancook.gobong.global.util.token.TokenManager;

@Service
@RequiredArgsConstructor
public class UserLoginService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final TokenManager tokenManager;

    @Transactional
    public LoginResponse login(String provider, String oAuthId) {
        OAuthProvider oAuthProvider = OAuthProvider.from(provider);
        User user = userRepository.findByOAuthProviderAndOAuthId(oAuthProvider, oAuthId)
                .orElseThrow(UserNotFoundException::new);

        TokenDto tokenDto = tokenManager.createTokenDto(user.getId());
        refreshTokenService.saveRefreshToken(user.getId(), tokenDto);
        return LoginResponse.of(tokenDto);
    }
}
