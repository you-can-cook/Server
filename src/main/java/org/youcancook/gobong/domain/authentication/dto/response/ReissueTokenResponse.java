package org.youcancook.gobong.domain.authentication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.youcancook.gobong.global.util.token.TokenDto;

@Getter
@Builder
@AllArgsConstructor
public class ReissueTokenResponse {
    private String grantType;
    private String accessToken;
    private String refreshToken;

    public static ReissueTokenResponse from(TokenDto tokenDto) {
        return ReissueTokenResponse.builder()
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .build();
    }
}
