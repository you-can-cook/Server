package org.youcancook.gobong.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Service
@RequiredArgsConstructor
public class UserSignupService {

    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final TokenManager tokenManager;

    @Transactional
    public SignupResponse signup(SignupDto signupDto) {
        validateDuplicateNickname(signupDto.getNickname());
        validateExistUser(signupDto.getOAuthProvider(), signupDto.getOAuthId());

        User user = createUser(signupDto);
        userRepository.save(user);

        TokenDto tokenDto = tokenManager.createTokenDto(user.getId());
        refreshTokenService.saveRefreshToken(user.getId(), tokenDto);
        return SignupResponse.from(tokenDto);
    }

    private void validateDuplicateNickname(String nickname) {
        if (isDuplicateNickname(nickname)) {
            throw new DuplicationNicknameException();
        }
    }

    private boolean isDuplicateNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    private void validateExistUser(OAuthProvider oAuthProvider, String oAuthId) {
        if (userRepository.existsByOAuthProviderAndOAuthId(oAuthProvider, oAuthId)) {
            throw new UserAlreadyExistsException();
        }
    }

    private User createUser(SignupDto signupDto) {
        return User.builder()
                .oAuthProvider(signupDto.getOAuthProvider())
                .oAuthId(signupDto.getOAuthId())
                .nickname(signupDto.getNickname())
                .profileImageURL(signupDto.getProfileImageURL())
                .build();
    }
}
