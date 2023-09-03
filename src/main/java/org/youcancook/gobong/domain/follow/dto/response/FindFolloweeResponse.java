package org.youcancook.gobong.domain.follow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FindFolloweeResponse {
    private long userId;
    private String nickname;
    private String profileImageURL;

    public static FindFolloweeResponse of(long userId, String nickname, String profileImageURL) {
        return FindFolloweeResponse.builder()
                .userId(userId)
                .nickname(nickname)
                .profileImageURL(profileImageURL)
                .build();
    }
}
