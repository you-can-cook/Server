package org.youcancook.gobong.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.youcancook.gobong.global.validation.annotation.UserNickname;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
public class UpdateUserInformationRequest {

    @UserNickname
    private String nickname;

    private String profileImageURL;
}
