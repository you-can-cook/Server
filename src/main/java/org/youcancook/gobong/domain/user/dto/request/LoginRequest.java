package org.youcancook.gobong.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "provider는 필수입니다.")
    private String provider;

    @NotBlank(message = "oAuthId는 필수입니다.")
    private String oauthId;

    @NotBlank(message = "temporaryToken은 필수입니다.")
    private String temporaryToken;
}
