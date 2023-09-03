package org.youcancook.gobong.domain.authentication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.youcancook.gobong.domain.authentication.dto.request.ReissueTokenRequest;
import org.youcancook.gobong.domain.authentication.dto.response.ReissueTokenResponse;
import org.youcancook.gobong.domain.authentication.entity.RefreshToken;
import org.youcancook.gobong.domain.authentication.exception.InvalidRefreshTokenException;
import org.youcancook.gobong.domain.authentication.exception.RefreshTokenNotFoundException;
import org.youcancook.gobong.domain.authentication.repository.RefreshTokenRepository;
import org.youcancook.gobong.global.util.clock.ClockService;
import org.youcancook.gobong.global.util.token.TokenDto;
import org.youcancook.gobong.global.util.token.TokenManager;

import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenManager tokenManager;
    private final ClockService clockService;

    @Transactional
    public void saveRefreshToken(Long userId, TokenDto tokenDto) {
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .refreshToken(tokenDto.getRefreshToken())
                .expiredAt(tokenDto.getRefreshTokenExpiredAt())
                .build();
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public ReissueTokenResponse reissueToken(ReissueTokenRequest request) {
        Long userId = tokenManager.getUserIdFromRefreshToken(request.getRefreshToken());
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(RefreshTokenNotFoundException::new);
        validateRefreshToken(refreshToken, request.getRefreshToken());

        TokenDto tokenDto = tokenManager.createTokenDto(userId);
        saveRefreshToken(userId, tokenDto);

        return ReissueTokenResponse.from(tokenDto);
    }

    private void validateRefreshToken(RefreshToken refreshToken, String requestRefreshToken) {
        if (isExpiredToken(refreshToken) || !isEqualToken(refreshToken, requestRefreshToken)) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidRefreshTokenException();
        }
    }

    private boolean isExpiredToken(RefreshToken refreshToken) {
        Date currentDate = clockService.getCurrentDate();
        return refreshToken.getExpiredAt().before(currentDate);
    }

    private boolean isEqualToken(RefreshToken refreshToken, String requestRefreshToken) {
        return Objects.equals(refreshToken.getRefreshToken(), requestRefreshToken);
    }
}
