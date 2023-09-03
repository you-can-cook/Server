package org.youcancook.gobong.domain.follow.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FindFollowerResponse {
    private long userId;
    private String nickname;
    private String profileImageURL;
    @JsonProperty("isFollowed")
    private Boolean isFollowed;

    public static FindFollowerResponse of(long userId, String nickname, String profileImageURL, boolean isFollowed) {
        return FindFollowerResponse.builder()
                .userId(userId)
                .nickname(nickname)
                .profileImageURL(profileImageURL)
                .isFollowed(isFollowed)
                .build();
    }
}
