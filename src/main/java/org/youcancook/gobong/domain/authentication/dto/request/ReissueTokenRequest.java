package org.youcancook.gobong.domain.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
public class ReissueTokenRequest {

    @NotBlank(message = "refreshToken은 필수입니다.")
    private String refreshToken;
}
