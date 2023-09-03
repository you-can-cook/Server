package org.youcancook.gobong.domain.recipe.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeAuthorResponse {

    private Long id;
    private String nickname;
    private String profileImageURL;
    private boolean isFollowing;
    private boolean isMyself;
}
