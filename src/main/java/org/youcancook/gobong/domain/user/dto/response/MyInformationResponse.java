package org.youcancook.gobong.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.youcancook.gobong.domain.user.entity.User;

@Getter
@Builder
@AllArgsConstructor
public class MyInformationResponse {
    private Long id;
    private String nickname;
    private String profileImageURL;
    private long followerNumber;
    private long followingNumber;
    private long recipeNumber;

    @JsonProperty("oAuthProvider")
    private String oauthProvider;

    public static MyInformationResponse of(User user, long followerNumber, long followingNumber, long recipeNumber) {
        return MyInformationResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .profileImageURL(user.getProfileImageURL())
                .oauthProvider(user.getOAuthProvider().name())
                .followerNumber(followerNumber)
                .followingNumber(followingNumber)
                .recipeNumber(recipeNumber)
                .build();
    }
}
