package org.youcancook.gobong.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.youcancook.gobong.domain.user.dto.request.SignupRequest;
import org.youcancook.gobong.domain.user.entity.OAuthProvider;

@Getter
@Builder
@AllArgsConstructor
public class SignupDto {

    private String nickname;
    private OAuthProvider oAuthProvider;
    private String oAuthId;
    private String profileImageURL;

    public static SignupDto from(SignupRequest signupRequest) {
        OAuthProvider oAuthProvider = OAuthProvider.from(signupRequest.getProvider());
        return SignupDto.builder()
                .nickname(signupRequest.getNickname())
                .oAuthProvider(oAuthProvider)
                .oAuthId(signupRequest.getOauthId())
                .profileImageURL(signupRequest.getProfileImageURL())
                .build();
    }
}
